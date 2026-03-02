<?php
namespace Controllers;

class Controller {
    protected function jsonResponse($data, $code = 200) {
        header('Content-Type: application/json');
        http_response_code($code);
        echo json_encode($data);
        exit();
    }

    protected function getPostData() {
        return json_decode(file_get_contents("php://input"), true);
    }

    protected function getUserId() {
        return $_REQUEST['user']['user_id'] ?? null;
    }

    protected function getUserRole() {
        return $_REQUEST['user']['role'] ?? null;
    }
}
