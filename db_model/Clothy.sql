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
    employee_is_active tinyint(3) NOT NULL DEFAULT 1,
	CONSTRAINT pk_employee PRIMARY KEY (employee_id)
);

CREATE TABLE IF NOT EXISTS clients (
	client_id bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    client_nif varchar(20) NOT NULL,
    client_name varchar(255) NOT NULL,
    client_surname varchar(255) DEFAULT '',
    client_address text,
    client_email varchar(255),
    client_phone varchar(20),
    CONSTRAINT pk_clients PRIMARY KEY (client_id)
);

CREATE TABLE IF NOT EXISTS sizes (
	size_id int(5) NOT NULL,
    size_value varchar(20) NOT NULL,
    CONSTRAINT pk_sizes PRIMARY KEY (size_id)
);

CREATE TABLE IF NOT EXISTS brands (
	brand_id bigint(11) UNSIGNED NOT NULL,
    brand_name varchar(255) NOT NULL,
    CONSTRAINT pk_brands PRIMARY KEY (brand_id)
);

CREATE TABLE IF NOT EXISTS categories (
	category_id bigint(11) UNSIGNED NOT NULL,
    category_name varchar(255) NOT NULL,
    category_description text,
    CONSTRAINT pk_categories PRIMARY KEY (category_id)
);

CREATE TABLE IF NOT EXISTS articles (
	article_id bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    article_code varchar(255) NOT NULL DEFAULT '',
    article_name varchar(255) NOT NULL,
    article_description text,
    article_price float(8,3) NOT NULL,
    article_category bigint(11) UNSIGNED,
    article_brand bigint(11) UNSIGNED,
    article_stock int(11) DEFAULT 0,
    CONSTRAINT pk_articles PRIMARY KEY (article_id),
    CONSTRAINT fk_articles_brands FOREIGN KEY (article_brand) REFERENCES brands(brand_id),
    CONSTRAINT fk_articles_categories FOREIGN KEY (article_category) REFERENCES categories(category_id)
);

CREATE TABLE IF NOT EXISTS sizes_articles_map (
	size_id int(5) NOT NULL,
    article_id bigint(11) UNSIGNED NOT NULL,
    CONSTRAINT pk_sizes_employees_map PRIMARY KEY (size_id, article_id),
    CONSTRAINT fk_sizes_employees_map_sizes FOREIGN KEY (size_id) REFERENCES sizes(size_id),
    CONSTRAINT fk_sizes_employees_map_articles FOREIGN KEY (article_id) REFERENCES articles(article_id)
);

CREATE TABLE IF NOT EXISTS sales (
	sale_id bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    sale_client bigint(11) UNSIGNED NOT NULL,
    sale_employee bigint(11) UNSIGNED NOT NULL,
    sale_date datetime,
    sale_payment varchar(20) NOT NULL DEFAULT 'TYPE_CASH',
    CONSTRAINT pk_sales PRIMARY KEY (sale_id),
    CONSTRAINT fk_sales_clients FOREIGN KEY (sale_client) REFERENCES clients(client_id),
    CONSTRAINT fk_sales_employees FOREIGN KEY (sale_employee) REFERENCES employees(employee_id)
);

CREATE TABLE IF NOT EXISTS sale_lines (
	sale_line_id bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    sale_line_article bigint(11) UNSIGNED,
    sale_line_sale bigint(11) UNSIGNED NOT NULL,
    sale_line_discount float(8,3) DEFAULT '0.0',
    sale_line_quantity int(3) DEFAULT 1,
    CONSTRAINT pk_sale_lines PRIMARY KEY (sale_line_id, sale_line_sale),
    CONSTRAINT fk_sale_lines_articles FOREIGN KEY (sale_line_article) REFERENCES articles(article_id),
    CONSTRAINT fk_sale_lines_sales FOREIGN KEY (sale_line_sale) REFERENCES sales(sale_id)
);

CREATE TABLE IF NOT EXISTS orders (
	order_id bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    order_date datetime,
    order_description text,
    order_employee bigint(11) UNSIGNED,
    order_status varchar(20) NOT NULL DEFAULT 'STATUS_DRAFT',
    CONSTRAINT pk_orders PRIMARY KEY (order_id),
    CONSTRAINT fk_orders_employees FOREIGN KEY (order_employee) REFERENCES employees(employee_id)
);
