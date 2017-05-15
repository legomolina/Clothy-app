DROP TABLE IF EXISTS articles;
CREATE TABLE articles (
    article_id bigint(11) unsigned NOT NULL,
    article_code varchar(255) NOT NULL DEFAULT '',
    article_name varchar(255) NOT NULL,
    article_description text,
    article_category bigint(11) unsigned DEFAULT NULL,
    article_brand bigint(11) unsigned DEFAULT NULL,
    article_price float(8,3) NOT NULL,
    PRIMARY KEY (article_id),
    KEY fk_articles_brands (article_brand),
    KEY fk_articles_categories (article_category),
    CONSTRAINT fk_articles_brands FOREIGN KEY (article_brand) REFERENCES brands (brand_id),
    CONSTRAINT fk_articles_categories FOREIGN KEY (article_category) REFERENCES categories (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS brands;
CREATE TABLE brands (
    brand_id bigint(11) unsigned NOT NULL,
    brand_name varchar(255) NOT NULL,
    brand_address text,
    brand_email varchar(255) NOT NULL,
    brand_phone varchar(20) NOT NULL,
    PRIMARY KEY (brand_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    category_id bigint(11) unsigned NOT NULL,
    category_name varchar(255) NOT NULL,
    category_description text,
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS clients;
CREATE TABLE clients (
    client_id bigint(11) unsigned NOT NULL,
    client_nif varchar(20) NOT NULL,
    client_name varchar(255) NOT NULL,
    client_surname varchar(255) DEFAULT '',
    client_address text,
    client_email varchar(255) DEFAULT NULL,
    client_phone varchar(20) DEFAULT NULL,
    PRIMARY KEY (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS employees;
CREATE TABLE employees (
    employee_id bigint(11) unsigned NOT NULL,
    employee_name varchar(255) NOT NULL,
    employee_surname varchar(255) DEFAULT '',
    employee_address text,
    employee_email varchar(255) NOT NULL,
    employee_phone varchar(20) NOT NULL,
    employee_login_name varchar(255) NOT NULL,
    employee_login_password varchar(255) NOT NULL,
    employee_login_type varchar(20) NOT NULL DEFAULT 'TYPE_USER',
    employee_is_active tinyint(3) NOT NULL DEFAULT '1',
    PRIMARY KEY (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    order_id bigint(11) unsigned NOT NULL,
    order_date datetime DEFAULT NULL,
    order_description text,
    order_employee bigint(11) unsigned DEFAULT NULL,
    order_status varchar(20) NOT NULL DEFAULT 'STATUS_DRAFT',
    PRIMARY KEY (order_id),
    KEY fk_orders_employees (order_employee),
    CONSTRAINT fk_orders_employees FOREIGN KEY (order_employee) REFERENCES employees (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sale_lines;
CREATE TABLE sale_lines (
    sale_line_id bigint(11) unsigned NOT NULL,
    sale_line_article bigint(11) unsigned DEFAULT NULL,
    sale_line_sale bigint(11) unsigned NOT NULL,
    sale_line_discount float(8,3) DEFAULT '0.000',
    sale_line_quantity int(3) DEFAULT '1',
    PRIMARY KEY (sale_line_id,sale_line_sale),
    KEY fk_sale_lines_articles (sale_line_article),
    KEY fk_sale_lines_sales (sale_line_sale),
    CONSTRAINT fk_sale_lines_articles FOREIGN KEY (sale_line_article) REFERENCES articles (article_id),
    CONSTRAINT fk_sale_lines_sales FOREIGN KEY (sale_line_sale) REFERENCES sales (sale_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sales;
CREATE TABLE sales (
    sale_id bigint(11) unsigned NOT NULL,
    sale_client bigint(11) unsigned NOT NULL,
    sale_employee bigint(11) unsigned NOT NULL,
    sale_date datetime DEFAULT NULL,
    sale_payment varchar(20) NOT NULL DEFAULT 'TYPE_CASH',
    PRIMARY KEY (sale_id),
    KEY fk_sales_clients (sale_client),
    KEY fk_sales_employees (sale_employee),
    CONSTRAINT fk_sales_clients FOREIGN KEY (sale_client) REFERENCES clients (client_id),
    CONSTRAINT fk_sales_employees FOREIGN KEY (sale_employee) REFERENCES employees (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sizes;
CREATE TABLE sizes (
    size_id int(5) NOT NULL,
    size_value varchar(20) NOT NULL,
    PRIMARY KEY (size_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sizes_articles_map;
CREATE TABLE sizes_articles_map (
    size_id int(5) NOT NULL,
    article_id bigint(11) unsigned NOT NULL,
    article_stock int(11) DEFAULT '0',
    PRIMARY KEY (size_id,article_id),
    KEY fk_sizes_employees_map_articles (article_id),
    CONSTRAINT fk_sizes_employees_map_articles FOREIGN KEY (article_id) REFERENCES articles (article_id),
    CONSTRAINT fk_sizes_employees_map_sizes FOREIGN KEY (size_id) REFERENCES sizes (size_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;