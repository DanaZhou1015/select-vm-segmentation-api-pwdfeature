ALTER TABLE `audience` ADD COLUMN `frozen_test_count` bigint(20) DEFAULT 0;
ALTER TABLE `audience` ADD COLUMN `frozen_control_count` bigint(20) DEFAULT 0;
ALTER TABLE `test_control_tasks` ADD COLUMN `frozen_flag` tinyint(1) DEFAULT b'0';
ALTER TABLE `test_control_tasks` ADD COLUMN `test_control_tasks` bigint(20) DEFAULT 0;
ALTER TABLE `test_control_tasks` ADD COLUMN `rules` longtext;