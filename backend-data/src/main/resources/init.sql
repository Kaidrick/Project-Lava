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
-- insert into if (not exists(select 1 from nav_menu)) NAV_MENU (ID, NAME, PATH, PID, LEAF, IDENT, ORDINAL)
insert into NAV_MENU (ID, NAME, PATH, PID, LEAF, IDENT, ORDINAL)
values (1, 'System', null, 0, false, '4970e4917f1648ada723cf79e2416dc2', 1),
       (2, 'GUI', null, 1, false, '45f1da18c8404c7bb1b622091fdddfcc', 0),
       (3, 'Platform', null, 1, false, '0c0a8bb4d5d643b18659fd79a9b704c0', 1),
       (4, 'Nav Menus', '/config/nav_menu', 2, true, '50f789b90ea24b309566634c5ed2f9d5', 0),
       (5, 'Preference', '/config', 3, true, '33742c3bd69a452abc3802d22785986a', 0),
       (6, 'Utilities', null, 0, false, '929c74dab0de4a55a949f23baf8dfbfa', 2),
       (7, 'Atlas Map (Test)', '/atlas', 6, true, 'a9ec4118cc3e4fffbdb2fef593ec2d17', 0),
       (8, 'Dashboard', '/', 0, true, 'f8876fd3dc964e6995020aa10f521248', 0),
       (9, 'Lua Debugger', '/bingo/debugger', 6, true, 'cef9cf5b2a7145f9a58430dda2bc6b19', 1),
       (10, 'Addons', '/addons', 0, true, 'd3e5f989283b4278927f8a427d7f19ca', 3),
       (11, 'About', '/about', 0, true, 'f439d8f5e0234ab3bc874bc0662141a4', 4);


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
insert into player_role (role_level, role_name)
values (1000, 'motd'),
       (1001, 'motd_recv'),
       (1002, 'motd_add'),
       (1003, 'motd_delete'),
       (300, 'send_chat'),
       (301, 'send_coalition_chat'),
       (302, 'send_all_chat'),
       (3100, 'chat_command'),
       (3101, '');


-- create default role group table
create table if not exists player_role_group
(
    id              bigint       not null auto_increment,
    role_group_name varchar(255) not null,
    primary key (id)
);

-- populate default role group
insert into player_role_group (role_group_name)
values ('guest'),
       ('user'),
       ('admin'),
       ('su');


-- create relation table between role group and player role
create table if not exists role_group_role_assignment
(
    id             bigint not null auto_increment,
    role_group_id  bigint not null,
    player_role_id bigint not null,
    primary key (id)
);

-- default values for guest group
insert into role_group_role_assignment (role_group_id, player_role_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7);



create table if not exists role_assignment
(
    id      bigint       not null auto_increment,
    ucid    varchar(255) not null,
    role_id bigint       not null,
    time    timestamp    not null,
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