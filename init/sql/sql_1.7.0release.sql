alter table channel add column channel_infobase_id BIGINT(20) default NULL;

alter table profiling DELETE column profiling_describtion;