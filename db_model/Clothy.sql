-- MySQL dump 10.13  Distrib 5.7.13, for linux-glibc2.5 (x86_64)
--
-- Host: localhost    Database: Clothy
-- ------------------------------------------------------
-- Server version	5.7.18-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `articles`
--

DROP TABLE IF EXISTS `articles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `articles` (
  `article_id` bigint(11) unsigned NOT NULL,
  `article_code` varchar(255) NOT NULL DEFAULT '',
  `article_name` varchar(255) NOT NULL,
  `article_description` text,
  `article_brand` bigint(11) unsigned DEFAULT NULL,
  `article_price` float(8,3) NOT NULL,
  PRIMARY KEY (`article_id`),
  KEY `fk_articles_brands` (`article_brand`),
  CONSTRAINT `fk_articles_brands` FOREIGN KEY (`article_brand`) REFERENCES `brands` (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `articles`
--

LOCK TABLES `articles` WRITE;
/*!40000 ALTER TABLE `articles` DISABLE KEYS */;
INSERT INTO `articles` VALUES (0,'123','nombre','descripcion',10,123.120),(4,'asd','asd','asd',10,150.600),(5,'PV001','Polo verde','Un polo verde cualquiera',11,119.990);
/*!40000 ALTER TABLE `articles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brands`
--

DROP TABLE IF EXISTS `brands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `brands` (
  `brand_id` bigint(11) unsigned NOT NULL,
  `brand_name` varchar(255) NOT NULL,
  `brand_address` text,
  `brand_email` varchar(255) NOT NULL,
  `brand_phone` varchar(20) NOT NULL,
  PRIMARY KEY (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brands`
--

LOCK TABLES `brands` WRITE;
/*!40000 ALTER TABLE `brands` DISABLE KEYS */;
INSERT INTO `brands` VALUES (1,'Sin marca','Calle del puma 34, África','puma@puma.es','123456789'),(2,'Sin marca','asdasdas<da','asd@asd.com','123456789'),(3,'Sin marca','asdasd','asd@asd.com','123123123123'),(4,'Sin marca','asd','asd@asd.com','123456789'),(5,'Sin marca','asd','asd@asd.com','123456789'),(6,'Sin marca','asd','asd@asd.com','123435623'),(7,'Sin marca','asd','asd@asd.com','112619061'),(8,'Sin marca','asd','asd@asasd.com','123456789'),(9,'Sin marca','ads','asd@asd.com','123456789'),(10,'Marca_1','adsasdasd','asd@asd.com','134567898'),(11,'Marca_2','Direccion marca','email@marca2.com','123456789');
/*!40000 ALTER TABLE `brands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `category_id` bigint(11) unsigned NOT NULL,
  `category_name` varchar(255) NOT NULL,
  `category_description` text,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Deporte','Categoría destinada a cosas de deporte'),(2,'Vaqueros','Categoría para agrupar los pantalones'),(3,'Pantalones','asdasdasd'),(4,'Camisas','asdadsasd'),(5,'Verano','Ropa de verano'),(6,'Invierno','');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories_articles_map`
--

DROP TABLE IF EXISTS `categories_articles_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories_articles_map` (
  `category_id` bigint(11) unsigned NOT NULL,
  `article_id` bigint(11) unsigned NOT NULL,
  PRIMARY KEY (`category_id`,`article_id`),
  KEY `fk_categories_articles_map_articles` (`article_id`),
  CONSTRAINT `fk_categories_articles_map_articles` FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`),
  CONSTRAINT `fk_categories_articles_map_categories` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories_articles_map`
--

LOCK TABLES `categories_articles_map` WRITE;
/*!40000 ALTER TABLE `categories_articles_map` DISABLE KEYS */;
INSERT INTO `categories_articles_map` VALUES (2,0),(3,0),(5,0),(1,4),(2,4),(3,4),(4,5),(5,5),(6,5);
/*!40000 ALTER TABLE `categories_articles_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clients` (
  `client_id` bigint(11) unsigned NOT NULL,
  `client_nif` varchar(20) NOT NULL,
  `client_name` varchar(255) NOT NULL,
  `client_surname` varchar(255) DEFAULT '',
  `client_address` text,
  `client_email` varchar(255) DEFAULT NULL,
  `client_phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (0,'00000000A','Sin cliente','','','',''),(1,'00000001A','Cliente_001','','adsasdasd','email@email.com','655021352'),(2,'48984651E','Nombre del cliente','Apellidos','direccion','email@email.com','12334565678');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employees` (
  `employee_id` bigint(11) unsigned NOT NULL,
  `employee_name` varchar(255) NOT NULL,
  `employee_surname` varchar(255) DEFAULT '',
  `employee_address` text,
  `employee_email` varchar(255) NOT NULL,
  `employee_phone` varchar(20) NOT NULL,
  `employee_login_name` varchar(255) NOT NULL,
  `employee_login_password` varchar(255) NOT NULL,
  `employee_login_type` varchar(20) NOT NULL DEFAULT 'TYPE_USER',
  `employee_is_active` tinyint(3) NOT NULL DEFAULT '1',
  PRIMARY KEY (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (3,'nombre','apellido','direccion','email@email.com','0','admin','$2a$12$LJHoYMQWVKz.lnOmxrMUzeR9ctpg7vQGwiDnZ5LWUwWSUoVeZLdHS','TYPE_ADMIN',1);
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `order_id` bigint(11) unsigned NOT NULL,
  `order_date` datetime DEFAULT NULL,
  `order_description` text,
  `order_employee` bigint(11) unsigned DEFAULT NULL,
  `order_status` varchar(20) NOT NULL DEFAULT 'STATUS_DRAFT',
  PRIMARY KEY (`order_id`),
  KEY `fk_orders_employees` (`order_employee`),
  CONSTRAINT `fk_orders_employees` FOREIGN KEY (`order_employee`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale_lines`
--

DROP TABLE IF EXISTS `sale_lines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_lines` (
  `sale_line_id` bigint(11) unsigned NOT NULL,
  `sale_line_article` bigint(11) unsigned DEFAULT NULL,
  `sale_line_sale` bigint(11) unsigned NOT NULL,
  `sale_line_discount` float(8,3) DEFAULT '0.000',
  `sale_line_quantity` int(3) DEFAULT '1',
  PRIMARY KEY (`sale_line_id`,`sale_line_sale`),
  KEY `fk_sale_lines_articles` (`sale_line_article`),
  KEY `fk_sale_lines_sales` (`sale_line_sale`),
  CONSTRAINT `fk_sale_lines_articles` FOREIGN KEY (`sale_line_article`) REFERENCES `articles` (`article_id`),
  CONSTRAINT `fk_sale_lines_sales` FOREIGN KEY (`sale_line_sale`) REFERENCES `sales` (`sale_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale_lines`
--

LOCK TABLES `sale_lines` WRITE;
/*!40000 ALTER TABLE `sale_lines` DISABLE KEYS */;
INSERT INTO `sale_lines` VALUES (0,0,1,0.000,1),(0,0,2,0.000,1),(1,0,1,0.000,1),(1,0,2,0.000,1),(2,0,1,0.000,1),(2,0,2,0.000,1),(3,0,1,0.000,1),(3,0,2,0.000,1),(4,0,1,0.000,1),(4,0,2,0.000,1),(5,0,2,0.000,1),(6,0,2,0.000,1),(7,0,2,0.100,2);
/*!40000 ALTER TABLE `sale_lines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sales` (
  `sale_id` bigint(11) unsigned NOT NULL,
  `sale_client` bigint(11) unsigned NOT NULL,
  `sale_employee` bigint(11) unsigned NOT NULL,
  `sale_date` datetime DEFAULT NULL,
  `sale_payment` varchar(20) NOT NULL DEFAULT 'TYPE_CASH',
  PRIMARY KEY (`sale_id`),
  KEY `fk_sales_clients` (`sale_client`),
  KEY `fk_sales_employees` (`sale_employee`),
  CONSTRAINT `fk_sales_clients` FOREIGN KEY (`sale_client`) REFERENCES `clients` (`client_id`),
  CONSTRAINT `fk_sales_employees` FOREIGN KEY (`sale_employee`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales`
--

LOCK TABLES `sales` WRITE;
/*!40000 ALTER TABLE `sales` DISABLE KEYS */;
INSERT INTO `sales` VALUES (1,1,3,'2017-05-25 00:00:00','TYPE_CARD'),(2,0,3,'2017-05-27 00:00:00','TYPE_CASH'),(3,0,3,'2017-05-28 00:00:00','TYPE_CARD'),(4,2,3,'2017-05-29 00:00:00','TYPE_CASH');
/*!40000 ALTER TABLE `sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sizes`
--

DROP TABLE IF EXISTS `sizes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sizes` (
  `size_id` int(5) NOT NULL,
  `size_value` varchar(20) NOT NULL,
  PRIMARY KEY (`size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sizes`
--

LOCK TABLES `sizes` WRITE;
/*!40000 ALTER TABLE `sizes` DISABLE KEYS */;
INSERT INTO `sizes` VALUES (1,'M'),(2,'L'),(3,'Sin talla');
/*!40000 ALTER TABLE `sizes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sizes_articles_map`
--

DROP TABLE IF EXISTS `sizes_articles_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sizes_articles_map` (
  `size_id` int(5) NOT NULL,
  `article_id` bigint(11) unsigned NOT NULL,
  `article_stock` int(11) DEFAULT '0',
  PRIMARY KEY (`size_id`,`article_id`),
  KEY `fk_sizes_employees_map_articles` (`article_id`),
  CONSTRAINT `fk_sizes_employees_map_articles` FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`),
  CONSTRAINT `fk_sizes_employees_map_sizes` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sizes_articles_map`
--

LOCK TABLES `sizes_articles_map` WRITE;
/*!40000 ALTER TABLE `sizes_articles_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `sizes_articles_map` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-05-29 11:23:51
