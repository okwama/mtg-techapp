<?php
namespace Controllers;

use Config\Database;
use Core\JWT;
use PDO;

class AuthController extends Controller {
    private $db;

    public function __construct() {
        $database = new Database();
        $this->db = $database->getConnection();
    }

    public function login() {
        $data = $this->getPostData();

        if (!isset($data['phone_number'], $data['password'])) {
            $this->jsonResponse(['message' => 'Missing phone number or password'], 400);
        }

        $query = "SELECT id, name, role, password FROM staff WHERE phone_number = ? LIMIT 0,1";
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $data['phone_number']);
        $stmt->execute();
        $user = $stmt->fetch();

        if ($user && password_verify($data['password'], $user['password'])) {
            $rememberMe = $data['remember_me'] ?? false;
            $expiration = $rememberMe ? (365 * 24 * 60 * 60) : (24 * 60 * 60);

            $payload = [
                'user_id' => $user['id'],
                'role' => $user['role'],
                'exp' => time() + $expiration
            ];

            $token = JWT::encode($payload);

            $this->jsonResponse([
                'message' => 'Login successful',
                'token' => $token,
                'user' => [
                    'id' => $user['id'],
                    'name' => $user['name'],
                    'role' => $user['role']
                ]
            ]);
        } else {
            $this->jsonResponse(['message' => 'Invalid credentials'], 401);
        }
    }

    public function getProfile() {
        $userId = $this->getUserId();
        
        $query = "SELECT s.id, s.name, s.phone_number, s.role, s.business_email, s.station_id, st.name as station_name 
                  FROM staff s 
                  LEFT JOIN Stations st ON s.station_id = st.id 
                  WHERE s.id = ? 
                  LIMIT 0,1";
        
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(1, $userId);
        $stmt->execute();
        $user = $stmt->fetch();

        if ($user) {
            $this->jsonResponse([
                'success' => true,
                'data' => [
                    'id' => (int)$user['id'],
                    'name' => $user['name'],
                    'phone_number' => $user['phone_number'],
                    'role' => $user['role'],
                    'business_email' => $user['business_email'],
                    'station_id' => $user['station_id'] ? (int)$user['station_id'] : null,
                    'station_name' => $user['station_name']
                ]
            ]);
        } else {
            $this->jsonResponse(['message' => 'User not found'], 404);
        }
    }
}
