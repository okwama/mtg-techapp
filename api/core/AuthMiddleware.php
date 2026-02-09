<?php
namespace Core;

class AuthMiddleware {
    public static function check($roles = []) {
        $headers = getallheaders();
        $authHeader = $headers['Authorization'] ?? $headers['authorization'] ?? '';

        if (!$authHeader || !preg_match('/Bearer\s(\S+)/', $authHeader, $matches)) {
            http_response_code(401);
            echo json_encode(['message' => 'Unauthorized: Token missing']);
            exit();
        }

        $token = $matches[1];
        $decoded = JWT::decode($token);

        if (!$decoded) {
            http_response_code(401);
            echo json_encode(['message' => 'Unauthorized: Invalid or expired token']);
            exit();
        }

        // Role-based check
        if (!empty($roles) && !in_array($decoded['role'], $roles)) {
            http_response_code(403);
            echo json_encode(['message' => 'Forbidden: Insufficient permissions']);
            exit();
        }

        // Attach user info to the request (optional, can be accessed via global/static)
        $_REQUEST['user'] = $decoded;
        return true;
    }
}
