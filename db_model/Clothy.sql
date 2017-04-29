CREATE DATABASE IF NOT EXISTS Clothy
CHARACTER SET utf8
COLLATE utf8_general_ci;

USE Clothy;

CREATE TABLE IF NOT EXISTS users (
	user_id bigint(11) NOT NULL AUTO_INCREMENT,
    user_name varchar(255) NOT NULL,
    user_surname varchar(255) DEFAULT '',
    user_address text,
    user_email varchar(255) NOT NULL,
    user_phone varchar(20) NOT NULL,
    user_login_name varchar(255) NOT NULL,
    user_login_password varchar(255) NOT NULL,
    user_login_type varchar(20) NOT NULL DEFAULT 'TYPE_USER',
	CONSTRAINT pk_users PRIMARY KEY (user_id)
);