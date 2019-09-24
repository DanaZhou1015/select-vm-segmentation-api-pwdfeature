DROP TABLE IF EXISTS `tenant`;

CREATE TABLE `tenant` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `created_by` varchar(255) NOT NULL,
                        `created_time` datetime NOT NULL,
                        `is_deleted` tinyint(4) DEFAULT '0',
                        `update_time` datetime DEFAULT NULL,
                        `ai_tenant` varchar(255) DEFAULT NULL,
                        `tenant_name` varchar(255) DEFAULT NULL,
                        `tenant_id` varchar(255) DEFAULT NULL,
                        `tenant_path` varchar(255) DEFAULT NULL,
                        `tenant_count_limit` bigint(20) DEFAULT '250',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `folder`;

CREATE TABLE `folder` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `created_by` varchar(255) NOT NULL,
                        `created_time` datetime NOT NULL,
                        `is_deleted` tinyint(4) DEFAULT '0',
                        `update_time` datetime DEFAULT NULL,
                        `folder_name` varchar(255) DEFAULT NULL,
                        `folder_type` varchar(255) DEFAULT NULL,
                        `folder_parent_id` int(11) DEFAULT NULL,
                        `tenant_id` bigint(20) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `audience`;

CREATE TABLE `audience` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `audience_name` varchar(255) DEFAULT NULL,
                          `audience_status` varchar(255) DEFAULT NULL,
                          `audience_type` varchar(255) DEFAULT NULL,
                          `audience_count` bigint(20) DEFAULT NULL,
                          `audience_code` varchar(255) DEFAULT NULL,
                          `audience_description` varchar(255) DEFAULT NULL,
                          `audience_cost` varchar(255) DEFAULT NULL,
                          `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                          `tenant_id` bigint(20) DEFAULT NULL,
                          `destination_id` bigint(20) DEFAULT NULL,
                          `taxonomy_id` varchar(255) DEFAULT NULL,
                          `folder_id` bigint(20) NOT NULL,
                          `audience_rule_json` longtext,
                          `audience_rule_json_display` longtext,
                          `created_time` datetime NOT NULL,
                          `update_time` datetime NOT NULL,
                          `created_by` varchar(255) NOT NULL,
                          `match_rate` longtext,
                          `match_status` varchar(255) DEFAULT NULL,
                          `connect_filename` varchar(255) DEFAULT NULL,
                          `frozen_audience_count` bigint(20) DEFAULT NULL,
                          `distribution_flag` tinyint(4) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FK202twfenew164ngxh53a2htap` (`folder_id`) USING BTREE,
                          CONSTRAINT `audience_ibfk_1` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `audience_count`;

CREATE TABLE `audience_count` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `created_by` varchar(255) NOT NULL,
                                `created_time` datetime NOT NULL,
                                `is_deleted` bit(1) DEFAULT b'0',
                                `update_time` datetime DEFAULT NULL,
                                `audience_count_count` int(11) DEFAULT NULL,
                                `tenant_id` bigint(20) NOT NULL,
                                `audience_count_taxonomy_id` varchar(255) DEFAULT NULL,
                                `audience_count_tenant_id` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKa6ad4sn5722jdj97oa2qxdvg6` (`tenant_id`),
                                CONSTRAINT `FKa6ad4sn5722jdj97oa2qxdvg6` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `audience_distribute_job`;

CREATE TABLE `audience_distribute_job` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `created_by` varchar(255) NOT NULL,
                                         `created_time` datetime NOT NULL,
                                         `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                         `update_time` datetime DEFAULT NULL,
                                         `distribute_job_audience_id` bigint(20) DEFAULT NULL,
                                         `distribute_job_notice_email` varchar(255) DEFAULT NULL,
                                         `distribute_job_status` varchar(255) DEFAULT NULL,
                                         `distribute_job_tenant_id` bigint(20) DEFAULT NULL,
                                         `distribute_job_update_by` varchar(255) DEFAULT NULL,
                                         `distribute_job_file_type` varchar(255) DEFAULT NULL,
                                         `distribute_job_audience_type` varchar(255) DEFAULT NULL,
                                         `distribute_job_destination_id` bigint(20) DEFAULT NULL,
                                         `distribute_job_rules` longtext,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bitmap_generation_tasks`;

CREATE TABLE `bitmap_generation_tasks` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `tenant_name` varchar(255) NOT NULL DEFAULT '',
                                         `task_info` varchar(255) DEFAULT NULL,
                                         `node_info` varchar(255) DEFAULT NULL,
                                         `create_time` datetime NOT NULL,
                                         `end_time` datetime NOT NULL,
                                         `task_status` varchar(255) DEFAULT NULL,
                                         `task_heartbeat` bigint(20) NOT NULL DEFAULT '0',
                                         `err_info` varchar(255) DEFAULT NULL,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `tenant_name` (`tenant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `channel`;

