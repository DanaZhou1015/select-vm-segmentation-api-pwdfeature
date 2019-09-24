-- 4. data move from sadb to acp
USE at;
insert into acp.tenant(id, tenant_name, tenant_id,tenant_path,created_by, created_time)
SELECT client.CLT_ID as id,client.CLT_NAME as tenant_name,client.CLT_CODE as tenant_id,client.CLT_PATH as tenant_path,client.CLT_NAME,NOW()
from client;

USE at;
INSERT INTO acp.version(id, tenant_id, version_name, created_by,version_operation_flag,version_tree_id,created_time,update_time)
select vision.VIS_ID,vision.VIS_CLT_ID,vision.VIS_NAME,user.USR_LOGIN_NAME, vision.VIS_OPERATION_FLAG,vision.VIS_TREE_ID,vision.VIS_CREATE_DT,vision.VIS_UPDATE_DT
from vision LEFT JOIN user
ON vision.VIS_USR_ID=user.USR_ID
WHERE vision.VIS_CLT_ID!=0;

USE at;
INSERT INTO acp.audience_distribute_job(id,created_by,created_time,is_deleted,update_time,distribute_job_audience_id,distribute_job_notice_email,distribute_job_status,distribute_job_tenant_id,distribute_job_update_by)
SELECT job.ADJ_ID,IFNULL(job.ADJ_CREATION_BY, 'amsdemo'),job.ADJ_CREATION_DT, 0,job.ADJ_UPDATE_DT,job.ADJ_AUD_ID,job.ADJ_NOTICE_EMAIL,'SEGMENT_DISTRIBUTED',job.ADJ_CLT_ID,job.ADJ_UPDATE_BY
FROM audience_download_job job;

use at;
INSERT INTO acp.audience_count(id,created_by, created_time, audience_count_count, audience_count_taxonomy_id, tenant_id)
SELECT CNT_ID,CNT_CREATION_BY,CNT_CREATION_DT,CNT_COUNT,CNT_TAXONOMY_ID,CNT_CLT_ID
from audience_count WHERE CNT_CLT_ID!=0;