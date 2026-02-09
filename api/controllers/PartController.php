<?php
namespace Controllers;

use Config\Database;
use PDO;

class PartController extends Controller {
    private $db;

    public function __construct() {
        $database = new Database();
        $this->db = $database->getConnection();
    }

    public function index() {
        $query = "SELECT * FROM Parts ORDER BY name ASC";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $parts = $stmt->fetchAll();
        
        $this->jsonResponse($parts);
    }

    public function requestParts() {
        $data = $this->getPostData();

        if (!isset($data['technician_id'], $data['part_id'], $data['quantity'], $data['station_id'])) {
            $this->jsonResponse(['message' => 'Missing required fields'], 400);
        }

        $query = "INSERT INTO parts_requests 
                  SET technician_id=:technician_id, part_id=:part_id, quantity=:quantity, 
                      station_id=:station_id, inspection_id=:inspection_id, reason=:reason, 
                      status='pending'";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':technician_id', $data['technician_id']);
        $stmt->bindParam(':part_id', $data['part_id']);
        $stmt->bindParam(':quantity', $data['quantity']);
        $stmt->bindParam(':station_id', $data['station_id']);
        $stmt->bindParam(':inspection_id', $data['inspection_id']);
        $stmt->bindParam(':reason', $data['reason']);

        if ($stmt->execute()) {
            $this->jsonResponse(['message' => 'Parts request submitted', 'id' => $this->db->lastInsertId()], 201);
        } else {
            $this->jsonResponse(['message' => 'Unable to submit request'], 500);
        }
    }

    public function approveRequest($id) {
        $data = $this->getPostData();
        if (!isset($data['approver_id'])) {
            $this->jsonResponse(['message' => 'Approver ID required'], 400);
        }

        $this->db->beginTransaction();

        try {
            // 1. Update request status
            $query = "UPDATE parts_requests SET status='approved', approver_id=:approver_id WHERE id=:id";
            $stmt = $this->db->prepare($query);
            $stmt->bindParam(':approver_id', $data['approver_id']);
            $stmt->bindParam(':id', $id);
            $stmt->execute();

            // 2. Ideally, deduct from inventory logic would go here
            // But this depends on how strictly you want to manage 'parts_inventory' table
            
            $this->db->commit();
            $this->jsonResponse(['message' => 'Parts request approved']);
        } catch (\Exception $e) {
            $this->db->rollBack();
            $this->jsonResponse(['message' => 'Failed to approve request: ' . $e->getMessage()], 500);
        }
    }
}