CREATE TABLE `channel` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `created_by` varchar(255) NOT NULL,
                         `created_time` datetime NOT NULL,
                         `is_deleted` tinyint(4) DEFAULT '0',
                         `update_time` datetime DEFAULT NULL,
                         `channel_name` varchar(255) DEFAULT NULL,
                         `channel_rules` varchar(255) DEFAULT NULL,
                         `channel_tenant_id` bigint(20) DEFAULT NULL,
                         `channel_path` varchar(255) DEFAULT NULL,
                         `channel_infobase_id` bigint(20) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datastore_account_log`;

CREATE TABLE `datastore_account_log` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `create_time` datetime NOT NULL,
                                       `update_time` datetime NOT NULL,
                                       `dal_campaign_id` bigint(20) NOT NULL DEFAULT '0',
                                       `dal_campaign_name` varchar(255) NOT NULL DEFAULT '',
                                       `dal_tenant_name` varchar(255) NOT NULL DEFAULT '',
                                       `dal_campaign_type` int(11) NOT NULL DEFAULT '0',
                                       `dal_account_id` bigint(20) NOT NULL DEFAULT '0',
                                       `dal_account_name` varchar(255) NOT NULL DEFAULT '',
                                       `dal_account_status` varchar(255) NOT NULL DEFAULT '',
                                       `dal_account_device_types` varchar(255) NOT NULL DEFAULT '',
                                       `dal_request_status` int(11) NOT NULL DEFAULT '0',
                                       `dal_request_data` longtext NOT NULL,
                                       `dal_response_data` longtext NOT NULL,
                                       `dal_batch_created` tinyint(1) NOT NULL DEFAULT '0',
                                       `dal_batch_request_status` int(11) NOT NULL DEFAULT '0',
                                       `dal_batch_request_data` longtext NOT NULL,
                                       `dal_batch_response_data` longtext NOT NULL,
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datastore_distribution_log`;

