<?php
namespace Controllers;

use Config\Database;
use PDO;

class InspectionController extends Controller {
    private $db;

    public function __construct() {
        $database = new Database();
        $this->db = $database->getConnection();
    }

    public function index() {
        $technicianId = $this->getUserId();
        $query = "SELECT i.*, c.owner_full_name, v.registration_number, c.scheduled_date 
                  FROM inspections i
                  LEFT JOIN conversions c ON i.conversion_id = c.id
                  LEFT JOIN Vehicles v ON i.vehicle_id = v.id
                  WHERE i.technician_id = ?
                  ORDER BY i.created_at DESC";
        
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $technicianId);
        $stmt->execute();
        $inspections = $stmt->fetchAll();
        
        // Decode checklist data for each inspection
        foreach ($inspections as &$inspection) {
            if (isset($inspection['checklist_data'])) {
                $inspection['checklist_data'] = json_decode($inspection['checklist_data']);
            }
        }
        
        $this->jsonResponse($inspections);
    }

    public function scheduled() {
        $technicianId = $this->getUserId();
        // For now, we show all scheduled conversions as potential inspections
        $query = "SELECT c.id as conversion_id, c.owner_full_name, c.vehicle_registration, 
                         c.make, c.model, c.scheduled_date, i.id as inspection_id, i.status as inspection_status
                  FROM conversions c
                  LEFT JOIN inspections i ON c.id = i.conversion_id
                  ORDER BY c.scheduled_date ASC";
        
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $scheduled = $stmt->fetchAll();
        
        $this->jsonResponse($scheduled);
    }

    public function clients() {
        // Union KeyAccounts and conversions (unique owner names)
        $query = "(SELECT id, name, contact, email, 'account' as source FROM KeyAccounts WHERE is_active = 1)
                  UNION
                  (SELECT id, owner_full_name as name, contact, email, 'conversion' as source FROM conversions WHERE status NOT IN ('declined', 'completed'))
                  ORDER BY name ASC";
        
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $clients = $stmt->fetchAll();
        $this->jsonResponse($clients);
    }

    public function vehicles($clientId) {
        $source = $_GET['source'] ?? 'account';
        
        if ($source === 'conversion') {
            $query = "SELECT id, vehicle_registration as registration_number, make, model, vin_chassis_number as vin_serial_number, 'conversion' as source 
                      FROM conversions WHERE id = ?";
            $stmt = $this->db->prepare($query);
            $stmt->bindParam(1, $clientId);
        } else {
            $query = "SELECT id, registration_number, make, model, vin_serial_number, 'account' as source FROM Vehicles WHERE key_account_id = ?
                      UNION
                      SELECT id, vehicle_registration as registration_number, make, model, vin_chassis_number as vin_serial_number, 'conversion' as source 
                      FROM conversions WHERE owner_full_name = (SELECT name FROM KeyAccounts WHERE id = ?)";
            $stmt = $this->db->prepare($query);
            $stmt->bindParam(1, $clientId);
            $stmt->bindParam(2, $clientId);
        }
        
        $stmt->execute();
        $vehicles = $stmt->fetchAll();
        $this->jsonResponse($vehicles);
    }

    public function store() {
        $data = $this->getPostData();

        if (!isset($data['station_id']) || (!isset($data['vehicle_id']) && !isset($data['conversion_id']))) {
            $this->jsonResponse(['message' => 'Missing required fields: station_id and at least vehicle_id or conversion_id'], 400);
        }

        $technician_id = $this->getUserId();
        $inspection_number = "INS-" . date("Ymd") . "-" . rand(1000, 9999);
        $conversion_id = $data['conversion_id'] ?? null;

        $query = "INSERT INTO inspections 
                   SET inspection_number=:inspection_number, conversion_id=:conversion_id, 
                       technician_id=:technician_id, vehicle_id=:vehicle_id, station_id=:station_id, 
                       status='pending'";
 
         $stmt = $this->db->prepare($query);
         $stmt->bindParam(':inspection_number', $inspection_number);
         $stmt->bindParam(':conversion_id', $conversion_id);
         $stmt->bindParam(':technician_id', $technician_id);
         
         $vid = $data['vehicle_id'] ?? null;
         $stmt->bindParam(':vehicle_id', $vid);
         
         $stmt->bindParam(':station_id', $data['station_id']);
 
         if ($stmt->execute()) {
             $inspectionId = (int)$this->db->lastInsertId();
             $target = $vid ?? ("Conversion " . $conversion_id);
             \Core\Logger::log('INSPECTION_STARTED', 'inspection', $inspectionId, "Inspection started for " . $target);
             $this->jsonResponse([
                'success' => true,
                'message' => 'Inspection started', 
                'data' => $inspectionId
            ], 201);
        } else {
            $this->jsonResponse(['success' => false, 'message' => 'Unable to start inspection'], 500);
        }
    }

    public function show($id) {
        // ... (no changes to show)
        $query = "SELECT i.*, c.owner_full_name, c.scheduled_date, s.name as technician_name, v.registration_number, v.make, v.model
                  FROM inspections i
                  LEFT JOIN conversions c ON i.conversion_id = c.id
                  LEFT JOIN staff s ON i.technician_id = s.id
                  LEFT JOIN Vehicles v ON i.vehicle_id = v.id
                  WHERE i.id = ? LIMIT 0,1";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $id);
        $stmt->execute();
        $inspection = $stmt->fetch();

        if ($inspection) {
            // Decode checklist data if exists
            if (!empty($inspection['checklist_data'])) {
                $inspection['checklist_data'] = json_decode($inspection['checklist_data'], true);
            }

            // Fallback for vehicle details from conversion if vehicle_id is null
            if (empty($inspection['registration_number']) && !empty($inspection['vehicle_registration'])) {
                $inspection['registration_number'] = $inspection['vehicle_registration'];
                $inspection['make'] = $inspection['make'] ?: $inspection['make'];
                $inspection['model'] = $inspection['model'] ?: $inspection['model'];
            }

            // Fetch photos
            $photoQuery = "SELECT * FROM inspection_photos WHERE inspection_id = ?";
            $photoStmt = $this->db->prepare($photoQuery);
            $photoStmt->bindParam(1, $id);
            $photoStmt->execute();
            $inspection['photos'] = $photoStmt->fetchAll();

            $this->jsonResponse($inspection);
        } else {
            $this->jsonResponse(['message' => 'Inspection not found'], 404);
        }
    }

    public function update($id) {
        $data = $this->getPostData();
        
        $query = "UPDATE inspections 
                  SET checklist_data = :checklist_data, 
                      summary = :summary, 
                      overall_condition = :overall_condition,
                      status = :status,
                      updated_at = CURRENT_TIMESTAMP 
                  WHERE id = :id";

        $stmt = $this->db->prepare($query);
        
        $checklist = isset($data['checklist_data']) ? json_encode($data['checklist_data']) : null;
        $stmt->bindParam(':checklist_data', $checklist);
        $stmt->bindParam(':summary', $data['summary']);
        $stmt->bindParam(':overall_condition', $data['overall_condition']);
        $stmt->bindParam(':status', $data['status']);
        $stmt->bindParam(':id', $id);

        if ($stmt->execute()) {
            \Core\Logger::log('INSPECTION_UPDATED', 'inspection', $id, "Inspection data updated", null, $data);
            $this->jsonResponse(['success' => true, 'message' => 'Inspection updated successfully']);
        } else {
            $this->jsonResponse(['success' => false, 'message' => 'Failed to update inspection'], 500);
        }
    }

    public function submit($id) {
        $query = "UPDATE inspections SET status = 'completed', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $id);

        if ($stmt->execute()) {
            \Core\Logger::log('INSPECTION_SUBMITTED', 'inspection', $id, "Inspection submitted for review");
            $this->jsonResponse(['success' => true, 'message' => 'Inspection submitted for review']);
        } else {
            $this->jsonResponse(['success' => false, 'message' => 'Failed to submit inspection'], 500);
        }
    }

    public function approve($id) {
        $data = $this->getPostData();
        $query = "UPDATE inspections 
                  SET status = 'approved', 
                      admin_comments = :comments, 
                      updated_at = CURRENT_TIMESTAMP 
                  WHERE id = :id";
        
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':comments', $data['admin_comments']);
        $stmt->bindParam(':id', $id);

        if ($stmt->execute()) {
            \Core\Logger::log('INSPECTION_APPROVED', 'inspection', $id, "Inspection approved by admin: " . ($data['admin_comments'] ?? ''));
            $this->jsonResponse(['success' => true, 'message' => 'Inspection approved']);
        } else {
            $this->jsonResponse(['success' => false, 'message' => 'Failed to approve inspection'], 500);
        }
    }

    public function reject($id) {
        $data = $this->getPostData();
        $query = "UPDATE inspections 
                  SET status = 'rejected', 
                      admin_comments = :comments, 
                      updated_at = CURRENT_TIMESTAMP 
                  WHERE id = :id";
        
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':comments', $data['admin_comments']);
        $stmt->bindParam(':id', $id);

        if ($stmt->execute()) {
            $this->jsonResponse(['success' => true, 'message' => 'Inspection rejected']);
        } else {
            $this->jsonResponse(['success' => false, 'message' => 'Failed to reject inspection'], 500);
        }
    }

    public function generateReport($id) {
        // Fetch detailed inspection data linked to conversion
        $query = "SELECT i.*, c.inspection_number as insp_no, c.owner_full_name, c.scheduled_date, 
                         s.name as technician_name, v.registration_number, v.make, v.model, v.vin_serial_number
                  FROM inspections i
                  LEFT JOIN conversions c ON i.conversion_id = c.id
                  LEFT JOIN staff s ON i.technician_id = s.id
                  LEFT JOIN Vehicles v ON i.vehicle_id = v.id
                  WHERE i.id = ? LIMIT 0,1";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $id);
        $stmt->execute();
        $inspection = $stmt->fetch();

        if (!$inspection) {
            $this->jsonResponse(['message' => 'Inspection not found'], 404);
        }

        // Fetch photos
        $photoQuery = "SELECT * FROM inspection_photos WHERE inspection_id = ?";
        $photoStmt = $this->db->prepare($photoQuery);
        $photoStmt->bindParam(1, $id);
        $photoStmt->execute();
        $inspection['photos'] = $photoStmt->fetchAll();

        // Generate PDF
        $html = \Core\PdfGenerator::renderReportTemplate($inspection);
        
        \Core\Logger::log('REPORT_GENERATED', 'inspection', $id, "PDF report generated for inspection " . $inspection['inspection_number']);
        
        \Core\PdfGenerator::generate($html, "Report-{$inspection['inspection_number']}.pdf");
    }

    public function uploadPhotos($id) {
        if (!isset($_FILES['photo'], $_POST['photo_type'])) {
            $this->jsonResponse(['message' => 'Missing file or photo type'], 400);
        }

        $file = $_FILES['photo'];
        $photoType = $_POST['photo_type'];
        $caption = $_POST['caption'] ?? '';

        // Temporary storage before uploading to Cloudinary
        $tempPath = sys_get_temp_dir() . DIRECTORY_SEPARATOR . basename($file['name']);
        if (!move_uploaded_file($file['tmp_name'], $tempPath)) {
            $this->jsonResponse(['message' => 'Failed to process uploaded file'], 500);
        }

        // Upload to Cloudinary
        $cloudinary = new \Core\CloudinaryService();
        $photoUrl = $cloudinary->upload($tempPath);

        // Delete temporary file
        unlink($tempPath);

        if (!$photoUrl) {
            $this->jsonResponse(['message' => 'Failed to upload photo to Cloudinary'], 500);
        }

        // Save to database
        $query = "INSERT INTO inspection_photos 
                  SET inspection_id = :insp_id, photo_url = :url, 
                      photo_type = :type, caption = :caption";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':insp_id', $id);
        $stmt->bindParam(':url', $photoUrl);
        $stmt->bindParam(':type', $photoType);
        $stmt->bindParam(':caption', $caption);

        if ($stmt->execute()) {
            \Core\Logger::log('PHOTO_UPLOADED', 'inspection', $id, "Photo uploaded: " . $photoType);
            $this->jsonResponse([
                'success' => true, 
                'message' => 'Photo uploaded successfully', 
                'data' => $photoUrl
            ], 201);
        } else {
            $this->jsonResponse(['success' => false, 'message' => 'Failed to save photo to database'], 500);
        }
    }
}
