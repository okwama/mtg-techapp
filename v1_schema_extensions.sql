-- Database Schema Extensions for MotorGAs Workshop System
-- Aligned with BRD requirements

-- 1. Tickets Table (Task Assignment)
CREATE TABLE IF NOT EXISTS `tickets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ticket_number` varchar(50) UNIQUE NOT NULL,
  `technician_id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `priority` enum('low','medium','high','urgent') DEFAULT 'medium',
  `status` enum('open','assigned','in-progress','hold','completed','cancelled') DEFAULT 'assigned',
  `due_date` datetime DEFAULT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`),
  FOREIGN KEY (`vehicle_id`) REFERENCES `Vehicles` (`id`),
  FOREIGN KEY (`created_by`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Inspections Table
CREATE TABLE IF NOT EXISTS `inspections` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inspection_number` varchar(50) UNIQUE NOT NULL,
  `ticket_id` int(11) DEFAULT NULL,
  `technician_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `status` enum('pending','in-progress','completed','approved','rejected') DEFAULT 'pending',
  `checklist_data` json DEFAULT NULL,
  `summary` text,
  `overall_condition` enum('excellent','good','fair','poor') DEFAULT NULL,
  `admin_comments` text,
  `inspection_date` datetime DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`),
  FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  FOREIGN KEY (`vehicle_id`) REFERENCES `Vehicles` (`id`),
  FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Inspection Photos Table
CREATE TABLE IF NOT EXISTS `inspection_photos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inspection_id` int(11) NOT NULL,
  `photo_url` varchar(500) NOT NULL,
  `photo_type` enum('exterior','interior','engine','damage','odometer') NOT NULL,
  `caption` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`inspection_id`) REFERENCES `inspections` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Service Approvals Table
CREATE TABLE IF NOT EXISTS `service_approvals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inspection_id` int(11) NOT NULL,
  `technician_id` int(11) NOT NULL,
  `admin_id` int(11) DEFAULT NULL,
  `service_description` text NOT NULL,
  `estimated_cost` decimal(12,2) DEFAULT '0.00',
  `labor_hours` decimal(5,2) DEFAULT '0.00',
  `parts_needed` json DEFAULT NULL,
  `status` enum('pending','approved','declined') DEFAULT 'pending',
  `admin_comments` text,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`inspection_id`) REFERENCES `inspections` (`id`),
  FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  FOREIGN KEY (`admin_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Parts Requests Table
CREATE TABLE IF NOT EXISTS `parts_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `technician_id` int(11) NOT NULL,
  `approver_id` int(11) DEFAULT NULL,
  `part_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT '1',
  `station_id` int(11) NOT NULL,
  `inspection_id` int(11) DEFAULT NULL,
  `reason` text,
  `status` enum('pending','approved','declined','fulfilled') DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  FOREIGN KEY (`approver_id`) REFERENCES `staff` (`id`),
  FOREIGN KEY (`part_id`) REFERENCES `Parts` (`id`),
  FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`),
  FOREIGN KEY (`inspection_id`) REFERENCES `inspections` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
