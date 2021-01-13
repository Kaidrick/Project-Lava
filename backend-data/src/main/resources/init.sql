-- create default player role table
create table if not exists player_role
(
    id bigint not null auto_increment,
    role_level int(11) not null,
    role_name varchar(255) not null,
    pid int(11) comment 'parent role group id',
    primary key(id)
);

-- populate player role table
insert into player_role (role_level, role_name)
values (1000, 'motd'), (1001, 'motd_recv'), (1002, 'motd_add'), (1003, 'motd_delete'),
       (300, 'send_chat'), (301, 'send_coalition_chat'), (302, 'send_all_chat'),
       (3100, 'chat_command'), (3101, '');


-- create default role group table
create table if not exists role_group
(
    id bigint not null auto_increment,
    role_group_name varchar(255) not null
);

-- populate default role group
insert into role_group (role_group_name)
values ('guest'), ('user'), ('admin'), ('su');


-- create relation table between role group and player role
create table if not exists role_group_role_assignment
(
    id bigint not null auto_increment,
    role_group_id bigint not null,
    player_role_id bigint not null
);

-- default values for guest group
insert into role_group_role_assignment (role_group_id, player_role_id)
values (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7);



create table if not exists role_assignment
(
    id bigint not null auto_increment,
    ucid varchar(255) not null,
    role_id bigint not null,
    time timestamp not null,
    primary key(id)
);

create table if not exists lava_system_log
(
    id bigint not null auto_increment,
    time timestamp not null,
    message varchar(255) not null,
    source varchar(255) not null,
    level varchar(64) not null,
    meta_id bigint,
    primary key(id)
);

DROP TABLE IF EXISTS export_object;
CREATE TABLE export_object  (
  runtime_id bigint(20) UNSIGNED NOT NULL,
  own_bank double NULL DEFAULT NULL,
  coalition varchar(255) NULL DEFAULT NULL,
coalition_id int(11) NULL DEFAULT NULL,
  country_id int(11) NULL DEFAULT NULL,
  group_name varchar(255) NULL DEFAULT NULL,
  own_heading double NULL DEFAULT NULL,
  own_name varchar(255) NULL DEFAULT NULL,
  own_pitch double NULL DEFAULT NULL,
  unit_name varchar(255) NULL DEFAULT NULL,
  geo_altitude double NOT NULL,
  geo_latitude double NOT NULL,
  geo_longitude double NOT NULL,
  vector_x double NOT NULL,
  vector_y double NOT NULL,
  vector_z double NOT NULL,

  PRIMARY KEY (runtime_id)
);

DROP TABLE IF EXISTS player_info;
CREATE TABLE player_info  (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  ipaddr varchar(255) NULL DEFAULT NULL,
  lang varchar(255) NULL DEFAULT NULL,
  name varchar(255) NULL DEFAULT NULL,
  net_id int(11) NOT NULL,
  pilot_id bigint(20) NOT NULL,
  ping int(11) NOT NULL,
  side int(11) NOT NULL,
  slot varchar(255) NULL DEFAULT NULL,
  started bit(1) NOT NULL,
  ucid varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (id)
);