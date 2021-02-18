SET MODE MySQL;

-- frontend navigation menu
create table if not exists nav_menu
(
    id      bigint       not null auto_increment,
    name    varchar(255) not null,
    path    varchar(255),
    pid     int(11) comment 'parent menu id',
    leaf    bit(1)       not null,
    ident   varchar(32)  not null,
    ordinal int(11)      not null,
    primary key (id)
);

-- default navigational menu configuration
INSERT INTO nav_menu(ID, NAME, PATH, PID, LEAF, IDENT, ORDINAL)
SELECT * FROM (
         SELECT 1 as id, 'System', null, 0, false, '4970e4917f1648ada723cf79e2416dc2', 1 UNION ALL
         SELECT 2 as id, 'GUI', null, 1, false, '45f1da18c8404c7bb1b622091fdddfcc', 0 UNION ALL
         SELECT 3 as id, 'Platform', null, 1, false, '0c0a8bb4d5d643b18659fd79a9b704c0', 1 UNION ALL
         SELECT 4 as id, 'Nav Menus', '/config/nav_menu', 2, true, '50f789b90ea24b309566634c5ed2f9d5', 0 UNION ALL
         SELECT 5 as id, 'Preference', '/config', 3, true, '33742c3bd69a452abc3802d22785986a', 0 UNION ALL
         SELECT 6 as id, 'Utilities', null, 0, false, '929c74dab0de4a55a949f23baf8dfbfa', 2 UNION ALL
         SELECT 7 as id, 'Atlas Map (Test)', '/atlas', 6, true, 'a9ec4118cc3e4fffbdb2fef593ec2d17', 0 UNION ALL
         SELECT 8 as id, 'Dashboard', '/', 0, true, 'f8876fd3dc964e6995020aa10f521248', 0 UNION ALL
         SELECT 9 as id, 'Lua Debugger', '/bingo/debugger', 6, true, 'cef9cf5b2a7145f9a58430dda2bc6b19', 1 UNION ALL
         SELECT 10 as id, 'Addons', '/addons', 0, true, 'd3e5f989283b4278927f8a427d7f19ca', 3 UNION ALL
         SELECT 11 as id, 'About', '/about', 0, true, 'f439d8f5e0234ab3bc874bc0662141a4', 4
) dual
WHERE NOT EXISTS (SELECT 1 FROM nav_menu);

-- create default player role table
create table if not exists player_role
(
    id         bigint       not null auto_increment,
    role_level int(11)      not null,
    role_name  varchar(255) not null,
    pid        int(11) comment 'parent role group id',
    primary key (id)
);

-- populate player role table
insert into player_role (id, role_level, role_name)
select *
from (
         select 1 as id, 1000, 'motd'
         union all
         select 2 as id, 1001, 'motd_recv'
         union all
         select 3 as id, 1002, 'motd_add'
         union all
         select 4 as id, 1003, 'motd_delete'
         union all
         select 5 as id, 300, 'send_chat'
         union all
         select 6 as id, 301, 'send_coalition_chat'
         union all
         select 7 as id, 302, 'send_all_chat'
         union all
         select 8 as id, 3100, 'chat_command'
         union all
         select 9 as id, 3101, ''
     ) dual
where not exists(select 1 from player_role);

-- create default role group table
create table if not exists player_role_group
(
    id              bigint       not null auto_increment,
    role_group_name varchar(255) not null,
    primary key (id)
);

-- populate default role group
insert into player_role_group (id, role_group_name)
select *
from (
         select 1 as id, 'guest' union all
         select 2 as id, 'user' union all
         select 3 as id, 'admin' union all
         select 4 as id, 'su'
     ) dual
where not exists(select 1 from player_role_group);

-- create relation table between role group and player role
create table if not exists role_group_role_assignment
(
    id             bigint not null auto_increment,
    role_group_id  bigint not null,
    player_role_id bigint not null,
    primary key (id)
);

-- default values for guest group
insert into role_group_role_assignment (id, role_group_id, player_role_id)
select *
from (
         select 1 as id, 1 as role_group_id, 1 as player_role_id
         union all
         select 2 as id, 1 as role_group_id, 2 as player_role_id
         union all
         select 3 as id, 1 as role_group_id, 3 as player_role_id
         union all
         select 4 as id, 1 as role_group_id, 4 as player_role_id
         union all
         select 5 as id, 1 as role_group_id, 5 as player_role_id
         union all
         select 6 as id, 1 as role_group_id, 6 as player_role_id
         union all
         select 7 as id, 1 as role_group_id, 7 as player_role_id
     ) dual
where not exists(select 1 from role_group_role_assignment);


create table if not exists role_assignment
(
    id      bigint       not null auto_increment,
    ucid    varchar(255) not null,
    role_id bigint       not null,
    time    timestamp    not null,
    primary key (id)
);

create table if not exists motd_message (
    id bigint not null auto_increment,
    index int not null,
    duration int,
    content varchar(255) not null,
    message_set bigint,

    primary key (id)
);

create table if not exists motd_message_set (
    id bigint not null auto_increment,
    name varchar(255) not null,
    create_time timestamp,
    last_edit_time timestamp,
    language varchar(100),

    primary key (id)
);

create table if not exists lava_system_log
(
    id      bigint       not null auto_increment,
    time    timestamp    not null,
    message varchar(255) not null,
    source  varchar(255) not null,
    level   varchar(64)  not null,
    meta_id bigint,
    primary key (id)
);

DROP TABLE IF EXISTS export_object;
CREATE TABLE export_object
(
    runtime_id    bigint(20)        UNSIGNED NOT NULL,
    own_bank      double                     NULL DEFAULT NULL,
    coalition     varchar(255)               NULL DEFAULT NULL,
    coalition_id  int(11)                    NULL DEFAULT NULL,
    country_id    int(11)                    NULL DEFAULT NULL,
    group_name    varchar(255)               NULL DEFAULT NULL,
    own_heading   double                     NULL DEFAULT NULL,
    own_name      varchar(255)               NULL DEFAULT NULL,
    own_pitch     double                     NULL DEFAULT NULL,
    unit_name     varchar(255)               NULL DEFAULT NULL,
    geo_altitude  double                     NOT NULL,
    geo_latitude  double                     NOT NULL,
    geo_longitude double                     NOT NULL,
    vector_x      double                     NOT NULL,
    vector_y      double                     NOT NULL,
    vector_z      double                     NOT NULL,

    PRIMARY KEY (runtime_id)
);

DROP TABLE IF EXISTS player_info;
CREATE TABLE player_info
(
    id       bigint(20)   NOT NULL AUTO_INCREMENT,
    ipaddr   varchar(255) NULL DEFAULT NULL,
    lang     varchar(255) NULL DEFAULT NULL,
    name     varchar(255) NULL DEFAULT NULL,
    net_id   int(11)      NOT NULL,
    pilot_id bigint(20)   NOT NULL,
    ping     int(11)      NOT NULL,
    side     int(11)      NOT NULL,
    slot     varchar(255) NULL DEFAULT NULL,
    started  bit(1)       NOT NULL,
    ucid     varchar(255) NULL DEFAULT NULL,
    PRIMARY KEY (id)
);