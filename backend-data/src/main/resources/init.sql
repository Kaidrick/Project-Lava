create table if not exists lava_system_log
(
    id bigint not null auto_increment,
    time varchar(255) not null,
    message varchar(255) not null,
    source varchar(255) not null,
    level varchar(64) not null,
    meta_id bigint,
    primary key(id)
);