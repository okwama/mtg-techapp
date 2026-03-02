<?php
namespace Controllers;

class HomeController extends Controller {
    public function index() {
        $this->jsonResponse([
            'status' => 'success',
            'message' => 'MotorGas API is live',
            'version' => '1.0.0',
            'timestamp' => date('Y-m-d H:i:s')
        ]);
    }
}
