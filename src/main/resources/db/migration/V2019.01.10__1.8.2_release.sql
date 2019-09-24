alter table universe modify column universe_threshold Float(4,2) default -1;
update universe set universe_threshold = -1 where universe_threshold is null;
alter table audience add column legal_flag TINYINT(4) default 1;