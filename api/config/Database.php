<?php
namespace Config;

use PDO;
use PDOException;

class Database {
    private $host;
    private $db_name;
    private $username;
    private $password;
    public $conn;

    public function getConnection() {
        $this->host = \Core\Env::get('DB_HOST', 'localhost');
        $this->db_name = \Core\Env::get('DB_NAME', 'impulsep_motorgas');
        $this->username = \Core\Env::get('DB_USER', 'root');
        $this->password = \Core\Env::get('DB_PASS', '');
        
        $this->conn = null;

        try {
            $this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->db_name, $this->username, $this->password);
            $this->conn->exec("set names utf8mb4");
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $this->conn->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
        } catch(PDOException $exception) {
            header('Content-Type: application/json');
            http_response_code(500);
            echo json_encode(["message" => "Connection error: " . $exception->getMessage()]);
            exit();
        }

        return $this->conn;
    }
}
