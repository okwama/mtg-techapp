-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Feb 12, 2026 at 10:12 AM
-- Server version: 10.6.24-MariaDB-cll-lve
-- PHP Version: 8.4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `impulsep_motorgas`
--

-- --------------------------------------------------------

--
-- Table structure for table `Category`
--

CREATE TABLE `Category` (
  `id` int(11) NOT NULL,
  `name` varchar(191) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Category`
--

INSERT INTO `Category` (`id`, `name`, `description`) VALUES
(1, 'newest', 'n');

-- --------------------------------------------------------

--
-- Table structure for table `chat_messages`
--

CREATE TABLE `chat_messages` (
  `id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `isRead` tinyint(1) DEFAULT 0,
  `readAt` timestamp NULL DEFAULT NULL,
  `message` text NOT NULL,
  `messageType` varchar(50) DEFAULT 'text',
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `chat_messages`
--

INSERT INTO `chat_messages` (`id`, `room_id`, `sender_id`, `isRead`, `readAt`, `message`, `messageType`, `sent_at`) VALUES
(150, 50, 9, 0, NULL, 'mor', 'text', '2026-01-16 16:58:23');

-- --------------------------------------------------------

--
-- Table structure for table `chat_rooms`
--

CREATE TABLE `chat_rooms` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `is_group` tinyint(1) DEFAULT 0,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `chat_rooms`
--

INSERT INTO `chat_rooms` (`id`, `name`, `description`, `is_group`, `created_by`, `created_at`) VALUES
(50, 'n', NULL, 0, 9, '2026-01-16 15:58:13');

-- --------------------------------------------------------

--
-- Table structure for table `chat_room_members`
--

CREATE TABLE `chat_room_members` (
  `id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `staff_id` int(11) NOT NULL,
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `chat_room_members`
--

INSERT INTO `chat_room_members` (`id`, `room_id`, `staff_id`, `joined_at`) VALUES
(124, 50, 9, '2026-01-16 15:58:13'),
(125, 50, 23, '2026-01-16 15:58:13');

-- --------------------------------------------------------

--
-- Table structure for table `checkin_records`
--

CREATE TABLE `checkin_records` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `station_id` int(11) NOT NULL,
  `station_name` varchar(255) DEFAULT NULL,
  `check_in_latitude` decimal(10,8) DEFAULT NULL,
  `check_in_longitude` decimal(11,8) DEFAULT NULL,
  `check_out_latitude` decimal(10,8) DEFAULT NULL,
  `check_out_longitude` decimal(11,8) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0=None, 1=Checked In, 2=Checked Out',
  `time_in` datetime DEFAULT NULL,
  `time_out` datetime DEFAULT NULL,
  `qr_data` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `opening_approved` tinyint(4) DEFAULT 0,
  `closing_approved` tinyint(4) DEFAULT 0,
  `variance_amount` decimal(12,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `checkin_records`
--

INSERT INTO `checkin_records` (`id`, `user_id`, `user_name`, `station_id`, `station_name`, `check_in_latitude`, `check_in_longitude`, `check_out_latitude`, `check_out_longitude`, `address`, `status`, `time_in`, `time_out`, `qr_data`, `created_at`, `updated_at`, `opening_approved`, `closing_approved`, `variance_amount`) VALUES
(2, 23, 'Benjamin Okwama', 1, 'Kisumu Station', -1.26468361, 36.80777209, NULL, NULL, 'Mogotio Road, Nairobi, Nairobi', 1, '2026-01-17 10:59:17', NULL, '{\"station_id\": 1}', '2026-01-17 07:59:17', '2026-01-22 12:40:51', 0, 0, 0.00),
(3, 23, 'Benjamin Okwama', 1, 'Kisumu Station 1', -1.23967377, 36.89389860, NULL, NULL, 'Nairobi, Nairobi, Nairobi', 1, '2026-01-21 14:29:38', NULL, '{station_id=1}', '2026-01-21 11:29:38', '2026-01-21 11:29:38', 0, 0, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `conversions`
--

CREATE TABLE `conversions` (
  `id` int(11) NOT NULL,
  `owner_full_name` varchar(255) NOT NULL,
  `national_id` varchar(50) DEFAULT NULL,
  `passport_id` varchar(50) DEFAULT NULL,
  `contact` varchar(50) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `vehicle_registration` varchar(50) NOT NULL,
  `make` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `year_of_manufacture` int(4) DEFAULT NULL,
  `engine_capacity` int(11) DEFAULT NULL,
  `vin_chassis_number` varchar(100) DEFAULT NULL,
  `current_fuel_type` enum('petrol','diesel','hybrid') NOT NULL DEFAULT 'petrol',
  `logbook_number` varchar(100) DEFAULT NULL,
  `scheduled_date` datetime DEFAULT NULL,
  `status` enum('pending','approved','declined','completed') NOT NULL DEFAULT 'pending',
  `comment` text DEFAULT NULL,
  `conversion_description` text DEFAULT NULL,
  `conversion_date` date DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `conversions`
--

INSERT INTO `conversions` (`id`, `owner_full_name`, `national_id`, `passport_id`, `contact`, `email`, `vehicle_registration`, `make`, `model`, `year_of_manufacture`, `engine_capacity`, `vin_chassis_number`, `current_fuel_type`, `logbook_number`, `scheduled_date`, `status`, `comment`, `conversion_description`, `conversion_date`, `created_by`, `created_at`, `updated_at`) VALUES
(1, 'new owner', '44', '33', '0790193625', 'bryanotieno09@gmail.com', 'KDG 111A', 'Toyota', 'Camry', 2024, 2000, '21334', 'petrol', 'asss1234', '2025-12-08 10:33:00', 'pending', 'approvedmm', NULL, NULL, NULL, '2025-12-07 10:18:24', '2026-02-11 19:53:59'),
(2, 'Jane Smith', '12345678', '35456789', '0712345678', 'bryanotieno09@gmail.com', 'KDA 222B', 'Toyota', 'Vits', 2017, 1300, '1111', 'petrol', 'ass1223', '2025-12-10 15:50:00', 'completed', '', NULL, NULL, NULL, '2025-12-07 12:39:26', '2026-01-15 08:07:33'),
(3, 'Jane Smith', '12345678', '35456789', '0790193625', 'bryanotieno09@gmail.com', 'KDG 111A', 'Toyota', 'Camry', 2024, 2000, '12334', 'petrol', 'asss1234', '2026-01-16 10:00:00', 'completed', 's', 'nnn', '2026-01-16', NULL, '2026-01-14 10:40:46', '2026-01-16 13:28:02'),
(4, 'Jane Smith', '455', '5678', '254702975604', 'bryanotieno09@gmail.com', 'KDG 111A', 'Toyota', 'Camry', 2020, 2000, 'df44', 'petrol', 'dee4', '2026-01-16 16:35:00', 'completed', 'c', 's', '2026-01-16', NULL, '2026-01-15 09:53:16', '2026-01-16 13:34:01'),
(5, 'Jane Smith', '12345678', '3', '+254702476974', 'bryanotieno09@gmail.com', 'KDV 222B', 'Nissan', 'nn', 2008, 1800, 'e', 'petrol', '3', NULL, 'completed', 'd', 's', '2026-01-16', NULL, '2026-01-16 13:32:04', '2026-01-16 13:32:20');

-- --------------------------------------------------------

--
-- Table structure for table `Country`
--

CREATE TABLE `Country` (
  `id` int(11) NOT NULL,
  `name` varchar(191) NOT NULL,
  `status` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Country`
--

INSERT INTO `Country` (`id`, `name`, `status`) VALUES
(1, 'Kenya', 1),
(2, 'Tanzania', 1),
(3, 'Comoros', 1),
(4, 'France', 1);

-- --------------------------------------------------------

--
-- Table structure for table `delete_acc`
--

CREATE TABLE `delete_acc` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` int(11) DEFAULT 0 COMMENT '0 = pending, 1 = cancelled, 2 = completed',
  `is_true` int(11) DEFAULT 0 COMMENT '0 = not deleted yet, 1 = deleted',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- --------------------------------------------------------

--
-- Table structure for table `departments`
--

CREATE TABLE `departments` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `departments`
--

INSERT INTO `departments` (`id`, `name`) VALUES
(1, 'Adminstrator'),
(2, 'Finance'),
(3, 'Business Development'),
(4, 'GM'),
(5, 'Executive');

-- --------------------------------------------------------

--
-- Table structure for table `FuelPrices`
--

CREATE TABLE `FuelPrices` (
  `id` int(11) NOT NULL,
  `stationId` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `fuelType` varchar(50) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `FuelPrices`
--

INSERT INTO `FuelPrices` (`id`, `stationId`, `price`, `fuelType`, `notes`, `created_at`) VALUES
(1, 1, 500.00, 'Regular', NULL, '2025-12-06 17:21:21'),
(2, 1, 99.00, NULL, NULL, '2025-12-06 17:24:09'),
(3, 1, 102.00, NULL, NULL, '2025-12-06 17:31:22'),
(4, 2, 99.00, NULL, NULL, '2026-01-15 07:53:12');

-- --------------------------------------------------------

--
-- Table structure for table `inspections`
--

CREATE TABLE `inspections` (
  `id` int(11) NOT NULL,
  `inspection_number` varchar(50) NOT NULL,
  `ticket_id` int(11) DEFAULT NULL,
  `conversion_id` int(11) DEFAULT NULL,
  `technician_id` int(11) NOT NULL,
  `vehicle_id` int(11) DEFAULT NULL,
  `station_id` int(11) NOT NULL,
  `status` enum('pending','in-progress','completed','approved','rejected') DEFAULT 'pending',
  `checklist_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`checklist_data`)),
  `summary` text DEFAULT NULL,
  `overall_condition` enum('excellent','good','fair','poor') DEFAULT NULL,
  `admin_comments` text DEFAULT NULL,
  `inspection_date` datetime DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `inspections`
--

INSERT INTO `inspections` (`id`, `inspection_number`, `ticket_id`, `conversion_id`, `technician_id`, `vehicle_id`, `station_id`, `status`, `checklist_data`, `summary`, `overall_condition`, `admin_comments`, `inspection_date`, `created_at`, `updated_at`) VALUES
(1, 'INS-20260211-2890', NULL, 1, 23, NULL, 1, 'completed', '[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}]', 'nothing ', 'good', NULL, '2026-02-11 22:32:27', '2026-02-11 20:32:27', '2026-02-12 07:57:48');

-- --------------------------------------------------------

--
-- Table structure for table `inspection_photos`
--

