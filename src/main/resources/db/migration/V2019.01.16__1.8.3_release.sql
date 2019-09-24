ALTER TABLE `audience` ADD COLUMN `lookalike_type` varchar(255) DEFAULT NULL;
ALTER TABLE `audience` ADD COLUMN `lookalike_include` tinyint(4) DEFAULT b'1';
ALTER TABLE `audience` ADD COLUMN `lookalike_file_path` varchar(255) NULL;
ALTER TABLE `audience` ADD COLUMN `lookalike_job_id` varchar(255) DEFAULT NULL;
ALTER TABLE `audience` ADD COLUMN `lookalike_result` longtext;
ALTER TABLE `audience` ADD COLUMN `lookalike_reach_value` int(6) DEFAULT 0;