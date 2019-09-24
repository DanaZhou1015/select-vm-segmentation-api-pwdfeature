CREATE TABLE `test_control_tasks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_name` varchar(255) NOT NULL DEFAULT '',
  `taxonomy_id` varchar(255) NOT NULL DEFAULT '',
  `job_id` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(20) NOT NULL DEFAULT '',
  `err_info` varchar(255) NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;