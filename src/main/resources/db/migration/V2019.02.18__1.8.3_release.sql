CREATE TABLE `lookalike_audience_jobs` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `tenant_name` varchar(255) NOT NULL DEFAULT '',
 `taxonomy_id` varchar(255) NOT NULL DEFAULT '',
 `job_id` varchar(255) NOT NULL DEFAULT '',
 `size` bigint(20) NOT NULL DEFAULT '0',
 `status` varchar(20) NOT NULL DEFAULT '',
 `err_info` varchar(255) NOT NULL DEFAULT '',
 `create_time` date DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;