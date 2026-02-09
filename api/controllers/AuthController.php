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
            $payload = [
                'user_id' => $user['id'],
                'role' => $user['role'],
                'exp' => time() + (24 * 60 * 60) // 24 hours
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
}
