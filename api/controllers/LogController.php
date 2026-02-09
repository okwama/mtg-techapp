<?php
namespace Controllers;

use Config\Database;
use PDO;

class LogController extends Controller {
    private $db;

    public function __construct() {
        $database = new Database();
        $this->db = $database->getConnection();
    }

    public function index() {
        $query = "SELECT l.*, s.name as user_name 
                  FROM technical_logs l
                  LEFT JOIN staff s ON l.user_id = s.id
                  ORDER BY l.created_at DESC LIMIT 100";
        
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $logs = $stmt->fetchAll();
        
        $this->jsonResponse($logs);
    }

    public function getByResource($type, $id) {
        $query = "SELECT l.*, s.name as user_name 
                  FROM technical_logs l
                  LEFT JOIN staff s ON l.user_id = s.id
                  WHERE l.resource_type = :type AND l.resource_id = :id
                  ORDER BY l.created_at DESC";
        
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':type', $type);
        $stmt->bindParam(':id', $id);
        $stmt->execute();
        $logs = $stmt->fetchAll();
        
        $this->jsonResponse($logs);
    }
}
