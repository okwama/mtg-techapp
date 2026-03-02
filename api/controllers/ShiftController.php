<?php
namespace Controllers;

use PDO;
use Exception;
use Config\Database;

class ShiftController extends Controller {
    private $conn;

    public function __construct() {
        $database = new Database();
        $this->conn = $database->getConnection();
    }

    /**
     * List all available stations
     */
    public function index() {
        try {
            $query = "SELECT s.*, r.name as region_name 
                     FROM Stations s 
                     LEFT JOIN Regions r ON s.regionId = r.id 
                     ORDER BY s.name ASC";
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            $stations = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return $this->jsonResponse($stations);
        } catch (Exception $e) {
            return $this->jsonResponse(['error' => $e->getMessage()], 500);
        }
    }

    /**
     * Check current shift status for the technician
     */
    public function status() {
        $userId = $this->getUserId();
        if (!$userId) {
            return $this->jsonResponse(['error' => 'Unauthorized'], 401);
        }

        try {
            // Find active shift (status = 1 means checked in)
            $query = "SELECT s.*, st.name as station_name 
                     FROM shifts s 
                     JOIN Stations st ON s.station_id = st.id 
                     WHERE s.userId = :userId AND s.status = 1 
                     ORDER BY s.id DESC LIMIT 1";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':userId', $userId);
            $stmt->execute();
            $shift = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($shift) {
                return $this->jsonResponse(['active' => true, 'shift' => $shift]);
            } else {
                return $this->jsonResponse(['active' => false]);
            }
        } catch (Exception $e) {
            return $this->jsonResponse(['error' => $e->getMessage()], 500);
        }
    }

    /**
     * Handle technician check-in
     */
    public function checkin() {
        $userId = $this->getUserId();
        $data = $this->getPostData();

        if (!$userId) return $this->jsonResponse(['error' => 'Unauthorized'], 401);
        if (!isset($data['station_id'])) return $this->jsonResponse(['error' => 'Station ID required'], 400);

        try {
            // Check for existing active shift
            $query = "SELECT id FROM shifts WHERE userId = :userId AND status = 1 LIMIT 1";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':userId', $userId);
            $stmt->execute();
            if ($stmt->fetch()) {
                return $this->jsonResponse(['error' => 'You already have an active shift. Please check out first.'], 400);
            }

            // Get station details
            $query = "SELECT name, address FROM Stations WHERE id = :station_id";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':station_id', $data['station_id']);
            $stmt->execute();
            $station = $stmt->fetch(PDO::FETCH_ASSOC);
            if (!$station) return $this->jsonResponse(['error' => 'Station not found'], 404);

            // Fetch user name
            $query = "SELECT name FROM staff WHERE id = :userId";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':userId', $userId);
            $stmt->execute();
            $user = $stmt->fetch(PDO::FETCH_ASSOC);

            $now = date('Y-m-d H:i:s');
            
            // Create shift
            $query = "INSERT INTO shifts (
                        date, time, userId, userName, station_id, station_name, 
                        status, checkInTime, latitude, longitude, 
                        createdAt, updatedAt, outlet_address, approvedAt
                      ) VALUES (
                        :date, :time, :userId, :userName, :station_id, :station_name, 
                        1, :checkInTime, :latitude, :longitude, 
                        :createdAt, :updatedAt, :outlet_address, ''
                      )";
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute([
                ':date' => $now,
                ':time' => date('H:i'),
                ':userId' => $userId,
                ':userName' => $user['name'] ?? 'Technician',
                ':station_id' => $data['station_id'],
                ':station_name' => $station['name'],
                ':checkInTime' => $now,
                ':latitude' => $data['latitude'] ?? null,
                ':longitude' => $data['longitude'] ?? null,
                ':createdAt' => $now,
                ':updatedAt' => $now,
                ':outlet_address' => $station['address'] ?? ''
            ]);

            return $this->jsonResponse([
                'message' => 'Checked in successfully',
                'shift_id' => $this->conn->lastInsertId()
            ]);
            
        } catch (Exception $e) {
            return $this->jsonResponse(['error' => $e->getMessage()], 500);
        }
    }

    /**
     * Handle technician check-out
     */
    public function checkout() {
        $userId = $this->getUserId();
        $data = $this->getPostData();

        if (!$userId) return $this->jsonResponse(['error' => 'Unauthorized'], 401);

        try {
            // Find active shift
            $query = "SELECT id, checkInTime FROM shifts WHERE userId = :userId AND status = 1 ORDER BY id DESC LIMIT 1";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':userId', $userId);
            $stmt->execute();
            $shift = $stmt->fetch(PDO::FETCH_ASSOC);

            if (!$shift) {
                return $this->jsonResponse(['error' => 'No active shift found'], 404);
            }

            $now = date('Y-m-d H:i:s');
            
            // Update shift to completed (status = 2)
            $query = "UPDATE shifts SET 
                        status = 2, 
                        checkoutTime = :checkoutTime, 
                        checkoutLatitude = :latitude, 
                        checkoutLongitude = :longitude, 
                        updatedAt = :updatedAt 
                      WHERE id = :id";
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute([
                ':checkoutTime' => $now,
                ':latitude' => $data['latitude'] ?? null,
                ':longitude' => $data['longitude'] ?? null,
                ':updatedAt' => $now,
                ':id' => $shift['id']
            ]);

            return $this->jsonResponse(['message' => 'Checked out successfully']);

        } catch (Exception $e) {
            return $this->jsonResponse(['error' => $e->getMessage()], 500);
        }
    }
}
