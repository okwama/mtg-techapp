<?php
namespace Core;

class JWT {
    private static function getSecret() {
        return \Core\Env::get('JWT_SECRET', 'motor_gas_secret_key_2026');
    }

    public static function encode($payload) {
        $secret = self::getSecret();
        $header = json_encode(['typ' => 'JWT', 'alg' => 'HS256']);
        $base64UrlHeader = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($header));
        
        $base64UrlPayload = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode(json_encode($payload)));
        
        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $secret, true);
        $base64UrlSignature = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($signature));
        
        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }

    public static function decode($token) {
        $secret = self::getSecret();
        $parts = explode('.', $token);
        if (count($parts) !== 3) return false;

        list($header, $payload, $signature) = $parts;
        $valid_sig = hash_hmac('sha256', $header . "." . $payload, $secret, true);
        $base64UrlSignature = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($valid_sig));

        if ($base64UrlSignature !== $signature) return false;

        return json_decode(base64_decode($payload), true);
    }
}
