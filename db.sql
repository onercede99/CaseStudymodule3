CREATE TABLE `admins` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `username` varchar(100) NOT NULL,
                          `password_hash` varchar(255) NOT NULL,
                          `full_name` varchar(150) DEFAULT NULL,
                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `username` (`username`)
);
CREATE TABLE `bookings` (
                            `booking_id` int NOT NULL AUTO_INCREMENT,
                            `room_id` int NOT NULL,
                            `guest_name` varchar(100) NOT NULL,
                            `guest_email` varchar(100) DEFAULT NULL,
                            `check_in_date` date NOT NULL,
                            `check_out_date` date NOT NULL,
                            `total_price` decimal(10,2) NOT NULL,
                            `booking_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `status` varchar(20) DEFAULT 'confirmed',
                            PRIMARY KEY (`booking_id`),
                            KEY `room_id` (`room_id`),
                            CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`)
) ;
CREATE TABLE `rooms` (
                         `room_id` int NOT NULL AUTO_INCREMENT,
                         `room_number` varchar(10) NOT NULL,
                         `room_type` varchar(50) NOT NULL,
                         `price_per_night` decimal(10,2) NOT NULL,
                         `description` text,
                         `image_url` varchar(500) DEFAULT NULL,
                         `is_available` tinyint(1) DEFAULT '1',
                         `capacity` int DEFAULT NULL,
                         PRIMARY KEY (`room_id`),
                         UNIQUE KEY `room_number` (`room_number`)
);