CREATE TABLE `datastore_distribution_log` (
                                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                            `create_time` datetime NOT NULL,
                                            `update_time` datetime NOT NULL,
                                            `ddl_campaign_id` bigint(20) NOT NULL DEFAULT '0',
                                            `ddl_campaign_name` varchar(255) NOT NULL DEFAULT '',
                                            `ddl_segment_id` bigint(20) NOT NULL DEFAULT '0',
                                            `ddl_segment_name` varchar(255) NOT NULL DEFAULT '',
                                            `ddl_segment_key` varchar(255) NOT NULL DEFAULT '',
                                            `ddl_segment_value` varchar(255) NOT NULL DEFAULT '',
                                            `ddl_response_data` longtext NOT NULL,
                                            `ddl_request_status` int(11) NOT NULL DEFAULT '0',
                                            `ddl_request_data` longtext NOT NULL,
                                            `ddl_data_type` int(11) NOT NULL DEFAULT '0',
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `profiling`;

CREATE TABLE `profiling` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `created_by` varchar(255) NOT NULL,
                           `created_time` datetime NOT NULL,
                           `is_deleted` tinyint(4) DEFAULT '0',
                           `update_time` datetime DEFAULT NULL,
                           `profiling_json` text,
                           `profiling_name` varchar(255) DEFAULT NULL,
                           `tenant_id` bigint(20) NOT NULL,
                           `profiling_description` varchar(255) DEFAULT NULL,
                           `destination_id` bigint(20) DEFAULT NULL,
                           `active` bit(1) DEFAULT b'0',
                           PRIMARY KEY (`id`),
                           KEY `FKq9h55ow2txbwvjys7k3i8dsrj` (`tenant_id`),
                           CONSTRAINT `FKq9h55ow2txbwvjys7k3i8dsrj` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `segment_lookup_task`;

CREATE TABLE `segment_lookup_task` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `create_time` datetime NOT NULL,
                                     `update_time` datetime NOT NULL,
                                     `audience_id` bigint(20) NOT NULL DEFAULT '0',
                                     `active` int(11) NOT NULL DEFAULT '0',
                                     `task_heartbeat` bigint(20) NOT NULL DEFAULT '0',
                                     `audience_name` varchar(255) NOT NULL DEFAULT '',
                                     `audience_type` int(11) NOT NULL DEFAULT '0',
                                     `acp_be_job_id` bigint(20) NOT NULL DEFAULT '0',
                                     `email_address` varchar(255) NOT NULL DEFAULT '',
                                     `user_name` varchar(255) NOT NULL DEFAULT '',
                                     `email_url` varchar(255) NOT NULL DEFAULT '',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `system_param`;

CREATE TABLE `system_param` (
                              `job_key` varchar(255) NOT NULL,
                              `last_run_time` date NOT NULL,
                              PRIMARY KEY (`job_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `taxonomy_sharing_record`;

CREATE TABLE `taxonomy_sharing_record` (
                                         `ID` int(11) NOT NULL AUTO_INCREMENT,
                                         `SHARING_TREE_ID` int(11) NOT NULL DEFAULT '0',
                                         `SHARING_TENANT` varchar(50) NOT NULL,
                                         `SHARING_OWNER_TENANT` varchar(50) NOT NULL,
                                         `SHARING_TARGET` varchar(50) NOT NULL,
                                         `SHARING_TYPE` varchar(50) DEFAULT NULL,
                                         `SHARING_TAXONOMY` varchar(100) NOT NULL,
                                         `SHARING_START_DATE` date NOT NULL,
                                         `SHARING_END_DATE` date NOT NULL,
                                         `SHARING_ACTIVE` tinyint(2) NOT NULL COMMENT '0,inactive;1,active',
                                         `SHARING_UUID` varchar(100) NOT NULL,
                                         PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tenant_channel`;

CREATE TABLE `tenant_channel` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `created_by` varchar(255) NOT NULL,
                                `created_time` datetime NOT NULL,
                                `is_deleted` tinyint(4) DEFAULT '0',
                                `update_time` datetime DEFAULT NULL,
                                `channel_sftp_host` varchar(255) NOT NULL,
                                `channel_sftp_key_file` varchar(255) DEFAULT NULL,
                                `channel_sftp_passphrase` varchar(255) DEFAULT NULL,
                                `channel_sftp_password` varchar(255) DEFAULT NULL,
                                `channel_sftp_path` varchar(255) NOT NULL,
                                `channel_sftp_port` int(11) NOT NULL,
                                `channel_sftp_username` varchar(255) NOT NULL,
                                `channel_id` bigint(20) NOT NULL,
                                `tenant_id` bigint(20) NOT NULL,
                                `channel_type` varchar(255) DEFAULT NULL,
                                `channel_connect_key` varchar(255) DEFAULT NULL,
                                `publisher_id` longtext,
                                PRIMARY KEY (`id`),
                                KEY `FKhyfet4wb3rmi8amfg62djsdr3` (`channel_id`),
                                KEY `FKm2nvla022i8q59dqwua6205lo` (`tenant_id`),
                                CONSTRAINT `FKhyfet4wb3rmi8amfg62djsdr3` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`),
                                CONSTRAINT `FKm2nvla022i8q59dqwua6205lo` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `universe`;

CREATE TABLE `universe` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `universe_name` varchar(255) NOT NULL,
                          `universe_system_name` varchar(255) NOT NULL,
                          `tenant_id` bigint(20) NOT NULL,
                          `tenant_path` varchar(255) NOT NULL,
                          `universe_count` bigint(20) NOT NULL,
                          `universe_job_id` varchar(255) DEFAULT NULL,
                          `universe_threshold` bigint(20) DEFAULT NULL,
                          `universe_rule_json` longtext,
                          `universe_status` varchar(255) NOT NULL,
                          `universe_type` varchar(255) NOT NULL,
                          `is_deleted` tinyint(4) NOT NULL,
                          `created_time` datetime NOT NULL,
                          `update_time` datetime NOT NULL,
                          `created_by` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `un_name_path` (`universe_system_name`,`tenant_path`),
                          KEY `tenant_id` (`tenant_id`),
                          CONSTRAINT `universe_ibfk_1` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `version`;

CREATE TABLE `version` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `version_name` varchar(255) DEFAULT NULL,
                         `version_operation_flag` int(11) DEFAULT '0',
                         `version_tree_id` varchar(255) DEFAULT NULL,
                         `tenant_id` bigint(20) NOT NULL,
                         `update_time` datetime DEFAULT NULL,
                         `created_time` datetime NOT NULL,
                         `created_by` varchar(255) NOT NULL,
                         `is_deleted` tinyint(4) DEFAULT '0',
                         `max_depth` int(11) DEFAULT '0',
                         `node_number` int(11) DEFAULT '0',
                         `updated_by` varchar(50) DEFAULT NULL,
                         `sync_flag` bit(1) DEFAULT b'0',
                         `datasource_id` varchar(255) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FKfhdsjks8ak4vxneguchqtrslb` (`tenant_id`),
                         CONSTRAINT `FKfhdsjks8ak4vxneguchqtrslb` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tenant_universe`;

CREATE TABLE `tenant_universe` (
                                 `username` varchar(200) NOT NULL DEFAULT '',
                                 `tenant_id` bigint(20) NOT NULL,
                                 `universe_id` bigint(20) NOT NULL,
                                 PRIMARY KEY (`username`,`tenant_id`),
                                 KEY `tenant_id` (`tenant_id`),
                                 KEY `universe_id` (`universe_id`),
                                 CONSTRAINT `tenant_universe_ibfk_1` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`),
                                 CONSTRAINT `tenant_universe_ibfk_2` FOREIGN KEY (`universe_id`) REFERENCES `universe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- ----------------------------
-- Procedure structure for tempProcedure_Test
-- ----------------------------
DROP PROCEDURE IF EXISTS `tempProcedure_Test`;
DELIMITER ;;
CREATE PROCEDURE `tempProcedure_Test`()
BEGIN
declare isDone int default 0;
declare intNumber int default 1;
declare tempId char(36);

declare historyIds cursor for select id from history;
declare continue handler for not FOUND set isDone = 1;
open historyIds;

REPEAT
fetch historyIds into tempId;
if not isDone THEN
set @orderId = (select order_id from history where id = tempId);
set @maxRecords = (select max_records from history where order_id = @orderId ORDER BY max_records desc LIMIT 1);
update history set records = @maxRecords where id = tempId;
set intNumber=intNumber+1;
end if;
until isDone end repeat;
close historyIds;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for getChildList
-- ----------------------------
DROP FUNCTION IF EXISTS `getChildList`;
DELIMITER ;;
CREATE FUNCTION `getChildList`(rootId long) RETURNS varchar(1000) CHARSET utf8
BEGIN
DECLARE sTemp VARCHAR(1000);
DECLARE sTempChd VARCHAR(1000);

SET sTemp = '$';
SET sTempChd =cast(rootId as CHAR);

WHILE sTempChd is not null DO
SET sTemp = concat(sTemp,',',sTempChd);
SELECT group_concat(id) INTO sTempChd FROM folder where FIND_IN_SET
                                                          (folder_parent_id,sTempChd)>0;
END WHILE;
RETURN sTemp;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for getParentList
-- ----------------------------
DROP FUNCTION IF EXISTS `getParentList`;
DELIMITER ;;
CREATE FUNCTION `getParentList`(rootId VARCHAR(1000)) RETURNS varchar(10000) CHARSET utf8
BEGIN
DECLARE sParentList varchar(10000);
DECLARE sParentTempList varchar(10000);
DECLARE sParentTemp varchar(10000);
DECLARE sIndex int;
DECLARE vId int;
DECLARE flag int;
while ifnull(rootId,'') <> '' do
select LOCATE(',',rootId) into sIndex from dual;
if sIndex = 0 then
select left(rootId,length(rootId)) into vId from dual;
else
select left(rootId,sIndex-1) into vId from dual;
end if;
SET sParentTemp =cast(vId as CHAR);
WHILE sParentTemp is not null DO
IF (sParentTempList is not null) THEN
SET sParentTempList = concat(sParentTemp,',',sParentTempList);
ELSE
SET sParentTempList = concat(sParentTemp);
END IF;
SELECT group_concat(folder_parent_id) INTO sParentTemp FROM folder where FIND_IN_SET(id,sParentTemp)>0;
END WHILE;
IF (sParentList is not null) THEN
SET sParentList = concat(sParentList,'|', sParentTempList);
ELSE
SET sParentList = concat(sParentTempList);
END IF;
select right(rootId,length(rootId)-length(vId)-1) into rootId from dual;
SET sParentTempList = NULL;
END WHILE;
RETURN sParentList;
END
;;
DELIMITER ;

-- 3 init folder
INSERT INTO `folder`(id,created_by,created_time,is_deleted,update_time,folder_name,folder_type,folder_parent_id,tenant_id) VALUES ('1', 'super', '2017-12-06 14:41:14', 0, '2017-12-06 14:41:17', 'Saved Segments', 'SAVED_SEGMENT', '0', null);
INSERT INTO `folder`(id,created_by,created_time,is_deleted,update_time,folder_name,folder_type,folder_parent_id,tenant_id) VALUES ('2', 'super', '2017-12-06 14:41:14', 0, '2017-12-06 14:41:17', 'Lookalike Group', 'LOOKALIKE_GROUP', '0', null);
INSERT INTO `folder`(id,created_by,created_time,is_deleted,update_time,folder_name,folder_type,folder_parent_id,tenant_id) VALUES ('3', 'super', '2017-12-06 14:41:14', 0, '2017-12-06 14:41:17', 'Campaign', 'CAMPAIGN', '0', null);
ALTER TABLE `folder` AUTO_INCREMENT=10;