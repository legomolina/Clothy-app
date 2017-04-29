CREATE DATABASE IF NOT EXISTS Clothy
CHARACTER SET utf8
COLLATE utf8_general_ci;

USE Clothy;

CREATE TABLE IF NOT EXISTS employees (
	employee_id bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    employee_name varchar(255) NOT NULL,
    employee_surname varchar(255) DEFAULT '',
    employee_address text,
    employee_email varchar(255) NOT NULL,
    employee_phone varchar(20) NOT NULL,
    employee_login_name varchar(255) NOT NULL,
    employee_login_password varchar(255) NOT NULL,
    employee_login_type varchar(20) NOT NULL DEFAULT 'TYPE_USER',
	CONSTRAINT pk_employee PRIMARY KEY (employee_id)
);