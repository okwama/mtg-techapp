<?php
namespace Controllers;

use Config\Database;
use PDO;

class TicketController extends Controller {
    private $db;

    public function __construct() {
        $database = new Database();
        $this->db = $database->getConnection();
    }

    public function index() {
        $query = "SELECT t.*, s.name as technician_name, st.name as station_name, v.registration_number 
                  FROM tickets t
                  LEFT JOIN staff s ON t.technician_id = s.id
                  LEFT JOIN Stations st ON t.station_id = st.id
                  LEFT JOIN Vehicles v ON t.vehicle_id = v.id
                  ORDER BY t.created_at DESC";
        
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $tickets = $stmt->fetchAll();
        
        $this->jsonResponse($tickets);
    }

    public function show($id) {
        $query = "SELECT t.*, s.name as technician_name, st.name as station_name, v.registration_number 
                  FROM tickets t
                  LEFT JOIN staff s ON t.technician_id = s.id
                  LEFT JOIN Stations st ON t.station_id = st.id
                  LEFT JOIN Vehicles v ON t.vehicle_id = v.id
                  WHERE t.id = ? LIMIT 0,1";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $id);
        $stmt->execute();
        $ticket = $stmt->fetch();

        if ($ticket) {
            $this->jsonResponse($ticket);
        } else {
            $this->jsonResponse(['message' => 'Ticket not found'], 404);
        }
    }

    public function store() {
        $data = $this->getPostData();

        if (!isset($data['title'], $data['technician_id'], $data['vehicle_id'], $data['station_id'], $data['created_by'])) {
            $this->jsonResponse(['message' => 'Missing required fields'], 400);
        }

        // Generate Ticket Number
        $ticket_number = "TKT-" . date("Ymd") . "-" . rand(1000, 9999);

        $query = "INSERT INTO tickets 
                  SET ticket_number=:ticket_number, title=:title, description=:description, 
                      technician_id=:technician_id, vehicle_id=:vehicle_id, station_id=:station_id, 
                      priority=:priority, status=:status, due_date=:due_date, created_by=:created_by";

        $stmt = $this->db->prepare($query);

        $stmt->bindParam(':ticket_number', $ticket_number);
        $stmt->bindParam(':title', $data['title']);
        $stmt->bindParam(':description', $data['description']);
        $stmt->bindParam(':technician_id', $data['technician_id']);
        $stmt->bindParam(':vehicle_id', $data['vehicle_id']);
        $stmt->bindParam(':station_id', $data['station_id']);
        $stmt->bindParam(':priority', $data['priority']);
        $stmt->bindParam(':status', $data['status']);
        $stmt->bindParam(':due_date', $data['due_date']);
        $stmt->bindParam(':created_by', $data['created_by']);

        if ($stmt->execute()) {
            $ticketId = $this->db->lastInsertId();
            \Core\Logger::log('TICKET_CREATED', 'ticket', $ticketId, "Ticket '{$data['title']}' created and assigned.", null, $data['status']);
            $this->jsonResponse(['message' => 'Ticket created successfully', 'ticket_id' => $ticketId], 201);
        } else {
            $this->jsonResponse(['message' => 'Unable to create ticket'], 500);
        }
    }

    public function update($id) {
        $data = $this->getPostData();
        
        // Fetch old status for logging
        $oldQuery = "SELECT status FROM tickets WHERE id = ? LIMIT 0,1";
        $oldStmt = $this->db->prepare($oldQuery);
        $oldStmt->execute([$id]);
        $oldTicket = $oldStmt->fetch();

        $query = "UPDATE tickets SET status = :status, updated_at = CURRENT_TIMESTAMP WHERE id = :id";
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':status', $data['status']);
        $stmt->bindParam(':id', $id);

        if ($stmt->execute()) {
            \Core\Logger::log('TICKET_STATUS_CHANGED', 'ticket', $id, "Ticket status updated to " . $data['status'], $oldTicket['status'], $data['status']);
            $this->jsonResponse(['message' => 'Ticket updated successfully']);
        } else {
            $this->jsonResponse(['message' => 'Unable to update ticket'], 500);
        }
    }
}