CREATE TABLE `inspection_photos` (
  `id` int(11) NOT NULL,
  `inspection_id` int(11) NOT NULL,
  `photo_url` varchar(500) NOT NULL,
  `photo_type` enum('exterior','interior','engine','damage','odometer') NOT NULL,
  `caption` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `InventoryLedger`
--

CREATE TABLE `InventoryLedger` (
  `id` int(11) NOT NULL,
  `stationId` int(11) NOT NULL,
  `transactionType` enum('IN','OUT','ADJUSTMENT') NOT NULL,
  `quantityIn` decimal(10,2) DEFAULT 0.00,
  `quantityOut` decimal(10,2) DEFAULT 0.00,
  `balance` decimal(10,2) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `previousQuantity` decimal(10,2) NOT NULL DEFAULT 0.00,
  `newQuantity` decimal(10,2) NOT NULL,
  `referenceNumber` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `InventoryLedger`
--

INSERT INTO `InventoryLedger` (`id`, `stationId`, `transactionType`, `quantityIn`, `quantityOut`, `balance`, `quantity`, `previousQuantity`, `newQuantity`, `referenceNumber`, `notes`, `createdBy`, `created_at`) VALUES
(1, 1, 'IN', 3.00, 0.00, 3.00, 3.00, 0.00, 3.00, 'A001', 'test receive', NULL, '2025-12-07 07:40:53'),
(2, 1, 'IN', 300.00, 0.00, 303.00, 300.00, 3.00, 303.00, 'A002', 'test', 22, '2025-12-07 07:45:44'),
(3, 1, 'OUT', 0.00, 0.00, 0.00, 21.00, 303.00, 282.00, 'SALE-KA-1-V-1', 'Sale to Key Account: Client 1, Vehicle: KDD 112D - test', 22, '2025-12-07 08:15:48'),
(4, 1, 'OUT', 0.00, 0.00, 0.00, 2.00, 282.00, 280.00, 'SALE-KA-1-V-1', 'Sale to Key Account: Client 1, Vehicle: KDD 112D - ytt', 23, '2025-12-07 08:25:15'),
(5, 1, 'OUT', 0.00, 0.00, 0.00, 1.00, 280.00, 279.00, 'SALE-KA-1-V-1', 'Sale to Key Account: Client 1, Vehicle: KDD 112D - test', 23, '2025-12-07 08:28:15'),
(6, 1, 'OUT', 0.00, 1.00, 278.00, 1.00, 279.00, 278.00, 'SALE-KA-1-V-1', 'Sale to Key Account: Client 1, Vehicle: KDD 112D - tt', 23, '2025-12-07 08:39:30'),
(7, 1, 'OUT', 0.00, 1.00, 277.00, 1.00, 278.00, 277.00, 'SALE-KA-1-V-1', 'Sale to Key Account: Client 1, Vehicle: KDD 112D - v', 22, '2025-12-07 08:41:37'),
(8, 1, 'OUT', 0.00, 1.00, 276.00, 1.00, 277.00, 276.00, 'SALE-KA-1-V-1', 'Sale to Key Account: Client 1, Vehicle: KDD 112D - gg', 22, '2025-12-07 08:44:35'),
(9, 1, 'OUT', 0.00, 3.00, 273.00, 3.00, 276.00, 273.00, 'SALE-KA-1-V-1', 'Sale to Key Account: Client 1, Vehicle: KDD 112D - testing', 22, '2025-12-07 08:56:46'),
(10, 1, 'OUT', 0.00, 4.00, 269.00, 4.00, 273.00, 269.00, 'SALE-REGULAR', 'Regular Sale - test', NULL, '2026-01-14 10:45:28'),
(11, 1, 'OUT', 0.00, 5.00, 264.00, 5.00, 269.00, 264.00, 'SALE-KA-2-V-2', 'Sale to Key Account: BRYAN OTIENO ONYANGO, Vehicle: KDD 333A', NULL, '2026-01-15 06:38:01'),
(12, 1, 'OUT', 0.00, 5.00, 259.00, 5.00, 264.00, 259.00, 'SALE-KA-2-V-2', 'Sale to Key Account: BRYAN OTIENO ONYANGO, Vehicle: KDD 333A', 22, '2026-01-15 06:39:45'),
(13, 1, 'OUT', 0.00, 6.00, 253.00, 6.00, 259.00, 253.00, 'SALE-KA-2-V-2', 'Sale to Key Account: BRYAN OTIENO ONYANGO, Vehicle: KDD 333A - n', 22, '2026-01-15 07:22:17'),
(14, 1, 'OUT', 0.00, 3.00, 250.00, 3.00, 253.00, 250.00, 'SALE-KA-2-V-2', 'Sale to Key Account: BRYAN OTIENO ONYANGO, Vehicle: KDD 333A', NULL, '2026-01-15 07:26:09'),
(15, 1, 'OUT', 0.00, 40.00, 210.00, 40.00, 250.00, 210.00, 'SALE-KA-2-V-2', 'Sale to Key Account: BRYAN OTIENO ONYANGO, Vehicle: KDD 333A - test', 22, '2026-01-15 07:40:42'),
(16, 2, 'IN', 50.00, 0.00, 50.00, 50.00, 0.00, 50.00, 'er', NULL, 23, '2026-01-15 07:56:17'),
(17, 2, 'OUT', 0.00, 7.00, 43.00, 7.00, 50.00, 43.00, 'SALE-KA-3-V-3', 'Sale to Key Account: BENJAMIN OKWAMA, Vehicle: KDD 112D', 21, '2026-01-15 07:56:35'),
(18, 1, 'OUT', 0.00, 30.00, 180.00, 30.00, 210.00, 180.00, 'SALE-REGULAR', 'Regular Sale - e', 22, '2026-01-16 14:08:20'),
(19, 1, 'OUT', 0.00, 1.00, 179.00, 1.00, 180.00, 179.00, 'SALE-REGULAR', 'Regular Sale', 22, '2026-01-16 14:18:51'),
(20, 1, 'OUT', 0.00, 2.00, 177.00, 2.00, 179.00, 177.00, 'SALE-REGULAR', 'Regular Sale', 22, '2026-01-16 14:23:33'),
(21, 1, 'OUT', 0.00, 4.00, 173.00, 4.00, 177.00, 173.00, 'SALE-KA-2-V-2', 'Sale to Key Account: BRYAN OTIENO ONYANGO, Vehicle: KDD 333A - d', 22, '2026-01-16 16:19:40'),
(22, 1, 'OUT', 0.00, 5.00, 168.00, 5.00, 173.00, 168.00, 'SALE-KA-2-V-2', 'Sale to Key Account: BRYAN OTIENO ONYANGO, Vehicle: KDD 333A - test', 22, '2026-01-16 16:23:03');

-- --------------------------------------------------------

--
-- Table structure for table `inventory_movements`
--

CREATE TABLE `inventory_movements` (
  `id` int(11) NOT NULL,
  `movement_type` enum('request','transfer','purchase','adjustment','return') NOT NULL,
  `source_station_id` int(11) DEFAULT NULL COMMENT 'NULL if from supplier',
  `destination_station_id` int(11) DEFAULT NULL COMMENT 'NULL if to customer/adjustment',
  `product_id` int(11) NOT NULL,
  `quantity` decimal(15,2) NOT NULL,
  `unit_price` decimal(15,2) DEFAULT NULL,
  `status` enum('pending','approved','dispatched','received','cancelled','completed') NOT NULL DEFAULT 'pending',
  `reference_number` varchar(100) DEFAULT NULL,
  `supplier_name` varchar(255) DEFAULT NULL,
  `vehicle_registration` varchar(50) DEFAULT NULL,
  `driver_name` varchar(255) DEFAULT NULL,
  `driver_contact` varchar(100) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `requested_by` int(11) DEFAULT NULL,
  `processed_by` int(11) DEFAULT NULL COMMENT 'Manager who approved/received',
  `movement_date` datetime DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `KeyAccountFuelPrices`
--

CREATE TABLE `KeyAccountFuelPrices` (
  `id` int(11) NOT NULL,
  `keyAccountId` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `notes` text DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `KeyAccountFuelPrices`
--

INSERT INTO `KeyAccountFuelPrices` (`id`, `keyAccountId`, `price`, `notes`, `updatedBy`, `created_at`) VALUES
(1, 3, 99.00, 'Updated from 100.00 to 99', NULL, '2026-01-22 12:35:39'),
(2, 1, 101.00, 'Updated from 99.00 to 101', NULL, '2026-01-22 12:40:00'),
(3, 2, 99.00, 'Updated from 0.00 to 99', 20, '2026-01-22 12:40:54');

-- --------------------------------------------------------

--
-- Table structure for table `KeyAccounts`
--

CREATE TABLE `KeyAccounts` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `contact` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `account_number` varchar(100) NOT NULL,
  `fuel_price` decimal(11,2) DEFAULT NULL,
  `type` enum('client','key_account') NOT NULL DEFAULT 'key_account',
  `description` text DEFAULT NULL,
  `region` varchar(191) DEFAULT NULL,
  `is_active` int(3) NOT NULL DEFAULT 1,
  `loyalty_points` decimal(15,2) NOT NULL DEFAULT 0.00,
  `balance` decimal(15,2) NOT NULL DEFAULT 0.00,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `client_type` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `KeyAccounts`
--

INSERT INTO `KeyAccounts` (`id`, `name`, `email`, `contact`, `password`, `account_number`, `fuel_price`, `type`, `description`, `region`, `is_active`, `loyalty_points`, `balance`, `created_at`, `updated_at`, `client_type`) VALUES
(1, 'Client 1', 'bryanotieno09@gmail.com', '0790193625', '', 'ASL001', 0.00, 'key_account', 'testing', NULL, 1, 0.00, 408.00, '2025-12-06 19:44:18', '2026-02-11 12:05:32', 'Drive-in'),
(2, 'BRYAN OTIENO ONYANGO', 'bryanotieno09@gmail.com', '+254702476974', '', 'CLT-1765642651619-0776', 99.00, 'client', 'Nairobi\nBranton Court', 'Nairobi', 1, 320.00, 12431.00, '2025-12-13 16:17:29', '2026-02-11 12:58:21', 'FSA'),
(3, 'BENJAMIN OKWAMA', 'bryanotieno09@gmail.com', '07999', '', 'CLT-1765645110412-6332', 101.00, 'client', 'bb', 'Nairobi', 1, 70.00, 693.00, '2025-12-13 16:58:28', '2026-02-11 12:01:52', 'FSA'),
(4, 'Test Client', 'bryanotieno09@gmail.com', '0790193625', '', 'AA111', 0.00, 'key_account', 'Test', NULL, 1, 0.00, 0.00, '2026-01-17 08:26:30', '2026-02-11 12:05:41', 'Drive-in'),
(7, 'nnddd', 's@gmail.com', 's2', '', '233', NULL, 'key_account', 's', NULL, 1, 0.00, 0.00, '2026-02-11 12:54:41', '2026-02-11 12:54:41', NULL),
(8, 'newest1', 'bryanotieno09@gmail.com', '254702975604', '', 'n', NULL, 'key_account', 'm', NULL, 1, 0.00, 0.00, '2026-02-11 12:56:22', '2026-02-11 12:56:22', 'FSA');

-- --------------------------------------------------------

--
-- Table structure for table `key_account_ledger`
--

CREATE TABLE `key_account_ledger` (
  `id` int(11) NOT NULL,
  `key_account_id` int(11) NOT NULL,
  `vehicle_id` int(11) DEFAULT NULL,
  `station_id` int(11) NOT NULL,
  `transaction_date` date NOT NULL,
  `transaction_type` enum('SALE','PAYMENT','ADJUSTMENT') NOT NULL,
  `quantity` decimal(10,2) DEFAULT 0.00,
  `unit_price` decimal(10,2) DEFAULT 0.00,
  `total_amount` decimal(15,2) NOT NULL DEFAULT 0.00,
  `debit` decimal(15,2) NOT NULL DEFAULT 0.00,
  `credit` decimal(15,2) NOT NULL DEFAULT 0.00,
  `balance` decimal(15,2) NOT NULL DEFAULT 0.00,
  `reference_number` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `key_account_ledger`
--

INSERT INTO `key_account_ledger` (`id`, `key_account_id`, `vehicle_id`, `station_id`, `transaction_date`, `transaction_type`, `quantity`, `unit_price`, `total_amount`, `debit`, `credit`, `balance`, `reference_number`, `description`, `notes`, `created_by`, `created_at`, `updated_at`) VALUES
(1, 1, NULL, 1, '2025-12-07', 'SALE', 1.00, 102.00, 102.00, 102.00, 0.00, 102.00, 'SALE-KA-1-V-1', 'Sale: 1.00L @ 102.00 per liter', 'gg', 22, '2025-12-07 08:44:36', '2025-12-07 08:44:36'),
(2, 1, NULL, 1, '2025-12-07', 'SALE', 3.00, 102.00, 306.00, 306.00, 0.00, 408.00, 'SALE-KA-1-V-1', 'Sale: 3.00L @ 102.00 per liter', 'testing', 22, '2025-12-07 08:56:48', '2025-12-07 08:56:48'),
(3, 2, 2, 1, '2026-01-15', 'SALE', 5.00, 102.00, 510.00, 510.00, 0.00, 510.00, 'SALE-KA-2-V-2', 'Sale: 5.00L @ 102.00 per liter', NULL, NULL, '2026-01-15 06:38:02', '2026-01-15 06:38:02'),
(4, 2, 2, 1, '2026-01-15', 'SALE', 5.00, 102.00, 510.00, 510.00, 0.00, 1020.00, 'SALE-KA-2-V-2', 'Sale: 5.00L @ 102.00 per liter', NULL, 22, '2026-01-15 06:39:46', '2026-01-15 06:39:46'),
(5, 2, 2, 1, '2026-01-15', 'SALE', 6.00, 102.00, 612.00, 612.00, 0.00, 1632.00, 'SALE-KA-2-V-2', 'Sale: 6.00L @ 102.00 per liter', 'n', 22, '2026-01-15 07:22:18', '2026-01-15 07:22:18'),
(6, 2, 2, 1, '2026-01-15', 'SALE', 3.00, 102.00, 306.00, 306.00, 0.00, 1938.00, 'SALE-KA-2-V-2', 'Sale: 3.00L @ 102.00 per liter', NULL, NULL, '2026-01-15 07:26:10', '2026-01-15 07:26:10'),
(7, 2, NULL, 1, '2026-01-15', 'PAYMENT', 0.00, 0.00, 200.00, 0.00, 200.00, 1738.00, 'test1234', 'Payment - Method: Cash', 'Payment method: Cash', NULL, '2026-01-15 07:34:44', '2026-01-15 07:34:44'),
(8, 2, 2, 1, '2026-01-15', 'SALE', 40.00, 102.00, 4080.00, 4080.00, 0.00, 5818.00, 'SALE-KA-2-V-2', 'Sale: 40.00L @ 102.00 per liter', 'test', 22, '2026-01-15 07:40:44', '2026-01-15 07:40:44'),
(9, 2, NULL, 1, '2026-01-15', 'PAYMENT', 0.00, 0.00, 10.00, 0.00, 10.00, 5808.00, '3w', 'Loyalty points redemption: 100.00 points redeemed for 10.00 KES', 'Points redemption - 100.00 points', NULL, '2026-01-15 07:46:38', '2026-01-15 07:46:38'),
(10, 3, 3, 2, '2026-01-15', 'SALE', 7.00, 99.00, 693.00, 693.00, 0.00, 693.00, 'SALE-KA-3-V-3', 'Sale: 7.00L @ 99.00 per liter', NULL, 21, '2026-01-15 07:56:37', '2026-01-15 07:56:37'),
(11, 2, 2, 1, '2026-01-16', 'SALE', 4.00, 102.00, 408.00, 408.00, 0.00, 6216.00, 'SALE-KA-2-V-2', 'Sale: 4.00L @ 102.00 per liter', 'd', 22, '2026-01-16 16:19:42', '2026-01-16 16:19:42'),
(12, 2, 2, 1, '2026-01-16', 'SALE', 5.00, 102.00, 510.00, 510.00, 0.00, 6726.00, 'SALE-KA-2-V-2', 'Sale: 5.00L @ 102.00 per liter', 'test', 22, '2026-01-16 16:23:05', '2026-01-16 16:23:05'),
(13, 2, 2, 1, '2026-01-16', 'PAYMENT', 0.00, 0.00, 510.00, 0.00, 510.00, 6216.00, 'SALE-KA-2-V-2-PAYMENT', 'Payment via Cash for sale: 5.00L @ 102.00 per liter', 'Payment method: Cash - test', 22, '2026-01-16 16:23:05', '2026-01-16 16:23:05'),
(14, 2, 2, 1, '2026-02-07', 'SALE', 155.00, 25.00, 3875.00, 3875.00, 0.00, 10091.00, 'SALE-ST-1-KA-2-V-000022', 'Fuel sale', NULL, 24, '2026-02-07 17:24:30', '2026-02-07 17:24:30'),
(15, 2, 2, 1, '2026-02-07', 'SALE', 12.60, 100.00, 1260.00, 1260.00, 0.00, 11351.00, 'SALE-ST-1-KA-2-V-000023', 'Fuel sale', NULL, 24, '2026-02-07 17:27:27', '2026-02-07 17:27:27'),
(16, 2, 2, 1, '2026-02-07', 'PAYMENT', 0.00, 0.00, 1260.00, 0.00, 1260.00, 10091.00, 'SALE-ST-1-KA-2-V-000023', 'Payment received (M-PESA)', NULL, 24, '2026-02-07 17:27:27', '2026-02-07 17:27:27'),
(17, 2, 2, 1, '2026-02-07', 'SALE', 15.00, 99.00, 1485.00, 1485.00, 0.00, 11576.00, 'SALE-ST-1-KA-2-V-000024', 'Fuel sale', NULL, 24, '2026-02-07 17:30:20', '2026-02-07 17:30:20'),
(18, 2, 2, 1, '2026-02-07', 'PAYMENT', 0.00, 0.00, 1485.00, 0.00, 1485.00, 10091.00, 'SALE-ST-1-KA-2-V-000024', 'Payment received (M-PESA)', NULL, 24, '2026-02-07 17:30:20', '2026-02-07 17:30:20'),
(19, 2, 2, 1, '2026-02-07', 'SALE', 11.70, 200.00, 2340.00, 2340.00, 0.00, 12431.00, 'SALE-ST-1-KA-2-V-000025', 'Fuel sale', NULL, 24, '2026-02-07 17:35:49', '2026-02-07 17:35:49'),
(20, 2, 2, 1, '2026-02-09', 'SALE', 1.00, 225.00, 225.00, 225.00, 0.00, 12656.00, 'SALE-ST-1-KA-2-V-000026', 'Fuel sale', NULL, 24, '2026-02-09 10:07:05', '2026-02-09 10:07:05'),
(21, 2, 2, 1, '2026-02-09', 'PAYMENT', 0.00, 0.00, 225.00, 0.00, 225.00, 12431.00, 'SALE-ST-1-KA-2-V-000026', 'Payment received (M-PESA)', NULL, 24, '2026-02-09 10:07:05', '2026-02-09 10:07:05');

-- --------------------------------------------------------

--
-- Table structure for table `leave_types`
--

CREATE TABLE `leave_types` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `default_days` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `leave_types`
--

INSERT INTO `leave_types` (`id`, `name`, `description`, `default_days`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'Annual Leave', 'Annual vacation leave', 21, 1, '2026-01-16 17:20:48', '2026-01-16 17:20:48'),
(3, 'Casual Leave', 'Casual leave for personal matters', 5, 1, '2026-01-16 17:20:48', '2026-01-16 17:20:48'),
(5, 'Paternity Leave', 'Paternity leave for new fathers', 14, 1, '2026-01-16 17:20:48', '2026-01-16 17:20:48'),
(6, 'Emergency Leave', 'Emergency leave for urgent matters', 3, 1, '2026-01-16 17:20:48', '2026-01-16 17:20:48'),
(8, 'Sick Leave', 'Medical leave for illness', 7, 1, '2026-01-16 17:21:16', '2026-01-16 17:21:16'),
(10, 'Maternity Leave', 'Maternity leave for expecting mothers', 90, 1, '2026-01-16 17:21:16', '2026-01-16 17:21:16'),
(11, 'Paternity Leave', 'Paternity leave for new fathers', 14, 1, '2026-01-16 17:21:16', '2026-01-16 17:21:16');

-- --------------------------------------------------------

--
-- Table structure for table `LoginHistory`
--

CREATE TABLE `LoginHistory` (
  `id` int(11) NOT NULL,
  `userId` int(11) DEFAULT NULL,
  `timezone` varchar(191) DEFAULT 'Africa/Nairobi',
  `duration` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT 0,
  `sessionEnd` varchar(191) DEFAULT NULL,
  `sessionStart` varchar(191) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `LoginHistory`
--

INSERT INTO `LoginHistory` (`id`, `userId`, `timezone`, `duration`, `status`, `sessionEnd`, `sessionStart`) VALUES
(3486, 22, 'Africa/Nairobi', NULL, 1, '2026-01-13 14:07:38', '2026-01-13 07:07:38');

-- --------------------------------------------------------

--
-- Table structure for table `loyalty_points_ledger`
--

CREATE TABLE `loyalty_points_ledger` (
  `id` int(11) NOT NULL,
  `key_account_id` int(11) NOT NULL,
  `sale_id` int(11) DEFAULT NULL,
  `transaction_date` datetime NOT NULL,
  `litres` decimal(10,2) NOT NULL DEFAULT 0.00,
  `points_rate` decimal(10,2) NOT NULL DEFAULT 10.00,
  `points_awarded` decimal(15,2) NOT NULL DEFAULT 0.00,
  `balance_after` decimal(15,2) NOT NULL DEFAULT 0.00,
  `reference_number` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `loyalty_points_ledger`
--

INSERT INTO `loyalty_points_ledger` (`id`, `key_account_id`, `sale_id`, `transaction_date`, `litres`, `points_rate`, `points_awarded`, `balance_after`, `reference_number`, `description`, `created_by`, `created_at`) VALUES
(1, 2, 6, '2026-01-15 10:26:09', 3.00, 10.00, 30.00, 30.00, 'SALE-KA-2-V-2', 'Loyalty points: +30.00 (3.00L × 10)', NULL, '2026-01-15 07:26:10'),
(2, 2, 7, '2026-01-15 10:40:43', 40.00, 10.00, 400.00, 430.00, 'SALE-KA-2-V-2', 'Loyalty points: +400.00 (40.00L × 10)', 22, '2026-01-15 07:40:43'),
(3, 2, NULL, '2026-01-15 10:42:19', 0.00, 0.00, -100.00, 330.00, 'A001', 'Points redemption: -100.00 points (Value: 10.00 KES)', NULL, '2026-01-15 07:42:19'),
(4, 2, NULL, '2026-01-15 10:46:37', 0.00, 0.00, -100.00, 230.00, '3w', 'Points redemption: -100.00 points (Value: 10.00 KES)', NULL, '2026-01-15 07:46:37'),
(5, 3, 8, '2026-01-15 10:56:36', 7.00, 10.00, 70.00, 70.00, 'SALE-KA-3-V-3', 'Loyalty points: +70.00 (7.00L × 10)', 21, '2026-01-15 07:56:36'),
(6, 2, 11, '2026-01-16 19:19:41', 4.00, 10.00, 40.00, 270.00, 'SALE-KA-2-V-2', 'Loyalty points: +40.00 (4.00L × 10)', 22, '2026-01-16 16:19:41'),
(7, 2, 12, '2026-01-16 19:23:05', 5.00, 10.00, 50.00, 320.00, 'SALE-KA-2-V-2', 'Loyalty points: +50.00 (5.00L × 10)', 22, '2026-01-16 16:23:04');

-- --------------------------------------------------------

--
-- Table structure for table `notices`
--

CREATE TABLE `notices` (
  `id` int(11) NOT NULL,
  `title` text NOT NULL,
  `content` text NOT NULL,
  `country_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` tinyint(3) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `notices`
--

INSERT INTO `notices` (`id`, `title`, `content`, `country_id`, `created_at`, `status`) VALUES
(13, 'Prices Drop', 'Price drop for all station to Ksh.99', 1, '2025-11-18 08:53:03', 1);

-- --------------------------------------------------------

--
-- Table structure for table `Parts`
--

CREATE TABLE `Parts` (
  `id` int(11) NOT NULL,
  `part_number` varchar(100) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `manufacturer` varchar(255) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `stock_quantity` int(11) DEFAULT 0,
  `min_stock_level` int(11) DEFAULT 0,
  `location` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Parts`
--

INSERT INTO `Parts` (`id`, `part_number`, `name`, `description`, `category`, `manufacturer`, `unit_price`, `stock_quantity`, `min_stock_level`, `location`, `created_at`, `updated_at`) VALUES
(1, 'c', 'newest2', NULL, 'newest', NULL, NULL, 0, 0, NULL, '2025-12-13 19:36:59', '2025-12-13 19:36:59'),
(2, '0000011AA', 'Part 1', '', 'newest', '', NULL, 0, 30, NULL, '2025-12-14 08:06:47', '2025-12-14 08:11:51');

-- --------------------------------------------------------

--
-- Table structure for table `parts_inventory`
--

CREATE TABLE `parts_inventory` (
  `id` int(11) NOT NULL,
  `store_id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `min_stock_level` int(11) DEFAULT 0,
  `location` varchar(255) DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `parts_inventory`
--

INSERT INTO `parts_inventory` (`id`, `store_id`, `part_id`, `quantity`, `min_stock_level`, `location`, `last_updated`) VALUES
(3, 2, 2, 20, 0, NULL, '2025-12-14 14:35:21'),
(4, 1, 2, 0, 0, NULL, '2025-12-14 12:44:27');

-- --------------------------------------------------------

--
-- Table structure for table `parts_inventory_ledger`
--

CREATE TABLE `parts_inventory_ledger` (
  `id` int(11) NOT NULL,
  `inventory_id` int(11) NOT NULL,
  `store_id` int(11) NOT NULL,
  `part_id` int(11) NOT NULL,
  `transaction_type` enum('IN','OUT','ADJUSTMENT','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
  `quantity` int(11) NOT NULL,
  `previous_quantity` int(11) NOT NULL,
  `new_quantity` int(11) NOT NULL,
  `reference_number` varchar(100) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `parts_requests`
--

CREATE TABLE `parts_requests` (
  `id` int(11) NOT NULL,
  `technician_id` int(11) NOT NULL,
  `approver_id` int(11) DEFAULT NULL,
  `part_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `station_id` int(11) NOT NULL,
  `inspection_id` int(11) DEFAULT NULL,
  `reason` text DEFAULT NULL,
  `status` enum('pending','approved','declined','fulfilled') DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_resets`
--

CREATE TABLE `password_resets` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL,
  `expires_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment_transactions`
--

CREATE TABLE `payment_transactions` (
  `id` int(11) NOT NULL,
  `sale_id` int(11) DEFAULT NULL COMMENT 'NULL for standalone payments (e.g., account top-ups)',
  `key_account_id` int(11) DEFAULT NULL COMMENT 'For credit account payments',
  `station_id` int(11) NOT NULL,
  `payment_method` enum('cash','mobile_money','card','credit','bank_transfer','other') NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `currency` varchar(3) DEFAULT 'KES',
  `external_payment_ref` varchar(50) DEFAULT NULL COMMENT 'M-Pesa Code, Card Auth, Bank Ref',
  `payment_account` varchar(50) DEFAULT NULL COMMENT 'Phone, Card Last 4, Account No.',
  `status` enum('pending','verified','failed','cancelled','refunded') DEFAULT 'pending',
  `verification_type` varchar(20) DEFAULT 'auto' COMMENT 'auto, manual_entry, stk_push, api',
  `initiated_by` int(11) DEFAULT NULL COMMENT 'Staff who started the payment',
  `verified_by` int(11) DEFAULT NULL COMMENT 'Staff/System who verified',
  `notes` text DEFAULT NULL,
  `provider_metadata` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Provider-specific tracking data (M-Pesa CheckoutRequestID, DPO tokens, etc.)' CHECK (json_valid(`provider_metadata`)),
  `initiated_at` datetime DEFAULT current_timestamp(),
  `verified_at` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `payment_transactions`
--

INSERT INTO `payment_transactions` (`id`, `sale_id`, `key_account_id`, `station_id`, `payment_method`, `amount`, `currency`, `external_payment_ref`, `payment_account`, `status`, `verification_type`, `initiated_by`, `verified_by`, `notes`, `provider_metadata`, `initiated_at`, `verified_at`, `created_at`, `updated_at`) VALUES
(1, 1, NULL, 1, 'cash', 306.00, 'KES', NULL, NULL, '', 'auto', 22, NULL, NULL, NULL, '2026-01-17 16:10:57', '2025-10-15 11:56:48', '2025-12-07 08:56:47', '2026-01-17 13:10:57'),
(2, 2, NULL, 1, 'cash', 408.00, 'KES', NULL, NULL, '', 'auto', NULL, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-14 13:45:29', '2026-01-14 10:45:29', '2026-01-17 13:10:57'),
(3, 3, NULL, 1, 'cash', 510.00, 'KES', NULL, NULL, '', 'auto', NULL, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-15 09:38:02', '2026-01-15 06:38:02', '2026-01-17 13:10:57'),
(4, 6, NULL, 1, 'cash', 306.00, 'KES', NULL, NULL, '', 'auto', NULL, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-15 10:26:09', '2026-01-15 07:26:09', '2026-01-17 13:10:57'),
(5, 7, NULL, 1, 'cash', 4080.00, 'KES', NULL, NULL, '', 'auto', 22, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-15 10:40:43', '2026-01-15 07:40:43', '2026-01-17 13:10:57'),
(6, 8, NULL, 2, 'cash', 693.00, 'KES', NULL, NULL, '', 'auto', 21, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-15 10:56:36', '2026-01-15 07:56:36', '2026-01-17 13:10:57'),
(7, 9, NULL, 1, 'mobile_money', 102.00, 'KES', NULL, NULL, '', 'auto', 22, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-16 17:18:52', '2026-01-16 14:18:52', '2026-01-17 13:10:57'),
(8, 10, NULL, 1, 'card', 204.00, 'KES', NULL, NULL, '', 'auto', 22, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-16 17:23:34', '2026-01-16 14:23:33', '2026-01-17 13:10:57'),
(9, 11, NULL, 1, 'cash', 408.00, 'KES', NULL, NULL, '', 'auto', 22, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-16 19:19:41', '2026-01-16 16:19:41', '2026-01-17 13:10:57'),
(10, 12, NULL, 1, 'cash', 510.00, 'KES', NULL, NULL, '', 'auto', 22, NULL, NULL, NULL, '2026-01-17 16:10:57', '2025-08-01 19:23:05', '2026-01-16 16:23:04', '2026-01-17 13:10:57'),
(11, 13, NULL, 1, 'cash', 2244.00, 'KES', NULL, NULL, '', 'auto', 23, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-16 21:33:38', '2026-01-16 18:33:38', '2026-01-17 13:10:57'),
(12, 14, NULL, 1, 'cash', 2550.00, 'KES', NULL, NULL, '', 'auto', 23, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-16 22:20:07', '2026-01-16 19:20:07', '2026-01-17 13:10:57'),
(13, 15, NULL, 1, 'cash', 1224.00, 'KES', NULL, NULL, '', 'auto', 23, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-16 22:22:16', '2026-01-16 19:22:16', '2026-01-17 13:10:57'),
(14, 16, NULL, 1, 'cash', 26010.00, 'KES', NULL, NULL, '', 'auto', 23, NULL, NULL, NULL, '2026-01-17 16:10:57', '2026-01-17 15:25:46', '2026-01-17 12:25:46', '2026-01-17 13:10:57'),
(16, NULL, NULL, 1, 'mobile_money', 100.00, 'KES', NULL, '254712345678', 'failed', 'stk_push', NULL, NULL, NULL, '{\"provider\": \"mpesa_daraja\", \"checkout_request_id\": \"ws_CO_17012026173333303712345678\", \"merchant_request_id\": \"ca92-40a2-a09c-24dc3dd2ad4423901\", \"response_code\": \"0\", \"response_description\": \"Success. Request accepted for processing\", \"result_code\": \"11\", \"result_description\": \"The DebitParty is in an invalid state.\", \"reconciled_at\": \"2026-01-17 17:37:07\"}', '2026-01-17 17:33:32', NULL, '2026-01-17 14:33:32', '2026-01-17 14:37:07'),
(17, NULL, NULL, 1, 'mobile_money', 100.00, 'KES', NULL, '254712345678', 'failed', 'stk_push', NULL, NULL, NULL, '{\"provider\": \"mpesa_daraja\", \"checkout_request_id\": \"ws_CO_17012026173334447712345678\", \"merchant_request_id\": \"9951-436a-b4a5-04d79601091e3926\", \"response_code\": \"0\", \"response_description\": \"Success. Request accepted for processing\", \"result_code\": \"11\", \"result_description\": \"The DebitParty is in an invalid state.\", \"reconciled_at\": \"2026-01-17 17:37:05\"}', '2026-01-17 17:33:33', NULL, '2026-01-17 14:33:33', '2026-01-17 14:37:05');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` enum('FUEL','PART','LPG') NOT NULL DEFAULT 'FUEL',
  `unit` varchar(50) DEFAULT 'Liter',
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `type`, `unit`, `is_active`, `created_at`) VALUES
(1, 'Petrol (PMS)', 'FUEL', 'Liter', 1, '2026-01-16 20:59:37'),
(2, 'Diesel (AGO)', 'FUEL', 'Liter', 1, '2026-01-16 20:59:37'),
(3, 'LPG Gas', 'LPG', 'Kg', 1, '2026-01-16 20:59:37');

-- --------------------------------------------------------

--
-- Table structure for table `pumps`
--

CREATE TABLE `pumps` (
  `id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `serial_number` varchar(100) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `nozzle_count` int(11) NOT NULL DEFAULT 4,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pumps`
--

INSERT INTO `pumps` (`id`, `station_id`, `serial_number`, `description`, `nozzle_count`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 1, '12qwr45', 'euro', 4, 1, '2026-01-17 12:24:45', '2026-01-17 12:24:45');

-- --------------------------------------------------------

--
-- Table structure for table `pump_readings`
--

CREATE TABLE `pump_readings` (
  `id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `pump_id` int(11) NOT NULL,
  `nozzle_number` int(11) NOT NULL,
  `reading_type` enum('OPENING','CLOSING') NOT NULL,
  `reading_value` decimal(12,3) NOT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `shift_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `approved_by` int(11) DEFAULT NULL,
  `status` enum('pending','approved','rejected') DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pump_readings`
--

INSERT INTO `pump_readings` (`id`, `station_id`, `pump_id`, `nozzle_number`, `reading_type`, `reading_value`, `image_url`, `shift_id`, `user_id`, `approved_by`, `status`, `created_at`) VALUES
(1, 1, 1, 1, 'OPENING', 10.000, 'https://res.cloudinary.com/otienobryan/image/upload/v1768995988/pump_readings/qlfr4qyelsqncg5iy3ll.jpg', 3, 23, NULL, 'pending', '2026-01-21 11:46:29'),
(2, 1, 1, 2, 'OPENING', 100.000, 'https://res.cloudinary.com/otienobryan/image/upload/v1768995990/pump_readings/qs1nxyit3pdc3hvvxx87.jpg', 3, 23, NULL, 'pending', '2026-01-21 11:46:31'),
(3, 1, 1, 3, 'OPENING', 250.000, 'https://res.cloudinary.com/otienobryan/image/upload/v1768995993/pump_readings/lnfjicsxxsqdgqxbikdv.jpg', 3, 23, NULL, 'pending', '2026-01-21 11:46:33'),
(4, 1, 1, 4, 'OPENING', 0.000, 'https://res.cloudinary.com/otienobryan/image/upload/v1768995994/pump_readings/up9hdadwfooqlixiog0g.jpg', 3, 23, NULL, 'pending', '2026-01-21 11:46:35');

-- --------------------------------------------------------

--
-- Table structure for table `Regions`
--

CREATE TABLE `Regions` (
  `id` int(11) NOT NULL,
  `name` varchar(191) NOT NULL,
  `countryId` int(11) NOT NULL,
  `status` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Regions`
--

INSERT INTO `Regions` (`id`, `name`, `countryId`, `status`) VALUES
(1, 'Nairobi', 1, 1),
(2, 'Kisumu', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `client_type` enum('regular','key_account') NOT NULL,
  `key_account_id` int(11) DEFAULT NULL,
  `vehicle_id` int(11) DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `payment_method` enum('CASH','CARD','M-PESA','CREDIT','other') DEFAULT NULL,
  `sale_date` datetime NOT NULL,
  `reference_number` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `payment_status` enum('pending','verified','failed','completed') DEFAULT 'completed',
  `external_payment_ref` varchar(50) DEFAULT NULL,
  `payment_account` varchar(50) DEFAULT NULL,
  `verification_type` varchar(50) DEFAULT 'auto'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`id`, `station_id`, `client_type`, `key_account_id`, `vehicle_id`, `quantity`, `unit_price`, `total_amount`, `payment_method`, `sale_date`, `reference_number`, `notes`, `created_by`, `created_at`, `updated_at`, `payment_status`, `external_payment_ref`, `payment_account`, `verification_type`) VALUES
(1, 1, 'key_account', 1, NULL, 3.00, 102.00, 306.00, NULL, '2025-10-15 11:56:48', 'SALE-KA-1-V-1', 'testing', 22, '2025-12-07 08:56:47', '2026-01-16 17:07:57', 'completed', NULL, NULL, 'auto'),
(2, 1, 'regular', NULL, NULL, 4.00, 102.00, 408.00, NULL, '2026-01-14 13:45:29', 'SALE-REGULAR', 'test', NULL, '2026-01-14 10:45:29', '2026-01-16 13:51:47', 'completed', NULL, NULL, 'auto'),
(3, 1, 'key_account', 2, 2, 5.00, 102.00, 510.00, NULL, '2026-01-15 09:38:02', 'SALE-KA-2-V-2', NULL, NULL, '2026-01-15 06:38:02', '2026-01-15 06:38:02', 'completed', NULL, NULL, 'auto'),
(6, 1, 'key_account', 2, 2, 3.00, 102.00, 306.00, NULL, '2026-01-15 10:26:09', 'SALE-KA-2-V-2', NULL, NULL, '2026-01-15 07:26:09', '2026-01-15 07:26:09', 'completed', NULL, NULL, 'auto'),
(7, 1, 'key_account', 2, 2, 40.00, 102.00, 4080.00, NULL, '2026-01-15 10:40:43', 'SALE-KA-2-V-2', 'test', 22, '2026-01-15 07:40:43', '2026-01-15 07:40:43', 'completed', NULL, NULL, 'auto'),
(8, 2, 'key_account', 3, 3, 7.00, 99.00, 693.00, NULL, '2026-01-15 10:56:36', 'SALE-KA-3-V-3', NULL, 21, '2026-01-15 07:56:36', '2026-01-15 07:56:36', 'completed', NULL, NULL, 'auto'),
(9, 1, 'regular', NULL, NULL, 1.00, 102.00, 102.00, '', '2026-01-16 17:18:52', 'SALE-REGULAR', NULL, 22, '2026-01-16 14:18:52', '2026-01-16 14:18:52', 'completed', NULL, NULL, 'auto'),
(10, 1, 'regular', NULL, NULL, 2.00, 102.00, 204.00, 'CARD', '2026-01-16 17:23:34', 'SALE-REGULAR', NULL, 22, '2026-01-16 14:23:33', '2026-01-16 14:23:33', 'completed', NULL, NULL, 'auto'),
(11, 1, 'key_account', 2, 2, 4.00, 102.00, 408.00, 'CASH', '2026-01-16 19:19:41', 'SALE-KA-2-V-2', 'd', 22, '2026-01-16 16:19:41', '2026-01-16 16:19:41', 'completed', NULL, NULL, 'auto'),
(12, 1, 'key_account', 2, 2, 5.00, 102.00, 510.00, 'CASH', '2025-08-01 19:23:05', 'SALE-KA-2-V-2', 'test', 22, '2026-01-16 16:23:04', '2026-01-16 17:08:17', 'completed', NULL, NULL, 'auto'),
(13, 1, 'key_account', 1, NULL, 22.00, 102.00, 2244.00, NULL, '2026-01-16 21:33:38', 'SALE-20260116-1-1-1768588418', NULL, 23, '2026-01-16 18:33:38', '2026-01-16 18:33:38', 'completed', NULL, NULL, 'auto'),
(14, 1, 'key_account', 2, NULL, 25.00, 102.00, 2550.00, NULL, '2026-01-16 22:20:07', 'SALE-20260116-1-2-1768591207', NULL, 23, '2026-01-16 19:20:07', '2026-01-16 19:20:07', 'completed', NULL, NULL, 'auto'),
(15, 1, 'key_account', 2, NULL, 12.00, 102.00, 1224.00, NULL, '2026-01-16 22:22:16', 'SALE-20260116-1-2-1768591336', NULL, 23, '2026-01-16 19:22:16', '2026-01-16 19:22:16', 'completed', NULL, NULL, 'auto'),
(16, 1, 'key_account', 4, NULL, 255.00, 102.00, 26010.00, NULL, '2026-01-17 15:25:46', 'SALE-20260117-1-4-1768652746', NULL, 23, '2026-01-17 12:25:46', '2026-01-17 12:25:46', 'completed', NULL, NULL, 'auto'),
(17, 1, 'key_account', 3, 3, 1.00, 2.00, 2.00, NULL, '2026-02-05 05:25:16', 'ref', NULL, 24, '2026-02-05 03:25:16', '2026-02-05 03:25:16', 'completed', NULL, NULL, 'auto'),
(18, 1, 'key_account', 2, 2, 19.20, 300.00, 5760.00, NULL, '2026-02-05 05:30:19', 'ref', NULL, 24, '2026-02-05 03:30:19', '2026-02-05 03:30:19', 'completed', NULL, NULL, 'auto'),
(21, 1, 'key_account', 3, 3, 1.00, 63.00, 63.00, 'M-PESA', '2026-02-07 20:16:27', 'SALE-ST-1-KA-3-V-000021', NULL, 24, '2026-02-07 18:16:27', '2026-02-07 17:16:27', 'completed', NULL, NULL, 'auto'),
(22, 1, 'key_account', 2, 2, 155.00, 25.00, 3875.00, 'CASH', '2026-02-07 20:24:30', 'SALE-ST-1-KA-2-V-000022', NULL, 24, '2026-02-07 18:24:30', '2026-02-07 17:24:30', 'completed', NULL, NULL, 'auto'),
(23, 1, 'key_account', 2, 2, 12.60, 100.00, 1260.00, 'M-PESA', '2026-02-07 20:27:27', 'SALE-ST-1-KA-2-V-000023', NULL, 24, '2026-02-07 18:27:27', '2026-02-07 17:27:27', 'completed', NULL, NULL, 'auto'),
(24, 1, 'key_account', 2, 2, 15.00, 99.00, 1485.00, 'M-PESA', '2026-02-07 20:30:20', 'SALE-ST-1-KA-2-V-000024', NULL, 24, '2026-02-07 18:30:20', '2026-02-07 17:30:20', 'completed', NULL, NULL, 'auto'),
(25, 1, 'key_account', 2, 2, 11.70, 200.00, 2340.00, 'CREDIT', '2026-02-07 20:35:49', 'SALE-ST-1-KA-2-V-000025', NULL, 24, '2026-02-07 18:35:49', '2026-02-07 17:35:49', 'completed', NULL, NULL, 'auto'),
(26, 1, 'key_account', 2, 2, 1.00, 225.00, 225.00, 'M-PESA', '2026-02-09 13:07:05', 'SALE-ST-1-KA-2-V-000026', NULL, 24, '2026-02-09 11:07:05', '2026-02-09 10:07:05', 'completed', NULL, NULL, 'auto');

-- --------------------------------------------------------

--
-- Table structure for table `service_approvals`
--

CREATE TABLE `service_approvals` (
  `id` int(11) NOT NULL,
  `inspection_id` int(11) NOT NULL,
  `technician_id` int(11) NOT NULL,
  `admin_id` int(11) DEFAULT NULL,
  `service_description` text NOT NULL,
  `estimated_cost` decimal(12,2) DEFAULT 0.00,
  `labor_hours` decimal(5,2) DEFAULT 0.00,
  `parts_needed` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`parts_needed`)),
  `status` enum('pending','approved','declined') DEFAULT 'pending',
  `admin_comments` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `shifts`
--

CREATE TABLE `shifts` (
  `id` int(11) NOT NULL,
  `date` datetime(3) NOT NULL,
  `time` varchar(191) NOT NULL,
  `userId` int(11) DEFAULT NULL,
  `userName` varchar(200) NOT NULL,
  `station_id` int(11) NOT NULL,
  `station_name` varchar(100) NOT NULL,
  `status` int(11) NOT NULL DEFAULT 0,
  `checkInTime` datetime(3) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `imageUrl` varchar(191) DEFAULT NULL,
  `notes` varchar(191) DEFAULT NULL,
  `checkoutLatitude` double DEFAULT NULL,
  `checkoutLongitude` double DEFAULT NULL,
  `checkoutTime` datetime(3) DEFAULT NULL,
  `showUpdateLocation` tinyint(1) NOT NULL DEFAULT 1,
  `routeId` int(11) DEFAULT NULL,
  `createdAt` varchar(50) NOT NULL,
  `updatedAt` varchar(50) NOT NULL,
  `outlet_address` varchar(100) NOT NULL,
  `approvedAt` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `shifts`
--

INSERT INTO `shifts` (`id`, `date`, `time`, `userId`, `userName`, `station_id`, `station_name`, `status`, `checkInTime`, `latitude`, `longitude`, `imageUrl`, `notes`, `checkoutLatitude`, `checkoutLongitude`, `checkoutTime`, `showUpdateLocation`, `routeId`, `createdAt`, `updatedAt`, `outlet_address`, `approvedAt`) VALUES
(10, '2026-01-26 09:02:29.000', '', 22, 'BRYAN OTIENO ONYANGO', 1, 'Kisumu Station 1', 2, '2026-01-26 21:22:40.000', -1.2312445, 36.8839172, NULL, NULL, NULL, NULL, NULL, 1, NULL, '', '', 'QV9M+FGM, Nairobi, Kenya', ''),
(11, '2026-01-27 07:37:45.000', '', 22, 'BRYAN OTIENO ONYANGO', 1, 'Kisumu Station 1', 2, '2026-01-27 08:02:00.000', -1.231243, 36.883921, NULL, NULL, NULL, NULL, '2026-01-27 10:02:00.000', 1, NULL, '', '', 'QV9M+FGM, Nairobi, Kenya', ''),
(12, '2026-02-05 10:05:24.000', '', 22, 'BRYAN OTIENO ONYANGO', 1, 'Kisumu Station 1', 2, '2026-02-05 10:09:05.000', -1.3008913, 36.7777615, NULL, NULL, NULL, NULL, NULL, 1, NULL, '', '', 'MQXH+J2X, Ndemi Ln, Nairobi, Kenya', '2026-02-05'),
(14, '2026-02-05 01:08:48.000', '', 24, 'manager 1', 1, 'Kisumu Station 1', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, '', '', '', ''),
(15, '2026-02-09 01:07:21.000', '', 24, 'manager 1', 1, 'Kisumu Station 1', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, '', '', '', ''),
(16, '2026-02-10 01:29:56.000', '', 22, 'BRYAN OTIENO ONYANGO', 1, 'Kisumu Station 1', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, '', '', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `shift_reconciliation`
--

CREATE TABLE `shift_reconciliation` (
  `id` int(11) NOT NULL,
  `shift_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `expected_cash` decimal(12,2) DEFAULT NULL,
  `collected_cash` decimal(12,2) DEFAULT NULL,
  `variance` decimal(12,2) DEFAULT NULL,
  `status` enum('pending','approved','rejected') DEFAULT NULL,
  `approved_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `photo_url` varchar(255) NOT NULL,
  `empl_no` varchar(50) NOT NULL,
  `id_no` varchar(50) NOT NULL,
  `role` varchar(255) NOT NULL,
  `designation` int(3) DEFAULT NULL,
  `phone_number` varchar(50) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `department` varchar(100) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  `business_email` varchar(255) DEFAULT NULL,
  `department_email` varchar(255) DEFAULT NULL,
  `salary` decimal(11,2) DEFAULT NULL,
  `employment_type` varchar(100) NOT NULL DEFAULT 'Contract',
  `gender` enum('Male','Female','Other') NOT NULL DEFAULT 'Male',
  `station_id` int(11) DEFAULT NULL,
  `stationss` int(11) DEFAULT NULL,
  `manager_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_active` int(3) NOT NULL,
  `avatar_url` varchar(200) NOT NULL DEFAULT 'https://via.placeholder.com/150',
  `status` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`id`, `name`, `photo_url`, `empl_no`, `id_no`, `role`, `designation`, `phone_number`, `password`, `department`, `department_id`, `business_email`, `department_email`, `salary`, `employment_type`, `gender`, `station_id`, `stationss`, `manager_id`, `created_at`, `updated_at`, `is_active`, `avatar_url`, `status`) VALUES
(9, 'admins', 'https://res.cloudinary.com/otienobryan/image/upload/v1757252591/uploads/wsxidqwfmgy8ib5tic1m.jpg', '123355', '9', 'executive', 0, '55', '$2a$10$me0dzhAfGglEGPhcK/34BuWmhYW3USYy3SeMbe46CQop102Yq./1S', 'Executive', 3, 'admin@gas.com', 'admin@gas.com', NULL, 'Contract', 'Male', NULL, NULL, NULL, '2025-07-19 12:38:19', '2026-01-23 16:54:53', 1, 'https://via.placeholder.com/150', 1),
(20, 's', 'https://res.cloudinary.com/otienobryan/image/upload/v1764762270/staff/mslbalzdvw4uznscynol.jpg', '2', '3', 's', 0, '55', '$2a$10$me0dzhAfGglEGPhcK/34BuWmhYW3USYy3SeMbe46CQop102Yq./1S', 'Business Development', 3, 'amarah@motorgas.africa', NULL, NULL, 'Contract', 'Male', NULL, NULL, NULL, '2025-12-03 11:44:31', '2026-02-11 13:24:36', 1, 'https://via.placeholder.com/150', 1),
(21, 'test', 'https://res.cloudinary.com/otienobryan/image/upload/v1765005297/staff/plpal99p6ayv4juqbclt.png', 't', '44', '', 0, '', '$2a$10$me0dzhAfGglEGPhcK/34BuWmhYW3USYy3SeMbe46CQop102Yq./1S', 'Reservations', 3, NULL, NULL, NULL, 'Contract', 'Male', NULL, NULL, NULL, '2025-12-03 11:54:48', '2025-12-06 07:14:57', 1, 'https://via.placeholder.com/150', 1),
(22, 'BRYAN OTIENO ONYANGO', 'https://via.placeholder.com/150', 'EMPL12356779', '3322', 'attendant', 0, '0790193625', '$2b$10$n0rsM50QpFHZTd0UT2fgOe0B8RzASVcI2U4lj8VYM3NWqP/q3Irxm', '2025-12-06', NULL, NULL, NULL, NULL, 'Contract', 'Male', 1, 1, NULL, '2025-12-06 17:44:48', '2026-01-23 16:40:14', 1, 'https://via.placeholder.com/150', 1),
(23, 'Benjamin Okwama', 'https://via.placeholder.com/150', '340114563', '466', 'attendant', 0, '0711376366', '$2b$10$n0rsM50QpFHZTd0UT2fgOe0B8RzASVcI2U4lj8VYM3NWqP/q3Irxm', '2025-12-06', NULL, NULL, NULL, NULL, 'Contract', 'Male', 1, 2, NULL, '2025-12-06 17:47:24', '2026-01-23 16:40:21', 1, 'https://via.placeholder.com/150', 1),
(24, 'manager 1', 'https://via.placeholder.com/150', '123355', '466', 'manager', 0, '0729855347', '$2b$10$n0rsM50QpFHZTd0UT2fgOe0B8RzASVcI2U4lj8VYM3NWqP/q3Irxm', '2025-12-06', NULL, NULL, NULL, NULL, 'Contract', 'Male', 1, 1, NULL, '2025-12-06 17:47:24', '2026-02-05 08:59:09', 1, 'https://via.placeholder.com/150', 1);

-- --------------------------------------------------------

--
-- Table structure for table `staff_leaves`
--

CREATE TABLE `staff_leaves` (
  `id` int(11) NOT NULL,
  `staff_id` int(11) NOT NULL,
  `leave_type_id` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `reason` text DEFAULT NULL,
  `attachment_url` varchar(500) DEFAULT NULL,
  `status` enum('pending','approved','rejected','cancelled') NOT NULL DEFAULT 'pending',
  `is_half_day` tinyint(1) NOT NULL DEFAULT 0,
  `approved_by` int(11) DEFAULT NULL,
  `applied_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `staff_leaves`
--

INSERT INTO `staff_leaves` (`id`, `staff_id`, `leave_type_id`, `start_date`, `end_date`, `reason`, `attachment_url`, `status`, `is_half_day`, `approved_by`, `applied_at`, `updated_at`) VALUES
(1, 23, 1, '2026-01-17', '2026-01-18', 'Yyyeeesfyyyttttttyyt', NULL, 'approved', 0, NULL, '2026-01-17 08:34:09', '2026-01-17 08:34:29');

-- --------------------------------------------------------

--
-- Table structure for table `Stations`
--

CREATE TABLE `Stations` (
  `id` int(11) NOT NULL,
  `name` varchar(191) NOT NULL,
  `address` varchar(255) NOT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `latitude` varchar(255) DEFAULT NULL,
  `regionId` int(11) NOT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `price` decimal(11,2) NOT NULL,
  `lpgQuantity` decimal(11,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Stations`
--

INSERT INTO `Stations` (`id`, `name`, `address`, `longitude`, `latitude`, `regionId`, `contact`, `price`, `lpgQuantity`) VALUES
(1, 'Kisumu Station 1', 'QV9M+FGM, Nairobi, Kenya', '36.8839297', '-1.2312339', 2, '0790193625', 102.00, 168.00),
(2, 'Ngong Road', '', NULL, NULL, 1, '0790193625', 99.00, 43.00);

-- --------------------------------------------------------

--
-- Table structure for table `stores`
--

CREATE TABLE `stores` (
  `id` int(11) NOT NULL,
  `store_code` varchar(20) NOT NULL,
  `store_name` varchar(100) NOT NULL,
  `address` text DEFAULT NULL,
  `country_id` int(11) NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `stores`
--

INSERT INTO `stores` (`id`, `store_code`, `store_name`, `address`, `country_id`, `is_active`, `created_at`) VALUES
(1, 'jj', 'jj', 'nn', 1, 1, '2025-12-13 20:01:29'),
(2, 'STR-0001', 'new store', 'here', 1, 1, '2025-12-13 20:23:12');

-- --------------------------------------------------------

--
-- Table structure for table `technical_logs`
--

CREATE TABLE `technical_logs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `action` varchar(255) NOT NULL,
  `resource_type` varchar(50) NOT NULL,
  `resource_id` int(11) NOT NULL,
  `old_value` text DEFAULT NULL,
  `new_value` text DEFAULT NULL,
  `details` text DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `technical_logs`
--

INSERT INTO `technical_logs` (`id`, `user_id`, `action`, `resource_type`, `resource_id`, `old_value`, `new_value`, `details`, `ip_address`, `created_at`) VALUES
(1, 23, 'INSPECTION_STARTED', 'inspection', 1, NULL, NULL, 'Inspection started for Conversion 1', '192.168.0.112', '2026-02-11 20:32:27'),
(2, 23, 'INSPECTION_UPDATED', 'inspection', 1, NULL, '{\"summary\":\"nothing \",\"overall_condition\":\"Good\",\"checklist_data\":[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}],\"status\":\"in-progress\"}', 'Inspection data updated', '192.168.100.12', '2026-02-12 07:18:26'),
(3, 23, 'INSPECTION_UPDATED', 'inspection', 1, NULL, '{\"summary\":\"nothing \",\"overall_condition\":\"good\",\"checklist_data\":[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}],\"status\":\"in-progress\"}', 'Inspection data updated', '192.168.100.12', '2026-02-12 07:18:42'),
(4, 23, 'INSPECTION_UPDATED', 'inspection', 1, NULL, '{\"summary\":\"nothing \",\"overall_condition\":\"good\",\"checklist_data\":[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}],\"status\":\"in-progress\"}', 'Inspection data updated', '192.168.100.12', '2026-02-12 07:19:07'),
(5, 23, 'INSPECTION_UPDATED', 'inspection', 1, NULL, '{\"summary\":\"nothing \",\"overall_condition\":\"Good\",\"checklist_data\":[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}],\"status\":\"in-progress\"}', 'Inspection data updated', '192.168.100.12', '2026-02-12 07:20:32'),
(6, 23, 'INSPECTION_UPDATED', 'inspection', 1, NULL, '{\"summary\":\"nothing \",\"overall_condition\":\"good\",\"checklist_data\":[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}],\"status\":\"in-progress\"}', 'Inspection data updated', '192.168.100.12', '2026-02-12 07:41:16'),
(7, 23, 'INSPECTION_UPDATED', 'inspection', 1, NULL, '{\"summary\":\"nothing \",\"overall_condition\":\"good\",\"checklist_data\":[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}],\"status\":\"in-progress\"}', 'Inspection data updated', '192.168.100.12', '2026-02-12 07:42:07'),
(8, 23, 'INSPECTION_UPDATED', 'inspection', 1, NULL, '{\"summary\":\"nothing \",\"overall_condition\":\"good\",\"checklist_data\":[{\"name\":\"Test Drive\",\"items\":[{\"id\":\"td_1\",\"name\":\"Engine Performance\",\"condition\":\"Good\"},{\"id\":\"td_2\",\"name\":\"Road Handling\",\"condition\":\"Good\"},{\"id\":\"td_3\",\"name\":\"Braking\",\"condition\":\"Good\"},{\"id\":\"td_4\",\"name\":\"Steering\\/Alignment\",\"condition\":\"Good\"},{\"id\":\"td_5\",\"name\":\"Transmission Shifting\",\"condition\":\"Good\"}]},{\"name\":\"Exterior Inspection\",\"items\":[{\"id\":\"ext_1\",\"name\":\"Paint Finish\",\"condition\":\"Good\"},{\"id\":\"ext_2\",\"name\":\"Body Damage\",\"condition\":\"Good\"},{\"id\":\"ext_3\",\"name\":\"Rust\",\"condition\":\"Good\"},{\"id\":\"ext_4\",\"name\":\"Windshield\\/Glass\",\"condition\":\"Good\"},{\"id\":\"ext_5\",\"name\":\"Headlights\\/Turn Signals\",\"condition\":\"Good\"}]},{\"name\":\"Electrical System\",\"items\":[{\"id\":\"elec_1\",\"name\":\"Battery\",\"condition\":\"Good\"},{\"id\":\"elec_2\",\"name\":\"Instrument Gauges\",\"condition\":\"Good\"},{\"id\":\"elec_3\",\"name\":\"Air Conditioning\",\"condition\":\"Good\"},{\"id\":\"elec_4\",\"name\":\"Heater Operation\",\"condition\":\"Good\"},{\"id\":\"elec_5\",\"name\":\"Wiper System\",\"condition\":\"Good\"}]},{\"name\":\"Under The Hood\",\"items\":[{\"id\":\"uth_1\",\"name\":\"Fluid Levels\",\"condition\":\"Good\"},{\"id\":\"uth_2\",\"name\":\"Hoses\",\"condition\":\"Good\"},{\"id\":\"uth_3\",\"name\":\"Belts\",\"condition\":\"Good\"},{\"id\":\"uth_4\",\"name\":\"Air Filter\",\"condition\":\"Good\"},{\"id\":\"uth_5\",\"name\":\"Radiator\",\"condition\":\"Good\"}]}],\"status\":\"in-progress\"}', 'Inspection data updated', '192.168.100.12', '2026-02-12 07:46:09'),
(9, 23, 'INSPECTION_SUBMITTED', 'inspection', 1, NULL, NULL, 'Inspection submitted for review', '192.168.100.12', '2026-02-12 07:57:48');

-- --------------------------------------------------------

--
-- Table structure for table `tickets`
--

CREATE TABLE `tickets` (
  `id` int(11) NOT NULL,
  `ticket_number` varchar(50) NOT NULL,
  `technician_id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `priority` enum('low','medium','high','urgent') DEFAULT 'medium',
  `status` enum('open','assigned','in-progress','hold','completed','cancelled') DEFAULT 'assigned',
  `due_date` datetime DEFAULT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Vehicles`
--

CREATE TABLE `Vehicles` (
  `id` int(11) NOT NULL,
  `key_account_id` int(11) NOT NULL,
  `registration_number` varchar(50) NOT NULL,
  `vin_serial_number` varchar(100) DEFAULT NULL,
  `vehicle_type` varchar(50) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `make` varchar(100) DEFAULT NULL,
  `model` varchar(255) NOT NULL,
  `trim_option` varchar(100) DEFAULT NULL,
  `transmission_type` varchar(50) DEFAULT NULL,
  `driven_wheel` varchar(50) DEFAULT NULL,
  `current_odo` int(11) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `driver_name` varchar(255) NOT NULL,
  `driver_contact` varchar(50) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Vehicles`
--

INSERT INTO `Vehicles` (`id`, `key_account_id`, `registration_number`, `vin_serial_number`, `vehicle_type`, `year`, `make`, `model`, `trim_option`, `transmission_type`, `driven_wheel`, `current_odo`, `color`, `driver_name`, `driver_contact`, `created_at`, `updated_at`) VALUES
(2, 2, 'KDD 333A', '2iii99', 'SUV', 2018, 'Ford', 'Camry', '23', 'CVT', 'FWD', 222, 'red', 'test driver', '123', '2025-12-13 16:47:43', '2025-12-13 16:47:43'),
(3, 3, 'KDD 112D', '2iii99b', 'Truck', 2024, '', 'nn', '23', 'Automatic', 'AWD', 777, 'red', 'test driver', '123', '2025-12-13 16:59:19', '2025-12-13 16:59:19'),
(4, 4, 'KDR 001P', NULL, NULL, NULL, NULL, 'Camry', NULL, NULL, NULL, NULL, NULL, 'test driver', '072111', '2026-01-17 08:26:58', '2026-02-05 10:48:27');

-- --------------------------------------------------------

--
-- Table structure for table `VisibilityReport`
--

CREATE TABLE `VisibilityReport` (
  `opening` int(191) DEFAULT NULL,
  `openingImage` varchar(255) DEFAULT NULL,
  `openAt` datetime DEFAULT NULL,
  `stationId` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `report_date` date GENERATED ALWAYS AS (cast(`openAt` as date)) STORED,
  `reportId` int(11) NOT NULL,
  `nozzleId` int(11) NOT NULL,
  `closing` int(11) NOT NULL,
  `closingImage` varchar(255) NOT NULL,
  `closedAt` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `VisibilityReport`
--

INSERT INTO `VisibilityReport` (`opening`, `openingImage`, `openAt`, `stationId`, `id`, `userId`, `reportId`, `nozzleId`, `closing`, `closingImage`, `closedAt`) VALUES
(202, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh4149.jpg', '2026-01-27 07:58:45', 1, 24, 22, 11, 2, 0, '', ''),
(3, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh6290.jpg', '2026-01-27 08:00:34', 1, 26, 22, 11, 3, 0, '', ''),
(4, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh8464.jpg', '2026-01-27 08:01:32', 1, 27, 22, 11, 4, 0, '', ''),
(1, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh7212.jpg', '2026-01-27 08:01:52', 1, 28, 22, 11, 1, 0, '', ''),
(3, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh4147.jpg', '2026-02-05 10:07:45', 1, 29, 22, 12, 1, 0, '', ''),
(1, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh6126.jpg', '2026-02-05 10:08:13', 1, 30, 22, 12, 2, 0, '', ''),
(5, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh2323.jpg', '2026-02-05 10:08:34', 1, 31, 22, 12, 3, 0, '', ''),
(6, 'https://impulsepromotions.co.ke/motogas/android/v1/image2/woosh9448.jpg', '2026-02-05 10:08:54', 1, 32, 22, 12, 4, 0, '', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Category`
--
ALTER TABLE `Category`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_name` (`name`),
  ADD KEY `idx_name_search` (`name`);

--
-- Indexes for table `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `room_id` (`room_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- Indexes for table `chat_rooms`
--
ALTER TABLE `chat_rooms`
  ADD PRIMARY KEY (`id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `chat_room_members`
--
ALTER TABLE `chat_room_members`
  ADD PRIMARY KEY (`id`),
  ADD KEY `room_id` (`room_id`),
  ADD KEY `staff_id` (`staff_id`);

--
-- Indexes for table `checkin_records`
--
ALTER TABLE `checkin_records`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_station_id` (`station_id`),
  ADD KEY `idx_time_in` (`time_in`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_user_date` (`user_id`,`time_in`);

--
-- Indexes for table `conversions`
--
ALTER TABLE `conversions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_owner_full_name` (`owner_full_name`),
  ADD KEY `idx_vehicle_registration` (`vehicle_registration`),
  ADD KEY `idx_contact` (`contact`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_created_by` (`created_by`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_scheduled_date` (`scheduled_date`),
  ADD KEY `idx_conversion_date` (`conversion_date`);

--
-- Indexes for table `Country`
--
ALTER TABLE `Country`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `delete_acc`
--
ALTER TABLE `delete_acc`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `status` (`status`);

--
-- Indexes for table `departments`
--
ALTER TABLE `departments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `FuelPrices`
--
ALTER TABLE `FuelPrices`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_stationId` (`stationId`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `inspections`
--
ALTER TABLE `inspections`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `inspection_number` (`inspection_number`),
  ADD KEY `ticket_id` (`ticket_id`),
  ADD KEY `technician_id` (`technician_id`),
  ADD KEY `vehicle_id` (`vehicle_id`),
  ADD KEY `station_id` (`station_id`),
  ADD KEY `fk_inspection_conversion` (`conversion_id`);

--
-- Indexes for table `inspection_photos`
--
ALTER TABLE `inspection_photos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `inspection_id` (`inspection_id`);

--
-- Indexes for table `InventoryLedger`
--
ALTER TABLE `InventoryLedger`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_stationId` (`stationId`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_transactionType` (`transactionType`),
  ADD KEY `idx_createdBy` (`createdBy`);

--
-- Indexes for table `inventory_movements`
--
ALTER TABLE `inventory_movements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_product_id` (`product_id`),
  ADD KEY `idx_source_station` (`source_station_id`),
  ADD KEY `idx_destination_station` (`destination_station_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_type` (`movement_type`),
  ADD KEY `fk_inv_requested_by` (`requested_by`),
  ADD KEY `fk_inv_processed_by` (`processed_by`);

--
-- Indexes for table `KeyAccountFuelPrices`
--
ALTER TABLE `KeyAccountFuelPrices`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_keyAccountId` (`keyAccountId`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_updatedBy` (`updatedBy`);

--
-- Indexes for table `KeyAccounts`
--
ALTER TABLE `KeyAccounts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `account_number` (`account_number`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_account_number` (`account_number`),
  ADD KEY `idx_name` (`name`),
  ADD KEY `idx_type` (`type`),
  ADD KEY `idx_region` (`region`),
  ADD KEY `idx_loyalty_points` (`loyalty_points`),
  ADD KEY `idx_balance` (`balance`);

--
-- Indexes for table `key_account_ledger`
--
ALTER TABLE `key_account_ledger`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_key_account_id` (`key_account_id`),
  ADD KEY `idx_vehicle_id` (`vehicle_id`),
  ADD KEY `idx_station_id` (`station_id`),
  ADD KEY `idx_transaction_date` (`transaction_date`),
  ADD KEY `idx_transaction_type` (`transaction_type`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_created_by` (`created_by`);

--
-- Indexes for table `leave_types`
--
ALTER TABLE `leave_types`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_is_active` (`is_active`);

--
-- Indexes for table `LoginHistory`
--
ALTER TABLE `LoginHistory`
  ADD PRIMARY KEY (`id`),
  ADD KEY `LoginHistory_userId_idx` (`userId`),
  ADD KEY `LoginHistory_userId_status_idx` (`userId`,`status`),
  ADD KEY `LoginHistory_sessionStart_idx` (`sessionStart`),
  ADD KEY `LoginHistory_sessionEnd_idx` (`sessionEnd`),
  ADD KEY `LoginHistory_userId_sessionStart_idx` (`userId`,`sessionStart`),
  ADD KEY `LoginHistory_status_sessionStart_idx` (`status`,`sessionStart`);

--
-- Indexes for table `loyalty_points_ledger`
--
ALTER TABLE `loyalty_points_ledger`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_key_account_id` (`key_account_id`),
  ADD KEY `idx_sale_id` (`sale_id`),
  ADD KEY `idx_transaction_date` (`transaction_date`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `notices`
--
ALTER TABLE `notices`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `Parts`
--
ALTER TABLE `Parts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_part_number` (`part_number`),
  ADD KEY `idx_name` (`name`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_manufacturer` (`manufacturer`),
  ADD KEY `idx_location` (`location`),
  ADD KEY `idx_stock_quantity` (`stock_quantity`);

--
-- Indexes for table `parts_inventory`
--
ALTER TABLE `parts_inventory`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_store_part` (`store_id`,`part_id`),
  ADD KEY `idx_store_id` (`store_id`),
  ADD KEY `idx_part_id` (`part_id`),
  ADD KEY `idx_quantity` (`quantity`);

--
-- Indexes for table `parts_inventory_ledger`
--
ALTER TABLE `parts_inventory_ledger`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_inventory_id` (`inventory_id`),
  ADD KEY `idx_store_id` (`store_id`),
  ADD KEY `idx_part_id` (`part_id`),
  ADD KEY `idx_transaction_type` (`transaction_type`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `parts_requests`
--
ALTER TABLE `parts_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `technician_id` (`technician_id`),
  ADD KEY `approver_id` (`approver_id`),
  ADD KEY `part_id` (`part_id`),
  ADD KEY `station_id` (`station_id`),
  ADD KEY `inspection_id` (`inspection_id`);

--
-- Indexes for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `email` (`email`);

--
-- Indexes for table `payment_transactions`
--
ALTER TABLE `payment_transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `initiated_by` (`initiated_by`),
  ADD KEY `verified_by` (`verified_by`),
  ADD KEY `idx_sale_id` (`sale_id`),
  ADD KEY `idx_key_account_id` (`key_account_id`),
  ADD KEY `idx_station_id` (`station_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_payment_method` (`payment_method`),
  ADD KEY `idx_external_ref` (`external_payment_ref`),
  ADD KEY `idx_initiated_at` (`initiated_at`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pumps`
--
ALTER TABLE `pumps`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_station_id` (`station_id`),
  ADD KEY `idx_is_active` (`is_active`);

--
-- Indexes for table `pump_readings`
--
ALTER TABLE `pump_readings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_station_id` (`station_id`),
  ADD KEY `idx_pump_id` (`pump_id`),
  ADD KEY `idx_shift_id` (`shift_id`),
  ADD KEY `idx_captured_by` (`user_id`);

--
-- Indexes for table `Regions`
--
ALTER TABLE `Regions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_countryId` (`countryId`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_station_id` (`station_id`),
  ADD KEY `idx_client_type` (`client_type`),
  ADD KEY `idx_key_account_id` (`key_account_id`),
  ADD KEY `idx_vehicle_id` (`vehicle_id`),
  ADD KEY `idx_sale_date` (`sale_date`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_created_by` (`created_by`),
  ADD KEY `idx_reference_number` (`reference_number`),
  ADD KEY `idx_payment_method` (`payment_method`);

--
-- Indexes for table `service_approvals`
--
ALTER TABLE `service_approvals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `inspection_id` (`inspection_id`),
  ADD KEY `technician_id` (`technician_id`),
  ADD KEY `admin_id` (`admin_id`);

--
-- Indexes for table `shifts`
--
ALTER TABLE `shifts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `JourneyPlan_routeId_fkey` (`routeId`),
  ADD KEY `salesrep_id` (`userId`);

--
-- Indexes for table `shift_reconciliation`
--
ALTER TABLE `shift_reconciliation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_shift_id` (`shift_id`),
  ADD KEY `fk_shift_reconciliation_user` (`user_id`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_staff_department` (`department_id`),
  ADD KEY `idx_staff_designation` (`designation`),
  ADD KEY `idx_staff_manager` (`manager_id`),
  ADD KEY `idx_station_id` (`stationss`),
  ADD KEY `station_id` (`station_id`);

--
-- Indexes for table `staff_leaves`
--
ALTER TABLE `staff_leaves`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_staff_id` (`staff_id`),
  ADD KEY `idx_leave_type_id` (`leave_type_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_start_date` (`start_date`),
  ADD KEY `idx_end_date` (`end_date`),
  ADD KEY `idx_dates_range` (`start_date`,`end_date`),
  ADD KEY `staff_leaves_ibfk_3` (`approved_by`);

--
-- Indexes for table `Stations`
--
ALTER TABLE `Stations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_regionId` (`regionId`);

--
-- Indexes for table `stores`
--
ALTER TABLE `stores`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_store_code` (`store_code`),
  ADD KEY `idx_store_name` (`store_name`),
  ADD KEY `idx_country_id` (`country_id`),
  ADD KEY `idx_is_active` (`is_active`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `technical_logs`
--
ALTER TABLE `technical_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `tickets`
--
ALTER TABLE `tickets`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ticket_number` (`ticket_number`),
  ADD KEY `technician_id` (`technician_id`),
  ADD KEY `station_id` (`station_id`),
  ADD KEY `vehicle_id` (`vehicle_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `Vehicles`
--
ALTER TABLE `Vehicles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_key_account_id` (`key_account_id`),
  ADD KEY `idx_registration_number` (`registration_number`),
  ADD KEY `idx_vin` (`vin_serial_number`),
  ADD KEY `idx_vehicle_type` (`vehicle_type`),
  ADD KEY `idx_make` (`make`);

--
-- Indexes for table `VisibilityReport`
--
ALTER TABLE `VisibilityReport`
  ADD PRIMARY KEY (`id`),
  ADD KEY `VisibilityReport_userId_idx` (`userId`),
  ADD KEY `VisibilityReport_clientId_idx` (`stationId`),
  ADD KEY `idx_visibility_report_user_id` (`userId`),
  ADD KEY `idx_visibility_report_client_id` (`stationId`),
  ADD KEY `idx_visibility_report_created_at` (`openAt`),
  ADD KEY `idx_visibility_report_composite` (`openAt`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Category`
--
ALTER TABLE `Category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `chat_messages`
--
ALTER TABLE `chat_messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=151;

--
-- AUTO_INCREMENT for table `chat_rooms`
--
ALTER TABLE `chat_rooms`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `chat_room_members`
--
ALTER TABLE `chat_room_members`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=126;

--
-- AUTO_INCREMENT for table `checkin_records`
--
ALTER TABLE `checkin_records`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `conversions`
--
ALTER TABLE `conversions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `Country`
--
ALTER TABLE `Country`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `delete_acc`
--
ALTER TABLE `delete_acc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `departments`
--
ALTER TABLE `departments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `FuelPrices`
--
ALTER TABLE `FuelPrices`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `inspections`
--
ALTER TABLE `inspections`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `inspection_photos`
--
ALTER TABLE `inspection_photos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `InventoryLedger`
--
ALTER TABLE `InventoryLedger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `inventory_movements`
--
ALTER TABLE `inventory_movements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `KeyAccountFuelPrices`
--
ALTER TABLE `KeyAccountFuelPrices`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `KeyAccounts`
--
ALTER TABLE `KeyAccounts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `key_account_ledger`
--
ALTER TABLE `key_account_ledger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `leave_types`
--
ALTER TABLE `leave_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `LoginHistory`
--
ALTER TABLE `LoginHistory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3487;

--
-- AUTO_INCREMENT for table `loyalty_points_ledger`
--
ALTER TABLE `loyalty_points_ledger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `notices`
--
ALTER TABLE `notices`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `Parts`
--
ALTER TABLE `Parts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `parts_inventory`
--
ALTER TABLE `parts_inventory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `parts_inventory_ledger`
--
ALTER TABLE `parts_inventory_ledger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `parts_requests`
--
ALTER TABLE `parts_requests`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `password_resets`
--
ALTER TABLE `password_resets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment_transactions`
--
ALTER TABLE `payment_transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `pumps`
--
ALTER TABLE `pumps`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `pump_readings`
--
ALTER TABLE `pump_readings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `Regions`
--
ALTER TABLE `Regions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `sales`
--
ALTER TABLE `sales`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `service_approvals`
--
ALTER TABLE `service_approvals`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `shifts`
--
ALTER TABLE `shifts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `shift_reconciliation`
--
ALTER TABLE `shift_reconciliation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `staff_leaves`
--
ALTER TABLE `staff_leaves`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `Stations`
--
ALTER TABLE `Stations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `stores`
--
ALTER TABLE `stores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `technical_logs`
--
ALTER TABLE `technical_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `tickets`
--
ALTER TABLE `tickets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Vehicles`
--
ALTER TABLE `Vehicles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `VisibilityReport`
--
ALTER TABLE `VisibilityReport`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `checkin_records`
--
ALTER TABLE `checkin_records`
  ADD CONSTRAINT `checkin_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `checkin_records_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `FuelPrices`
--
ALTER TABLE `FuelPrices`
  ADD CONSTRAINT `FuelPrices_ibfk_1` FOREIGN KEY (`stationId`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `inspections`
--
ALTER TABLE `inspections`
  ADD CONSTRAINT `fk_inspection_conversion` FOREIGN KEY (`conversion_id`) REFERENCES `conversions` (`id`),
  ADD CONSTRAINT `inspections_ibfk_1` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`),
  ADD CONSTRAINT `inspections_ibfk_2` FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `inspections_ibfk_3` FOREIGN KEY (`vehicle_id`) REFERENCES `Vehicles` (`id`),
  ADD CONSTRAINT `inspections_ibfk_4` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `inspection_photos`
--
ALTER TABLE `inspection_photos`
  ADD CONSTRAINT `inspection_photos_ibfk_1` FOREIGN KEY (`inspection_id`) REFERENCES `inspections` (`id`);

--
-- Constraints for table `InventoryLedger`
--
ALTER TABLE `InventoryLedger`
  ADD CONSTRAINT `InventoryLedger_ibfk_1` FOREIGN KEY (`stationId`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `inventory_movements`
--
ALTER TABLE `inventory_movements`
  ADD CONSTRAINT `fk_inv_destination` FOREIGN KEY (`destination_station_id`) REFERENCES `Stations` (`id`),
  ADD CONSTRAINT `fk_inv_processed_by` FOREIGN KEY (`processed_by`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `fk_inv_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `fk_inv_requested_by` FOREIGN KEY (`requested_by`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `fk_inv_source` FOREIGN KEY (`source_station_id`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `KeyAccountFuelPrices`
--
ALTER TABLE `KeyAccountFuelPrices`
  ADD CONSTRAINT `KeyAccountFuelPrices_ibfk_1` FOREIGN KEY (`keyAccountId`) REFERENCES `KeyAccounts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_keyaccountfuelprices_updatedby` FOREIGN KEY (`updatedBy`) REFERENCES `staff` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `key_account_ledger`
--
ALTER TABLE `key_account_ledger`
  ADD CONSTRAINT `key_account_ledger_ibfk_1` FOREIGN KEY (`key_account_id`) REFERENCES `KeyAccounts` (`id`),
  ADD CONSTRAINT `key_account_ledger_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `Vehicles` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `key_account_ledger_ibfk_3` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `loyalty_points_ledger`
--
ALTER TABLE `loyalty_points_ledger`
  ADD CONSTRAINT `loyalty_points_ledger_ibfk_1` FOREIGN KEY (`key_account_id`) REFERENCES `KeyAccounts` (`id`),
  ADD CONSTRAINT `loyalty_points_ledger_ibfk_2` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `parts_inventory`
--
ALTER TABLE `parts_inventory`
  ADD CONSTRAINT `parts_inventory_ibfk_1` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `parts_inventory_ibfk_2` FOREIGN KEY (`part_id`) REFERENCES `Parts` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `parts_inventory_ledger`
--
ALTER TABLE `parts_inventory_ledger`
  ADD CONSTRAINT `parts_inventory_ledger_ibfk_1` FOREIGN KEY (`inventory_id`) REFERENCES `parts_inventory` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `parts_inventory_ledger_ibfk_2` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `parts_inventory_ledger_ibfk_3` FOREIGN KEY (`part_id`) REFERENCES `Parts` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `parts_requests`
--
ALTER TABLE `parts_requests`
  ADD CONSTRAINT `parts_requests_ibfk_1` FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `parts_requests_ibfk_2` FOREIGN KEY (`approver_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `parts_requests_ibfk_3` FOREIGN KEY (`part_id`) REFERENCES `Parts` (`id`),
  ADD CONSTRAINT `parts_requests_ibfk_4` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`),
  ADD CONSTRAINT `parts_requests_ibfk_5` FOREIGN KEY (`inspection_id`) REFERENCES `inspections` (`id`);

--
-- Constraints for table `payment_transactions`
--
ALTER TABLE `payment_transactions`
  ADD CONSTRAINT `payment_transactions_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `payment_transactions_ibfk_2` FOREIGN KEY (`key_account_id`) REFERENCES `KeyAccounts` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `payment_transactions_ibfk_3` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`),
  ADD CONSTRAINT `payment_transactions_ibfk_4` FOREIGN KEY (`initiated_by`) REFERENCES `staff` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `payment_transactions_ibfk_5` FOREIGN KEY (`verified_by`) REFERENCES `staff` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `pumps`
--
ALTER TABLE `pumps`
  ADD CONSTRAINT `pumps_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `pump_readings`
--
ALTER TABLE `pump_readings`
  ADD CONSTRAINT `fk_pump_readings_user` FOREIGN KEY (`user_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `pump_readings_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`),
  ADD CONSTRAINT `pump_readings_ibfk_2` FOREIGN KEY (`pump_id`) REFERENCES `pumps` (`id`),
  ADD CONSTRAINT `pump_readings_ibfk_3` FOREIGN KEY (`shift_id`) REFERENCES `checkin_records` (`id`);

--
-- Constraints for table `Regions`
--
ALTER TABLE `Regions`
  ADD CONSTRAINT `Regions_ibfk_1` FOREIGN KEY (`countryId`) REFERENCES `Country` (`id`);

--
-- Constraints for table `sales`
--
ALTER TABLE `sales`
  ADD CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`),
  ADD CONSTRAINT `sales_ibfk_2` FOREIGN KEY (`key_account_id`) REFERENCES `KeyAccounts` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `sales_ibfk_3` FOREIGN KEY (`vehicle_id`) REFERENCES `Vehicles` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `service_approvals`
--
ALTER TABLE `service_approvals`
  ADD CONSTRAINT `service_approvals_ibfk_1` FOREIGN KEY (`inspection_id`) REFERENCES `inspections` (`id`),
  ADD CONSTRAINT `service_approvals_ibfk_2` FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `service_approvals_ibfk_3` FOREIGN KEY (`admin_id`) REFERENCES `staff` (`id`);

--
-- Constraints for table `shift_reconciliation`
--
ALTER TABLE `shift_reconciliation`
  ADD CONSTRAINT `fk_shift_reconciliation_user` FOREIGN KEY (`user_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `shift_reconciliation_ibfk_1` FOREIGN KEY (`shift_id`) REFERENCES `checkin_records` (`id`);

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `staff_ibfk_station` FOREIGN KEY (`stationss`) REFERENCES `Stations` (`id`);

--
-- Constraints for table `staff_leaves`
--
ALTER TABLE `staff_leaves`
  ADD CONSTRAINT `staff_leaves_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `staff_leaves_ibfk_2` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_types` (`id`),
  ADD CONSTRAINT `staff_leaves_ibfk_3` FOREIGN KEY (`approved_by`) REFERENCES `staff` (`id`);

--
-- Constraints for table `Stations`
--
ALTER TABLE `Stations`
  ADD CONSTRAINT `Stations_ibfk_1` FOREIGN KEY (`regionId`) REFERENCES `Regions` (`id`);

--
-- Constraints for table `technical_logs`
--
ALTER TABLE `technical_logs`
  ADD CONSTRAINT `technical_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `staff` (`id`);

--
-- Constraints for table `tickets`
--
ALTER TABLE `tickets`
  ADD CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`technician_id`) REFERENCES `staff` (`id`),
  ADD CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `Stations` (`id`),
  ADD CONSTRAINT `tickets_ibfk_3` FOREIGN KEY (`vehicle_id`) REFERENCES `Vehicles` (`id`),
  ADD CONSTRAINT `tickets_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `staff` (`id`);

--
-- Constraints for table `Vehicles`
--
ALTER TABLE `Vehicles`
  ADD CONSTRAINT `Vehicles_ibfk_1` FOREIGN KEY (`key_account_id`) REFERENCES `KeyAccounts` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
