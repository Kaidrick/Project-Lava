create table lava_system_log
(
    id integer not null,
    name varchar(255) not null,
    message varchar(255) not null,
    source_name varchar(255) not null,
    level varchar(64) not null,
    primary key(id)
);