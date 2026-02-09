<?php
namespace Core;

use Cloudinary\Cloudinary;
use Cloudinary\Configuration\Configuration;

class CloudinaryService {
    private $cloudinary;

    public function __construct() {
        $this->cloudinary = new Cloudinary([
            'cloud' => [
                'cloud_name' => Env::get('CLOUDINARY_CLOUD_NAME'),
                'api_key'    => Env::get('CLOUDINARY_API_KEY'),
                'api_secret' => Env::get('CLOUDINARY_API_SECRET'),
            ],
            'url' => [
                'secure' => true
            ]
        ]);
    }

    public function upload($filePath, $folder = 'inspections') {
        try {
            $result = $this->cloudinary->uploadApi()->upload($filePath, [
                'folder' => $folder,
                'use_filename' => true,
                'unique_filename' => true,
            ]);
            return $result['secure_url'];
        } catch (\Exception $e) {
            error_log("Cloudinary Upload Error: " . $e->getMessage());
            return null;
        }
    }
}
