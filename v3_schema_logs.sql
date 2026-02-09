-- Technical Logs (Audit Trail) Schema
-- Tracks status changes and key actions across the system

CREATE TABLE IF NOT EXISTS `technical_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `action` varchar(255) NOT NULL, -- e.g., 'TICKET_CREATED', 'INSPECTION_APPROVED', 'STATUS_CHANGE'
  `resource_type` varchar(50) NOT NULL, -- e.g., 'ticket', 'inspection', 'service_approval'
  `resource_id` int(11) NOT NULL,
  `old_value` text DEFAULT NULL,
  `new_value` text DEFAULT NULL,
  `details` text DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
