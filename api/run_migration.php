<?php
require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/config/Database.php';

use Config\Database;
use Core\Env;

// Load Environment Variables
Env::load(__DIR__ . '/.env');

try {
    $dbObj = new Database();
    $db = $dbObj->getConnection();
    
    // Check if conversion_id column exists
    $checkColumn = $db->query("SHOW COLUMNS FROM inspections LIKE 'conversion_id'");
    if ($checkColumn->rowCount() == 0) {
        echo "Adding conversion_id to inspections table...\n";
        $db->exec("ALTER TABLE inspections ADD COLUMN conversion_id INT(11) DEFAULT NULL AFTER ticket_id");
        $db->exec("ALTER TABLE inspections ADD CONSTRAINT fk_inspection_conversion FOREIGN KEY (conversion_id) REFERENCES conversions(id)");
    } else {
        echo "conversion_id column already exists.\n";
    }

    // Make vehicle_id nullable
    echo "Updating vehicle_id to be nullable...\n";
    $db->exec("ALTER TABLE inspections MODIFY COLUMN vehicle_id INT(11) DEFAULT NULL");

    echo "Migration completed successfully!\n";
} catch (Exception $e) {
    echo "Migration failed: " . $e->getMessage() . "\n";
}
