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
        $query = "SELECT i.*, t.ticket_number, s.name as technician_name, v.registration_number 
                  FROM inspections i
                  LEFT JOIN tickets t ON i.ticket_id = t.id
                  LEFT JOIN staff s ON i.technician_id = s.id
                  LEFT JOIN Vehicles v ON i.vehicle_id = v.id
                  ORDER BY i.created_at DESC";
        
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $inspections = $stmt->fetchAll();
        
        $this->jsonResponse($inspections);
    }

    public function store() {
        $data = $this->getPostData();

        if (!isset($data['technician_id'], $data['vehicle_id'], $data['station_id'])) {
            $this->jsonResponse(['message' => 'Missing required fields'], 400);
        }

        $inspection_number = "INS-" . date("Ymd") . "-" . rand(1000, 9999);

        $query = "INSERT INTO inspections 
                  SET inspection_number=:inspection_number, ticket_id=:ticket_id, 
                      technician_id=:technician_id, vehicle_id=:vehicle_id, station_id=:station_id, 
                      status='pending'";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':inspection_number', $inspection_number);
        $stmt->bindParam(':ticket_id', $data['ticket_id']);
        $stmt->bindParam(':technician_id', $data['technician_id']);
        $stmt->bindParam(':vehicle_id', $data['vehicle_id']);
        $stmt->bindParam(':station_id', $data['station_id']);

        if ($stmt->execute()) {
            $inspectionId = $this->db->lastInsertId();
            \Core\Logger::log('INSPECTION_STARTED', 'inspection', $inspectionId, "Inspection started for vehicle " . $data['vehicle_id']);
            $this->jsonResponse(['message' => 'Inspection started', 'inspection_id' => $inspectionId], 201);
        } else {
            $this->jsonResponse(['message' => 'Unable to start inspection'], 500);
        }
    }

    public function show($id) {
        // ... (no changes to show)
        $query = "SELECT i.*, t.ticket_number, s.name as technician_name, v.registration_number 
                  FROM inspections i
                  LEFT JOIN tickets t ON i.ticket_id = t.id
                  LEFT JOIN staff s ON i.technician_id = s.id
                  LEFT JOIN Vehicles v ON i.vehicle_id = v.id
                  WHERE i.id = ? LIMIT 0,1";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $id);
        $stmt->execute();
        $inspection = $stmt->fetch();

        if ($inspection) {
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
            $this->jsonResponse(['message' => 'Inspection updated successfully']);
        } else {
            $this->jsonResponse(['message' => 'Failed to update inspection'], 500);
        }
    }

    public function submit($id) {
        $query = "UPDATE inspections SET status = 'completed', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $id);

        if ($stmt->execute()) {
            \Core\Logger::log('INSPECTION_SUBMITTED', 'inspection', $id, "Inspection submitted for review");
            $this->jsonResponse(['message' => 'Inspection submitted for review']);
        } else {
            $this->jsonResponse(['message' => 'Failed to submit inspection'], 500);
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
            $this->jsonResponse(['message' => 'Inspection approved']);
        } else {
            $this->jsonResponse(['message' => 'Failed to approve inspection'], 500);
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
            $this->jsonResponse(['message' => 'Inspection rejected']);
        } else {
            $this->jsonResponse(['message' => 'Failed to reject inspection'], 500);
        }
    }

    public function generateReport($id) {
        // Fetch detailed inspection data
        $query = "SELECT i.*, t.ticket_number, s.name as technician_name, v.registration_number 
                  FROM inspections i
                  LEFT JOIN tickets t ON i.ticket_id = t.id
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
            $this->jsonResponse(['message' => 'Photo uploaded successfully', 'photo_url' => $photoUrl], 201);
        } else {
            $this->jsonResponse(['message' => 'Failed to save photo to database'], 500);
        }
    }
}
