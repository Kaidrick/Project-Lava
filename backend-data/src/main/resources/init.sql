create table if not exists player_role
(
    id bigint not null auto_increment,
    role_level int(11) not null,
    role_name varchar(255) not null,
    primary key(id)
);

insert into player_role (role_level, role_name)
values (1000, 'motd'), (1001, 'motd_recv'), (1002, 'motd_add'), (1003, 'motd_delete');

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