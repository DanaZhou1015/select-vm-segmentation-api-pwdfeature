SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for audience
-- ----------------------------
DROP TABLE IF EXISTS `audience`;
CREATE TABLE `audience` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `audience_name` varchar(255) DEFAULT NULL,
  `audience_code` varchar(255) DEFAULT NULL,
  `audience_description` varchar(255) DEFAULT NULL,
  `audience_type` varchar(255) DEFAULT NULL,
  `audience_count` bigint(20) DEFAULT NULL,
  `audience_cost` varchar(255) DEFAULT NULL,
  `audience_status` varchar(255) DEFAULT NULL,
  `audience_rule_json` longtext,
  `audience_rule_json_display` longtext,
  `folder_id` bigint(20) NOT NULL,
  `tenant_id` bigint(20) DEFAULT NULL,
  `destination_id` bigint(20) DEFAULT NULL,
  `taxonomy_id` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK202twfenew164ngxh53a2htap` (`folder_id`),
  CONSTRAINT `FK202twfenew164ngxh53a2htap` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for audience_count
-- ----------------------------
DROP TABLE IF EXISTS `audience_count`;
CREATE TABLE `audience_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `audience_count_count` int(11) DEFAULT NULL,
  `tenant_id` bigint(20) NOT NULL,
  `audience_count_taxonomy_id` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa6ad4sn5722jdj97oa2qxdvg6` (`tenant_id`),
  CONSTRAINT `FKa6ad4sn5722jdj97oa2qxdvg6` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for audience_distribute_job
-- ----------------------------
DROP TABLE IF EXISTS `audience_distribute_job`;
CREATE TABLE `audience_distribute_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `distribute_job_audience_id` bigint(20) NOT NULL,
  `distribute_job_tenant_id` bigint(20) NOT NULL,
  `distribute_job_status` varchar(255) NOT NULL,
  `distribute_job_notice_email` varchar(255) NOT NULL,
  `distribute_job_update_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `update_time` datetime DEFAULT NULL,
  `created_time` datetime NOT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for channel
-- ----------------------------
DROP TABLE IF EXISTS `channel`;
CREATE TABLE `channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `channel_name` varchar(255) DEFAULT NULL,
  `channel_tenant_id` bigint(20) DEFAULT NULL,
  `channel_infobase_id` bigint(20) DEFAULT NULL,
  `channel_rules` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `channel_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for folder
-- ----------------------------
DROP TABLE IF EXISTS `folder`;
CREATE TABLE `folder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `folder_name` varchar(255) DEFAULT NULL,
  `folder_type` varchar(255) DEFAULT NULL,
  `folder_parent_id` bigint(20) DEFAULT NULL,
  `tenant_id` bigint(20) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for profiling
-- ----------------------------
DROP TABLE IF EXISTS `profiling`;
CREATE TABLE `profiling` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `profiling_name` varchar(255) DEFAULT NULL,
  `profiling_description` varchar(255) DEFAULT NULL,
  `profiling_json` text,
  `tenant_id` bigint(20) NOT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq9h55ow2txbwvjys7k3i8dsrj` (`tenant_id`) USING BTREE,
  CONSTRAINT `FKq9h55ow2txbwvjys7k3i8dsrj` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`),
  CONSTRAINT `profiling_ibfk_1` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` varchar(255) DEFAULT NULL,
  `tenant_name` varchar(255) DEFAULT NULL,
  `tenant_path` varchar(255) DEFAULT NULL,
  `tenant_count_limit` bigint(20) DEFAULT '250',
  `ai_tenant` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tenant_channel
-- ----------------------------
DROP TABLE IF EXISTS `tenant_channel`;
CREATE TABLE `tenant_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) NOT NULL,
  `channel_id` bigint(20) NOT NULL,
  `channel_type` varchar(255) DEFAULT NULL,
  `channel_sftp_path` varchar(255) NOT NULL,
  `channel_sftp_port` int(11) NOT NULL,
  `channel_sftp_username` varchar(255) DEFAULT NULL,
  `channel_sftp_password` varchar(255) DEFAULT NULL,
  `channel_sftp_host` varchar(255) NOT NULL,
  `channel_sftp_key_file` varchar(255) DEFAULT NULL,
  `channel_sftp_passphrase` varchar(255) DEFAULT NULL,
  `channel_connect_key` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhyfet4wb3rmi8amfg62djsdr3` (`channel_id`),
  KEY `FKm2nvla022i8q59dqwua6205lo` (`tenant_id`),
  CONSTRAINT `FKhyfet4wb3rmi8amfg62djsdr3` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`),
  CONSTRAINT `FKm2nvla022i8q59dqwua6205lo` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for version
-- ----------------------------
DROP TABLE IF EXISTS `version`;
CREATE TABLE `version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version_name` varchar(255) DEFAULT NULL,
  `version_tree_id` varchar(255) DEFAULT NULL,
  `version_operation_flag` bit(1) DEFAULT NULL,
  `tenant_id` bigint(20) NOT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfhdsjks8ak4vxneguchqtrslb` (`tenant_id`),
  CONSTRAINT `FKfhdsjks8ak4vxneguchqtrslb` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
INSERT INTO `folder`(id,created_by,created_time,is_deleted,update_time,folder_name,folder_type,folder_parent_id,tenant_id) VALUES ('1', 'super', '2017-12-06 14:41:14', '\0', '2017-12-06 14:41:17', 'Saved Segments', 'SAVED_SEGMENT', '0', null);
INSERT INTO `folder`(id,created_by,created_time,is_deleted,update_time,folder_name,folder_type,folder_parent_id,tenant_id) VALUES ('2', 'super', '2017-12-06 14:41:14', '\0', '2017-12-06 14:41:17', 'Lookalike Group', 'LOOKALIKE_GROUP', '0', null);
INSERT INTO `folder`(id,created_by,created_time,is_deleted,update_time,folder_name,folder_type,folder_parent_id,tenant_id) VALUES ('3', 'super', '2017-12-06 14:41:14', '\0', '2017-12-06 14:41:17', 'Campaign', 'CAMPAIGN', '0', null);
ALTER TABLE `folder` AUTO_INCREMENT=10;