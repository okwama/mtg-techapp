<?php
require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/core/Env.php';
require_once __DIR__ . '/config/Database.php';

use Config\Database;

$db = (new Database())->getConnection();
$stmt = $db->query("DESCRIBE inspections");
$columns = $stmt->fetchAll();

foreach ($columns as $column) {
    echo "Field: {$column['Field']}, Type: {$column['Type']}, Null: {$column['Null']}, Key: {$column['Key']}\n";
}
