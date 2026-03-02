-- Migration: Add conversion_id to inspections table
-- Date: 2026-02-11

ALTER TABLE `inspections` 
ADD COLUMN `conversion_id` int(11) DEFAULT NULL AFTER `ticket_id`,
ADD CONSTRAINT `fk_inspection_conversion` FOREIGN KEY (`conversion_id`) REFERENCES `conversions`(`id`) ON DELETE SET NULL;
