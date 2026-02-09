<?php
namespace Core;

use Config\Database;
use PDO;

class Logger {
    public static function log($action, $resourceType, $resourceId, $details = null, $oldValue = null, $newValue = null) {
        $database = new Database();
        $db = $database->getConnection();

        // Get User ID from Request (attached by AuthMiddleware)
        $userId = $_REQUEST['user']['user_id'] ?? null;
        if (!$userId) return false;

        $ip = $_SERVER['REMOTE_ADDR'] ?? 'unknown';

        $query = "INSERT INTO technical_logs 
                  SET user_id = :user_id, action = :action, resource_type = :res_type, 
                      resource_id = :res_id, details = :details, old_value = :old_val, 
                      new_value = :new_val, ip_address = :ip";

        $stmt = $db->prepare($query);
        $stmt->bindParam(':user_id', $userId);
        $stmt->bindParam(':action', $action);
        $stmt->bindParam(':res_type', $resourceType);
        $stmt->bindParam(':res_id', $resourceId);
        $stmt->bindParam(':details', $details);
        
        $oldValueJson = $oldValue ? (is_array($oldValue) ? json_encode($oldValue) : $oldValue) : null;
        $newValueJson = $newValue ? (is_array($newValue) ? json_encode($newValue) : $newValue) : null;
        
        $stmt->bindParam(':old_val', $oldValueJson);
        $stmt->bindParam(':new_val', $newValueJson);
        $stmt->bindParam(':ip', $ip);

        return $stmt->execute();
    }
}
