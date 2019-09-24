CREATE TABLE `universe_integration` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `universe_id` bigint(20) NOT NULL,
                                     `drop_off_point` varchar(255) NOT NULL,
                                     `lr_audience_id` varchar(255) NOT NULL,
                                     `onboard_destination_id` varchar(255) DEFAULT NULL,
                                     `onboard_integration_id` varchar(255) DEFAULT NULL,
                                     `datastore_destination_id` varchar(255) DEFAULT NULL,
                                     `datastore_integration_id` varchar(255) DEFAULT NULL,
                                     `is_deleted` tinyint(4) NOT NULL,
                                     `created_time` datetime NOT NULL,
                                     `update_time` datetime NOT NULL,
                                     `created_by` varchar(255) DEFAULT NULL,
                                     PRIMARY KEY (`id`),
                                     KEY `universe_integration_ibfk_1` (`universe_id`),
                                     CONSTRAINT `universe_integration_ibfk_1` FOREIGN KEY (`universe_id`) REFERENCES `universe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;