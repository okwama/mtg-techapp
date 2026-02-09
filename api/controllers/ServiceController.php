<?php
namespace Controllers;

use Config\Database;
use PDO;

class ServiceController extends Controller {
    private $db;

    public function __construct() {
        $database = new Database();
        $this->db = $database->getConnection();
    }

    public function index() {
        $query = "SELECT sa.*, i.inspection_number, t.name as technician_name, a.name as admin_name 
                  FROM service_approvals sa
                  LEFT JOIN inspections i ON sa.inspection_id = i.id
                  LEFT JOIN staff t ON sa.technician_id = t.id
                  LEFT JOIN staff a ON sa.admin_id = a.id
                  ORDER BY sa.created_at DESC";
        
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $approvals = $stmt->fetchAll();
        
        $this->jsonResponse($approvals);
    }

    public function store() {
        $data = $this->getPostData();

        if (!isset($data['inspection_id'], $data['technician_id'], $data['service_description'])) {
            $this->jsonResponse(['message' => 'Missing required fields'], 400);
        }

        $query = "INSERT INTO service_approvals 
                  SET inspection_id=:inspection_id, technician_id=:technician_id, 
                      service_description=:service_description, estimated_cost=:estimated_cost, 
                      labor_hours=:labor_hours, parts_needed=:parts_needed, status='pending'";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':inspection_id', $data['inspection_id']);
        $stmt->bindParam(':technician_id', $data['technician_id']);
        $stmt->bindParam(':service_description', $data['service_description']);
        $stmt->bindParam(':estimated_cost', $data['estimated_cost']);
        $stmt->bindParam(':labor_hours', $data['labor_hours']);
        $parts_needed = isset($data['parts_needed']) ? json_encode($data['parts_needed']) : null;
        $stmt->bindParam(':parts_needed', $parts_needed);

        if ($stmt->execute()) {
            $this->jsonResponse(['message' => 'Service approval request submitted', 'id' => $this->db->lastInsertId()], 201);
        } else {
            $this->jsonResponse(['message' => 'Unable to submit request'], 500);
        }
    }

    public function approve($id) {
        $data = $this->getPostData();
        if (!isset($data['admin_id'])) {
            $this->jsonResponse(['message' => 'Admin ID required for approval'], 400);
        }

        $query = "UPDATE service_approvals 
                  SET status='approved', admin_id=:admin_id, admin_comments=:comments, updated_at=CURRENT_TIMESTAMP 
                  WHERE id=:id";
        
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':admin_id', $data['admin_id']);
        $stmt->bindParam(':comments', $data['admin_comments']);
        $stmt->bindParam(':id', $id);

        if ($stmt->execute()) {
            $this->jsonResponse(['message' => 'Service request approved']);
        } else {
            $this->jsonResponse(['message' => 'Failed to approve request'], 500);
        }
    }

    public function decline($id) {
        $data = $this->getPostData();
        if (!isset($data['admin_id'])) {
            $this->jsonResponse(['message' => 'Admin ID required'], 400);
        }

        $query = "UPDATE service_approvals 
                  SET status='declined', admin_id=:admin_id, admin_comments=:comments, updated_at=CURRENT_TIMESTAMP 
                  WHERE id=:id";
        
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':admin_id', $data['admin_id']);
        $stmt->bindParam(':comments', $data['admin_comments']);
        $stmt->bindParam(':id', $id);

        if ($stmt->execute()) {
            $this->jsonResponse(['message' => 'Service request declined']);
        } else {
            $this->jsonResponse(['message' => 'Failed to decline request'], 500);
        }
    }
}
