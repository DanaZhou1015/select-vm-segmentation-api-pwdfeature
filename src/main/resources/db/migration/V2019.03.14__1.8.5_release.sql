ALTER TABLE `universe` ADD COLUMN `owner_tenant_path` varchar(255) NOT NULL;
ALTER TABLE `universe` drop index `un_name_path`;
ALTER TABLE `universe` add unique key `un_name_path` (`universe_system_name`,`tenant_path`,`owner_tenant_path`);
ALTER TABLE `audience` ADD COLUMN `error_code` varchar(255) DEFAULT NULL;
UPDATE `universe` SET `owner_tenant_path` = `tenant_path`;