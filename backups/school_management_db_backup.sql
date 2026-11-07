-- MySQL dump 10.13  Distrib 9.5.0, for macos26.1 (arm64)
--
-- Host: localhost    Database: school_management_db
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `absence_alert_log`
--

DROP TABLE IF EXISTS `absence_alert_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `absence_alert_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `absence_date` date NOT NULL,
  `sent_at` datetime(6) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_absence_alert_student_date` (`student_id`,`absence_date`),
  KEY `FKbat8kmbu9odd63v8t20pxjg4g` (`created_by`),
  KEY `FKchf9bfryy39ufs8f6yrwehrds` (`updated_by`),
  KEY `FKagutiy14yr9mfuksr89mnoag` (`school_id`),
  CONSTRAINT `FK5fc8p7rdvio7f9g39emtyame6` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKagutiy14yr9mfuksr89mnoag` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKbat8kmbu9odd63v8t20pxjg4g` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKchf9bfryy39ufs8f6yrwehrds` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `absence_alert_log`
--

LOCK TABLES `absence_alert_log` WRITE;
/*!40000 ALTER TABLE `absence_alert_log` DISABLE KEYS */;
INSERT INTO `absence_alert_log` VALUES (1,'2026-10-25 10:32:13.907979','2026-10-25 10:32:13.907979','2026-10-25','2026-10-25 10:32:13.901656',NULL,NULL,1,13),(2,'2026-10-25 10:32:18.663902','2026-10-25 10:32:18.663902','2026-10-25','2026-10-25 10:32:18.663334',NULL,NULL,1,19),(3,'2026-10-25 10:32:25.547599','2026-10-25 10:32:25.547599','2026-10-25','2026-10-25 10:32:25.547198',NULL,NULL,1,25),(4,'2026-10-25 10:32:29.829101','2026-10-25 10:32:29.829101','2026-10-25','2026-10-25 10:32:29.828845',NULL,NULL,1,31);
/*!40000 ALTER TABLE `absence_alert_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `academic_year`
--

DROP TABLE IF EXISTS `academic_year`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `academic_year` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt4930e1oa58g2e6cuw4uyy62o` (`created_by`),
  KEY `FK17un3393lcki6ltgj47ftwu` (`updated_by`),
  KEY `FK7p7aivj6xdfa7hwq1ks7jenbe` (`school_id`),
  CONSTRAINT `FK17un3393lcki6ltgj47ftwu` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK7p7aivj6xdfa7hwq1ks7jenbe` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKt4930e1oa58g2e6cuw4uyy62o` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `academic_year`
--

LOCK TABLES `academic_year` WRITE;
/*!40000 ALTER TABLE `academic_year` DISABLE KEYS */;
INSERT INTO `academic_year` VALUES (1,'2026-10-24 21:35:35.007983','2026-10-24 21:35:35.007983',_binary '','2026-11-20 00:00:00.000000','2026 Academic Year','2026-01-20 00:00:00.000000',1,1,1);
/*!40000 ALTER TABLE `academic_year` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `audience_type` enum('ALL','CLASS','ROLE','USER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `body` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `publish_at` datetime(6) NOT NULL,
  `status` enum('DRAFT','PUBLISHED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_role` enum('FINANCE_OFFICER','PARENT','PRINCIPAL','SCHOOL_ADMIN','STUDENT','SYSTEM_ADMIN','TEACHER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `author_id` bigint NOT NULL,
  `target_class_id` bigint DEFAULT NULL,
  `target_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq1iek8k93xcmndu6f4cx64up0` (`created_by`),
  KEY `FK2bn3arkx4a4gdxkea3c7wu31o` (`updated_by`),
  KEY `FK1y9jg5i43ta12lr747xffwtmy` (`school_id`),
  KEY `FKi9blkkxbv694mmsom1ocshpaj` (`author_id`),
  KEY `FKh3kore3k6t0s5aqaiyjp3m60a` (`target_class_id`),
  KEY `FKln7rm02bljtuhnj7c5vwsmlt5` (`target_user_id`),
  CONSTRAINT `FK1y9jg5i43ta12lr747xffwtmy` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK2bn3arkx4a4gdxkea3c7wu31o` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKh3kore3k6t0s5aqaiyjp3m60a` FOREIGN KEY (`target_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKi9blkkxbv694mmsom1ocshpaj` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKln7rm02bljtuhnj7c5vwsmlt5` FOREIGN KEY (`target_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKq1iek8k93xcmndu6f4cx64up0` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `annual_fee`
--

DROP TABLE IF EXISTS `annual_fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `annual_fee` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` double NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `academic_year_id` bigint NOT NULL,
  `school_class_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_annual_fee_class_year_name` (`school_class_id`,`academic_year_id`,`name`),
  KEY `FK5wc0doyujptqa97bilnfxe59w` (`created_by`),
  KEY `FKogphsj9y1hbtcasx78ktiigor` (`updated_by`),
  KEY `FKgjxaceijxx4svlhufjwssf2ok` (`school_id`),
  KEY `FK7yvjauaankv4v1i5412e7dr8k` (`academic_year_id`),
  CONSTRAINT `FK5wc0doyujptqa97bilnfxe59w` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK7yvjauaankv4v1i5412e7dr8k` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year` (`id`),
  CONSTRAINT `FKgjxaceijxx4svlhufjwssf2ok` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKkccjuv1a7c1wkhofj10471jr0` FOREIGN KEY (`school_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKogphsj9y1hbtcasx78ktiigor` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annual_fee`
--

LOCK TABLES `annual_fee` WRITE;
/*!40000 ALTER TABLE `annual_fee` DISABLE KEYS */;
INSERT INTO `annual_fee` VALUES (1,'2026-10-25 08:11:37.626384','2026-10-25 08:11:37.626384',250,'Every student must pay before schools are reopen.','Registration Fee',1,1,1,1,5),(2,'2026-10-25 08:11:37.626384','2026-10-25 08:11:37.626384',250,'Every student must pay before schools are reopen.','Registration Fee',1,1,1,1,4),(3,'2026-10-25 08:11:37.626384','2026-10-25 08:11:37.626384',250,'Every student must pay before schools are reopen.','Registration Fee',1,1,1,1,2),(4,'2026-10-25 08:11:37.626384','2026-10-25 08:11:37.626384',250,'Every student must pay before schools are reopen.','Registration Fee',1,1,1,1,1),(5,'2026-10-25 08:11:37.626385','2026-10-25 08:11:37.626385',250,'Every student must pay before schools are reopen.','Registration Fee',1,1,1,1,3),(6,'2026-10-25 08:11:37.626384','2026-10-25 08:11:37.626384',250,'Every student must pay before schools are reopen.','Registration Fee',1,1,1,1,6);
/*!40000 ALTER TABLE `annual_fee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assessment`
--

DROP TABLE IF EXISTS `assessment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assessment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `date` date NOT NULL,
  `max_mark` double NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('CONTINUOUS','EXAM') COLLATE utf8mb4_unicode_ci NOT NULL,
  `weight` int NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `assignment_id` bigint NOT NULL,
  `examination_id` bigint DEFAULT NULL,
  `term_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9jwbxdccyfwkv0a9et7w56wh4` (`created_by`),
  KEY `FK8ayq9lhthhln9rjcv465iga6c` (`updated_by`),
  KEY `FK28486d5ubt89yit3dfyowwxw8` (`school_id`),
  KEY `FKgl9ic6rq6iwkrn7mcpg4thb7c` (`assignment_id`),
  KEY `FKa1f2eothjw75vy3p6b9muxydh` (`examination_id`),
  KEY `FKr6lqa6vbk4ekrg3cg8frir67g` (`term_id`),
  CONSTRAINT `FK28486d5ubt89yit3dfyowwxw8` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK8ayq9lhthhln9rjcv465iga6c` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK9jwbxdccyfwkv0a9et7w56wh4` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKa1f2eothjw75vy3p6b9muxydh` FOREIGN KEY (`examination_id`) REFERENCES `examination` (`id`),
  CONSTRAINT `FKgl9ic6rq6iwkrn7mcpg4thb7c` FOREIGN KEY (`assignment_id`) REFERENCES `subject_class_assignment` (`id`),
  CONSTRAINT `FKr6lqa6vbk4ekrg3cg8frir67g` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assessment`
--

LOCK TABLES `assessment` WRITE;
/*!40000 ALTER TABLE `assessment` DISABLE KEYS */;
INSERT INTO `assessment` VALUES (1,'2026-10-25 11:34:46.519155','2026-10-25 11:36:38.363956','2026-10-26',100,'Topic Test','CONTINUOUS',0,3,3,1,1,NULL,4);
/*!40000 ALTER TABLE `assessment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `date` date NOT NULL,
  `status` enum('ABSENT','EXCUSED','LATE','PRESENT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `lesson_id` bigint NOT NULL,
  `marked_by` bigint DEFAULT NULL,
  `school_class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_attendance_student_lesson` (`student_id`,`lesson_id`),
  KEY `FKir82dwygi2dkvw2h1kythowkc` (`created_by`),
  KEY `FKpjqf432e66c0dsqttinreoixc` (`updated_by`),
  KEY `FKr4ms1d0xrggll9shckfiviee0` (`school_id`),
  KEY `FKam01ddvne08oa3exny156v7al` (`lesson_id`),
  KEY `FKjxe0xwy9lmfln80bdt0weg884` (`marked_by`),
  KEY `FKndx0uoeee5552dp5yvxna89yh` (`school_class_id`),
  CONSTRAINT `FKam01ddvne08oa3exny156v7al` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`id`),
  CONSTRAINT `FKir82dwygi2dkvw2h1kythowkc` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKjxe0xwy9lmfln80bdt0weg884` FOREIGN KEY (`marked_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKndx0uoeee5552dp5yvxna89yh` FOREIGN KEY (`school_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKnq6vm31it076obtjf2qp5coim` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKpjqf432e66c0dsqttinreoixc` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKr4ms1d0xrggll9shckfiviee0` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `action` enum('ACADEMIC_YEAR_CREATED','ACADEMIC_YEAR_DELETED','ACADEMIC_YEAR_UPDATED','ATTENDANCE_CHANGED','FAILED_LOGIN','FEE_STRUCTURE_CREATED','FEE_STRUCTURE_DELETED','FEE_STRUCTURE_UPDATED','HOLIDAY_CREATED','HOLIDAY_DELETED','HOLIDAY_UPDATED','LOGIN','LOGOUT','PAYMENT_RECORDED','PAYMENT_REVERSED','RESULT_CHANGED_AFTER_FINALISATION','STUDENT_FEE_CLASS_CHANGED','STUDENT_STATUS_CHANGED','TERM_CREATED','TERM_DELETED','TERM_UPDATED','TIMETABLE_CREATED','TIMETABLE_DELETED','TIMETABLE_PUBLISHED','TIMETABLE_UPDATED','USER_ACCOUNT_UNLOCKED','USER_CREATED','USER_DEACTIVATED','USER_ROLE_CHANGED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `summary` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_id` bigint DEFAULT NULL,
  `target_type` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `actor_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrj0qpmha84j765otjta8qhins` (`created_by`),
  KEY `FKs5983ukn2biuliwf5ygdffocj` (`updated_by`),
  KEY `FKkycn6hobsg2n4bymd1jcoao5j` (`school_id`),
  KEY `FK3alrhfr7kbvkppgjtxur3cyb6` (`actor_id`),
  CONSTRAINT `FK3alrhfr7kbvkppgjtxur3cyb6` FOREIGN KEY (`actor_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKkycn6hobsg2n4bymd1jcoao5j` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKrj0qpmha84j765otjta8qhins` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKs5983ukn2biuliwf5ygdffocj` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'2026-10-25 15:50:25.880643','2026-10-25 15:50:25.880643','LOGOUT','Logged out',1,'USER',1,1,1,1),(2,'2026-10-25 15:50:27.681255','2026-10-25 15:50:27.681255','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(3,'2026-10-25 16:40:32.354118','2026-10-25 16:40:32.354118','PAYMENT_RECORDED','Recorded payment of M 200.00 for Test Student 019; balance changed from M 200.00 to M 0.00',NULL,'PAYMENT',1,1,1,1),(4,'2026-10-25 17:22:03.222638','2026-10-25 17:22:03.222638','LOGOUT','Logged out',1,'USER',1,1,1,1),(5,'2026-10-25 17:22:08.383846','2026-10-25 17:22:08.383846','LOGIN','Logged in successfully',6,'USER',6,6,1,6),(6,'2026-10-25 17:26:40.686105','2026-10-25 17:26:40.686105','LOGOUT','Logged out',6,'USER',6,6,1,6),(7,'2026-10-25 17:26:42.768909','2026-10-25 17:26:42.768909','LOGIN','Logged in successfully',6,'USER',6,6,1,6),(8,'2026-10-25 17:38:45.904140','2026-10-25 17:38:45.904140','LOGOUT','Logged out',6,'USER',6,6,1,6),(9,'2026-10-25 17:38:47.242727','2026-10-25 17:38:47.242727','LOGIN','Logged in successfully',6,'USER',6,6,1,6),(10,'2026-10-25 17:44:03.681869','2026-10-25 17:44:03.681869','LOGOUT','Logged out',6,'USER',6,6,1,6),(11,'2026-10-25 17:44:08.490065','2026-10-25 17:44:08.490065','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(12,'2026-10-25 17:44:31.635950','2026-10-25 17:44:31.635950','LOGOUT','Logged out',1,'USER',1,1,1,1),(13,'2026-10-25 17:44:41.751566','2026-10-25 17:44:41.751566','LOGIN','Logged in successfully',6,'USER',6,6,1,6),(14,'2026-10-25 18:03:21.958624','2026-10-25 18:03:21.958624','LOGOUT','Logged out',6,'USER',6,6,1,6),(15,'2026-10-25 18:03:27.385650','2026-10-25 18:03:27.385650','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(16,'2026-10-25 18:05:01.170838','2026-10-25 18:05:01.170838','LOGOUT','Logged out',1,'USER',1,1,1,1),(27,'2026-10-25 18:21:10.500411','2026-10-25 18:21:10.500411','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(28,'2026-10-25 18:44:50.589021','2026-10-25 18:44:50.589021','PAYMENT_RECORDED','Recorded payment of M 3000.00 for Lebo Mona; balance changed from M 3000.00 to M 0.00',NULL,'PAYMENT',1,1,1,1),(30,'2026-11-06 21:19:19.580661','2026-11-06 21:19:19.580661','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(31,'2026-11-06 21:21:42.175327','2026-11-06 21:21:42.175327','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(32,'2026-11-06 21:21:58.552777','2026-11-06 21:21:58.552777','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(33,'2026-11-06 21:22:07.728321','2026-11-06 21:22:07.728321','LOGOUT','Logged out',1,'USER',1,1,1,1),(34,'2026-11-06 21:22:26.346553','2026-11-06 21:22:26.346553','LOGIN','Logged in successfully',1,'USER',1,1,1,1),(35,'2026-11-06 21:22:57.444844','2026-11-06 21:22:57.444844','LOGIN','Logged in successfully',1,'USER',1,1,1,1);
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blacklisted_token`
--

DROP TABLE IF EXISTS `blacklisted_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blacklisted_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd8f94e6nes93c15hn3lmijlld` (`token`),
  KEY `FK7sdeh2ggmxlkoj5u55giwx4p8` (`created_by`),
  KEY `FKj3ibrrvdn5a1qy14ktnu1dhnd` (`updated_by`),
  CONSTRAINT `FK7sdeh2ggmxlkoj5u55giwx4p8` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKj3ibrrvdn5a1qy14ktnu1dhnd` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blacklisted_token`
--

LOCK TABLES `blacklisted_token` WRITE;
/*!40000 ALTER TABLE `blacklisted_token` DISABLE KEYS */;
INSERT INTO `blacklisted_token` VALUES (1,'2026-10-24 21:30:49.160106','2026-10-24 21:30:49.160106','2026-10-25 21:29:59.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5Mjg3NzM5OSwiZXhwIjoxNzkyOTYzNzk5fQ._Rs3CN4Ap76kM9eMz4Zs92ZV4r86IC-C3ZSvJvUbcu8',NULL,NULL),(2,'2026-10-24 21:56:37.340940','2026-10-24 21:56:37.340940','2026-10-25 21:31:33.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5Mjg3NzQ5MywiZXhwIjoxNzkyOTYzODkzfQ.uJ7Hz6mjueoEyMkpBecZmEJ-hKLDKbUY0YP5pLKvvFk',NULL,NULL),(3,'2026-10-24 21:57:22.923615','2026-10-24 21:57:22.923615','2026-10-25 21:56:50.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwbGF5aW5ndW5pdHM0NzZAZ21haWwuY29tIiwidXNlcklkIjo0LCJyb2xlcyI6WyJGSU5BTkNFX09GRklDRVIiXSwic2Nob29sSWRzIjpbMV0sImZ1bGxOYW1lIjoiRmluYW5jZSBPZmZpY2VyIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5Mjg3OTAxMCwiZXhwIjoxNzkyOTY1NDEwfQ.2deahJotL4kBChr4V9FNY14pMoAoeUf3kRu3qY40PB8',NULL,NULL),(4,'2026-10-24 21:59:47.701858','2026-10-24 21:59:47.701858','2026-10-25 21:57:56.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWJvbW9uYTcxQGdtYWlsLmNvbSIsInVzZXJJZCI6NSwicm9sZXMiOlsiU1RVREVOVCJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJMZWJvIE1vbmEiLCJzY2hvb2xOYW1lIjoiVHNvaW55YW5lIEhpZ2ggU2Nob29sIiwiaWF0IjoxNzkyODc5MDc2LCJleHAiOjE3OTI5NjU0NzZ9.3TlmaadtcqRM87f26Hb5YrZiGCFIwvMEsoG90tqRkYM',NULL,NULL),(5,'2026-10-24 22:00:43.895520','2026-10-24 22:00:43.895520','2026-10-25 22:00:30.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWJvaGFuZ21vbmFtYW5lM0BnbWFpbC5jb20iLCJ1c2VySWQiOjYsInJvbGVzIjpbIlBBUkVOVCJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJNYWxlYm9oYW5nIE1vbmEiLCJzY2hvb2xOYW1lIjoiVHNvaW55YW5lIEhpZ2ggU2Nob29sIiwiaWF0IjoxNzkyODc5MjMwLCJleHAiOjE3OTI5NjU2MzB9.snGNwMADJXKCPcoxvA4vmmioiureEPTKdpo86jH6OqA',NULL,NULL),(6,'2026-10-24 22:05:43.424497','2026-10-24 22:05:43.424497','2026-10-25 22:05:41.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWJvaGFuZ21vbmFtYW5lM0BnbWFpbC5jb20iLCJ1c2VySWQiOjYsInJvbGVzIjpbIlBBUkVOVCJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJNYWxlYm9oYW5nIE1vbmEiLCJzY2hvb2xOYW1lIjoiVHNvaW55YW5lIEhpZ2ggU2Nob29sIiwiaWF0IjoxNzkyODc5NTQxLCJleHAiOjE3OTI5NjU5NDF9.ATDZXAzh2jhw-Hz6vhR_-8lSeJRCVL1adliRETm33pQ',NULL,NULL),(7,'2026-10-25 10:12:12.849731','2026-10-25 10:12:12.849731','2026-10-25 22:05:48.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5Mjg3OTU0OCwiZXhwIjoxNzkyOTY1OTQ4fQ.0A1mK_tohk_52KuOax1G9_bvYANw2vWDgPGQ4LTlC5A',NULL,NULL),(8,'2026-10-25 11:37:47.011036','2026-10-25 11:37:47.011036','2026-10-26 10:16:04.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWhhbGFiZXQ3QGdtYWlsLmNvbSIsInVzZXJJZCI6Mywicm9sZXMiOlsiVEVBQ0hFUiJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJNYWhhbGEgQmV0Iiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5MjkyMzM2NCwiZXhwIjoxNzkzMDA5NzY0fQ.bAJGkEIvUo34C029NpFYGF89-QEugv9dcZoB3XOmp3o',NULL,NULL),(9,'2026-10-25 15:50:25.843406','2026-10-25 15:50:25.843406','2026-10-26 11:37:49.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5MjkyODI2OSwiZXhwIjoxNzkzMDE0NjY5fQ.F6J8gFwZskM3ampz0kjhSOVknwFehisV_Aj96sF29nw',NULL,NULL),(10,'2026-10-25 17:22:03.109661','2026-10-25 17:22:03.109661','2026-10-26 15:50:27.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5Mjk0MzQyNywiZXhwIjoxNzkzMDI5ODI3fQ.1N5T3i3A9_T2tWZ-Bzui7WFYZGtKwbl8kUG5xB7ww4o',NULL,NULL),(11,'2026-10-25 17:26:40.601324','2026-10-25 17:26:40.601324','2026-10-26 17:22:08.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWJvaGFuZ21vbmFtYW5lM0BnbWFpbC5jb20iLCJ1c2VySWQiOjYsInJvbGVzIjpbIlBBUkVOVCJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJNYWxlYm9oYW5nIE1vbmEiLCJzY2hvb2xOYW1lIjoiVHNvaW55YW5lIEhpZ2ggU2Nob29sIiwiaWF0IjoxNzkyOTQ4OTI4LCJleHAiOjE3OTMwMzUzMjh9.IgsuFXIhe7MQ3W4FKIkQRn7xMaL4eG9zxm3-ZcHevL8',NULL,NULL),(12,'2026-10-25 17:38:45.848238','2026-10-25 17:38:45.848238','2026-10-26 17:26:42.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWJvaGFuZ21vbmFtYW5lM0BnbWFpbC5jb20iLCJ1c2VySWQiOjYsInJvbGVzIjpbIlBBUkVOVCJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJNYWxlYm9oYW5nIE1vbmEiLCJzY2hvb2xOYW1lIjoiVHNvaW55YW5lIEhpZ2ggU2Nob29sIiwiaWF0IjoxNzkyOTQ5MjAyLCJleHAiOjE3OTMwMzU2MDJ9.yM5LYJYJPVkKJqztakWFNeeNVeGZle7J7fJXBwzzOzA',NULL,NULL),(13,'2026-10-25 17:44:03.631747','2026-10-25 17:44:03.631747','2026-10-26 17:38:47.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWJvaGFuZ21vbmFtYW5lM0BnbWFpbC5jb20iLCJ1c2VySWQiOjYsInJvbGVzIjpbIlBBUkVOVCJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJNYWxlYm9oYW5nIE1vbmEiLCJzY2hvb2xOYW1lIjoiVHNvaW55YW5lIEhpZ2ggU2Nob29sIiwiaWF0IjoxNzkyOTQ5OTI3LCJleHAiOjE3OTMwMzYzMjd9.VeAd2mw6Gv0SmCO9apgYyh3683OrJr5RZ99YVhm5-MA',NULL,NULL),(14,'2026-10-25 17:44:31.625026','2026-10-25 17:44:31.625026','2026-10-26 17:44:08.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5Mjk1MDI0OCwiZXhwIjoxNzkzMDM2NjQ4fQ.hSpK8vM7QjwiFEtQM-3nGLP_KcTG6-GIgjNqf_FKQtY',NULL,NULL),(15,'2026-10-25 18:03:21.841448','2026-10-25 18:03:21.841448','2026-10-26 17:44:41.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWJvaGFuZ21vbmFtYW5lM0BnbWFpbC5jb20iLCJ1c2VySWQiOjYsInJvbGVzIjpbIlBBUkVOVCJdLCJzY2hvb2xJZHMiOlsxXSwiZnVsbE5hbWUiOiJNYWxlYm9oYW5nIE1vbmEiLCJzY2hvb2xOYW1lIjoiVHNvaW55YW5lIEhpZ2ggU2Nob29sIiwiaWF0IjoxNzkyOTUwMjgxLCJleHAiOjE3OTMwMzY2ODF9.Q2X3iGZr0PTYiCuex6MO-g9lBoxbfgzfn5XJyPrbJuU',NULL,NULL),(16,'2026-10-25 18:05:01.132754','2026-10-25 18:05:01.132754','2026-10-26 18:03:27.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5Mjk1MTQwNywiZXhwIjoxNzkzMDM3ODA3fQ.sGbFfHCxnudStZh4ZzkvG0MsBRrY46BANFXUhwpH7qc',NULL,NULL),(17,'2026-11-06 21:22:07.718564','2026-11-06 21:22:07.718564','2026-11-07 21:21:58.000000','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25hbWFuZS5sZWJvaGFuZzQ1QGdtYWlsLmNvbSIsInVzZXJJZCI6MSwicm9sZXMiOlsiU0NIT09MX0FETUlOIl0sInNjaG9vbElkcyI6WzFdLCJmdWxsTmFtZSI6IkxlYm9oYW5nIE1vbmFtYW5lIiwic2Nob29sTmFtZSI6IlRzb2lueWFuZSBIaWdoIFNjaG9vbCIsImlhdCI6MTc5NDAwMDExOCwiZXhwIjoxNzk0MDg2NTE4fQ.a3HUZ01fhVtiVCWoFdRiffu0s5I2BqW0_3kM2aF_0sU',NULL,NULL);
/*!40000 ALTER TABLE `blacklisted_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_file`
--

DROP TABLE IF EXISTS `data_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `data_file` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `contents` mediumblob,
  `file_size` bigint DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfcb8bduvqfb7wdkpiwj1kdj9r` (`created_by`),
  KEY `FK7esh30cv5f50hbvprnwrrktv5` (`updated_by`),
  CONSTRAINT `FK7esh30cv5f50hbvprnwrrktv5` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKfcb8bduvqfb7wdkpiwj1kdj9r` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_file`
--

LOCK TABLES `data_file` WRITE;
/*!40000 ALTER TABLE `data_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `examination`
--

DROP TABLE IF EXISTS `examination`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `examination` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `end_date` date NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmu54mptov1a6a0qqxa9qfu0vr` (`created_by`),
  KEY `FK9vx4dc3barw1hkq6jhtiunpat` (`updated_by`),
  KEY `FK2o31lm5ftgscoqkenn55ro8uo` (`school_id`),
  KEY `FKj96udlb3pt6vp61d95u8jef7p` (`term_id`),
  CONSTRAINT `FK2o31lm5ftgscoqkenn55ro8uo` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK9vx4dc3barw1hkq6jhtiunpat` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKj96udlb3pt6vp61d95u8jef7p` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`),
  CONSTRAINT `FKmu54mptov1a6a0qqxa9qfu0vr` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `examination`
--

LOCK TABLES `examination` WRITE;
/*!40000 ALTER TABLE `examination` DISABLE KEYS */;
/*!40000 ALTER TABLE `examination` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `examination_subject_class`
--

DROP TABLE IF EXISTS `examination_subject_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `examination_subject_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `examination_id` bigint NOT NULL,
  `school_class_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_subject_class` (`examination_id`,`subject_id`,`school_class_id`),
  KEY `FKi954vcoss3qxmc1j3f28obekt` (`created_by`),
  KEY `FKs1dv1mc4rg6trjn2c7uo41s0h` (`updated_by`),
  KEY `FKmvvj9f8yqryd0kyyenh6jx4q8` (`school_class_id`),
  KEY `FKi4npe4tgjyo2b0egnmw4xrftk` (`subject_id`),
  CONSTRAINT `FKi4npe4tgjyo2b0egnmw4xrftk` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`),
  CONSTRAINT `FKi954vcoss3qxmc1j3f28obekt` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKmvvj9f8yqryd0kyyenh6jx4q8` FOREIGN KEY (`school_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKpbblbvy5n9xwllqt80wc5btgm` FOREIGN KEY (`examination_id`) REFERENCES `examination` (`id`),
  CONSTRAINT `FKs1dv1mc4rg6trjn2c7uo41s0h` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `examination_subject_class`
--

LOCK TABLES `examination_subject_class` WRITE;
/*!40000 ALTER TABLE `examination_subject_class` DISABLE KEYS */;
/*!40000 ALTER TABLE `examination_subject_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_item`
--

DROP TABLE IF EXISTS `fee_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` double NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mandatory` bit(1) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `fee_structure_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6bqmoh03q6tufcvaxn84ifgo3` (`created_by`),
  KEY `FKb0cf4aml6bba77mrldewscn0t` (`updated_by`),
  KEY `FKbenqpoy3nl9h9j2892axbqsiw` (`school_id`),
  KEY `FKqgwhawm7musbbotfpjr66kgkb` (`fee_structure_id`),
  CONSTRAINT `FK6bqmoh03q6tufcvaxn84ifgo3` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKb0cf4aml6bba77mrldewscn0t` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbenqpoy3nl9h9j2892axbqsiw` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKqgwhawm7musbbotfpjr66kgkb` FOREIGN KEY (`fee_structure_id`) REFERENCES `fee_structure` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_item`
--

LOCK TABLES `fee_item` WRITE;
/*!40000 ALTER TABLE `fee_item` DISABLE KEYS */;
INSERT INTO `fee_item` VALUES (7,'2026-10-25 08:12:08.493950','2026-10-25 08:12:08.493950',950,NULL,_binary '','School Fees',1,1,1,8),(8,'2026-10-25 08:12:08.493926','2026-10-25 08:12:08.493926',950,NULL,_binary '','School Fees',1,1,1,9),(9,'2026-10-25 08:12:08.494888','2026-10-25 08:12:08.494888',950,NULL,_binary '','School Fees',1,1,1,11),(10,'2026-10-25 08:12:08.494420','2026-10-25 08:12:08.494420',950,NULL,_binary '','School Fees',1,1,1,7),(11,'2026-10-25 08:12:08.495285','2026-10-25 08:12:08.495285',950,NULL,_binary '','School Fees',1,1,1,12),(12,'2026-10-25 08:12:08.496325','2026-10-25 08:12:08.496325',950,NULL,_binary '','School Fees',1,1,1,10),(13,'2026-10-25 08:12:26.499073','2026-10-25 08:12:26.499073',600,NULL,_binary '','School Fees',1,1,1,15),(14,'2026-10-25 08:12:26.499174','2026-10-25 08:12:26.499174',600,NULL,_binary '','School Fees',1,1,1,14),(15,'2026-10-25 08:12:26.499066','2026-10-25 08:12:26.499066',600,NULL,_binary '','School Fees',1,1,1,16),(16,'2026-10-25 08:12:26.499827','2026-10-25 08:12:26.499827',600,NULL,_binary '','School Fees',1,1,1,17),(17,'2026-10-25 08:12:26.499881','2026-10-25 08:12:26.499881',600,NULL,_binary '','School Fees',1,1,1,13),(18,'2026-10-25 08:12:26.500409','2026-10-25 08:12:26.500409',600,NULL,_binary '','School Fees',1,1,1,18),(19,'2026-10-25 08:12:36.576126','2026-10-25 08:12:36.576126',600,NULL,_binary '','School Fees',1,1,1,21),(20,'2026-10-25 08:12:36.576456','2026-10-25 08:12:36.576456',600,NULL,_binary '','School Fees',1,1,1,19),(21,'2026-10-25 08:12:36.577366','2026-10-25 08:12:36.577366',600,NULL,_binary '','School Fees',1,1,1,20),(22,'2026-10-25 08:12:36.577804','2026-10-25 08:12:36.577804',600,NULL,_binary '','School Fees',1,1,1,22),(23,'2026-10-25 08:12:36.579024','2026-10-25 08:12:36.579024',600,NULL,_binary '','School Fees',1,1,1,23),(24,'2026-10-25 08:12:36.579240','2026-10-25 08:12:36.579240',600,NULL,_binary '','School Fees',1,1,1,24),(25,'2026-10-25 08:12:49.229912','2026-10-25 08:12:49.229912',600,NULL,_binary '','School Fees',1,1,1,26),(26,'2026-10-25 08:12:49.229768','2026-10-25 08:12:49.229768',600,NULL,_binary '','School Fees',1,1,1,25),(27,'2026-10-25 08:12:49.230405','2026-10-25 08:12:49.230405',600,NULL,_binary '','School Fees',1,1,1,27),(28,'2026-10-25 08:12:49.231065','2026-10-25 08:12:49.231065',600,NULL,_binary '','School Fees',1,1,1,29),(29,'2026-10-25 08:12:49.231444','2026-10-25 08:12:49.231444',600,NULL,_binary '','School Fees',1,1,1,28),(30,'2026-10-25 08:12:49.232124','2026-10-25 08:12:49.232124',600,NULL,_binary '','School Fees',1,1,1,30);
/*!40000 ALTER TABLE `fee_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_structure`
--

DROP TABLE IF EXISTS `fee_structure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_structure` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `school_class_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fee_structure_class_term` (`school_class_id`,`term_id`),
  KEY `FKgndy622f1ap2frj2mgqh35hlu` (`created_by`),
  KEY `FK65aiiaph0cvpkg3i86afnx9md` (`updated_by`),
  KEY `FKb5ugldxmbr0gfw6eg9hrovv9i` (`school_id`),
  KEY `FKhuja3yvtes4gy43sw6ar5vbrx` (`term_id`),
  CONSTRAINT `FK65aiiaph0cvpkg3i86afnx9md` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKb5ugldxmbr0gfw6eg9hrovv9i` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKgndy622f1ap2frj2mgqh35hlu` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKhuja3yvtes4gy43sw6ar5vbrx` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`),
  CONSTRAINT `FKm2p231ko6vh07cs3mf2xowfvk` FOREIGN KEY (`school_class_id`) REFERENCES `school_class` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_structure`
--

LOCK TABLES `fee_structure` WRITE;
/*!40000 ALTER TABLE `fee_structure` DISABLE KEYS */;
INSERT INTO `fee_structure` VALUES (7,'2026-10-25 08:12:08.490424','2026-10-25 08:12:08.490424',1,1,1,2,1),(8,'2026-10-25 08:12:08.487722','2026-10-25 08:12:08.487722',1,1,1,1,1),(9,'2026-10-25 08:12:08.489362','2026-10-25 08:12:08.489362',1,1,1,5,1),(10,'2026-10-25 08:12:08.487722','2026-10-25 08:12:08.487722',1,1,1,3,1),(11,'2026-10-25 08:12:08.487722','2026-10-25 08:12:08.487722',1,1,1,6,1),(12,'2026-10-25 08:12:08.487723','2026-10-25 08:12:08.487723',1,1,1,4,1),(13,'2026-10-25 08:12:26.495826','2026-10-25 08:12:26.495826',1,1,1,1,2),(14,'2026-10-25 08:12:26.495530','2026-10-25 08:12:26.495530',1,1,1,5,2),(15,'2026-10-25 08:12:26.495813','2026-10-25 08:12:26.495813',1,1,1,2,2),(16,'2026-10-25 08:12:26.495530','2026-10-25 08:12:26.495530',1,1,1,3,2),(17,'2026-10-25 08:12:26.495530','2026-10-25 08:12:26.495530',1,1,1,6,2),(18,'2026-10-25 08:12:26.498445','2026-10-25 08:12:26.498445',1,1,1,4,2),(19,'2026-10-25 08:12:36.574471','2026-10-25 08:12:36.574471',1,1,1,1,3),(20,'2026-10-25 08:12:36.574472','2026-10-25 08:12:36.574472',1,1,1,3,3),(21,'2026-10-25 08:12:36.574471','2026-10-25 08:12:36.574471',1,1,1,5,3),(22,'2026-10-25 08:12:36.574967','2026-10-25 08:12:36.574967',1,1,1,4,3),(23,'2026-10-25 08:12:36.575313','2026-10-25 08:12:36.575313',1,1,1,2,3),(24,'2026-10-25 08:12:36.575941','2026-10-25 08:12:36.575941',1,1,1,6,3),(25,'2026-10-25 08:12:49.227593','2026-10-25 08:12:49.227593',1,1,1,5,4),(26,'2026-10-25 08:12:49.227065','2026-10-25 08:12:49.227065',1,1,1,4,4),(27,'2026-10-25 08:12:49.227551','2026-10-25 08:12:49.227551',1,1,1,6,4),(28,'2026-10-25 08:12:49.227193','2026-10-25 08:12:49.227193',1,1,1,2,4),(29,'2026-10-25 08:12:49.227065','2026-10-25 08:12:49.227065',1,1,1,1,4),(30,'2026-10-25 08:12:49.227083','2026-10-25 08:12:49.227083',1,1,1,3,4);
/*!40000 ALTER TABLE `fee_structure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade_band`
--

DROP TABLE IF EXISTS `grade_band`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_band` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `grade` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `max_score` double NOT NULL,
  `min_score` double NOT NULL,
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_grade_band_school_grade` (`school_id`,`grade`),
  KEY `FKh3axv3ocapcu8k4e5empb9yr3` (`created_by`),
  KEY `FKk66b933jlnhnnotwjxr3khe97` (`updated_by`),
  CONSTRAINT `FKbfmpisku6mmbp5y65q176mkcu` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKh3axv3ocapcu8k4e5empb9yr3` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKk66b933jlnhnnotwjxr3khe97` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grade_band`
--

LOCK TABLES `grade_band` WRITE;
/*!40000 ALTER TABLE `grade_band` DISABLE KEYS */;
/*!40000 ALTER TABLE `grade_band` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lesson`
--

DROP TABLE IF EXISTS `lesson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lesson` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `date` date NOT NULL,
  `status` enum('CANCELLED','COMPLETED','MISSED','SCHEDULED','SUBSTITUTED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `substitute_teacher_id` bigint DEFAULT NULL,
  `timetable_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk0jsiwfotnul5xtjbd2og4j4h` (`created_by`),
  KEY `FKeh3m60rxc70hhicic31glqbg` (`updated_by`),
  KEY `FK3jkavrpsm61wgkhtyimiv4rly` (`school_id`),
  KEY `FKia4l2yle0updvj19e81h4g7d5` (`substitute_teacher_id`),
  KEY `FKjkvglb9r59bldihak12ua4f3w` (`timetable_id`),
  CONSTRAINT `FK3jkavrpsm61wgkhtyimiv4rly` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKeh3m60rxc70hhicic31glqbg` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKia4l2yle0updvj19e81h4g7d5` FOREIGN KEY (`substitute_teacher_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKjkvglb9r59bldihak12ua4f3w` FOREIGN KEY (`timetable_id`) REFERENCES `timetable` (`id`),
  CONSTRAINT `FKk0jsiwfotnul5xtjbd2og4j4h` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lesson`
--

LOCK TABLES `lesson` WRITE;
/*!40000 ALTER TABLE `lesson` DISABLE KEYS */;
INSERT INTO `lesson` VALUES (1,'2026-10-25 08:04:22.846681','2026-10-25 12:17:06.858287','2026-10-04','CANCELLED',1,1,1,NULL,1),(2,'2026-10-25 08:04:22.857403','2026-10-25 12:17:06.858406','2026-10-04','CANCELLED',1,1,1,NULL,2),(3,'2026-10-25 08:04:22.864933','2026-10-25 12:17:06.858474','2026-10-04','CANCELLED',1,1,1,NULL,3),(4,'2026-10-25 08:04:22.867845','2026-10-25 12:17:06.858527','2026-10-04','CANCELLED',1,1,1,NULL,5),(5,'2026-10-25 08:04:22.869183','2026-10-25 12:17:06.858575','2026-10-04','CANCELLED',1,1,1,NULL,4),(6,'2026-10-25 08:04:22.870531','2026-10-25 12:17:06.858642','2026-10-04','CANCELLED',1,1,1,NULL,6),(7,'2026-10-25 08:04:22.871843','2026-10-25 12:17:06.858694','2026-10-04','CANCELLED',1,1,1,NULL,7),(8,'2026-10-25 08:04:22.874281','2026-10-25 12:17:06.858742','2026-10-04','CANCELLED',1,1,1,NULL,8),(9,'2026-10-25 08:04:22.875673','2026-10-25 12:17:06.858791','2026-10-04','CANCELLED',1,1,1,NULL,9),(10,'2026-10-25 08:04:22.876982','2026-10-25 12:17:06.858843','2026-10-04','CANCELLED',1,1,1,NULL,10),(11,'2026-10-25 08:04:22.878342','2026-10-25 08:12:38.544179','2026-10-11','MISSED',1,1,1,NULL,1),(12,'2026-10-25 08:04:22.879755','2026-10-25 08:12:38.544223','2026-10-11','MISSED',1,1,1,NULL,2),(13,'2026-10-25 08:04:22.881159','2026-10-25 08:12:38.544263','2026-10-11','MISSED',1,1,1,NULL,3),(14,'2026-10-25 08:04:22.882701','2026-10-25 08:12:38.544309','2026-10-11','MISSED',1,1,1,NULL,5),(15,'2026-10-25 08:04:22.884475','2026-10-25 08:12:38.544359','2026-10-11','MISSED',1,1,1,NULL,4),(16,'2026-10-25 08:04:22.886040','2026-10-25 08:12:38.544400','2026-10-11','MISSED',1,1,1,NULL,6),(17,'2026-10-25 08:04:22.887330','2026-10-25 08:12:38.544440','2026-10-11','MISSED',1,1,1,NULL,7),(18,'2026-10-25 08:04:22.888545','2026-10-25 08:12:38.544486','2026-10-11','MISSED',1,1,1,NULL,8),(19,'2026-10-25 08:04:22.890546','2026-10-25 08:12:38.544527','2026-10-11','MISSED',1,1,1,NULL,9),(20,'2026-10-25 08:04:22.892089','2026-10-25 08:12:38.544570','2026-10-11','MISSED',1,1,1,NULL,10),(21,'2026-10-25 08:04:22.893425','2026-10-25 08:12:38.544613','2026-10-18','MISSED',1,1,1,NULL,1),(22,'2026-10-25 08:04:22.908444','2026-10-25 08:12:38.544659','2026-10-18','MISSED',1,1,1,NULL,2),(23,'2026-10-25 08:04:22.912176','2026-10-25 08:12:38.544709','2026-10-18','MISSED',1,1,1,NULL,3),(24,'2026-10-25 08:04:22.914228','2026-10-25 08:12:38.544750','2026-10-18','MISSED',1,1,1,NULL,5),(25,'2026-10-25 08:04:22.916048','2026-10-25 08:12:38.544796','2026-10-18','MISSED',1,1,1,NULL,4),(26,'2026-10-25 08:04:22.917731','2026-10-25 08:12:38.544837','2026-10-18','MISSED',1,1,1,NULL,6),(27,'2026-10-25 08:04:22.919772','2026-10-25 08:12:38.544877','2026-10-18','MISSED',1,1,1,NULL,7),(28,'2026-10-25 08:04:22.922252','2026-10-25 08:12:38.544918','2026-10-18','MISSED',1,1,1,NULL,8),(29,'2026-10-25 08:04:22.924923','2026-10-25 08:12:38.544983','2026-10-18','MISSED',1,1,1,NULL,9),(30,'2026-10-25 08:04:22.926866','2026-10-25 08:12:38.545033','2026-10-18','MISSED',1,1,1,NULL,10),(31,'2026-10-25 08:04:22.928679','2026-10-25 12:16:38.200860','2026-10-25','CANCELLED',1,1,1,NULL,1),(32,'2026-10-25 08:04:22.930275','2026-10-25 12:16:38.203176','2026-10-25','CANCELLED',1,1,1,NULL,2),(33,'2026-10-25 08:04:22.932286','2026-10-25 12:16:38.203282','2026-10-25','CANCELLED',1,1,1,NULL,3),(34,'2026-10-25 08:04:22.933986','2026-10-25 12:16:38.203357','2026-10-25','CANCELLED',1,1,1,NULL,5),(35,'2026-10-25 08:04:22.936128','2026-10-25 12:16:38.203426','2026-10-25','CANCELLED',1,1,1,NULL,4),(36,'2026-10-25 08:04:22.938147','2026-10-25 12:16:38.203494','2026-10-25','CANCELLED',1,1,1,NULL,6),(37,'2026-10-25 08:04:22.939950','2026-10-25 12:16:38.203779','2026-10-25','CANCELLED',1,1,1,NULL,7),(38,'2026-10-25 08:04:22.941429','2026-10-25 12:16:38.203858','2026-10-25','CANCELLED',1,1,1,NULL,8),(39,'2026-10-25 08:04:22.942651','2026-10-25 12:16:38.203939','2026-10-25','CANCELLED',1,1,1,NULL,9),(40,'2026-10-25 08:04:22.943940','2026-10-25 12:16:38.204008','2026-10-25','CANCELLED',1,1,1,NULL,10),(41,'2026-10-25 10:25:20.713595','2026-10-25 12:16:38.204191','2026-10-25','CANCELLED',3,1,1,NULL,13),(42,'2026-10-25 10:25:20.757470','2026-10-25 12:16:38.204270','2026-10-25','CANCELLED',3,1,1,NULL,14),(43,'2026-10-25 10:25:20.760386','2026-10-25 12:16:38.204343','2026-10-25','CANCELLED',3,1,1,NULL,37),(44,'2026-10-25 10:25:20.764050','2026-10-25 12:16:38.204417','2026-10-25','CANCELLED',3,1,1,NULL,38),(45,'2026-10-25 10:31:10.787603','2026-10-25 12:17:06.858890','2026-10-04','CANCELLED',3,1,1,NULL,21),(46,'2026-10-25 10:31:10.836119','2026-10-25 12:17:06.858937','2026-10-04','CANCELLED',3,1,1,NULL,22),(47,'2026-10-25 10:31:10.838465','2026-10-25 12:17:06.858986','2026-10-04','CANCELLED',3,1,1,NULL,23),(48,'2026-10-25 10:31:10.840709','2026-10-25 12:17:06.859033','2026-10-04','CANCELLED',3,1,1,NULL,24),(49,'2026-10-25 10:31:10.842996','2026-10-25 12:17:06.859096','2026-10-04','CANCELLED',3,1,1,NULL,45),(50,'2026-10-25 10:31:10.845269','2026-10-25 12:17:06.859149','2026-10-04','CANCELLED',3,1,1,NULL,49),(51,'2026-10-25 10:31:10.847419','2026-10-25 12:17:06.859201','2026-10-04','CANCELLED',3,1,1,NULL,44),(52,'2026-10-25 10:31:10.849649','2026-10-25 12:17:06.859247','2026-10-04','CANCELLED',3,1,1,NULL,48),(53,'2026-10-25 10:31:10.852063','2026-10-25 12:17:06.859298','2026-10-04','CANCELLED',3,1,1,NULL,50),(54,'2026-10-25 10:31:10.853901','2026-10-25 12:17:06.859351','2026-10-04','CANCELLED',3,1,1,NULL,51),(55,'2026-10-25 10:31:10.855754','2026-10-25 10:45:53.061584','2026-10-11','MISSED',3,3,1,NULL,21),(56,'2026-10-25 10:31:10.857669','2026-10-25 10:45:53.061625','2026-10-11','MISSED',3,3,1,NULL,22),(57,'2026-10-25 10:31:10.859990','2026-10-25 10:45:53.061671','2026-10-11','MISSED',3,3,1,NULL,23),(58,'2026-10-25 10:31:10.861919','2026-10-25 10:45:53.061719','2026-10-11','MISSED',3,3,1,NULL,24),(59,'2026-10-25 10:31:10.863581','2026-10-25 10:45:53.061779','2026-10-11','MISSED',3,3,1,NULL,45),(60,'2026-10-25 10:31:10.865462','2026-10-25 10:45:53.061820','2026-10-11','MISSED',3,3,1,NULL,49),(61,'2026-10-25 10:31:10.868430','2026-10-25 10:45:53.061863','2026-10-11','MISSED',3,3,1,NULL,44),(62,'2026-10-25 10:31:10.870544','2026-10-25 10:45:53.061905','2026-10-11','MISSED',3,3,1,NULL,48),(63,'2026-10-25 10:31:10.872425','2026-10-25 10:45:53.061944','2026-10-11','MISSED',3,3,1,NULL,50),(64,'2026-10-25 10:31:10.874381','2026-10-25 10:45:53.061982','2026-10-11','MISSED',3,3,1,NULL,51),(65,'2026-10-25 10:31:10.876242','2026-10-25 10:45:53.062021','2026-10-18','MISSED',3,3,1,NULL,21),(66,'2026-10-25 10:31:10.877886','2026-10-25 10:45:53.062060','2026-10-18','MISSED',3,3,1,NULL,22),(67,'2026-10-25 10:31:10.879478','2026-10-25 10:45:53.062110','2026-10-18','MISSED',3,3,1,NULL,23),(68,'2026-10-25 10:31:10.881085','2026-10-25 10:45:53.062167','2026-10-18','MISSED',3,3,1,NULL,24),(69,'2026-10-25 10:31:10.883302','2026-10-25 10:45:53.062519','2026-10-18','MISSED',3,3,1,NULL,45),(70,'2026-10-25 10:31:10.885200','2026-10-25 10:45:53.062631','2026-10-18','MISSED',3,3,1,NULL,49),(71,'2026-10-25 10:31:10.886995','2026-10-25 10:45:53.062702','2026-10-18','MISSED',3,3,1,NULL,44),(72,'2026-10-25 10:31:10.888705','2026-10-25 10:45:53.062763','2026-10-18','MISSED',3,3,1,NULL,48),(73,'2026-10-25 10:31:10.890524','2026-10-25 10:45:53.062812','2026-10-18','MISSED',3,3,1,NULL,50),(74,'2026-10-25 10:31:10.892310','2026-10-25 10:45:53.062857','2026-10-18','MISSED',3,3,1,NULL,51),(75,'2026-10-25 10:31:10.894000','2026-10-25 12:16:38.204491','2026-10-25','CANCELLED',3,1,1,NULL,21),(76,'2026-10-25 10:31:10.895529','2026-10-25 12:16:38.204563','2026-10-25','CANCELLED',3,1,1,NULL,22),(77,'2026-10-25 10:31:10.897156','2026-10-25 12:16:38.204629','2026-10-25','CANCELLED',3,1,1,NULL,23),(78,'2026-10-25 10:31:10.899143','2026-10-25 12:16:38.204693','2026-10-25','CANCELLED',3,1,1,NULL,24),(79,'2026-10-25 10:31:10.900869','2026-10-25 12:16:38.204758','2026-10-25','CANCELLED',3,1,1,NULL,45),(80,'2026-10-25 10:31:10.902500','2026-10-25 12:16:38.204823','2026-10-25','CANCELLED',3,1,1,NULL,49),(81,'2026-10-25 10:31:10.904009','2026-10-25 12:16:38.204889','2026-10-25','CANCELLED',3,1,1,NULL,44),(82,'2026-10-25 10:31:10.905682','2026-10-25 12:16:38.204955','2026-10-25','CANCELLED',3,1,1,NULL,48),(83,'2026-10-25 10:31:10.907271','2026-10-25 12:16:38.205023','2026-10-25','CANCELLED',3,1,1,NULL,50),(84,'2026-10-25 10:31:10.909134','2026-10-25 12:16:38.205114','2026-10-25','CANCELLED',3,1,1,NULL,51);
/*!40000 ALTER TABLE `lesson` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mark`
--

DROP TABLE IF EXISTS `mark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mark` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `score` double DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `assessment_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mark_assessment_student` (`assessment_id`,`student_id`),
  KEY `FKkinjhmdsofm3luu3s61riey70` (`created_by`),
  KEY `FKf0ptqhygubgskvp43qv990yem` (`updated_by`),
  KEY `FKn248ppigyamh8nsuq92xohjmb` (`school_id`),
  KEY `FKcwocngy0rfmqdhqwm3qlrfamx` (`student_id`),
  CONSTRAINT `FK34mmrpjds2brm47oyit66qp12` FOREIGN KEY (`assessment_id`) REFERENCES `assessment` (`id`),
  CONSTRAINT `FKcwocngy0rfmqdhqwm3qlrfamx` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKf0ptqhygubgskvp43qv990yem` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKkinjhmdsofm3luu3s61riey70` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKn248ppigyamh8nsuq92xohjmb` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mark`
--

LOCK TABLES `mark` WRITE;
/*!40000 ALTER TABLE `mark` DISABLE KEYS */;
/*!40000 ALTER TABLE `mark` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `body` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `edited_at` datetime(6) DEFAULT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `sent_at` datetime(6) NOT NULL,
  `subject` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `thread_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `recipient_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6almbf6e2si9ese16dnm7y6af` (`created_by`),
  KEY `FKrk5juxsjvbiekbdk16fptbeo5` (`updated_by`),
  KEY `FK31iau3dm2k4xfkfqkbfhoyq0e` (`school_id`),
  KEY `FKiup8wew331d92o7u3k8d918o3` (`recipient_id`),
  KEY `FKcnj2qaf5yc36v2f90jw2ipl9b` (`sender_id`),
  CONSTRAINT `FK31iau3dm2k4xfkfqkbfhoyq0e` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK6almbf6e2si9ese16dnm7y6af` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKcnj2qaf5yc36v2f90jw2ipl9b` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKiup8wew331d92o7u3k8d918o3` FOREIGN KEY (`recipient_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrk5juxsjvbiekbdk16fptbeo5` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,'2026-10-24 21:55:18.913281','2026-10-24 21:55:18.913281','Hi, hojoang, neke batla ho bona na eya sebetsa.',NULL,NULL,NULL,'2026-10-24 21:55:18.904520','Testing','2d9a6a8d-635f-4b37-94cd-9c9bf5bf1777',1,1,1,2,1);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent`
--

DROP TABLE IF EXISTS `parent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parent` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `relationship` enum('FATHER','GUARDIAN','MOTHER','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `invited_at` datetime(6) DEFAULT NULL,
  `login_enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2er3k7pagjgspesr4bn7a2aa3` (`user_id`),
  KEY `FKk4k6gnc6lxl1xmccdweqoisnq` (`created_by`),
  KEY `FKemnkag2r5j9pw1ig5ixc3w3ot` (`updated_by`),
  KEY `FKb4ivdsgti4js74ipm3beexxtq` (`school_id`),
  CONSTRAINT `FKb4ivdsgti4js74ipm3beexxtq` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKemnkag2r5j9pw1ig5ixc3w3ot` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKk4k6gnc6lxl1xmccdweqoisnq` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKl4w905h1321m2ide56ov5efb0` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent`
--

LOCK TABLES `parent` WRITE;
/*!40000 ALTER TABLE `parent` DISABLE KEYS */;
INSERT INTO `parent` VALUES (1,'2026-10-24 21:51:56.303779','2026-10-24 21:51:56.303779','MOTHER',1,1,1,6,NULL,1);
/*!40000 ALTER TABLE `parent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `used` bit(1) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg0guo4k8krgpwuagos61oc06j` (`token`),
  KEY `FKk6io9704rmrrs91o69qc4eiro` (`created_by`),
  KEY `FKimll1p3v5ube4fifkf4gymamc` (`updated_by`),
  KEY `FK5lwtbncug84d4ero33v3cfxvl` (`user_id`),
  CONSTRAINT `FK5lwtbncug84d4ero33v3cfxvl` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKimll1p3v5ube4fifkf4gymamc` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKk6io9704rmrrs91o69qc4eiro` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `details` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `label` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int NOT NULL,
  `type` enum('CARD','CASH','ECOCASH','EFT','MPESA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbg104x5rtyc47w2fv4gqbwp7p` (`created_by`),
  KEY `FK2lydyj5svk6m6kt1pys0f5jr8` (`updated_by`),
  CONSTRAINT `FK2lydyj5svk6m6kt1pys0f5jr8` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbg104x5rtyc47w2fv4gqbwp7p` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `period`
--

DROP TABLE IF EXISTS `period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `period` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `end_time` time NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_time` time NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LESSON',
  PRIMARY KEY (`id`),
  KEY `FKcqkr0eqjwuo8f46xrle2rnrr1` (`created_by`),
  KEY `FKon89sa08kdum2skkbetunes4x` (`updated_by`),
  KEY `FK97qls3bc7g0tdkv0mbgm7a08x` (`school_id`),
  CONSTRAINT `FK97qls3bc7g0tdkv0mbgm7a08x` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKcqkr0eqjwuo8f46xrle2rnrr1` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKon89sa08kdum2skkbetunes4x` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `period`
--

LOCK TABLES `period` WRITE;
/*!40000 ALTER TABLE `period` DISABLE KEYS */;
INSERT INTO `period` VALUES (1,'2026-10-25 07:03:48.854565','2026-10-25 07:03:48.854565','08:40:00','Period 1','08:00:00',1,1,1,'LESSON'),(2,'2026-10-25 07:04:13.151945','2026-10-25 07:04:13.151945','09:20:00','Period 2','08:40:00',1,1,1,'LESSON'),(3,'2026-10-25 07:04:29.155673','2026-10-25 07:04:29.155673','10:00:00','Period 3','09:20:00',1,1,1,'LESSON'),(4,'2026-10-25 07:04:44.854568','2026-10-25 07:04:44.854568','10:40:00','Period 4','10:00:00',1,1,1,'LESSON'),(5,'2026-10-25 07:04:57.404752','2026-10-25 07:04:57.404752','11:00:00','Break','10:40:00',1,1,1,'BREAK'),(6,'2026-10-25 07:05:14.328473','2026-10-25 07:05:14.328473','11:40:00','Period 5','11:00:00',1,1,1,'LESSON'),(7,'2026-10-25 07:05:39.315815','2026-10-25 07:05:39.315815','12:20:00','Period 6','11:40:00',1,1,1,'LESSON'),(8,'2026-10-25 07:05:59.468840','2026-10-25 07:05:59.468840','13:00:00','Period 7','12:20:00',1,1,1,'LESSON'),(9,'2026-10-25 07:06:18.441578','2026-10-25 07:06:18.441578','14:00:00','Lunch','13:00:00',1,1,1,'LUNCH'),(10,'2026-10-25 07:06:39.941481','2026-10-25 07:06:39.941481','14:40:00','Period 8','14:00:00',1,1,1,'LESSON'),(11,'2026-10-25 07:07:04.896419','2026-10-25 07:07:04.896419','15:20:00','Period 9','14:40:00',1,1,1,'LESSON'),(12,'2026-10-25 07:07:26.477185','2026-10-25 07:07:26.477185','16:00:00','Period 10','15:20:00',1,1,1,'LESSON'),(13,'2026-10-25 14:23:15.380641','2026-10-25 14:53:47.646560','17:00:00','Sports','16:00:00',1,1,1,'BREAK');
/*!40000 ALTER TABLE `period` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plan_price`
--

DROP TABLE IF EXISTS `plan_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_price` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `currency` enum('LSL','USD','ZAR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `plan` enum('ANNUAL','FREE_TRIAL','MONTHLY','ONE_OFF') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_price_plan_currency` (`plan`,`currency`),
  KEY `FKg5bkuv3hlfummqxx9ncy2b8lp` (`created_by`),
  KEY `FKrxg9sry8k4auq0gs21flwaxsw` (`updated_by`),
  CONSTRAINT `FKg5bkuv3hlfummqxx9ncy2b8lp` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrxg9sry8k4auq0gs21flwaxsw` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plan_price`
--

LOCK TABLES `plan_price` WRITE;
/*!40000 ALTER TABLE `plan_price` DISABLE KEYS */;
/*!40000 ALTER TABLE `plan_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion`
--

DROP TABLE IF EXISTS `promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `batch_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `new_status` enum('ACTIVE','COMPLETED','GRADUATED','INACTIVE','TRANSFERRED','WITHDRAWN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `previous_status` enum('ACTIVE','COMPLETED','GRADUATED','INACTIVE','TRANSFERRED','WITHDRAWN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `promoted_at` datetime(6) NOT NULL,
  `reason` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `from_class_id` bigint DEFAULT NULL,
  `promoted_by` bigint DEFAULT NULL,
  `student_id` bigint NOT NULL,
  `to_class_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKed4e0hg9wgaqem0ejlqj0wifh` (`created_by`),
  KEY `FKo508qkqpdndm8plrb209w2egk` (`updated_by`),
  KEY `FKi6l70rrq70kc468fmjfretl3q` (`school_id`),
  KEY `FKpx3sjkfsr6pdltf2s3lrx6g9q` (`from_class_id`),
  KEY `FKf2j5vqp8rdm93nofhdorphxjl` (`promoted_by`),
  KEY `FK8m73yaibi63gpxe9l0nk3ntdw` (`student_id`),
  KEY `FKksi3lp8w9bllni8pmmi03ndq9` (`to_class_id`),
  CONSTRAINT `FK8m73yaibi63gpxe9l0nk3ntdw` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKed4e0hg9wgaqem0ejlqj0wifh` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKf2j5vqp8rdm93nofhdorphxjl` FOREIGN KEY (`promoted_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKi6l70rrq70kc468fmjfretl3q` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKksi3lp8w9bllni8pmmi03ndq9` FOREIGN KEY (`to_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKo508qkqpdndm8plrb209w2egk` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKpx3sjkfsr6pdltf2s3lrx6g9q` FOREIGN KEY (`from_class_id`) REFERENCES `school_class` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion`
--

LOCK TABLES `promotion` WRITE;
/*!40000 ALTER TABLE `promotion` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `result_audit`
--

DROP TABLE IF EXISTS `result_audit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `result_audit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `new_mark` double DEFAULT NULL,
  `previous_mark` double DEFAULT NULL,
  `reason` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `changed_by` bigint DEFAULT NULL,
  `term_result_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKg9ay3v3kcxuhqbb555f6ys2t7` (`created_by`),
  KEY `FKatc4fcgr6w7s91ny6b92fr9i3` (`updated_by`),
  KEY `FK87qiontcwrcy7k19govwfljmj` (`school_id`),
  KEY `FK4eer9u2l5xd3cm178a5m633tg` (`changed_by`),
  KEY `FKfr1pkrire21p6nb76grbkw8oh` (`term_result_id`),
  CONSTRAINT `FK4eer9u2l5xd3cm178a5m633tg` FOREIGN KEY (`changed_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK87qiontcwrcy7k19govwfljmj` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKatc4fcgr6w7s91ny6b92fr9i3` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKfr1pkrire21p6nb76grbkw8oh` FOREIGN KEY (`term_result_id`) REFERENCES `term_result` (`id`),
  CONSTRAINT `FKg9ay3v3kcxuhqbb555f6ys2t7` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `result_audit`
--

LOCK TABLES `result_audit` WRITE;
/*!40000 ALTER TABLE `result_audit` DISABLE KEYS */;
/*!40000 ALTER TABLE `result_audit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `school`
--

DROP TABLE IF EXISTS `school`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `school` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `absence_alerts_enabled` bit(1) NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_phone` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `currency` enum('LSL','USD','ZAR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `term_structure` enum('FOUR_QUARTER','ONE_TERM','THREE_TERM','TWO_TERM') COLLATE utf8mb4_unicode_ci NOT NULL,
  `timezone` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `logo_file_id` bigint DEFAULT NULL,
  `timetable_published` tinyint(1) NOT NULL DEFAULT '0',
  `timetable_published_at` datetime(6) DEFAULT NULL,
  `timetable_published_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK198to0luchbqx6stua8bu0cm6` (`contact_email`),
  KEY `FK2j8om9pcx7sm6ysicjano7sav` (`created_by`),
  KEY `FK5vurmoolo6lsco4y9dte87t9w` (`updated_by`),
  KEY `FK1md2635hyiqaax3t1hs4p92at` (`logo_file_id`),
  KEY `FKbem7bt3wohmk2s92ywlpv4qgq` (`timetable_published_by_id`),
  CONSTRAINT `FK1md2635hyiqaax3t1hs4p92at` FOREIGN KEY (`logo_file_id`) REFERENCES `data_file` (`id`),
  CONSTRAINT `FK2j8om9pcx7sm6ysicjano7sav` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK5vurmoolo6lsco4y9dte87t9w` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbem7bt3wohmk2s92ywlpv4qgq` FOREIGN KEY (`timetable_published_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school`
--

LOCK TABLES `school` WRITE;
/*!40000 ALTER TABLE `school` DISABLE KEYS */;
INSERT INTO `school` VALUES (1,'2026-10-24 21:29:53.676338','2026-10-25 14:48:43.119626',_binary '','Leribe, Pitseng, Tsoinyane','info@tsoinyane.org','63274567','LSL','Tsoinyane High School','ACTIVE','THREE_TERM','Africa/Maseru',NULL,NULL,NULL,1,'2026-10-25 14:48:43.092004',1);
/*!40000 ALTER TABLE `school` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `school_class`
--

DROP TABLE IF EXISTS `school_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `school_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `grade_level` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `academic_year_id` bigint NOT NULL,
  `class_teacher_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9iki4mt8nhikj87o5w0omablt` (`created_by`),
  KEY `FKr5dritubrggswmt6auw4e6l7a` (`updated_by`),
  KEY `FK2br5afl4106t79kv6m2bgwu8b` (`school_id`),
  KEY `FKjo25cg2moss7sur93l1yx2y0f` (`academic_year_id`),
  KEY `FKs2j6qwf0filaxsx400l5wqa4i` (`class_teacher_id`),
  CONSTRAINT `FK2br5afl4106t79kv6m2bgwu8b` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK9iki4mt8nhikj87o5w0omablt` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKjo25cg2moss7sur93l1yx2y0f` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year` (`id`),
  CONSTRAINT `FKr5dritubrggswmt6auw4e6l7a` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKs2j6qwf0filaxsx400l5wqa4i` FOREIGN KEY (`class_teacher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school_class`
--

LOCK TABLES `school_class` WRITE;
/*!40000 ALTER TABLE `school_class` DISABLE KEYS */;
INSERT INTO `school_class` VALUES (1,'2026-10-24 21:42:38.049871','2026-10-25 07:33:27.951346',8,'Grade 8A',1,1,1,1,3),(2,'2026-10-24 21:43:08.067175','2026-10-25 07:33:32.618512',8,'Grade 8B',1,1,1,1,7),(3,'2026-10-24 21:43:21.948122','2026-10-25 07:33:38.414098',9,'Grade 9A',1,1,1,1,9),(4,'2026-10-24 21:43:34.876159','2026-10-25 07:33:44.482695',9,'Grade 9B',1,1,1,1,8),(5,'2026-10-24 21:43:46.179324','2026-10-25 07:33:59.127659',10,'Grade 10',1,1,1,1,29),(6,'2026-10-24 21:44:02.052262','2026-10-25 07:34:03.734918',11,'Grade 11',1,1,1,1,28);
/*!40000 ALTER TABLE `school_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `school_holiday`
--

DROP TABLE IF EXISTS `school_holiday`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `school_holiday` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `end_date` date NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `recurring` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrv25r7e8pwjrgrlc40ca02vub` (`created_by`),
  KEY `FKmpopqwuqempgshd9b37m8txui` (`updated_by`),
  KEY `FKru7fodwmn6sgbcngimkeohlhb` (`school_id`),
  CONSTRAINT `FKmpopqwuqempgshd9b37m8txui` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKru7fodwmn6sgbcngimkeohlhb` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKrv25r7e8pwjrgrlc40ca02vub` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school_holiday`
--

LOCK TABLES `school_holiday` WRITE;
/*!40000 ALTER TABLE `school_holiday` DISABLE KEYS */;
INSERT INTO `school_holiday` VALUES (1,'2026-10-25 12:05:07.440040','2026-10-25 12:14:01.520613','2026-02-21','Armed Forces Day','2026-02-21',1,1,1,_binary ''),(2,'2026-10-25 12:07:31.407171','2026-10-25 12:13:57.907480','2026-03-11','Moshoeshoe\'s Birthday','2026-03-11',1,1,1,_binary ''),(3,'2026-10-25 12:07:54.892219','2026-10-25 12:13:54.438987','2026-03-21','Human Rights Day','2026-03-21',1,1,1,_binary ''),(4,'2026-10-25 12:13:48.874511','2026-10-25 12:13:48.874511','2026-04-27','Freedom Day','2026-04-27',1,1,1,_binary ''),(5,'2026-10-25 12:14:39.606019','2026-10-25 12:14:39.606019','2026-05-01','Workers\' Day','2026-05-01',1,1,1,_binary ''),(6,'2026-10-25 12:14:59.843339','2026-10-25 12:14:59.843339','2026-05-10','Mother\'s Day','2026-05-10',1,1,1,_binary ''),(7,'2026-10-25 12:15:25.483827','2026-10-25 12:15:25.483827','2026-05-25','Africa Day','2026-05-25',1,1,1,_binary ''),(8,'2026-10-25 12:15:55.426877','2026-10-25 12:15:55.426877','2026-06-16','Youth Day','2026-06-16',1,1,1,_binary ''),(9,'2026-10-25 12:16:14.501242','2026-10-25 12:16:14.501242','2026-06-21','Father\'s Day','2026-06-21',1,1,1,_binary ''),(10,'2026-10-25 12:16:38.108093','2026-10-25 14:03:02.299836','2026-07-17','King\'s Birthday','2026-07-17',1,1,1,_binary ''),(11,'2026-10-25 12:17:06.838205','2026-10-25 12:17:06.838205','2026-10-04','Independance Day','2026-10-04',1,1,1,_binary ''),(12,'2026-10-25 12:17:45.847940','2026-10-25 12:17:45.847940','2026-12-25','Christmas Day','2026-12-25',1,1,1,_binary ''),(13,'2026-10-25 12:18:04.469621','2026-10-25 12:18:04.469621','2026-12-26','Boxing Day','2026-12-26',1,1,1,_binary '');
/*!40000 ALTER TABLE `school_holiday` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `admission_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_of_birth` datetime(6) NOT NULL,
  `gender` enum('FEMALE','MALE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('ACTIVE','COMPLETED','GRADUATED','INACTIVE','TRANSFERRED','WITHDRAWN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `current_class_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt405ejyqvybyy9iw9all2jiin` (`admission_number`),
  UNIQUE KEY `UKbkix9btnoi1n917ll7bplkvg5` (`user_id`),
  KEY `FK6vp56dswb08f05yofg4j52qxi` (`created_by`),
  KEY `FKe7y9osymb78ehvma3viufqmag` (`updated_by`),
  KEY `FK1vm0oqhk9viil6eocn49rj1l9` (`school_id`),
  KEY `FKrlnefp3qvs42v7urk1hi1v4kl` (`current_class_id`),
  CONSTRAINT `FK1vm0oqhk9viil6eocn49rj1l9` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK6vp56dswb08f05yofg4j52qxi` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKe7y9osymb78ehvma3viufqmag` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKk5m148xqefonqw7bgnpm0snwj` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrlnefp3qvs42v7urk1hi1v4kl` FOREIGN KEY (`current_class_id`) REFERENCES `school_class` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,'2026-10-24 21:51:10.692173','2026-10-25 17:44:23.114145','2026-000001','2026-10-27 00:00:00.000000','MALE','ACTIVE',1,1,1,1,5),(2,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000002','2008-01-02 00:00:00.000000','MALE','ACTIVE',1,1,1,1,46),(3,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000003','2008-01-03 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,47),(4,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000004','2008-01-04 00:00:00.000000','MALE','ACTIVE',1,1,1,3,48),(5,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000005','2008-01-05 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,49),(6,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000006','2008-01-06 00:00:00.000000','MALE','ACTIVE',1,1,1,5,50),(7,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000007','2008-01-07 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,51),(8,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000008','2008-01-08 00:00:00.000000','MALE','ACTIVE',1,1,1,1,52),(9,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000009','2008-01-09 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,53),(10,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000010','2008-01-10 00:00:00.000000','MALE','ACTIVE',1,1,1,3,54),(11,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000011','2008-01-11 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,55),(12,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000012','2008-01-12 00:00:00.000000','MALE','ACTIVE',1,1,1,5,56),(13,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000013','2008-01-13 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,57),(14,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000014','2008-01-14 00:00:00.000000','MALE','ACTIVE',1,1,1,1,58),(15,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000015','2008-01-15 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,59),(16,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000016','2008-01-16 00:00:00.000000','MALE','ACTIVE',1,1,1,3,60),(17,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000017','2008-01-17 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,61),(18,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000018','2008-01-18 00:00:00.000000','MALE','ACTIVE',1,1,1,5,62),(19,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000019','2008-01-19 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,63),(20,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000020','2008-01-20 00:00:00.000000','MALE','ACTIVE',1,1,1,1,64),(21,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000021','2008-01-21 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,65),(22,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000022','2008-01-22 00:00:00.000000','MALE','ACTIVE',1,1,1,3,66),(23,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000023','2008-01-23 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,67),(24,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000024','2008-01-24 00:00:00.000000','MALE','ACTIVE',1,1,1,5,68),(25,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000025','2008-01-25 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,69),(26,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000026','2008-01-26 00:00:00.000000','MALE','ACTIVE',1,1,1,1,70),(27,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000027','2008-01-27 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,71),(28,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000028','2008-01-28 00:00:00.000000','MALE','ACTIVE',1,1,1,3,72),(29,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000029','2008-01-29 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,73),(30,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000030','2008-01-30 00:00:00.000000','MALE','ACTIVE',1,1,1,5,74),(31,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000031','2008-01-31 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,75),(32,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000032','2008-02-01 00:00:00.000000','MALE','ACTIVE',1,1,1,1,76),(33,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000033','2008-02-02 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,77),(34,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000034','2008-02-03 00:00:00.000000','MALE','ACTIVE',1,1,1,3,78),(35,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000035','2008-02-04 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,79),(36,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000036','2008-02-05 00:00:00.000000','MALE','ACTIVE',1,1,1,5,80),(37,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000037','2008-02-06 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,81),(38,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000038','2008-02-07 00:00:00.000000','MALE','ACTIVE',1,1,1,1,82),(39,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000039','2008-02-08 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,83),(40,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000040','2008-02-09 00:00:00.000000','MALE','ACTIVE',1,1,1,3,84),(41,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000041','2008-02-10 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,85),(42,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000042','2008-02-11 00:00:00.000000','MALE','ACTIVE',1,1,1,5,86),(43,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000043','2008-02-12 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,87),(44,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000044','2008-02-13 00:00:00.000000','MALE','ACTIVE',1,1,1,1,88),(45,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000045','2008-02-14 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,89),(46,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000046','2008-02-15 00:00:00.000000','MALE','ACTIVE',1,1,1,3,90),(47,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000047','2008-02-16 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,91),(48,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000048','2008-02-17 00:00:00.000000','MALE','ACTIVE',1,1,1,5,92),(49,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000049','2008-02-18 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,93),(50,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000050','2008-02-19 00:00:00.000000','MALE','ACTIVE',1,1,1,1,94),(51,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000051','2008-02-20 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,95),(52,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000052','2008-02-21 00:00:00.000000','MALE','ACTIVE',1,1,1,3,96),(53,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000053','2008-02-22 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,97),(54,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000054','2008-02-23 00:00:00.000000','MALE','ACTIVE',1,1,1,5,98),(55,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000055','2008-02-24 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,99),(56,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000056','2008-02-25 00:00:00.000000','MALE','ACTIVE',1,1,1,1,100),(57,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000057','2008-02-26 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,101),(58,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000058','2008-02-27 00:00:00.000000','MALE','ACTIVE',1,1,1,3,102),(59,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000059','2008-02-28 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,103),(60,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000060','2008-02-29 00:00:00.000000','MALE','ACTIVE',1,1,1,5,104),(61,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000061','2008-03-01 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,105),(62,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000062','2008-03-02 00:00:00.000000','MALE','ACTIVE',1,1,1,1,106),(63,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000063','2008-03-03 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,107),(64,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000064','2008-03-04 00:00:00.000000','MALE','ACTIVE',1,1,1,3,108),(65,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000065','2008-03-05 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,109),(66,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000066','2008-03-06 00:00:00.000000','MALE','ACTIVE',1,1,1,5,110),(67,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000067','2008-03-07 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,111),(68,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000068','2008-03-08 00:00:00.000000','MALE','ACTIVE',1,1,1,1,112),(69,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000069','2008-03-09 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,113),(70,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000070','2008-03-10 00:00:00.000000','MALE','ACTIVE',1,1,1,3,114),(71,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000071','2008-03-11 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,115),(72,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000072','2008-03-12 00:00:00.000000','MALE','ACTIVE',1,1,1,5,116),(73,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000073','2008-03-13 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,117),(74,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000074','2008-03-14 00:00:00.000000','MALE','ACTIVE',1,1,1,1,118),(75,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000075','2008-03-15 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,119),(76,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000076','2008-03-16 00:00:00.000000','MALE','ACTIVE',1,1,1,3,120),(77,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000077','2008-03-17 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,121),(78,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000078','2008-03-18 00:00:00.000000','MALE','ACTIVE',1,1,1,5,122),(79,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000079','2008-03-19 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,123),(80,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000080','2008-03-20 00:00:00.000000','MALE','ACTIVE',1,1,1,1,124),(81,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000081','2008-03-21 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,125),(82,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000082','2008-03-22 00:00:00.000000','MALE','ACTIVE',1,1,1,3,126),(83,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000083','2008-03-23 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,127),(84,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000084','2008-03-24 00:00:00.000000','MALE','ACTIVE',1,1,1,5,128),(85,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000085','2008-03-25 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,129),(86,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000086','2008-03-26 00:00:00.000000','MALE','ACTIVE',1,1,1,1,130),(87,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000087','2008-03-27 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,131),(88,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000088','2008-03-28 00:00:00.000000','MALE','ACTIVE',1,1,1,3,132),(89,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000089','2008-03-29 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,133),(90,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000090','2008-03-30 00:00:00.000000','MALE','ACTIVE',1,1,1,5,134),(91,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000091','2008-03-31 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,135),(92,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000092','2008-04-01 00:00:00.000000','MALE','ACTIVE',1,1,1,1,136),(93,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000093','2008-04-02 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,137),(94,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000094','2008-04-03 00:00:00.000000','MALE','ACTIVE',1,1,1,3,138),(95,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000095','2008-04-04 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,139),(96,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000096','2008-04-05 00:00:00.000000','MALE','ACTIVE',1,1,1,5,140),(97,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000097','2008-04-06 00:00:00.000000','FEMALE','ACTIVE',1,1,1,6,141),(98,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000098','2008-04-07 00:00:00.000000','MALE','ACTIVE',1,1,1,1,142),(99,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000099','2008-04-08 00:00:00.000000','FEMALE','ACTIVE',1,1,1,2,143),(100,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000100','2008-04-09 00:00:00.000000','MALE','ACTIVE',1,1,1,3,144),(101,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000','2026-000101','2008-04-10 00:00:00.000000','FEMALE','ACTIVE',1,1,1,4,145);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_fee_account`
--

DROP TABLE IF EXISTS `student_fee_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_fee_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount_paid` double NOT NULL,
  `last_reminder_amount` double DEFAULT NULL,
  `last_reminder_sent_at` datetime(6) DEFAULT NULL,
  `last_statement_sent_at` datetime(6) DEFAULT NULL,
  `total_adjustments` double NOT NULL,
  `total_fees` double NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `school_class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_fee_account` (`student_id`,`term_id`),
  KEY `FK8l4ec26kl8hm4loyif0pspovp` (`created_by`),
  KEY `FKn66hg6gtxty08ml0htf73rvtf` (`updated_by`),
  KEY `FKss16u25570iu7tq9w6ewqk4ch` (`school_id`),
  KEY `FKkd69owqmwgvv4awmsh4xdjnhc` (`school_class_id`),
  KEY `FKl41lm5gum8vgk4f44kpmeglni` (`term_id`),
  CONSTRAINT `FK6rh2b1awncxpgvs1vofp0xarx` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FK8l4ec26kl8hm4loyif0pspovp` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKkd69owqmwgvv4awmsh4xdjnhc` FOREIGN KEY (`school_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKl41lm5gum8vgk4f44kpmeglni` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`),
  CONSTRAINT `FKn66hg6gtxty08ml0htf73rvtf` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKss16u25570iu7tq9w6ewqk4ch` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=405 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_account`
--

LOCK TABLES `student_fee_account` WRITE;
/*!40000 ALTER TABLE `student_fee_account` DISABLE KEYS */;
INSERT INTO `student_fee_account` VALUES (1,'2026-10-25 08:05:52.982532','2026-10-25 08:12:08.552556',0,NULL,NULL,NULL,0,1200,1,1,1,2,3,1),(2,'2026-10-25 08:05:52.982532','2026-10-25 12:34:43.350665',1200,1200,'2026-10-25 08:19:39.162938',NULL,0,1200,1,1,1,1,2,1),(3,'2026-10-25 08:05:52.982656','2026-10-25 08:12:08.552549',0,NULL,NULL,NULL,0,1200,1,1,1,5,6,1),(4,'2026-10-25 08:05:52.982532','2026-10-25 08:12:08.552549',0,NULL,NULL,NULL,0,1200,1,1,1,4,5,1),(5,'2026-10-25 08:05:52.982599','2026-10-25 08:12:08.552556',0,NULL,NULL,NULL,0,1200,1,1,1,3,4,1),(6,'2026-10-25 08:05:52.982669','2026-10-25 08:12:08.552549',0,NULL,NULL,NULL,0,1200,1,1,1,6,7,1),(7,'2026-10-25 08:05:53.015037','2026-10-25 08:12:08.578302',0,NULL,NULL,NULL,0,1200,1,1,1,3,10,1),(8,'2026-10-25 08:05:53.015096','2026-10-25 08:12:08.578302',0,NULL,NULL,NULL,0,1200,1,1,1,5,12,1),(9,'2026-10-25 08:05:53.015501','2026-10-25 08:12:08.578561',0,NULL,NULL,NULL,0,1200,1,1,1,6,13,1),(10,'2026-10-25 08:05:53.016059','2026-10-25 08:12:08.574261',0,NULL,NULL,NULL,0,1200,1,1,1,4,11,1),(11,'2026-10-25 08:05:53.015860','2026-10-25 12:55:23.644119',1200,NULL,NULL,NULL,0,1200,1,1,1,1,8,1),(12,'2026-10-25 08:05:53.016080','2026-10-25 08:12:08.574415',0,NULL,NULL,NULL,0,1200,1,1,1,2,9,1),(13,'2026-10-25 08:05:53.019723','2026-10-25 08:12:08.588670',0,NULL,NULL,NULL,0,1200,1,1,1,3,16,1),(14,'2026-10-25 08:05:53.019688','2026-10-25 08:12:08.588768',0,NULL,NULL,NULL,0,1200,1,1,1,6,19,1),(15,'2026-10-25 08:05:53.019805','2026-10-25 08:12:08.586472',0,NULL,NULL,NULL,0,1200,1,1,1,2,15,1),(16,'2026-10-25 08:05:53.019735','2026-10-25 08:12:08.586277',0,NULL,NULL,NULL,0,1200,1,1,1,4,17,1),(17,'2026-10-25 08:05:53.019688','2026-10-25 08:12:08.617058',0,NULL,NULL,NULL,0,1200,1,1,1,5,18,1),(18,'2026-10-25 08:05:53.020899','2026-10-25 12:57:54.145425',1200,NULL,NULL,NULL,0,1200,1,1,1,1,14,1),(19,'2026-10-25 08:05:53.023886','2026-10-25 08:12:08.626180',0,NULL,NULL,NULL,0,1200,1,1,1,3,22,1),(20,'2026-10-25 08:05:53.023886','2026-10-25 08:12:08.621750',0,NULL,NULL,NULL,0,1200,1,1,1,2,21,1),(21,'2026-10-25 08:05:53.023921','2026-10-25 08:12:08.626700',0,NULL,NULL,NULL,0,1200,1,1,1,5,24,1),(22,'2026-10-25 08:05:53.024499','2026-10-25 08:12:08.624737',0,NULL,NULL,NULL,0,1200,1,1,1,6,25,1),(23,'2026-10-25 08:05:53.024345','2026-10-25 08:12:08.622460',0,NULL,NULL,NULL,0,1200,1,1,1,4,23,1),(24,'2026-10-25 08:05:53.024858','2026-10-25 13:14:28.124692',1200,NULL,NULL,NULL,0,1200,1,1,1,1,20,1),(25,'2026-10-25 08:05:53.027460','2026-10-25 08:12:08.633784',0,NULL,NULL,NULL,0,1200,1,1,1,3,28,1),(26,'2026-10-25 08:05:53.027507','2026-10-25 08:12:08.631314',0,NULL,NULL,NULL,0,1200,1,1,1,2,27,1),(27,'2026-10-25 08:05:53.027867','2026-10-25 08:12:08.635117',0,NULL,NULL,NULL,0,1200,1,1,1,5,30,1),(28,'2026-10-25 08:05:53.027812','2026-10-25 08:12:08.633512',0,NULL,NULL,NULL,0,1200,1,1,1,6,31,1),(29,'2026-10-25 08:05:53.029249','2026-10-25 08:12:08.630858',0,NULL,NULL,NULL,0,1200,1,1,1,4,29,1),(30,'2026-10-25 08:05:53.029724','2026-10-25 08:12:08.633782',0,NULL,NULL,NULL,0,1200,1,1,1,1,26,1),(31,'2026-10-25 08:05:53.031050','2026-10-25 08:12:08.641425',0,NULL,NULL,NULL,0,1200,1,1,1,3,34,1),(32,'2026-10-25 08:05:53.031324','2026-10-25 08:12:08.638991',0,NULL,NULL,NULL,0,1200,1,1,1,2,33,1),(33,'2026-10-25 08:05:53.033313','2026-10-25 08:12:08.641424',0,NULL,NULL,NULL,0,1200,1,1,1,5,36,1),(34,'2026-10-25 08:05:53.033477','2026-10-25 08:12:08.644407',0,NULL,NULL,NULL,0,1200,1,1,1,6,37,1),(35,'2026-10-25 08:05:53.033870','2026-10-25 08:12:08.638829',0,NULL,NULL,NULL,0,1200,1,1,1,4,35,1),(36,'2026-10-25 08:05:53.035007','2026-10-25 08:12:08.647218',0,NULL,NULL,NULL,0,1200,1,1,1,3,40,1),(37,'2026-10-25 08:05:53.035640','2026-10-25 08:12:08.641973',0,NULL,NULL,NULL,0,1200,1,1,1,1,32,1),(38,'2026-10-25 08:05:53.036332','2026-10-25 08:12:08.645646',0,NULL,NULL,NULL,0,1200,1,1,1,2,39,1),(39,'2026-10-25 08:05:53.037396','2026-10-25 08:12:08.652585',0,NULL,NULL,NULL,0,1200,1,1,1,6,43,1),(40,'2026-10-25 08:05:53.037723','2026-10-25 08:12:08.645998',0,NULL,NULL,NULL,0,1200,1,1,1,4,41,1),(41,'2026-10-25 08:05:53.037867','2026-10-25 08:12:08.651414',0,NULL,NULL,NULL,0,1200,1,1,1,5,42,1),(42,'2026-10-25 08:05:53.038231','2026-10-25 08:12:08.657216',0,NULL,NULL,NULL,0,1200,1,1,1,3,46,1),(43,'2026-10-25 08:05:53.040136','2026-10-25 08:12:08.651228',0,NULL,NULL,NULL,0,1200,1,1,1,1,38,1),(44,'2026-10-25 08:05:53.040528','2026-10-25 08:12:08.656028',0,NULL,NULL,NULL,0,1200,1,1,1,2,45,1),(45,'2026-10-25 08:05:53.041566','2026-10-25 08:12:08.659306',0,NULL,NULL,NULL,0,1200,1,1,1,6,49,1),(46,'2026-10-25 08:05:53.041649','2026-10-25 08:12:08.655430',0,NULL,NULL,NULL,0,1200,1,1,1,4,47,1),(47,'2026-10-25 08:05:53.042139','2026-10-25 08:12:08.663456',0,NULL,NULL,NULL,0,1200,1,1,1,3,52,1),(48,'2026-10-25 08:05:53.041994','2026-10-25 08:12:08.658590',0,NULL,NULL,NULL,0,1200,1,1,1,5,48,1),(49,'2026-10-25 08:05:53.043251','2026-10-25 08:12:08.658866',0,NULL,NULL,NULL,0,1200,1,1,1,1,44,1),(50,'2026-10-25 08:05:53.044335','2026-10-25 08:12:08.661534',0,NULL,NULL,NULL,0,1200,1,1,1,2,51,1),(51,'2026-10-25 08:05:53.045622','2026-10-25 08:12:08.668421',0,NULL,NULL,NULL,0,1200,1,1,1,3,58,1),(52,'2026-10-25 08:05:53.045807','2026-10-25 08:12:08.665884',0,NULL,NULL,NULL,0,1200,1,1,1,6,55,1),(53,'2026-10-25 08:05:53.046020','2026-10-25 08:12:08.661942',0,NULL,NULL,NULL,0,1200,1,1,1,4,53,1),(54,'2026-10-25 08:05:53.046019','2026-10-25 08:12:08.664380',0,NULL,NULL,NULL,0,1200,1,1,1,5,54,1),(55,'2026-10-25 08:05:53.047268','2026-10-25 08:12:08.664914',0,NULL,NULL,NULL,0,1200,1,1,1,1,50,1),(56,'2026-10-25 08:05:53.047743','2026-10-25 08:12:08.667016',0,NULL,NULL,NULL,0,1200,1,1,1,2,57,1),(57,'2026-10-25 08:05:53.048700','2026-10-25 08:12:08.673851',0,NULL,NULL,NULL,0,1200,1,1,1,3,64,1),(58,'2026-10-25 08:05:53.049148','2026-10-25 08:12:08.671719',0,NULL,NULL,NULL,0,1200,1,1,1,6,61,1),(59,'2026-10-25 08:05:53.050694','2026-10-25 08:12:08.668262',0,NULL,NULL,NULL,0,1200,1,1,1,4,59,1),(60,'2026-10-25 08:05:53.050731','2026-10-25 08:12:08.669532',0,NULL,NULL,NULL,0,1200,1,1,1,5,60,1),(61,'2026-10-25 08:05:53.050733','2026-10-25 08:12:08.670012',0,NULL,NULL,NULL,0,1200,1,1,1,1,56,1),(62,'2026-10-25 08:05:53.051017','2026-10-25 08:12:08.673259',0,NULL,NULL,NULL,0,1200,1,1,1,2,63,1),(63,'2026-10-25 08:05:53.053293','2026-10-25 08:12:08.679714',0,NULL,NULL,NULL,0,1200,1,1,1,3,70,1),(64,'2026-10-25 08:05:53.053547','2026-10-25 08:12:08.676819',0,NULL,NULL,NULL,0,1200,1,1,1,6,67,1),(65,'2026-10-25 08:05:53.053926','2026-10-25 08:12:08.674613',0,NULL,NULL,NULL,0,1200,1,1,1,5,66,1),(66,'2026-10-25 08:05:53.054781','2026-10-25 08:12:08.674516',0,NULL,NULL,NULL,0,1200,1,1,1,4,65,1),(67,'2026-10-25 08:05:53.055028','2026-10-25 08:12:08.675380',0,NULL,NULL,NULL,0,1200,1,1,1,1,62,1),(68,'2026-10-25 08:05:53.055049','2026-10-25 08:12:08.679541',0,NULL,NULL,NULL,0,1200,1,1,1,2,69,1),(69,'2026-10-25 08:05:53.057704','2026-10-25 08:12:08.682090',0,NULL,NULL,NULL,0,1200,1,1,1,5,72,1),(70,'2026-10-25 08:05:53.057926','2026-10-25 08:12:08.680369',0,NULL,NULL,NULL,0,1200,1,1,1,4,71,1),(71,'2026-10-25 08:05:53.057909','2026-10-25 08:12:08.682652',0,NULL,NULL,NULL,0,1200,1,1,1,6,73,1),(72,'2026-10-25 08:05:53.058355','2026-10-25 08:12:08.685632',0,NULL,NULL,NULL,0,1200,1,1,1,3,76,1),(73,'2026-10-25 08:05:53.058771','2026-10-25 08:12:08.684768',0,NULL,NULL,NULL,0,1200,1,1,1,2,75,1),(74,'2026-10-25 08:05:53.059683','2026-10-25 08:12:08.681075',0,NULL,NULL,NULL,0,1200,1,1,1,1,68,1),(75,'2026-10-25 08:05:53.061579','2026-10-25 08:12:08.684760',0,NULL,NULL,NULL,0,1200,1,1,1,4,77,1),(76,'2026-10-25 08:05:53.061593','2026-10-25 08:12:08.688760',0,NULL,NULL,NULL,0,1200,1,1,1,6,79,1),(77,'2026-10-25 08:05:53.061913','2026-10-25 08:12:08.687152',0,NULL,NULL,NULL,0,1200,1,1,1,5,78,1),(78,'2026-10-25 08:05:53.061906','2026-10-25 08:12:08.691062',0,NULL,NULL,NULL,0,1200,1,1,1,3,82,1),(79,'2026-10-25 08:05:53.063241','2026-10-25 08:12:08.690363',0,NULL,NULL,NULL,0,1200,1,1,1,2,81,1),(80,'2026-10-25 08:05:53.063879','2026-10-25 08:12:08.686000',0,NULL,NULL,NULL,0,1200,1,1,1,1,74,1),(81,'2026-10-25 08:05:53.065241','2026-10-25 08:12:08.696384',0,NULL,NULL,NULL,0,1200,1,1,1,6,85,1),(82,'2026-10-25 08:05:53.066188','2026-10-25 08:12:08.697565',0,NULL,NULL,NULL,0,1200,1,1,1,3,88,1),(83,'2026-10-25 08:05:53.066424','2026-10-25 08:12:08.690629',0,NULL,NULL,NULL,0,1200,1,1,1,4,83,1),(84,'2026-10-25 08:05:53.066429','2026-10-25 08:12:08.691454',0,NULL,NULL,NULL,0,1200,1,1,1,5,84,1),(85,'2026-10-25 08:05:53.067321','2026-10-25 08:12:08.696388',0,NULL,NULL,NULL,0,1200,1,1,1,2,87,1),(86,'2026-10-25 08:05:53.068459','2026-10-25 08:12:08.691479',0,NULL,NULL,NULL,0,1200,1,1,1,1,80,1),(87,'2026-10-25 08:05:53.069403','2026-10-25 08:12:08.702255',0,NULL,NULL,NULL,0,1200,1,1,1,6,91,1),(88,'2026-10-25 08:05:53.069564','2026-10-25 08:12:08.703317',0,NULL,NULL,NULL,0,1200,1,1,1,3,94,1),(89,'2026-10-25 08:05:53.070299','2026-10-25 08:12:08.696997',0,NULL,NULL,NULL,0,1200,1,1,1,5,90,1),(90,'2026-10-25 08:05:53.071430','2026-10-25 08:12:08.696139',0,NULL,NULL,NULL,0,1200,1,1,1,4,89,1),(91,'2026-10-25 08:05:53.072744','2026-10-25 08:12:08.707451',0,NULL,NULL,NULL,0,1200,1,1,1,6,97,1),(92,'2026-10-25 08:05:53.073227','2026-10-25 08:12:08.701196',0,NULL,NULL,NULL,0,1200,1,1,1,2,93,1),(93,'2026-10-25 08:05:53.073474','2026-10-25 08:12:08.696983',0,NULL,NULL,NULL,0,1200,1,1,1,1,86,1),(94,'2026-10-25 08:05:53.073875','2026-10-25 08:12:08.708982',0,NULL,NULL,NULL,0,1200,1,1,1,3,100,1),(95,'2026-10-25 08:05:53.074410','2026-10-25 08:12:08.703436',0,NULL,NULL,NULL,0,1200,1,1,1,5,96,1),(96,'2026-10-25 08:05:53.075695','2026-10-25 08:12:08.701911',0,NULL,NULL,NULL,0,1200,1,1,1,4,95,1),(97,'2026-10-25 08:05:53.077116','2026-10-25 08:12:08.707080',0,NULL,NULL,NULL,0,1200,1,1,1,2,99,1),(98,'2026-10-25 08:05:53.078384','2026-10-25 08:12:08.702326',0,NULL,NULL,NULL,0,1200,1,1,1,1,92,1),(99,'2026-10-25 08:05:53.080858','2026-10-25 08:12:08.707615',0,NULL,NULL,NULL,0,1200,1,1,1,4,101,1),(100,'2026-10-25 08:05:53.083308','2026-10-25 08:12:08.708764',0,NULL,NULL,NULL,0,1200,1,1,1,1,98,1),(101,'2026-10-25 08:12:26.508620','2026-10-25 08:12:26.508620',0,NULL,NULL,NULL,0,600,1,1,1,6,7,2),(102,'2026-10-25 08:12:26.508405','2026-10-25 08:12:26.508405',0,NULL,NULL,NULL,0,600,1,1,1,5,6,2),(103,'2026-10-25 08:12:26.508340','2026-10-25 08:12:26.508340',0,NULL,NULL,NULL,0,600,1,1,1,2,3,2),(104,'2026-10-25 08:12:26.508363','2026-10-25 08:12:26.508363',0,NULL,NULL,NULL,0,600,1,1,1,1,2,2),(105,'2026-10-25 08:12:26.508417','2026-10-25 08:12:26.508417',0,NULL,NULL,NULL,0,600,1,1,1,3,4,2),(106,'2026-10-25 08:12:26.511550','2026-10-25 08:12:26.511550',0,NULL,NULL,NULL,0,600,1,1,1,4,5,2),(107,'2026-10-25 08:12:26.514166','2026-10-25 08:12:26.514166',0,NULL,NULL,NULL,0,600,1,1,1,6,13,2),(108,'2026-10-25 08:12:26.514263','2026-10-25 08:12:26.514263',0,NULL,NULL,NULL,0,600,1,1,1,2,9,2),(109,'2026-10-25 08:12:26.514170','2026-10-25 08:12:26.514170',0,NULL,NULL,NULL,0,600,1,1,1,5,12,2),(110,'2026-10-25 08:12:26.514543','2026-10-25 08:12:26.514543',0,NULL,NULL,NULL,0,600,1,1,1,1,8,2),(111,'2026-10-25 08:12:26.515545','2026-10-25 08:12:26.515545',0,NULL,NULL,NULL,0,600,1,1,1,3,10,2),(112,'2026-10-25 08:12:26.515739','2026-10-25 08:12:26.515739',0,NULL,NULL,NULL,0,600,1,1,1,4,11,2),(113,'2026-10-25 08:12:26.517448','2026-10-25 08:12:26.517448',0,NULL,NULL,NULL,0,600,1,1,1,6,19,2),(114,'2026-10-25 08:12:26.517453','2026-10-25 08:12:26.517453',0,NULL,NULL,NULL,0,600,1,1,1,5,18,2),(115,'2026-10-25 08:12:26.517452','2026-10-25 08:12:26.517452',0,NULL,NULL,NULL,0,600,1,1,1,2,15,2),(116,'2026-10-25 08:12:26.517953','2026-10-25 08:12:26.517953',0,NULL,NULL,NULL,0,600,1,1,1,1,14,2),(117,'2026-10-25 08:12:26.518272','2026-10-25 08:12:26.518272',0,NULL,NULL,NULL,0,600,1,1,1,4,17,2),(118,'2026-10-25 08:12:26.518700','2026-10-25 08:12:26.518700',0,NULL,NULL,NULL,0,600,1,1,1,3,16,2),(119,'2026-10-25 08:12:26.519994','2026-10-25 08:12:26.519994',0,NULL,NULL,NULL,0,600,1,1,1,6,25,2),(120,'2026-10-25 08:12:26.520239','2026-10-25 08:12:26.520239',0,NULL,NULL,NULL,0,600,1,1,1,5,24,2),(121,'2026-10-25 08:12:26.520644','2026-10-25 08:12:26.520644',0,NULL,NULL,NULL,0,600,1,1,1,1,20,2),(122,'2026-10-25 08:12:26.520705','2026-10-25 08:12:26.520705',0,NULL,NULL,NULL,0,600,1,1,1,4,23,2),(123,'2026-10-25 08:12:26.521606','2026-10-25 08:12:26.521606',0,NULL,NULL,NULL,0,600,1,1,1,2,21,2),(124,'2026-10-25 08:12:26.521958','2026-10-25 08:12:26.521958',0,NULL,NULL,NULL,0,600,1,1,1,3,22,2),(125,'2026-10-25 08:12:26.522259','2026-10-25 08:12:26.522259',0,NULL,NULL,NULL,0,600,1,1,1,5,30,2),(126,'2026-10-25 08:12:26.522874','2026-10-25 08:12:26.522874',0,NULL,NULL,NULL,0,600,1,1,1,6,31,2),(127,'2026-10-25 08:12:26.523270','2026-10-25 08:12:26.523270',0,NULL,NULL,NULL,0,600,1,1,1,4,29,2),(128,'2026-10-25 08:12:26.523491','2026-10-25 08:12:26.523491',0,NULL,NULL,NULL,0,600,1,1,1,1,26,2),(129,'2026-10-25 08:12:26.524368','2026-10-25 08:12:26.524368',0,NULL,NULL,NULL,0,600,1,1,1,2,27,2),(130,'2026-10-25 08:12:26.524671','2026-10-25 08:12:26.524671',0,NULL,NULL,NULL,0,600,1,1,1,3,28,2),(131,'2026-10-25 08:12:26.524994','2026-10-25 08:12:26.524994',0,NULL,NULL,NULL,0,600,1,1,1,5,36,2),(132,'2026-10-25 08:12:26.525247','2026-10-25 08:12:26.525247',0,NULL,NULL,NULL,0,600,1,1,1,6,37,2),(133,'2026-10-25 08:12:26.525486','2026-10-25 08:12:26.525486',0,NULL,NULL,NULL,0,600,1,1,1,4,35,2),(134,'2026-10-25 08:12:26.525731','2026-10-25 08:12:26.525731',0,NULL,NULL,NULL,0,600,1,1,1,1,32,2),(135,'2026-10-25 08:12:26.526624','2026-10-25 08:12:26.526624',0,NULL,NULL,NULL,0,600,1,1,1,2,33,2),(136,'2026-10-25 08:12:26.526808','2026-10-25 08:12:26.526808',0,NULL,NULL,NULL,0,600,1,1,1,3,34,2),(137,'2026-10-25 08:12:26.529447','2026-10-25 08:12:26.529447',0,NULL,NULL,NULL,0,600,1,1,1,5,42,2),(138,'2026-10-25 08:12:26.529525','2026-10-25 08:12:26.529525',0,NULL,NULL,NULL,0,600,1,1,1,6,43,2),(139,'2026-10-25 08:12:26.529730','2026-10-25 08:12:26.529730',0,NULL,NULL,NULL,0,600,1,1,1,4,41,2),(140,'2026-10-25 08:12:26.529768','2026-10-25 08:12:26.529768',0,NULL,NULL,NULL,0,600,1,1,1,2,39,2),(141,'2026-10-25 08:12:26.529924','2026-10-25 08:12:26.529924',0,NULL,NULL,NULL,0,600,1,1,1,3,40,2),(142,'2026-10-25 08:12:26.530163','2026-10-25 08:12:26.530163',0,NULL,NULL,NULL,0,600,1,1,1,1,38,2),(143,'2026-10-25 08:12:26.532114','2026-10-25 08:12:26.532114',0,NULL,NULL,NULL,0,600,1,1,1,4,47,2),(144,'2026-10-25 08:12:26.532099','2026-10-25 08:12:26.532099',0,NULL,NULL,NULL,0,600,1,1,1,5,48,2),(145,'2026-10-25 08:12:26.532244','2026-10-25 08:12:26.532244',0,NULL,NULL,NULL,0,600,1,1,1,6,49,2),(146,'2026-10-25 08:12:26.532379','2026-10-25 08:12:26.532379',0,NULL,NULL,NULL,0,600,1,1,1,3,46,2),(147,'2026-10-25 08:12:26.532318','2026-10-25 08:12:26.532318',0,NULL,NULL,NULL,0,600,1,1,1,2,45,2),(148,'2026-10-25 08:12:26.532530','2026-10-25 08:12:26.532530',0,NULL,NULL,NULL,0,600,1,1,1,1,44,2),(149,'2026-10-25 08:12:26.534772','2026-10-25 08:12:26.534772',0,NULL,NULL,NULL,0,600,1,1,1,6,55,2),(150,'2026-10-25 08:12:26.534939','2026-10-25 08:12:26.534939',0,NULL,NULL,NULL,0,600,1,1,1,2,51,2),(151,'2026-10-25 08:12:26.534868','2026-10-25 08:12:26.534868',0,NULL,NULL,NULL,0,600,1,1,1,3,52,2),(152,'2026-10-25 08:12:26.534922','2026-10-25 08:12:26.534922',0,NULL,NULL,NULL,0,600,1,1,1,4,53,2),(153,'2026-10-25 08:12:26.535168','2026-10-25 08:12:26.535168',0,NULL,NULL,NULL,0,600,1,1,1,5,54,2),(154,'2026-10-25 08:12:26.535471','2026-10-25 08:12:26.535471',0,NULL,NULL,NULL,0,600,1,1,1,1,50,2),(155,'2026-10-25 08:12:26.537567','2026-10-25 08:12:26.537567',0,NULL,NULL,NULL,0,600,1,1,1,4,59,2),(156,'2026-10-25 08:12:26.537508','2026-10-25 08:12:26.537508',0,NULL,NULL,NULL,0,600,1,1,1,6,61,2),(157,'2026-10-25 08:12:26.537907','2026-10-25 08:12:26.537907',0,NULL,NULL,NULL,0,600,1,1,1,1,56,2),(158,'2026-10-25 08:12:26.538416','2026-10-25 08:12:26.538416',0,NULL,NULL,NULL,0,600,1,1,1,5,60,2),(159,'2026-10-25 08:12:26.538211','2026-10-25 08:12:26.538211',0,NULL,NULL,NULL,0,600,1,1,1,3,58,2),(160,'2026-10-25 08:12:26.538852','2026-10-25 08:12:26.538852',0,NULL,NULL,NULL,0,600,1,1,1,2,57,2),(161,'2026-10-25 08:12:26.541027','2026-10-25 08:12:26.541027',0,NULL,NULL,NULL,0,600,1,1,1,5,66,2),(162,'2026-10-25 08:12:26.540810','2026-10-25 08:12:26.540810',0,NULL,NULL,NULL,0,600,1,1,1,4,65,2),(163,'2026-10-25 08:12:26.541092','2026-10-25 08:12:26.541092',0,NULL,NULL,NULL,0,600,1,1,1,3,64,2),(164,'2026-10-25 08:12:26.541131','2026-10-25 08:12:26.541131',0,NULL,NULL,NULL,0,600,1,1,1,6,67,2),(165,'2026-10-25 08:12:26.541370','2026-10-25 08:12:26.541370',0,NULL,NULL,NULL,0,600,1,1,1,1,62,2),(166,'2026-10-25 08:12:26.541610','2026-10-25 08:12:26.541610',0,NULL,NULL,NULL,0,600,1,1,1,2,63,2),(167,'2026-10-25 08:12:26.543664','2026-10-25 08:12:26.543664',0,NULL,NULL,NULL,0,600,1,1,1,3,70,2),(168,'2026-10-25 08:12:26.543754','2026-10-25 08:12:26.543754',0,NULL,NULL,NULL,0,600,1,1,1,5,72,2),(169,'2026-10-25 08:12:26.543863','2026-10-25 08:12:26.543863',0,NULL,NULL,NULL,0,600,1,1,1,4,71,2),(170,'2026-10-25 08:12:26.543866','2026-10-25 08:12:26.543866',0,NULL,NULL,NULL,0,600,1,1,1,1,68,2),(171,'2026-10-25 08:12:26.544240','2026-10-25 08:12:26.544240',0,NULL,NULL,NULL,0,600,1,1,1,6,73,2),(172,'2026-10-25 08:12:26.544584','2026-10-25 08:12:26.544584',0,NULL,NULL,NULL,0,600,1,1,1,2,69,2),(173,'2026-10-25 08:12:26.546387','2026-10-25 08:12:26.546387',0,NULL,NULL,NULL,0,600,1,1,1,1,74,2),(174,'2026-10-25 08:12:26.546336','2026-10-25 08:12:26.546336',0,NULL,NULL,NULL,0,600,1,1,1,5,78,2),(175,'2026-10-25 08:12:26.546542','2026-10-25 08:12:26.546542',0,NULL,NULL,NULL,0,600,1,1,1,4,77,2),(176,'2026-10-25 08:12:26.546670','2026-10-25 08:12:26.546670',0,NULL,NULL,NULL,0,600,1,1,1,6,79,2),(177,'2026-10-25 08:12:26.546801','2026-10-25 08:12:26.546801',0,NULL,NULL,NULL,0,600,1,1,1,2,75,2),(178,'2026-10-25 08:12:26.547244','2026-10-25 08:12:26.547244',0,NULL,NULL,NULL,0,600,1,1,1,3,76,2),(179,'2026-10-25 08:12:26.548693','2026-10-25 08:12:26.548693',0,NULL,NULL,NULL,0,600,1,1,1,1,80,2),(180,'2026-10-25 08:12:26.548812','2026-10-25 08:12:26.548812',0,NULL,NULL,NULL,0,600,1,1,1,5,84,2),(181,'2026-10-25 08:12:26.549223','2026-10-25 08:12:26.549223',0,NULL,NULL,NULL,0,600,1,1,1,6,85,2),(182,'2026-10-25 08:12:26.549308','2026-10-25 08:12:26.549308',0,NULL,NULL,NULL,0,600,1,1,1,4,83,2),(183,'2026-10-25 08:12:26.549539','2026-10-25 08:12:26.549539',0,NULL,NULL,NULL,0,600,1,1,1,2,81,2),(184,'2026-10-25 08:12:26.549566','2026-10-25 08:12:26.549566',0,NULL,NULL,NULL,0,600,1,1,1,3,82,2),(185,'2026-10-25 08:12:26.551559','2026-10-25 08:12:26.551559',0,NULL,NULL,NULL,0,600,1,1,1,1,86,2),(186,'2026-10-25 08:12:26.551847','2026-10-25 08:12:26.551847',0,NULL,NULL,NULL,0,600,1,1,1,4,89,2),(187,'2026-10-25 08:12:26.551848','2026-10-25 08:12:26.551848',0,NULL,NULL,NULL,0,600,1,1,1,2,87,2),(188,'2026-10-25 08:12:26.552060','2026-10-25 08:12:26.552060',0,NULL,NULL,NULL,0,600,1,1,1,3,88,2),(189,'2026-10-25 08:12:26.551995','2026-10-25 08:12:26.551995',0,NULL,NULL,NULL,0,600,1,1,1,6,91,2),(190,'2026-10-25 08:12:26.552093','2026-10-25 08:12:26.552093',0,NULL,NULL,NULL,0,600,1,1,1,5,90,2),(191,'2026-10-25 08:12:26.554127','2026-10-25 08:12:26.554127',0,NULL,NULL,NULL,0,600,1,1,1,1,92,2),(192,'2026-10-25 08:12:26.554459','2026-10-25 08:12:26.554459',0,NULL,NULL,NULL,0,600,1,1,1,4,95,2),(193,'2026-10-25 08:12:26.554500','2026-10-25 08:12:26.554500',0,NULL,NULL,NULL,0,600,1,1,1,6,97,2),(194,'2026-10-25 08:12:26.554629','2026-10-25 08:12:26.554629',0,NULL,NULL,NULL,0,600,1,1,1,2,93,2),(195,'2026-10-25 08:12:26.554837','2026-10-25 08:12:26.554837',0,NULL,NULL,NULL,0,600,1,1,1,5,96,2),(196,'2026-10-25 08:12:26.554707','2026-10-25 08:12:26.554707',0,NULL,NULL,NULL,0,600,1,1,1,3,94,2),(197,'2026-10-25 08:12:26.557081','2026-10-25 08:12:26.557081',0,NULL,NULL,NULL,0,600,1,1,1,4,101,2),(198,'2026-10-25 08:12:26.557345','2026-10-25 08:12:26.557345',0,NULL,NULL,NULL,0,600,1,1,1,3,100,2),(199,'2026-10-25 08:12:26.557215','2026-10-25 08:12:26.557215',0,NULL,NULL,NULL,0,600,1,1,1,1,98,2),(200,'2026-10-25 08:12:26.557350','2026-10-25 08:12:26.557350',0,NULL,NULL,NULL,0,600,1,1,1,2,99,2),(201,'2026-10-25 08:12:36.584828','2026-10-25 08:12:36.584828',0,NULL,NULL,NULL,0,600,1,1,1,1,2,3),(202,'2026-10-25 08:12:36.585056','2026-10-25 08:12:36.585056',0,NULL,NULL,NULL,0,600,1,1,1,6,7,3),(203,'2026-10-25 08:12:36.585303','2026-10-25 08:12:36.585303',0,NULL,NULL,NULL,0,600,1,1,1,3,4,3),(204,'2026-10-25 08:12:36.585574','2026-10-25 08:12:36.585574',0,NULL,NULL,NULL,0,600,1,1,1,2,3,3),(205,'2026-10-25 08:12:36.585787','2026-10-25 08:12:36.585787',0,NULL,NULL,NULL,0,600,1,1,1,5,6,3),(206,'2026-10-25 08:12:36.587502','2026-10-25 08:12:36.587502',0,NULL,NULL,NULL,0,600,1,1,1,4,5,3),(207,'2026-10-25 08:12:36.588440','2026-10-25 08:12:36.588440',0,NULL,NULL,NULL,0,600,1,1,1,1,8,3),(208,'2026-10-25 08:12:36.588598','2026-10-25 08:12:36.588598',0,NULL,NULL,NULL,0,600,1,1,1,5,12,3),(209,'2026-10-25 08:12:36.588525','2026-10-25 08:12:36.588525',0,NULL,NULL,NULL,0,600,1,1,1,3,10,3),(210,'2026-10-25 08:12:36.591400','2026-10-25 08:12:36.591400',0,NULL,NULL,NULL,0,600,1,1,1,6,13,3),(211,'2026-10-25 08:12:36.591426','2026-10-25 08:12:36.591426',0,NULL,NULL,NULL,0,600,1,1,1,2,9,3),(212,'2026-10-25 08:12:36.592186','2026-10-25 08:12:36.592186',0,NULL,NULL,NULL,0,600,1,1,1,4,11,3),(213,'2026-10-25 08:12:36.592642','2026-10-25 08:12:36.592642',0,NULL,NULL,NULL,0,600,1,1,1,3,16,3),(214,'2026-10-25 08:12:36.592523','2026-10-25 08:12:36.592523',0,NULL,NULL,NULL,0,600,1,1,1,1,14,3),(215,'2026-10-25 08:12:36.592519','2026-10-25 08:12:36.592519',0,NULL,NULL,NULL,0,600,1,1,1,5,18,3),(216,'2026-10-25 08:12:36.594824','2026-10-25 08:12:36.594824',0,NULL,NULL,NULL,0,600,1,1,1,2,15,3),(217,'2026-10-25 08:12:36.595082','2026-10-25 08:12:36.595082',0,NULL,NULL,NULL,0,600,1,1,1,3,22,3),(218,'2026-10-25 08:12:36.595012','2026-10-25 08:12:36.595012',0,NULL,NULL,NULL,0,600,1,1,1,6,19,3),(219,'2026-10-25 08:12:36.595208','2026-10-25 08:12:36.595208',0,NULL,NULL,NULL,0,600,1,1,1,4,17,3),(220,'2026-10-25 08:12:36.595507','2026-10-25 08:12:36.595507',0,NULL,NULL,NULL,0,600,1,1,1,1,20,3),(221,'2026-10-25 08:12:36.595870','2026-10-25 08:12:36.595870',0,NULL,NULL,NULL,0,600,1,1,1,5,24,3),(222,'2026-10-25 08:12:36.597675','2026-10-25 08:12:36.597675',0,NULL,NULL,NULL,0,600,1,1,1,3,28,3),(223,'2026-10-25 08:12:36.597772','2026-10-25 08:12:36.597772',0,NULL,NULL,NULL,0,600,1,1,1,4,23,3),(224,'2026-10-25 08:12:36.598015','2026-10-25 08:12:36.598015',0,NULL,NULL,NULL,0,600,1,1,1,6,25,3),(225,'2026-10-25 08:12:36.598124','2026-10-25 08:12:36.598124',0,NULL,NULL,NULL,0,600,1,1,1,2,21,3),(226,'2026-10-25 08:12:36.598506','2026-10-25 08:12:36.598506',0,NULL,NULL,NULL,0,600,1,1,1,5,30,3),(227,'2026-10-25 08:12:36.598345','2026-10-25 08:12:36.598345',0,NULL,NULL,NULL,0,600,1,1,1,1,26,3),(228,'2026-10-25 08:12:36.600798','2026-10-25 08:12:36.600798',0,NULL,NULL,NULL,0,600,1,1,1,6,31,3),(229,'2026-10-25 08:12:36.601010','2026-10-25 08:12:36.601010',0,NULL,NULL,NULL,0,600,1,1,1,3,34,3),(230,'2026-10-25 08:12:36.601170','2026-10-25 08:12:36.601170',0,NULL,NULL,NULL,0,600,1,1,1,4,29,3),(231,'2026-10-25 08:12:36.601199','2026-10-25 08:12:36.601199',0,NULL,NULL,NULL,0,600,1,1,1,2,27,3),(232,'2026-10-25 08:12:36.601394','2026-10-25 08:12:36.601394',0,NULL,NULL,NULL,0,600,1,1,1,5,36,3),(233,'2026-10-25 08:12:36.601945','2026-10-25 14:07:48.150882',0,NULL,NULL,NULL,600,600,1,1,1,1,32,3),(234,'2026-10-25 08:12:36.603492','2026-10-25 08:12:36.603492',0,NULL,NULL,NULL,0,600,1,1,1,4,35,3),(235,'2026-10-25 08:12:36.603677','2026-10-25 08:12:36.603677',0,NULL,NULL,NULL,0,600,1,1,1,6,37,3),(236,'2026-10-25 08:12:36.603573','2026-10-25 08:12:36.603573',0,NULL,NULL,NULL,0,600,1,1,1,3,40,3),(237,'2026-10-25 08:12:36.603903','2026-10-25 08:12:36.603903',0,NULL,NULL,NULL,0,600,1,1,1,5,42,3),(238,'2026-10-25 08:12:36.604518','2026-10-25 08:12:36.604518',0,NULL,NULL,NULL,0,600,1,1,1,1,38,3),(239,'2026-10-25 08:12:36.635541','2026-10-25 08:12:36.635541',0,NULL,NULL,NULL,0,600,1,1,1,2,33,3),(240,'2026-10-25 08:12:36.636335','2026-10-25 08:12:36.636335',0,NULL,NULL,NULL,0,600,1,1,1,4,41,3),(241,'2026-10-25 08:12:36.636455','2026-10-25 08:12:36.636455',0,NULL,NULL,NULL,0,600,1,1,1,3,46,3),(242,'2026-10-25 08:12:36.636988','2026-10-25 08:12:36.636988',0,NULL,NULL,NULL,0,600,1,1,1,5,48,3),(243,'2026-10-25 08:12:36.637359','2026-10-25 08:12:36.637359',0,NULL,NULL,NULL,0,600,1,1,1,6,43,3),(244,'2026-10-25 08:12:36.638493','2026-10-25 08:12:36.638493',0,NULL,NULL,NULL,0,600,1,1,1,1,44,3),(245,'2026-10-25 08:12:36.640215','2026-10-25 08:12:36.640215',0,NULL,NULL,NULL,0,600,1,1,1,2,39,3),(246,'2026-10-25 08:12:36.641373','2026-10-25 08:12:36.641373',0,NULL,NULL,NULL,0,600,1,1,1,3,52,3),(247,'2026-10-25 08:12:36.641515','2026-10-25 08:12:36.641515',0,NULL,NULL,NULL,0,600,1,1,1,6,49,3),(248,'2026-10-25 08:12:36.641521','2026-10-25 08:12:36.641521',0,NULL,NULL,NULL,0,600,1,1,1,5,54,3),(249,'2026-10-25 08:12:36.642909','2026-10-25 08:12:36.642909',0,NULL,NULL,NULL,0,600,1,1,1,4,47,3),(250,'2026-10-25 08:12:36.643391','2026-10-25 08:12:36.643391',0,NULL,NULL,NULL,0,600,1,1,1,1,50,3),(251,'2026-10-25 08:12:36.644631','2026-10-25 08:12:36.644631',0,NULL,NULL,NULL,0,600,1,1,1,3,58,3),(252,'2026-10-25 08:12:36.644889','2026-10-25 08:12:36.644889',0,NULL,NULL,NULL,0,600,1,1,1,2,45,3),(253,'2026-10-25 08:12:36.645429','2026-10-25 08:12:36.645429',0,NULL,NULL,NULL,0,600,1,1,1,6,55,3),(254,'2026-10-25 08:12:36.647027','2026-10-25 08:12:36.647027',0,NULL,NULL,NULL,0,600,1,1,1,1,56,3),(255,'2026-10-25 08:12:36.646441','2026-10-25 08:12:36.646441',0,NULL,NULL,NULL,0,600,1,1,1,5,60,3),(256,'2026-10-25 08:12:36.649184','2026-10-25 08:12:36.649184',0,NULL,NULL,NULL,0,600,1,1,1,3,64,3),(257,'2026-10-25 08:12:36.649326','2026-10-25 08:12:36.649326',0,NULL,NULL,NULL,0,600,1,1,1,4,53,3),(258,'2026-10-25 08:12:36.650331','2026-10-25 08:12:36.650331',0,NULL,NULL,NULL,0,600,1,1,1,6,61,3),(259,'2026-10-25 08:12:36.650914','2026-10-25 08:12:36.650914',0,NULL,NULL,NULL,0,600,1,1,1,2,51,3),(260,'2026-10-25 08:12:36.651161','2026-10-25 08:12:36.651161',0,NULL,NULL,NULL,0,600,1,1,1,5,66,3),(261,'2026-10-25 08:12:36.654549','2026-10-25 08:12:36.654549',0,NULL,NULL,NULL,0,600,1,1,1,3,70,3),(262,'2026-10-25 08:12:36.655655','2026-10-25 08:12:36.655655',0,NULL,NULL,NULL,0,600,1,1,1,5,72,3),(263,'2026-10-25 08:12:36.655615','2026-10-25 08:12:36.655615',0,NULL,NULL,NULL,0,600,1,1,1,6,67,3),(264,'2026-10-25 08:12:36.657216','2026-10-25 08:12:36.657216',0,NULL,NULL,NULL,0,600,1,1,1,2,57,3),(265,'2026-10-25 08:12:36.662122','2026-10-25 08:12:36.662122',0,NULL,NULL,NULL,0,600,1,1,1,4,59,3),(266,'2026-10-25 08:12:36.662367','2026-10-25 08:12:36.662367',0,NULL,NULL,NULL,0,600,1,1,1,5,78,3),(267,'2026-10-25 08:12:36.663090','2026-10-25 08:12:36.663090',0,NULL,NULL,NULL,0,600,1,1,1,2,63,3),(268,'2026-10-25 08:12:36.663434','2026-10-25 08:12:36.663434',0,NULL,NULL,NULL,0,600,1,1,1,6,73,3),(269,'2026-10-25 08:12:36.663430','2026-10-25 08:12:36.663430',0,NULL,NULL,NULL,0,600,1,1,1,3,76,3),(270,'2026-10-25 08:12:36.663483','2026-10-25 08:12:36.663483',0,NULL,NULL,NULL,0,600,1,1,1,1,62,3),(271,'2026-10-25 08:12:36.666332','2026-10-25 08:12:36.666332',0,NULL,NULL,NULL,0,600,1,1,1,2,69,3),(272,'2026-10-25 08:12:36.666679','2026-10-25 08:12:36.666679',0,NULL,NULL,NULL,0,600,1,1,1,3,82,3),(273,'2026-10-25 08:12:36.666490','2026-10-25 08:12:36.666490',0,NULL,NULL,NULL,0,600,1,1,1,6,79,3),(274,'2026-10-25 08:12:36.666676','2026-10-25 08:12:36.666676',0,NULL,NULL,NULL,0,600,1,1,1,4,65,3),(275,'2026-10-25 08:12:36.666904','2026-10-25 08:12:36.666904',0,NULL,NULL,NULL,0,600,1,1,1,1,68,3),(276,'2026-10-25 08:12:36.667118','2026-10-25 08:12:36.667118',0,NULL,NULL,NULL,0,600,1,1,1,5,84,3),(277,'2026-10-25 08:12:36.670105','2026-10-25 08:12:36.670105',0,NULL,NULL,NULL,0,600,1,1,1,4,71,3),(278,'2026-10-25 08:12:36.670036','2026-10-25 08:12:36.670036',0,NULL,NULL,NULL,0,600,1,1,1,2,75,3),(279,'2026-10-25 08:12:36.670055','2026-10-25 08:12:36.670055',0,NULL,NULL,NULL,0,600,1,1,1,3,88,3),(280,'2026-10-25 08:12:36.670113','2026-10-25 08:12:36.670113',0,NULL,NULL,NULL,0,600,1,1,1,1,74,3),(281,'2026-10-25 08:12:36.671495','2026-10-25 08:12:36.671495',0,NULL,NULL,NULL,0,600,1,1,1,5,90,3),(282,'2026-10-25 08:12:36.672152','2026-10-25 08:12:36.672152',0,NULL,NULL,NULL,0,600,1,1,1,6,85,3),(283,'2026-10-25 08:12:36.673799','2026-10-25 08:12:36.673799',0,NULL,NULL,NULL,0,600,1,1,1,2,81,3),(284,'2026-10-25 08:12:36.673964','2026-10-25 08:12:36.673964',0,NULL,NULL,NULL,0,600,1,1,1,4,77,3),(285,'2026-10-25 08:12:36.674222','2026-10-25 08:12:36.674222',0,NULL,NULL,NULL,0,600,1,1,1,3,94,3),(286,'2026-10-25 08:12:36.674994','2026-10-25 08:12:36.674994',0,NULL,NULL,NULL,0,600,1,1,1,5,96,3),(287,'2026-10-25 08:12:36.675370','2026-10-25 08:12:36.675370',0,NULL,NULL,NULL,0,600,1,1,1,6,91,3),(288,'2026-10-25 08:12:36.676454','2026-10-25 08:12:36.676454',0,NULL,NULL,NULL,0,600,1,1,1,2,87,3),(289,'2026-10-25 08:12:36.676763','2026-10-25 08:12:36.676763',0,NULL,NULL,NULL,0,600,1,1,1,4,83,3),(290,'2026-10-25 08:12:36.677119','2026-10-25 08:12:36.677119',0,NULL,NULL,NULL,0,600,1,1,1,3,100,3),(291,'2026-10-25 08:12:36.677974','2026-10-25 08:12:36.677974',0,NULL,NULL,NULL,0,600,1,1,1,6,97,3),(292,'2026-10-25 08:12:36.679086','2026-10-25 08:12:36.679086',0,NULL,NULL,NULL,0,600,1,1,1,1,80,3),(293,'2026-10-25 08:12:36.679328','2026-10-25 08:12:36.679328',0,NULL,NULL,NULL,0,600,1,1,1,2,93,3),(294,'2026-10-25 08:12:36.679773','2026-10-25 08:12:36.679773',0,NULL,NULL,NULL,0,600,1,1,1,4,89,3),(295,'2026-10-25 08:12:36.681848','2026-10-25 08:12:36.681848',0,NULL,NULL,NULL,0,600,1,1,1,2,99,3),(296,'2026-10-25 08:12:36.681851','2026-10-25 08:12:36.681851',0,NULL,NULL,NULL,0,600,1,1,1,1,86,3),(297,'2026-10-25 08:12:36.682339','2026-10-25 08:12:36.682339',0,NULL,NULL,NULL,0,600,1,1,1,4,95,3),(298,'2026-10-25 08:12:36.683726','2026-10-25 08:12:36.683726',0,NULL,NULL,NULL,0,600,1,1,1,1,92,3),(299,'2026-10-25 08:12:36.684103','2026-10-25 08:12:36.684103',0,NULL,NULL,NULL,0,600,1,1,1,4,101,3),(300,'2026-10-25 08:12:36.685498','2026-10-25 08:12:36.685498',0,NULL,NULL,NULL,0,600,1,1,1,1,98,3),(301,'2026-10-25 08:12:49.235997','2026-10-25 08:12:49.235997',0,NULL,NULL,NULL,0,600,1,1,1,4,5,4),(302,'2026-10-25 08:12:49.235997','2026-10-25 08:12:49.235997',0,NULL,NULL,NULL,0,600,1,1,1,6,7,4),(303,'2026-10-25 08:12:49.236256','2026-10-25 13:55:37.926018',1800,NULL,NULL,NULL,0,600,1,1,1,1,2,4),(304,'2026-10-25 08:12:49.236134','2026-10-25 08:12:49.236134',0,NULL,NULL,NULL,0,600,1,1,1,5,6,4),(305,'2026-10-25 08:12:49.236312','2026-10-25 08:12:49.236312',0,NULL,NULL,NULL,0,600,1,1,1,2,3,4),(306,'2026-10-25 08:12:49.236670','2026-10-25 08:12:49.236670',0,NULL,NULL,NULL,0,600,1,1,1,3,4,4),(307,'2026-10-25 08:12:49.238744','2026-10-25 08:12:49.238744',0,NULL,NULL,NULL,0,600,1,1,1,3,10,4),(308,'2026-10-25 08:12:49.238759','2026-10-25 08:12:49.238759',0,NULL,NULL,NULL,0,600,1,1,1,6,13,4),(309,'2026-10-25 08:12:49.238832','2026-10-25 08:12:49.238832',0,NULL,NULL,NULL,0,600,1,1,1,2,9,4),(310,'2026-10-25 08:12:49.238952','2026-10-25 08:12:49.238952',0,NULL,NULL,NULL,0,600,1,1,1,4,11,4),(311,'2026-10-25 08:12:49.239028','2026-10-25 13:56:09.223250',1800,NULL,NULL,NULL,0,600,1,1,1,1,8,4),(312,'2026-10-25 08:12:49.239157','2026-10-25 08:12:49.239157',0,NULL,NULL,NULL,0,600,1,1,1,5,12,4),(313,'2026-10-25 08:12:49.240884','2026-10-25 08:12:49.240884',0,NULL,NULL,NULL,0,600,1,1,1,3,16,4),(314,'2026-10-25 08:12:49.240927','2026-10-25 08:12:49.240927',0,NULL,NULL,NULL,0,600,1,1,1,6,19,4),(315,'2026-10-25 08:12:49.240925','2026-10-25 08:12:49.240925',0,NULL,NULL,NULL,0,600,1,1,1,2,15,4),(316,'2026-10-25 08:12:49.241143','2026-10-25 08:12:49.241143',0,NULL,NULL,NULL,0,600,1,1,1,4,17,4),(317,'2026-10-25 08:12:49.241156','2026-10-25 14:01:39.482027',1800,NULL,NULL,NULL,0,600,1,1,1,1,14,4),(318,'2026-10-25 08:12:49.241262','2026-10-25 08:12:49.241262',0,NULL,NULL,NULL,0,600,1,1,1,5,18,4),(319,'2026-10-25 08:12:49.242989','2026-10-25 08:12:49.242989',0,NULL,NULL,NULL,0,600,1,1,1,2,21,4),(320,'2026-10-25 08:12:49.243075','2026-10-25 08:12:49.243075',0,NULL,NULL,NULL,0,600,1,1,1,6,25,4),(321,'2026-10-25 08:12:49.243093','2026-10-25 08:12:49.243093',0,NULL,NULL,NULL,0,600,1,1,1,3,22,4),(322,'2026-10-25 08:12:49.243232','2026-10-25 08:12:49.243232',0,NULL,NULL,NULL,0,600,1,1,1,5,24,4),(323,'2026-10-25 08:12:49.243206','2026-10-25 08:12:49.243206',0,NULL,NULL,NULL,0,600,1,1,1,4,23,4),(324,'2026-10-25 08:12:49.243298','2026-10-25 16:40:32.436688',1800,NULL,NULL,NULL,0,600,1,1,1,1,20,4),(325,'2026-10-25 08:12:49.245148','2026-10-25 08:12:49.245148',0,NULL,NULL,NULL,0,600,1,1,1,3,28,4),(326,'2026-10-25 08:12:49.245187','2026-10-25 08:12:49.245187',0,NULL,NULL,NULL,0,600,1,1,1,2,27,4),(327,'2026-10-25 08:12:49.245183','2026-10-25 08:12:49.245183',0,NULL,NULL,NULL,0,600,1,1,1,6,31,4),(328,'2026-10-25 08:12:49.245344','2026-10-25 08:12:49.245344',0,NULL,NULL,NULL,0,600,1,1,1,5,30,4),(329,'2026-10-25 08:12:49.245402','2026-10-25 08:12:49.245402',0,NULL,NULL,NULL,0,600,1,1,1,4,29,4),(330,'2026-10-25 08:12:49.245369','2026-10-25 08:12:49.245369',0,NULL,NULL,NULL,0,600,1,1,1,1,26,4),(331,'2026-10-25 08:12:49.249112','2026-10-25 08:12:49.249112',0,NULL,NULL,NULL,0,600,1,1,1,5,36,4),(332,'2026-10-25 08:12:49.249340','2026-10-25 08:12:49.249340',0,NULL,NULL,NULL,0,600,1,1,1,6,37,4),(333,'2026-10-25 08:12:49.249423','2026-10-25 08:12:49.249423',0,NULL,NULL,NULL,0,600,1,1,1,3,34,4),(334,'2026-10-25 08:12:49.253190','2026-10-25 08:12:49.253190',0,NULL,NULL,NULL,0,600,1,1,1,2,33,4),(335,'2026-10-25 08:12:49.253201','2026-10-25 14:08:06.365048',2400,NULL,NULL,NULL,0,600,1,1,1,1,32,4),(336,'2026-10-25 08:12:49.253407','2026-10-25 08:12:49.253407',0,NULL,NULL,NULL,0,600,1,1,1,4,35,4),(337,'2026-10-25 08:12:49.253429','2026-10-25 08:12:49.253429',0,NULL,NULL,NULL,0,600,1,1,1,6,43,4),(338,'2026-10-25 08:12:49.253589','2026-10-25 08:12:49.253589',0,NULL,NULL,NULL,0,600,1,1,1,3,40,4),(339,'2026-10-25 08:12:49.253619','2026-10-25 08:12:49.253619',0,NULL,NULL,NULL,0,600,1,1,1,5,42,4),(340,'2026-10-25 08:12:49.255766','2026-10-25 08:12:49.255766',0,NULL,NULL,NULL,0,600,1,1,1,6,49,4),(341,'2026-10-25 08:12:49.255771','2026-10-25 08:12:49.255771',0,NULL,NULL,NULL,0,600,1,1,1,4,41,4),(342,'2026-10-25 08:12:49.255780','2026-10-25 08:12:49.255780',0,NULL,NULL,NULL,0,600,1,1,1,1,38,4),(343,'2026-10-25 08:12:49.255983','2026-10-25 08:12:49.255983',0,NULL,NULL,NULL,0,600,1,1,1,2,39,4),(344,'2026-10-25 08:12:49.256007','2026-10-25 08:12:49.256007',0,NULL,NULL,NULL,0,600,1,1,1,3,46,4),(345,'2026-10-25 08:12:49.256043','2026-10-25 08:12:49.256043',0,NULL,NULL,NULL,0,600,1,1,1,5,48,4),(346,'2026-10-25 08:12:49.258295','2026-10-25 08:12:49.258295',0,NULL,NULL,NULL,0,600,1,1,1,6,55,4),(347,'2026-10-25 08:12:49.258345','2026-10-25 08:12:49.258345',0,NULL,NULL,NULL,0,600,1,1,1,3,52,4),(348,'2026-10-25 08:12:49.258295','2026-10-25 08:12:49.258295',0,NULL,NULL,NULL,0,600,1,1,1,2,45,4),(349,'2026-10-25 08:12:49.258530','2026-10-25 08:12:49.258530',0,NULL,NULL,NULL,0,600,1,1,1,5,54,4),(350,'2026-10-25 08:12:49.258552','2026-10-25 08:12:49.258552',0,NULL,NULL,NULL,0,600,1,1,1,4,47,4),(351,'2026-10-25 08:12:49.258533','2026-10-25 08:12:49.258533',0,NULL,NULL,NULL,0,600,1,1,1,1,44,4),(352,'2026-10-25 08:12:49.261989','2026-10-25 08:12:49.261989',0,NULL,NULL,NULL,0,600,1,1,1,2,51,4),(353,'2026-10-25 08:12:49.262369','2026-10-25 08:12:49.262369',0,NULL,NULL,NULL,0,600,1,1,1,3,58,4),(354,'2026-10-25 08:12:49.262369','2026-10-25 08:12:49.262369',0,NULL,NULL,NULL,0,600,1,1,1,6,61,4),(355,'2026-10-25 08:12:49.263023','2026-10-25 08:12:49.263023',0,NULL,NULL,NULL,0,600,1,1,1,4,53,4),(356,'2026-10-25 08:12:49.263019','2026-10-25 08:12:49.263019',0,NULL,NULL,NULL,0,600,1,1,1,5,60,4),(357,'2026-10-25 08:12:49.263427','2026-10-25 08:12:49.263427',0,NULL,NULL,NULL,0,600,1,1,1,1,50,4),(358,'2026-10-25 08:12:49.265841','2026-10-25 08:12:49.265841',0,NULL,NULL,NULL,0,600,1,1,1,2,57,4),(359,'2026-10-25 08:12:49.266173','2026-10-25 08:12:49.266173',0,NULL,NULL,NULL,0,600,1,1,1,4,59,4),(360,'2026-10-25 08:12:49.266376','2026-10-25 08:12:49.266376',0,NULL,NULL,NULL,0,600,1,1,1,1,56,4),(361,'2026-10-25 08:12:49.266452','2026-10-25 08:12:49.266452',0,NULL,NULL,NULL,0,600,1,1,1,3,64,4),(362,'2026-10-25 08:12:49.266671','2026-10-25 08:12:49.266671',0,NULL,NULL,NULL,0,600,1,1,1,6,67,4),(363,'2026-10-25 08:12:49.267609','2026-10-25 08:12:49.267609',0,NULL,NULL,NULL,0,600,1,1,1,5,66,4),(364,'2026-10-25 08:12:49.269062','2026-10-25 08:12:49.269062',0,NULL,NULL,NULL,0,600,1,1,1,2,63,4),(365,'2026-10-25 08:12:49.269615','2026-10-25 08:12:49.269615',0,NULL,NULL,NULL,0,600,1,1,1,4,65,4),(366,'2026-10-25 08:12:49.269543','2026-10-25 08:12:49.269543',0,NULL,NULL,NULL,0,600,1,1,1,3,70,4),(367,'2026-10-25 08:12:49.269665','2026-10-25 08:12:49.269665',0,NULL,NULL,NULL,0,600,1,1,1,1,62,4),(368,'2026-10-25 08:12:49.270345','2026-10-25 08:12:49.270345',0,NULL,NULL,NULL,0,600,1,1,1,6,73,4),(369,'2026-10-25 08:12:49.270980','2026-10-25 08:12:49.270980',0,NULL,NULL,NULL,0,600,1,1,1,5,72,4),(370,'2026-10-25 08:12:49.271994','2026-10-25 08:12:49.271994',0,NULL,NULL,NULL,0,600,1,1,1,2,69,4),(371,'2026-10-25 08:12:49.272280','2026-10-25 08:12:49.272280',0,NULL,NULL,NULL,0,600,1,1,1,4,71,4),(372,'2026-10-25 08:12:49.272381','2026-10-25 08:12:49.272381',0,NULL,NULL,NULL,0,600,1,1,1,3,76,4),(373,'2026-10-25 08:12:49.273007','2026-10-25 08:12:49.273007',0,NULL,NULL,NULL,0,600,1,1,1,6,79,4),(374,'2026-10-25 08:12:49.273336','2026-10-25 08:12:49.273336',0,NULL,NULL,NULL,0,600,1,1,1,5,78,4),(375,'2026-10-25 08:12:49.273621','2026-10-25 08:12:49.273621',0,NULL,NULL,NULL,0,600,1,1,1,2,75,4),(376,'2026-10-25 08:12:49.273330','2026-10-25 08:12:49.273330',0,NULL,NULL,NULL,0,600,1,1,1,1,68,4),(377,'2026-10-25 08:12:49.274181','2026-10-25 08:12:49.274181',0,NULL,NULL,NULL,0,600,1,1,1,4,77,4),(378,'2026-10-25 08:12:49.274333','2026-10-25 08:12:49.274333',0,NULL,NULL,NULL,0,600,1,1,1,3,82,4),(379,'2026-10-25 08:12:49.275351','2026-10-25 08:12:49.275351',0,NULL,NULL,NULL,0,600,1,1,1,6,85,4),(380,'2026-10-25 08:12:49.275638','2026-10-25 08:12:49.275638',0,NULL,NULL,NULL,0,600,1,1,1,2,81,4),(381,'2026-10-25 08:12:49.275868','2026-10-25 08:12:49.275868',0,NULL,NULL,NULL,0,600,1,1,1,1,74,4),(382,'2026-10-25 08:12:49.275819','2026-10-25 08:12:49.275819',0,NULL,NULL,NULL,0,600,1,1,1,5,84,4),(383,'2026-10-25 08:12:49.276258','2026-10-25 08:12:49.276258',0,NULL,NULL,NULL,0,600,1,1,1,4,83,4),(384,'2026-10-25 08:12:49.276567','2026-10-25 08:12:49.276567',0,NULL,NULL,NULL,0,600,1,1,1,3,88,4),(385,'2026-10-25 08:12:49.277544','2026-10-25 08:12:49.277544',0,NULL,NULL,NULL,0,600,1,1,1,6,91,4),(386,'2026-10-25 08:12:49.277707','2026-10-25 08:12:49.277707',0,NULL,NULL,NULL,0,600,1,1,1,5,90,4),(387,'2026-10-25 08:12:49.277702','2026-10-25 08:12:49.277702',0,NULL,NULL,NULL,0,600,1,1,1,2,87,4),(388,'2026-10-25 08:12:49.278385','2026-10-25 08:12:49.278385',0,NULL,NULL,NULL,0,600,1,1,1,4,89,4),(389,'2026-10-25 08:12:49.278538','2026-10-25 08:12:49.278538',0,NULL,NULL,NULL,0,600,1,1,1,1,80,4),(390,'2026-10-25 08:12:49.278689','2026-10-25 08:12:49.278689',0,NULL,NULL,NULL,0,600,1,1,1,3,94,4),(391,'2026-10-25 08:12:49.279503','2026-10-25 08:12:49.279503',0,NULL,NULL,NULL,0,600,1,1,1,6,97,4),(392,'2026-10-25 08:12:49.279751','2026-10-25 08:12:49.279751',0,NULL,NULL,NULL,0,600,1,1,1,5,96,4),(393,'2026-10-25 08:12:49.279794','2026-10-25 08:12:49.279794',0,NULL,NULL,NULL,0,600,1,1,1,2,93,4),(394,'2026-10-25 08:12:49.280692','2026-10-25 08:12:49.280692',0,NULL,NULL,NULL,0,600,1,1,1,1,86,4),(395,'2026-10-25 08:12:49.280775','2026-10-25 08:12:49.280775',0,NULL,NULL,NULL,0,600,1,1,1,3,100,4),(396,'2026-10-25 08:12:49.280936','2026-10-25 08:12:49.280936',0,NULL,NULL,NULL,0,600,1,1,1,4,95,4),(397,'2026-10-25 08:12:49.282449','2026-10-25 08:12:49.282449',0,NULL,NULL,NULL,0,600,1,1,1,2,99,4),(398,'2026-10-25 08:12:49.282819','2026-10-25 08:12:49.282819',0,NULL,NULL,NULL,0,600,1,1,1,1,92,4),(399,'2026-10-25 08:12:49.283996','2026-10-25 08:12:49.283996',0,NULL,NULL,NULL,0,600,1,1,1,4,101,4),(400,'2026-10-25 08:12:49.284966','2026-10-25 14:01:58.178820',3000,NULL,NULL,NULL,0,600,1,1,1,1,98,4),(401,'2026-10-25 17:44:23.069064','2026-10-25 17:44:23.069064',0,NULL,NULL,NULL,0,1200,1,1,1,1,1,1),(402,'2026-10-25 17:44:23.099723','2026-10-25 17:44:23.099723',0,NULL,NULL,NULL,0,600,1,1,1,1,1,2),(403,'2026-10-25 17:44:23.106353','2026-10-25 17:44:23.106353',0,NULL,NULL,NULL,0,600,1,1,1,1,1,3),(404,'2026-10-25 17:44:23.111048','2026-10-25 18:44:50.604450',3000,NULL,NULL,NULL,0,600,1,1,1,1,1,4);
/*!40000 ALTER TABLE `student_fee_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_fee_adjustment`
--

DROP TABLE IF EXISTS `student_fee_adjustment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_fee_adjustment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` double NOT NULL,
  `applied_at` datetime(6) NOT NULL,
  `reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` enum('DISCOUNT','SCHOLARSHIP','WAIVER','WRITEOFF') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  `approved_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi9e97qnh79uag3b141e887wyn` (`created_by`),
  KEY `FKdrumtn8vx5ituex4n9c3f0foe` (`updated_by`),
  KEY `FK4i3nuf4we95vwltm10xg6gm2h` (`school_id`),
  KEY `FK8l0ejclin726vs50gsuhubnjm` (`account_id`),
  KEY `FKl060u3j36xu5rgqun3k9c8mif` (`approved_by`),
  CONSTRAINT `FK4i3nuf4we95vwltm10xg6gm2h` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK8l0ejclin726vs50gsuhubnjm` FOREIGN KEY (`account_id`) REFERENCES `student_fee_account` (`id`),
  CONSTRAINT `FKdrumtn8vx5ituex4n9c3f0foe` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKi9e97qnh79uag3b141e887wyn` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKl060u3j36xu5rgqun3k9c8mif` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_adjustment`
--

LOCK TABLES `student_fee_adjustment` WRITE;
/*!40000 ALTER TABLE `student_fee_adjustment` DISABLE KEYS */;
INSERT INTO `student_fee_adjustment` VALUES (1,'2026-10-25 14:07:48.118457','2026-10-25 14:07:48.118457',600,'2026-10-25 14:07:48.103717','The student was not arrived at our school','WRITEOFF',1,1,1,233,1);
/*!40000 ALTER TABLE `student_fee_adjustment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_fee_line`
--

DROP TABLE IF EXISTS `student_fee_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_fee_line` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` double NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `from_annual_fee` bit(1) NOT NULL,
  `mandatory` bit(1) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK17tus24fwbqadrw3ibboxal38` (`created_by`),
  KEY `FK2uxui69hs1y1iprc36vo4fpyu` (`updated_by`),
  KEY `FKmw6apa8oo918npwj177ur8jmh` (`school_id`),
  KEY `FK3pvsphal5nhi8hkx46n18t16s` (`account_id`),
  CONSTRAINT `FK17tus24fwbqadrw3ibboxal38` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK2uxui69hs1y1iprc36vo4fpyu` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK3pvsphal5nhi8hkx46n18t16s` FOREIGN KEY (`account_id`) REFERENCES `student_fee_account` (`id`),
  CONSTRAINT `FKmw6apa8oo918npwj177ur8jmh` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=606 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_line`
--

LOCK TABLES `student_fee_line` WRITE;
/*!40000 ALTER TABLE `student_fee_line` DISABLE KEYS */;
INSERT INTO `student_fee_line` VALUES (101,'2026-10-25 08:12:08.508881','2026-10-25 08:12:08.508881',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,3),(102,'2026-10-25 08:12:08.509307','2026-10-25 08:12:08.509307',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,2),(103,'2026-10-25 08:12:08.509575','2026-10-25 08:12:08.509575',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,1),(104,'2026-10-25 08:12:08.509646','2026-10-25 08:12:08.509646',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,6),(105,'2026-10-25 08:12:08.510389','2026-10-25 08:12:08.510389',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,5),(106,'2026-10-25 08:12:08.511674','2026-10-25 08:12:08.511674',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,4),(107,'2026-10-25 08:12:08.545283','2026-10-25 08:12:08.545283',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,3),(108,'2026-10-25 08:12:08.545283','2026-10-25 08:12:08.545283',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,2),(109,'2026-10-25 08:12:08.545317','2026-10-25 08:12:08.545317',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,5),(110,'2026-10-25 08:12:08.545283','2026-10-25 08:12:08.545283',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,4),(111,'2026-10-25 08:12:08.545317','2026-10-25 08:12:08.545317',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,1),(112,'2026-10-25 08:12:08.545283','2026-10-25 08:12:08.545283',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,6),(113,'2026-10-25 08:12:08.569550','2026-10-25 08:12:08.569550',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,9),(114,'2026-10-25 08:12:08.569717','2026-10-25 08:12:08.569717',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,12),(115,'2026-10-25 08:12:08.569606','2026-10-25 08:12:08.569606',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,8),(116,'2026-10-25 08:12:08.569550','2026-10-25 08:12:08.569550',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,7),(117,'2026-10-25 08:12:08.570103','2026-10-25 08:12:08.570103',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,10),(118,'2026-10-25 08:12:08.571072','2026-10-25 08:12:08.571072',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,11),(119,'2026-10-25 08:12:08.572958','2026-10-25 08:12:08.572958',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,12),(120,'2026-10-25 08:12:08.572741','2026-10-25 08:12:08.572741',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,10),(121,'2026-10-25 08:12:08.573411','2026-10-25 08:12:08.573411',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,8),(122,'2026-10-25 08:12:08.573297','2026-10-25 08:12:08.573297',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,7),(123,'2026-10-25 08:12:08.574102','2026-10-25 08:12:08.574102',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,9),(124,'2026-10-25 08:12:08.573810','2026-10-25 08:12:08.573810',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,11),(125,'2026-10-25 08:12:08.580488','2026-10-25 08:12:08.580488',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,16),(126,'2026-10-25 08:12:08.580502','2026-10-25 08:12:08.580502',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,15),(127,'2026-10-25 08:12:08.582452','2026-10-25 08:12:08.582452',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,18),(128,'2026-10-25 08:12:08.582760','2026-10-25 08:12:08.582760',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,13),(129,'2026-10-25 08:12:08.582724','2026-10-25 08:12:08.582724',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,14),(130,'2026-10-25 08:12:08.583254','2026-10-25 08:12:08.583254',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,17),(131,'2026-10-25 08:12:08.584215','2026-10-25 08:12:08.584215',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,16),(132,'2026-10-25 08:12:08.584397','2026-10-25 08:12:08.584397',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,15),(133,'2026-10-25 08:12:08.586277','2026-10-25 08:12:08.586277',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,13),(134,'2026-10-25 08:12:08.586715','2026-10-25 08:12:08.586715',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,14),(135,'2026-10-25 08:12:08.586277','2026-10-25 08:12:08.586277',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,18),(136,'2026-10-25 08:12:08.588816','2026-10-25 08:12:08.588816',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,17),(137,'2026-10-25 08:12:08.617738','2026-10-25 08:12:08.617738',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,20),(138,'2026-10-25 08:12:08.618062','2026-10-25 08:12:08.618062',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,23),(139,'2026-10-25 08:12:08.619254','2026-10-25 08:12:08.619254',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,22),(140,'2026-10-25 08:12:08.619318','2026-10-25 08:12:08.619318',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,24),(141,'2026-10-25 08:12:08.619920','2026-10-25 08:12:08.619920',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,19),(142,'2026-10-25 08:12:08.620492','2026-10-25 08:12:08.620492',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,21),(143,'2026-10-25 08:12:08.620804','2026-10-25 08:12:08.620804',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,20),(144,'2026-10-25 08:12:08.620807','2026-10-25 08:12:08.620807',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,23),(145,'2026-10-25 08:12:08.623653','2026-10-25 08:12:08.623653',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,24),(146,'2026-10-25 08:12:08.623045','2026-10-25 08:12:08.623045',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,22),(147,'2026-10-25 08:12:08.624258','2026-10-25 08:12:08.624258',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,19),(148,'2026-10-25 08:12:08.624858','2026-10-25 08:12:08.624858',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,21),(149,'2026-10-25 08:12:08.625643','2026-10-25 08:12:08.625643',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,29),(150,'2026-10-25 08:12:08.626331','2026-10-25 08:12:08.626331',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,26),(151,'2026-10-25 08:12:08.627920','2026-10-25 08:12:08.627920',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,28),(152,'2026-10-25 08:12:08.628015','2026-10-25 08:12:08.628015',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,30),(153,'2026-10-25 08:12:08.629473','2026-10-25 08:12:08.629473',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,29),(154,'2026-10-25 08:12:08.629790','2026-10-25 08:12:08.629790',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,25),(155,'2026-10-25 08:12:08.629888','2026-10-25 08:12:08.629888',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,27),(156,'2026-10-25 08:12:08.629816','2026-10-25 08:12:08.629816',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,26),(157,'2026-10-25 08:12:08.631955','2026-10-25 08:12:08.631955',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,30),(158,'2026-10-25 08:12:08.632492','2026-10-25 08:12:08.632492',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,28),(159,'2026-10-25 08:12:08.632646','2026-10-25 08:12:08.632646',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,25),(160,'2026-10-25 08:12:08.633967','2026-10-25 08:12:08.633967',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,27),(161,'2026-10-25 08:12:08.634513','2026-10-25 08:12:08.634513',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,32),(162,'2026-10-25 08:12:08.634376','2026-10-25 08:12:08.634376',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,35),(163,'2026-10-25 08:12:08.636928','2026-10-25 08:12:08.636928',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,32),(164,'2026-10-25 08:12:08.636582','2026-10-25 08:12:08.636582',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,31),(165,'2026-10-25 08:12:08.636935','2026-10-25 08:12:08.636935',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,35),(166,'2026-10-25 08:12:08.636821','2026-10-25 08:12:08.636821',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,37),(167,'2026-10-25 08:12:08.637893','2026-10-25 08:12:08.637893',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,33),(168,'2026-10-25 08:12:08.637897','2026-10-25 08:12:08.637897',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,34),(169,'2026-10-25 08:12:08.640267','2026-10-25 08:12:08.640267',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,31),(170,'2026-10-25 08:12:08.640585','2026-10-25 08:12:08.640585',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,33),(171,'2026-10-25 08:12:08.640339','2026-10-25 08:12:08.640339',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,37),(172,'2026-10-25 08:12:08.641652','2026-10-25 08:12:08.641652',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,40),(173,'2026-10-25 08:12:08.642414','2026-10-25 08:12:08.642414',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,38),(174,'2026-10-25 08:12:08.643113','2026-10-25 08:12:08.643113',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,34),(175,'2026-10-25 08:12:08.644149','2026-10-25 08:12:08.644149',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,36),(176,'2026-10-25 08:12:08.644137','2026-10-25 08:12:08.644137',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,41),(177,'2026-10-25 08:12:08.644486','2026-10-25 08:12:08.644486',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,38),(178,'2026-10-25 08:12:08.644671','2026-10-25 08:12:08.644671',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,40),(179,'2026-10-25 08:12:08.644664','2026-10-25 08:12:08.644664',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,43),(180,'2026-10-25 08:12:08.646394','2026-10-25 08:12:08.646394',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,36),(181,'2026-10-25 08:12:08.646620','2026-10-25 08:12:08.646620',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,39),(182,'2026-10-25 08:12:08.646891','2026-10-25 08:12:08.646891',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,41),(183,'2026-10-25 08:12:08.648117','2026-10-25 08:12:08.648117',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,43),(184,'2026-10-25 08:12:08.650102','2026-10-25 08:12:08.650102',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,39),(185,'2026-10-25 08:12:08.650797','2026-10-25 08:12:08.650797',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,44),(186,'2026-10-25 08:12:08.651000','2026-10-25 08:12:08.651000',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,46),(187,'2026-10-25 08:12:08.652656','2026-10-25 08:12:08.652656',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,42),(188,'2026-10-25 08:12:08.654349','2026-10-25 08:12:08.654349',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,44),(189,'2026-10-25 08:12:08.654392','2026-10-25 08:12:08.654392',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,46),(190,'2026-10-25 08:12:08.655066','2026-10-25 08:12:08.655066',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,48),(191,'2026-10-25 08:12:08.656356','2026-10-25 08:12:08.656356',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,49),(192,'2026-10-25 08:12:08.656526','2026-10-25 08:12:08.656526',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,42),(193,'2026-10-25 08:12:08.656822','2026-10-25 08:12:08.656822',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,45),(194,'2026-10-25 08:12:08.657783','2026-10-25 08:12:08.657783',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,50),(195,'2026-10-25 08:12:08.657934','2026-10-25 08:12:08.657934',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,48),(196,'2026-10-25 08:12:08.657981','2026-10-25 08:12:08.657981',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,49),(197,'2026-10-25 08:12:08.658273','2026-10-25 08:12:08.658273',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,53),(198,'2026-10-25 08:12:08.658477','2026-10-25 08:12:08.658477',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,45),(199,'2026-10-25 08:12:08.659681','2026-10-25 08:12:08.659681',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,47),(200,'2026-10-25 08:12:08.660459','2026-10-25 08:12:08.660459',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,50),(201,'2026-10-25 08:12:08.660777','2026-10-25 08:12:08.660777',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,55),(202,'2026-10-25 08:12:08.661152','2026-10-25 08:12:08.661152',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,53),(203,'2026-10-25 08:12:08.661238','2026-10-25 08:12:08.661238',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,54),(204,'2026-10-25 08:12:08.661779','2026-10-25 08:12:08.661779',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,52),(205,'2026-10-25 08:12:08.661941','2026-10-25 08:12:08.661941',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,47),(206,'2026-10-25 08:12:08.663456','2026-10-25 08:12:08.663456',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,56),(207,'2026-10-25 08:12:08.663692','2026-10-25 08:12:08.663692',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,54),(208,'2026-10-25 08:12:08.663683','2026-10-25 08:12:08.663683',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,55),(209,'2026-10-25 08:12:08.664713','2026-10-25 08:12:08.664713',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,59),(210,'2026-10-25 08:12:08.664928','2026-10-25 08:12:08.664928',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,52),(211,'2026-10-25 08:12:08.665552','2026-10-25 08:12:08.665552',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,51),(212,'2026-10-25 08:12:08.666199','2026-10-25 08:12:08.666199',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,60),(213,'2026-10-25 08:12:08.666291','2026-10-25 08:12:08.666291',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,56),(214,'2026-10-25 08:12:08.666884','2026-10-25 08:12:08.666884',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,61),(215,'2026-10-25 08:12:08.667166','2026-10-25 08:12:08.667166',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,59),(216,'2026-10-25 08:12:08.667600','2026-10-25 08:12:08.667600',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,51),(217,'2026-10-25 08:12:08.668772','2026-10-25 08:12:08.668772',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,58),(218,'2026-10-25 08:12:08.668791','2026-10-25 08:12:08.668791',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,60),(219,'2026-10-25 08:12:08.668937','2026-10-25 08:12:08.668937',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,61),(220,'2026-10-25 08:12:08.669706','2026-10-25 08:12:08.669706',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,62),(221,'2026-10-25 08:12:08.670633','2026-10-25 08:12:08.670633',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,57),(222,'2026-10-25 08:12:08.670727','2026-10-25 08:12:08.670727',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,66),(223,'2026-10-25 08:12:08.671057','2026-10-25 08:12:08.671057',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,58),(224,'2026-10-25 08:12:08.671329','2026-10-25 08:12:08.671329',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,65),(225,'2026-10-25 08:12:08.672163','2026-10-25 08:12:08.672163',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,67),(226,'2026-10-25 08:12:08.672362','2026-10-25 08:12:08.672362',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,62),(227,'2026-10-25 08:12:08.672847','2026-10-25 08:12:08.672847',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,57),(228,'2026-10-25 08:12:08.673294','2026-10-25 08:12:08.673294',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,66),(229,'2026-10-25 08:12:08.673464','2026-10-25 08:12:08.673464',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,64),(230,'2026-10-25 08:12:08.673790','2026-10-25 08:12:08.673790',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,65),(231,'2026-10-25 08:12:08.674342','2026-10-25 08:12:08.674342',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,67),(232,'2026-10-25 08:12:08.675818','2026-10-25 08:12:08.675818',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,68),(233,'2026-10-25 08:12:08.675825','2026-10-25 08:12:08.675825',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,64),(234,'2026-10-25 08:12:08.676459','2026-10-25 08:12:08.676459',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,63),(235,'2026-10-25 08:12:08.676879','2026-10-25 08:12:08.676879',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,70),(236,'2026-10-25 08:12:08.677928','2026-10-25 08:12:08.677928',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,69),(237,'2026-10-25 08:12:08.678619','2026-10-25 08:12:08.678619',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,74),(238,'2026-10-25 08:12:08.678561','2026-10-25 08:12:08.678561',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,68),(239,'2026-10-25 08:12:08.678835','2026-10-25 08:12:08.678835',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,63),(240,'2026-10-25 08:12:08.679028','2026-10-25 08:12:08.679028',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,71),(241,'2026-10-25 08:12:08.679466','2026-10-25 08:12:08.679466',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,70),(242,'2026-10-25 08:12:08.680284','2026-10-25 08:12:08.680284',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,74),(243,'2026-10-25 08:12:08.681171','2026-10-25 08:12:08.681171',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,69),(244,'2026-10-25 08:12:08.681498','2026-10-25 08:12:08.681498',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,72),(245,'2026-10-25 08:12:08.681527','2026-10-25 08:12:08.681527',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,73),(246,'2026-10-25 08:12:08.681498','2026-10-25 08:12:08.681498',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,71),(247,'2026-10-25 08:12:08.682018','2026-10-25 08:12:08.682018',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,75),(248,'2026-10-25 08:12:08.683028','2026-10-25 08:12:08.683028',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,80),(249,'2026-10-25 08:12:08.683815','2026-10-25 08:12:08.683815',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,72),(250,'2026-10-25 08:12:08.683967','2026-10-25 08:12:08.683967',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,73),(251,'2026-10-25 08:12:08.684047','2026-10-25 08:12:08.684047',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,75),(252,'2026-10-25 08:12:08.684446','2026-10-25 08:12:08.684446',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,77),(253,'2026-10-25 08:12:08.684897','2026-10-25 08:12:08.684897',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,76),(254,'2026-10-25 08:12:08.685206','2026-10-25 08:12:08.685206',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,80),(255,'2026-10-25 08:12:08.686308','2026-10-25 08:12:08.686308',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,77),(256,'2026-10-25 08:12:08.686764','2026-10-25 08:12:08.686764',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,79),(257,'2026-10-25 08:12:08.686893','2026-10-25 08:12:08.686893',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,83),(258,'2026-10-25 08:12:08.687337','2026-10-25 08:12:08.687337',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,76),(259,'2026-10-25 08:12:08.688086','2026-10-25 08:12:08.688086',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,78),(260,'2026-10-25 08:12:08.688338','2026-10-25 08:12:08.688338',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,86),(261,'2026-10-25 08:12:08.688643','2026-10-25 08:12:08.688643',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,84),(262,'2026-10-25 08:12:08.689040','2026-10-25 08:12:08.689040',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,79),(263,'2026-10-25 08:12:08.689876','2026-10-25 08:12:08.689876',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,83),(264,'2026-10-25 08:12:08.690251','2026-10-25 08:12:08.690251',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,78),(265,'2026-10-25 08:12:08.690640','2026-10-25 08:12:08.690640',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,84),(266,'2026-10-25 08:12:08.690803','2026-10-25 08:12:08.690803',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,86),(267,'2026-10-25 08:12:08.691231','2026-10-25 08:12:08.691231',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,81),(268,'2026-10-25 08:12:08.692664','2026-10-25 08:12:08.692664',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,85),(269,'2026-10-25 08:12:08.692823','2026-10-25 08:12:08.692823',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,90),(270,'2026-10-25 08:12:08.693250','2026-10-25 08:12:08.693250',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,82),(271,'2026-10-25 08:12:08.693695','2026-10-25 08:12:08.693695',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,89),(272,'2026-10-25 08:12:08.693611','2026-10-25 08:12:08.693611',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,93),(273,'2026-10-25 08:12:08.694814','2026-10-25 08:12:08.694814',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,81),(274,'2026-10-25 08:12:08.695369','2026-10-25 08:12:08.695369',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,90),(275,'2026-10-25 08:12:08.695369','2026-10-25 08:12:08.695369',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,85),(276,'2026-10-25 08:12:08.695839','2026-10-25 08:12:08.695839',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,82),(277,'2026-10-25 08:12:08.695783','2026-10-25 08:12:08.695783',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,93),(278,'2026-10-25 08:12:08.696030','2026-10-25 08:12:08.696030',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,89),(279,'2026-10-25 08:12:08.698305','2026-10-25 08:12:08.698305',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,92),(280,'2026-10-25 08:12:08.698467','2026-10-25 08:12:08.698467',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,98),(281,'2026-10-25 08:12:08.698599','2026-10-25 08:12:08.698599',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,96),(282,'2026-10-25 08:12:08.698599','2026-10-25 08:12:08.698599',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,87),(283,'2026-10-25 08:12:08.698690','2026-10-25 08:12:08.698690',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,95),(284,'2026-10-25 08:12:08.699302','2026-10-25 08:12:08.699302',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,88),(285,'2026-10-25 08:12:08.700276','2026-10-25 08:12:08.700276',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,92),(286,'2026-10-25 08:12:08.700771','2026-10-25 08:12:08.700771',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,98),(287,'2026-10-25 08:12:08.701000','2026-10-25 08:12:08.701000',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,96),(288,'2026-10-25 08:12:08.700990','2026-10-25 08:12:08.700990',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,87),(289,'2026-10-25 08:12:08.701923','2026-10-25 08:12:08.701923',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,88),(290,'2026-10-25 08:12:08.702050','2026-10-25 08:12:08.702050',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,95),(291,'2026-10-25 08:12:08.703464','2026-10-25 08:12:08.703464',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,97),(292,'2026-10-25 08:12:08.703831','2026-10-25 08:12:08.703831',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,99),(293,'2026-10-25 08:12:08.704170','2026-10-25 08:12:08.704170',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,91),(294,'2026-10-25 08:12:08.704089','2026-10-25 08:12:08.704089',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,100),(295,'2026-10-25 08:12:08.704851','2026-10-25 08:12:08.704851',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,94),(296,'2026-10-25 08:12:08.705823','2026-10-25 08:12:08.705823',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,97),(297,'2026-10-25 08:12:08.706290','2026-10-25 08:12:08.706290',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,99),(298,'2026-10-25 08:12:08.706262','2026-10-25 08:12:08.706262',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,91),(299,'2026-10-25 08:12:08.707071','2026-10-25 08:12:08.707071',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,100),(300,'2026-10-25 08:12:08.707725','2026-10-25 08:12:08.707725',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,94),(301,'2026-10-25 08:12:26.511365','2026-10-25 08:12:26.511365',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,101),(302,'2026-10-25 08:12:26.511319','2026-10-25 08:12:26.511319',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,102),(303,'2026-10-25 08:12:26.511463','2026-10-25 08:12:26.511463',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,103),(304,'2026-10-25 08:12:26.512782','2026-10-25 08:12:26.512782',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,104),(305,'2026-10-25 08:12:26.513028','2026-10-25 08:12:26.513028',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,105),(306,'2026-10-25 08:12:26.513045','2026-10-25 08:12:26.513045',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,106),(307,'2026-10-25 08:12:26.514960','2026-10-25 08:12:26.514960',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,108),(308,'2026-10-25 08:12:26.514951','2026-10-25 08:12:26.514951',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,109),(309,'2026-10-25 08:12:26.515037','2026-10-25 08:12:26.515037',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,107),(310,'2026-10-25 08:12:26.515353','2026-10-25 08:12:26.515353',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,110),(311,'2026-10-25 08:12:26.516269','2026-10-25 08:12:26.516269',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,111),(312,'2026-10-25 08:12:26.516284','2026-10-25 08:12:26.516284',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,112),(313,'2026-10-25 08:12:26.518208','2026-10-25 08:12:26.518208',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,113),(314,'2026-10-25 08:12:26.518262','2026-10-25 08:12:26.518262',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,115),(315,'2026-10-25 08:12:26.518383','2026-10-25 08:12:26.518383',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,114),(316,'2026-10-25 08:12:26.518489','2026-10-25 08:12:26.518489',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,116),(317,'2026-10-25 08:12:26.518985','2026-10-25 08:12:26.518985',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,117),(318,'2026-10-25 08:12:26.519169','2026-10-25 08:12:26.519169',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,118),(319,'2026-10-25 08:12:26.520691','2026-10-25 08:12:26.520691',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,120),(320,'2026-10-25 08:12:26.520482','2026-10-25 08:12:26.520482',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,119),(321,'2026-10-25 08:12:26.521311','2026-10-25 08:12:26.521311',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,122),(322,'2026-10-25 08:12:26.521232','2026-10-25 08:12:26.521232',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,121),(323,'2026-10-25 08:12:26.522258','2026-10-25 08:12:26.522258',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,123),(324,'2026-10-25 08:12:26.522697','2026-10-25 08:12:26.522697',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,124),(325,'2026-10-25 08:12:26.522927','2026-10-25 08:12:26.522927',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,125),(326,'2026-10-25 08:12:26.523509','2026-10-25 08:12:26.523509',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,126),(327,'2026-10-25 08:12:26.523857','2026-10-25 08:12:26.523857',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,127),(328,'2026-10-25 08:12:26.524028','2026-10-25 08:12:26.524028',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,128),(329,'2026-10-25 08:12:26.524976','2026-10-25 08:12:26.524976',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,129),(330,'2026-10-25 08:12:26.525239','2026-10-25 08:12:26.525239',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,130),(331,'2026-10-25 08:12:26.525481','2026-10-25 08:12:26.525481',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,131),(332,'2026-10-25 08:12:26.527798','2026-10-25 08:12:26.527798',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,132),(333,'2026-10-25 08:12:26.528249','2026-10-25 08:12:26.528249',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,133),(334,'2026-10-25 08:12:26.528267','2026-10-25 08:12:26.528267',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,135),(335,'2026-10-25 08:12:26.528416','2026-10-25 08:12:26.528416',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,136),(336,'2026-10-25 08:12:26.528430','2026-10-25 08:12:26.528430',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,134),(337,'2026-10-25 08:12:26.530065','2026-10-25 08:12:26.530065',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,138),(338,'2026-10-25 08:12:26.530069','2026-10-25 08:12:26.530069',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,137),(339,'2026-10-25 08:12:26.530332','2026-10-25 08:12:26.530332',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,139),(340,'2026-10-25 08:12:26.530328','2026-10-25 08:12:26.530328',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,140),(341,'2026-10-25 08:12:26.530424','2026-10-25 08:12:26.530424',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,141),(342,'2026-10-25 08:12:26.530682','2026-10-25 08:12:26.530682',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,142),(343,'2026-10-25 08:12:26.532719','2026-10-25 08:12:26.532719',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,145),(344,'2026-10-25 08:12:26.532807','2026-10-25 08:12:26.532807',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,144),(345,'2026-10-25 08:12:26.532863','2026-10-25 08:12:26.532863',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,146),(346,'2026-10-25 08:12:26.532942','2026-10-25 08:12:26.532942',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,147),(347,'2026-10-25 08:12:26.532683','2026-10-25 08:12:26.532683',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,143),(348,'2026-10-25 08:12:26.533052','2026-10-25 08:12:26.533052',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,148),(349,'2026-10-25 08:12:26.535544','2026-10-25 08:12:26.535544',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,149),(350,'2026-10-25 08:12:26.535835','2026-10-25 08:12:26.535835',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,152),(351,'2026-10-25 08:12:26.535861','2026-10-25 08:12:26.535861',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,150),(352,'2026-10-25 08:12:26.535860','2026-10-25 08:12:26.535860',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,151),(353,'2026-10-25 08:12:26.535963','2026-10-25 08:12:26.535963',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,153),(354,'2026-10-25 08:12:26.536097','2026-10-25 08:12:26.536097',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,154),(355,'2026-10-25 08:12:26.538079','2026-10-25 08:12:26.538079',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,155),(356,'2026-10-25 08:12:26.538614','2026-10-25 08:12:26.538614',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,156),(357,'2026-10-25 08:12:26.538865','2026-10-25 08:12:26.538865',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,157),(358,'2026-10-25 08:12:26.538898','2026-10-25 08:12:26.538898',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,158),(359,'2026-10-25 08:12:26.539180','2026-10-25 08:12:26.539180',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,159),(360,'2026-10-25 08:12:26.539352','2026-10-25 08:12:26.539352',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,160),(361,'2026-10-25 08:12:26.541601','2026-10-25 08:12:26.541601',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,163),(362,'2026-10-25 08:12:26.541686','2026-10-25 08:12:26.541686',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,162),(363,'2026-10-25 08:12:26.541597','2026-10-25 08:12:26.541597',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,161),(364,'2026-10-25 08:12:26.541724','2026-10-25 08:12:26.541724',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,164),(365,'2026-10-25 08:12:26.541889','2026-10-25 08:12:26.541889',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,165),(366,'2026-10-25 08:12:26.542085','2026-10-25 08:12:26.542085',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,166),(367,'2026-10-25 08:12:26.544363','2026-10-25 08:12:26.544363',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,167),(368,'2026-10-25 08:12:26.544395','2026-10-25 08:12:26.544395',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,168),(369,'2026-10-25 08:12:26.544468','2026-10-25 08:12:26.544468',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,169),(370,'2026-10-25 08:12:26.544543','2026-10-25 08:12:26.544543',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,170),(371,'2026-10-25 08:12:26.544686','2026-10-25 08:12:26.544686',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,171),(372,'2026-10-25 08:12:26.544987','2026-10-25 08:12:26.544987',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,172),(373,'2026-10-25 08:12:26.546771','2026-10-25 08:12:26.546771',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,173),(374,'2026-10-25 08:12:26.546921','2026-10-25 08:12:26.546921',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,174),(375,'2026-10-25 08:12:26.546988','2026-10-25 08:12:26.546988',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,175),(376,'2026-10-25 08:12:26.547183','2026-10-25 08:12:26.547183',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,176),(377,'2026-10-25 08:12:26.547646','2026-10-25 08:12:26.547646',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,178),(378,'2026-10-25 08:12:26.547461','2026-10-25 08:12:26.547461',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,177),(379,'2026-10-25 08:12:26.549239','2026-10-25 08:12:26.549239',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,179),(380,'2026-10-25 08:12:26.549544','2026-10-25 08:12:26.549544',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,180),(381,'2026-10-25 08:12:26.549829','2026-10-25 08:12:26.549829',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,181),(382,'2026-10-25 08:12:26.549923','2026-10-25 08:12:26.549923',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,183),(383,'2026-10-25 08:12:26.549959','2026-10-25 08:12:26.549959',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,182),(384,'2026-10-25 08:12:26.550012','2026-10-25 08:12:26.550012',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,184),(385,'2026-10-25 08:12:26.551964','2026-10-25 08:12:26.551964',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,185),(386,'2026-10-25 08:12:26.552215','2026-10-25 08:12:26.552215',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,186),(387,'2026-10-25 08:12:26.552446','2026-10-25 08:12:26.552446',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,188),(388,'2026-10-25 08:12:26.552444','2026-10-25 08:12:26.552444',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,187),(389,'2026-10-25 08:12:26.552559','2026-10-25 08:12:26.552559',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,189),(390,'2026-10-25 08:12:26.552745','2026-10-25 08:12:26.552745',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,190),(391,'2026-10-25 08:12:26.554858','2026-10-25 08:12:26.554858',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,193),(392,'2026-10-25 08:12:26.554720','2026-10-25 08:12:26.554720',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,191),(393,'2026-10-25 08:12:26.554884','2026-10-25 08:12:26.554884',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,192),(394,'2026-10-25 08:12:26.555115','2026-10-25 08:12:26.555115',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,194),(395,'2026-10-25 08:12:26.555428','2026-10-25 08:12:26.555428',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,196),(396,'2026-10-25 08:12:26.555643','2026-10-25 08:12:26.555643',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,195),(397,'2026-10-25 08:12:26.557818','2026-10-25 08:12:26.557818',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,197),(398,'2026-10-25 08:12:26.557912','2026-10-25 08:12:26.557912',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,199),(399,'2026-10-25 08:12:26.558209','2026-10-25 08:12:26.558209',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,200),(400,'2026-10-25 08:12:26.558325','2026-10-25 08:12:26.558325',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,198),(401,'2026-10-25 08:12:36.586099','2026-10-25 08:12:36.586099',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,203),(402,'2026-10-25 08:12:36.586286','2026-10-25 08:12:36.586286',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,205),(403,'2026-10-25 08:12:36.586036','2026-10-25 08:12:36.586036',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,201),(404,'2026-10-25 08:12:36.586240','2026-10-25 08:12:36.586240',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,202),(405,'2026-10-25 08:12:36.588275','2026-10-25 08:12:36.588275',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,206),(406,'2026-10-25 08:12:36.587544','2026-10-25 08:12:36.587544',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,204),(407,'2026-10-25 08:12:36.588986','2026-10-25 08:12:36.588986',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,207),(408,'2026-10-25 08:12:36.589834','2026-10-25 08:12:36.589834',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,209),(409,'2026-10-25 08:12:36.589641','2026-10-25 08:12:36.589641',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,208),(410,'2026-10-25 08:12:36.592420','2026-10-25 08:12:36.592420',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,210),(411,'2026-10-25 08:12:36.592603','2026-10-25 08:12:36.592603',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,211),(412,'2026-10-25 08:12:36.592796','2026-10-25 08:12:36.592796',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,212),(413,'2026-10-25 08:12:36.593105','2026-10-25 08:12:36.593105',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,213),(414,'2026-10-25 08:12:36.593266','2026-10-25 08:12:36.593266',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,214),(415,'2026-10-25 08:12:36.593419','2026-10-25 08:12:36.593419',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,215),(416,'2026-10-25 08:12:36.595424','2026-10-25 08:12:36.595424',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,216),(417,'2026-10-25 08:12:36.595679','2026-10-25 08:12:36.595679',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,217),(418,'2026-10-25 08:12:36.595733','2026-10-25 08:12:36.595733',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,219),(419,'2026-10-25 08:12:36.595581','2026-10-25 08:12:36.595581',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,218),(420,'2026-10-25 08:12:36.596061','2026-10-25 08:12:36.596061',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,220),(421,'2026-10-25 08:12:36.596555','2026-10-25 08:12:36.596555',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,221),(422,'2026-10-25 08:12:36.598657','2026-10-25 08:12:36.598657',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,222),(423,'2026-10-25 08:12:36.598534','2026-10-25 08:12:36.598534',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,223),(424,'2026-10-25 08:12:36.598940','2026-10-25 08:12:36.598940',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,225),(425,'2026-10-25 08:12:36.598851','2026-10-25 08:12:36.598851',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,224),(426,'2026-10-25 08:12:36.599060','2026-10-25 08:12:36.599060',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,226),(427,'2026-10-25 08:12:36.599422','2026-10-25 08:12:36.599422',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,227),(428,'2026-10-25 08:12:36.601373','2026-10-25 08:12:36.601373',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,228),(429,'2026-10-25 08:12:36.601571','2026-10-25 08:12:36.601571',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,229),(430,'2026-10-25 08:12:36.601659','2026-10-25 08:12:36.601659',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,230),(431,'2026-10-25 08:12:36.601913','2026-10-25 08:12:36.601913',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,232),(432,'2026-10-25 08:12:36.602418','2026-10-25 08:12:36.602418',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,233),(433,'2026-10-25 08:12:36.603342','2026-10-25 08:12:36.603342',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,231),(434,'2026-10-25 08:12:36.604124','2026-10-25 08:12:36.604124',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,234),(435,'2026-10-25 08:12:36.604270','2026-10-25 08:12:36.604270',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,235),(436,'2026-10-25 08:12:36.604313','2026-10-25 08:12:36.604313',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,236),(437,'2026-10-25 08:12:36.604834','2026-10-25 08:12:36.604834',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,237),(438,'2026-10-25 08:12:36.605341','2026-10-25 08:12:36.605341',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,238),(439,'2026-10-25 08:12:36.636762','2026-10-25 08:12:36.636762',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,239),(440,'2026-10-25 08:12:36.637202','2026-10-25 08:12:36.637202',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,241),(441,'2026-10-25 08:12:36.637273','2026-10-25 08:12:36.637273',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,240),(442,'2026-10-25 08:12:36.638208','2026-10-25 08:12:36.638208',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,242),(443,'2026-10-25 08:12:36.638724','2026-10-25 08:12:36.638724',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,243),(444,'2026-10-25 08:12:36.639066','2026-10-25 08:12:36.639066',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,244),(445,'2026-10-25 08:12:36.640918','2026-10-25 08:12:36.640918',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,245),(446,'2026-10-25 08:12:36.642434','2026-10-25 08:12:36.642434',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,246),(447,'2026-10-25 08:12:36.642682','2026-10-25 08:12:36.642682',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,247),(448,'2026-10-25 08:12:36.642463','2026-10-25 08:12:36.642463',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,248),(449,'2026-10-25 08:12:36.644533','2026-10-25 08:12:36.644533',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,250),(450,'2026-10-25 08:12:36.644400','2026-10-25 08:12:36.644400',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,249),(451,'2026-10-25 08:12:36.645449','2026-10-25 08:12:36.645449',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,251),(452,'2026-10-25 08:12:36.645464','2026-10-25 08:12:36.645464',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,252),(453,'2026-10-25 08:12:36.647387','2026-10-25 08:12:36.647387',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,253),(454,'2026-10-25 08:12:36.648093','2026-10-25 08:12:36.648093',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,255),(455,'2026-10-25 08:12:36.649996','2026-10-25 08:12:36.649996',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,256),(456,'2026-10-25 08:12:36.650064','2026-10-25 08:12:36.650064',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,257),(457,'2026-10-25 08:12:36.651225','2026-10-25 08:12:36.651225',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,258),(458,'2026-10-25 08:12:36.652116','2026-10-25 08:12:36.652116',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,260),(459,'2026-10-25 08:12:36.653614','2026-10-25 08:12:36.653614',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,259),(460,'2026-10-25 08:12:36.655344','2026-10-25 08:12:36.655344',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,261),(461,'2026-10-25 08:12:36.656199','2026-10-25 08:12:36.656199',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,262),(462,'2026-10-25 08:12:36.657993','2026-10-25 08:12:36.657993',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,264),(463,'2026-10-25 08:12:36.658170','2026-10-25 08:12:36.658170',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,254),(464,'2026-10-25 08:12:36.659181','2026-10-25 08:12:36.659181',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,263),(465,'2026-10-25 08:12:36.663257','2026-10-25 08:12:36.663257',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,266),(466,'2026-10-25 08:12:36.663224','2026-10-25 08:12:36.663224',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,265),(467,'2026-10-25 08:12:36.663895','2026-10-25 08:12:36.663895',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,268),(468,'2026-10-25 08:12:36.663804','2026-10-25 08:12:36.663804',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,267),(469,'2026-10-25 08:12:36.664370','2026-10-25 08:12:36.664370',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,270),(470,'2026-10-25 08:12:36.664304','2026-10-25 08:12:36.664304',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,269),(471,'2026-10-25 08:12:36.667431','2026-10-25 08:12:36.667431',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,272),(472,'2026-10-25 08:12:36.666941','2026-10-25 08:12:36.666941',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,271),(473,'2026-10-25 08:12:36.667514','2026-10-25 08:12:36.667514',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,273),(474,'2026-10-25 08:12:36.667565','2026-10-25 08:12:36.667565',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,274),(475,'2026-10-25 08:12:36.667676','2026-10-25 08:12:36.667676',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,275),(476,'2026-10-25 08:12:36.668628','2026-10-25 08:12:36.668628',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,276),(477,'2026-10-25 08:12:36.671112','2026-10-25 08:12:36.671112',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,278),(478,'2026-10-25 08:12:36.671298','2026-10-25 08:12:36.671298',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,277),(479,'2026-10-25 08:12:36.671653','2026-10-25 08:12:36.671653',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,279),(480,'2026-10-25 08:12:36.672119','2026-10-25 08:12:36.672119',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,281),(481,'2026-10-25 08:12:36.673058','2026-10-25 08:12:36.673058',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,282),(482,'2026-10-25 08:12:36.674470','2026-10-25 08:12:36.674470',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,283),(483,'2026-10-25 08:12:36.674603','2026-10-25 08:12:36.674603',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,284),(484,'2026-10-25 08:12:36.674796','2026-10-25 08:12:36.674796',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,285),(485,'2026-10-25 08:12:36.675572','2026-10-25 08:12:36.675572',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,286),(486,'2026-10-25 08:12:36.675841','2026-10-25 08:12:36.675841',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,287),(487,'2026-10-25 08:12:36.676753','2026-10-25 08:12:36.676753',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,280),(488,'2026-10-25 08:12:36.676968','2026-10-25 08:12:36.676968',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,288),(489,'2026-10-25 08:12:36.677344','2026-10-25 08:12:36.677344',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,289),(490,'2026-10-25 08:12:36.677712','2026-10-25 08:12:36.677712',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,290),(491,'2026-10-25 08:12:36.678523','2026-10-25 08:12:36.678523',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,291),(492,'2026-10-25 08:12:36.679764','2026-10-25 08:12:36.679764',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,292),(493,'2026-10-25 08:12:36.679949','2026-10-25 08:12:36.679949',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,293),(494,'2026-10-25 08:12:36.680455','2026-10-25 08:12:36.680455',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,294),(495,'2026-10-25 08:12:36.682304','2026-10-25 08:12:36.682304',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,296),(496,'2026-10-25 08:12:36.682338','2026-10-25 08:12:36.682338',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,295),(497,'2026-10-25 08:12:36.682727','2026-10-25 08:12:36.682727',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,297),(498,'2026-10-25 08:12:36.684075','2026-10-25 08:12:36.684075',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,298),(499,'2026-10-25 08:12:36.684431','2026-10-25 08:12:36.684431',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,299),(500,'2026-10-25 08:12:36.685835','2026-10-25 08:12:36.685835',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,300),(501,'2026-10-25 08:12:49.237063','2026-10-25 08:12:49.237063',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,303),(502,'2026-10-25 08:12:49.237041','2026-10-25 08:12:49.237041',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,302),(503,'2026-10-25 08:12:49.237182','2026-10-25 08:12:49.237182',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,306),(504,'2026-10-25 08:12:49.237180','2026-10-25 08:12:49.237180',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,305),(505,'2026-10-25 08:12:49.237231','2026-10-25 08:12:49.237231',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,301),(506,'2026-10-25 08:12:49.237287','2026-10-25 08:12:49.237287',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,304),(507,'2026-10-25 08:12:49.239309','2026-10-25 08:12:49.239309',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,307),(508,'2026-10-25 08:12:49.239324','2026-10-25 08:12:49.239324',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,308),(509,'2026-10-25 08:12:49.239466','2026-10-25 08:12:49.239466',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,309),(510,'2026-10-25 08:12:49.239541','2026-10-25 08:12:49.239541',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,310),(511,'2026-10-25 08:12:49.239574','2026-10-25 08:12:49.239574',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,311),(512,'2026-10-25 08:12:49.239861','2026-10-25 08:12:49.239861',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,312),(513,'2026-10-25 08:12:49.241430','2026-10-25 08:12:49.241430',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,313),(514,'2026-10-25 08:12:49.241492','2026-10-25 08:12:49.241492',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,315),(515,'2026-10-25 08:12:49.241484','2026-10-25 08:12:49.241484',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,314),(516,'2026-10-25 08:12:49.241662','2026-10-25 08:12:49.241662',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,316),(517,'2026-10-25 08:12:49.241711','2026-10-25 08:12:49.241711',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,317),(518,'2026-10-25 08:12:49.241784','2026-10-25 08:12:49.241784',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,318),(519,'2026-10-25 08:12:49.243535','2026-10-25 08:12:49.243535',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,320),(520,'2026-10-25 08:12:49.243534','2026-10-25 08:12:49.243534',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,319),(521,'2026-10-25 08:12:49.243584','2026-10-25 08:12:49.243584',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,321),(522,'2026-10-25 08:12:49.243712','2026-10-25 08:12:49.243712',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,322),(523,'2026-10-25 08:12:49.243818','2026-10-25 08:12:49.243818',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,323),(524,'2026-10-25 08:12:49.243799','2026-10-25 08:12:49.243799',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,324),(525,'2026-10-25 08:12:49.245699','2026-10-25 08:12:49.245699',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,327),(526,'2026-10-25 08:12:49.245682','2026-10-25 08:12:49.245682',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,325),(527,'2026-10-25 08:12:49.245862','2026-10-25 08:12:49.245862',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,328),(528,'2026-10-25 08:12:49.246685','2026-10-25 08:12:49.246685',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,326),(529,'2026-10-25 08:12:49.246669','2026-10-25 08:12:49.246669',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,330),(530,'2026-10-25 08:12:49.251646','2026-10-25 08:12:49.251646',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,329),(531,'2026-10-25 08:12:49.251646','2026-10-25 08:12:49.251646',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,333),(532,'2026-10-25 08:12:49.251807','2026-10-25 08:12:49.251807',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,331),(533,'2026-10-25 08:12:49.251806','2026-10-25 08:12:49.251806',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,332),(534,'2026-10-25 08:12:49.253887','2026-10-25 08:12:49.253887',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,335),(535,'2026-10-25 08:12:49.253985','2026-10-25 08:12:49.253985',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,337),(536,'2026-10-25 08:12:49.254019','2026-10-25 08:12:49.254019',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,336),(537,'2026-10-25 08:12:49.254090','2026-10-25 08:12:49.254090',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,338),(538,'2026-10-25 08:12:49.254164','2026-10-25 08:12:49.254164',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,334),(539,'2026-10-25 08:12:49.254144','2026-10-25 08:12:49.254144',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,339),(540,'2026-10-25 08:12:49.256444','2026-10-25 08:12:49.256444',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,342),(541,'2026-10-25 08:12:49.256449','2026-10-25 08:12:49.256449',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,340),(542,'2026-10-25 08:12:49.256440','2026-10-25 08:12:49.256440',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,343),(543,'2026-10-25 08:12:49.256589','2026-10-25 08:12:49.256589',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,344),(544,'2026-10-25 08:12:49.256603','2026-10-25 08:12:49.256603',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,341),(545,'2026-10-25 08:12:49.256680','2026-10-25 08:12:49.256680',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,345),(546,'2026-10-25 08:12:49.258977','2026-10-25 08:12:49.258977',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,348),(547,'2026-10-25 08:12:49.258974','2026-10-25 08:12:49.258974',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,346),(548,'2026-10-25 08:12:49.258976','2026-10-25 08:12:49.258976',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,347),(549,'2026-10-25 08:12:49.259239','2026-10-25 08:12:49.259239',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,351),(550,'2026-10-25 08:12:49.259237','2026-10-25 08:12:49.259237',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,349),(551,'2026-10-25 08:12:49.259229','2026-10-25 08:12:49.259229',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,350),(552,'2026-10-25 08:12:49.263058','2026-10-25 08:12:49.263058',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,352),(553,'2026-10-25 08:12:49.264001','2026-10-25 08:12:49.264001',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,354),(554,'2026-10-25 08:12:49.264558','2026-10-25 08:12:49.264558',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,355),(555,'2026-10-25 08:12:49.264603','2026-10-25 08:12:49.264603',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,357),(556,'2026-10-25 08:12:49.264583','2026-10-25 08:12:49.264583',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,353),(557,'2026-10-25 08:12:49.264778','2026-10-25 08:12:49.264778',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,356),(558,'2026-10-25 08:12:49.266473','2026-10-25 08:12:49.266473',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,358),(559,'2026-10-25 08:12:49.266631','2026-10-25 08:12:49.266631',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,359),(560,'2026-10-25 08:12:49.266927','2026-10-25 08:12:49.266927',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,360),(561,'2026-10-25 08:12:49.267048','2026-10-25 08:12:49.267048',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,361),(562,'2026-10-25 08:12:49.267604','2026-10-25 08:12:49.267604',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,362),(563,'2026-10-25 08:12:49.268394','2026-10-25 08:12:49.268394',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,363),(564,'2026-10-25 08:12:49.269694','2026-10-25 08:12:49.269694',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,364),(565,'2026-10-25 08:12:49.270246','2026-10-25 08:12:49.270246',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,365),(566,'2026-10-25 08:12:49.270353','2026-10-25 08:12:49.270353',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,367),(567,'2026-10-25 08:12:49.270333','2026-10-25 08:12:49.270333',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,366),(568,'2026-10-25 08:12:49.270923','2026-10-25 08:12:49.270923',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,368),(569,'2026-10-25 08:12:49.271536','2026-10-25 08:12:49.271536',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,369),(570,'2026-10-25 08:12:49.272321','2026-10-25 08:12:49.272321',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,370),(571,'2026-10-25 08:12:49.272727','2026-10-25 08:12:49.272727',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,372),(572,'2026-10-25 08:12:49.272816','2026-10-25 08:12:49.272816',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,371),(573,'2026-10-25 08:12:49.273409','2026-10-25 08:12:49.273409',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,373),(574,'2026-10-25 08:12:49.274065','2026-10-25 08:12:49.274065',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,375),(575,'2026-10-25 08:12:49.274113','2026-10-25 08:12:49.274113',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,376),(576,'2026-10-25 08:12:49.274082','2026-10-25 08:12:49.274082',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,374),(577,'2026-10-25 08:12:49.274616','2026-10-25 08:12:49.274616',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,377),(578,'2026-10-25 08:12:49.274959','2026-10-25 08:12:49.274959',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,378),(579,'2026-10-25 08:12:49.275802','2026-10-25 08:12:49.275802',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,379),(580,'2026-10-25 08:12:49.276069','2026-10-25 08:12:49.276069',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,380),(581,'2026-10-25 08:12:49.276257','2026-10-25 08:12:49.276257',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,382),(582,'2026-10-25 08:12:49.276286','2026-10-25 08:12:49.276286',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,381),(583,'2026-10-25 08:12:49.276755','2026-10-25 08:12:49.276755',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,383),(584,'2026-10-25 08:12:49.277046','2026-10-25 08:12:49.277046',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,384),(585,'2026-10-25 08:12:49.277878','2026-10-25 08:12:49.277878',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,385),(586,'2026-10-25 08:12:49.278093','2026-10-25 08:12:49.278093',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,386),(587,'2026-10-25 08:12:49.278245','2026-10-25 08:12:49.278245',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,387),(588,'2026-10-25 08:12:49.278849','2026-10-25 08:12:49.278849',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,388),(589,'2026-10-25 08:12:49.278931','2026-10-25 08:12:49.278931',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,389),(590,'2026-10-25 08:12:49.279004','2026-10-25 08:12:49.279004',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,390),(591,'2026-10-25 08:12:49.279950','2026-10-25 08:12:49.279950',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,391),(592,'2026-10-25 08:12:49.280192','2026-10-25 08:12:49.280192',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,392),(593,'2026-10-25 08:12:49.280266','2026-10-25 08:12:49.280266',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,393),(594,'2026-10-25 08:12:49.281097','2026-10-25 08:12:49.281097',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,394),(595,'2026-10-25 08:12:49.281359','2026-10-25 08:12:49.281359',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,395),(596,'2026-10-25 08:12:49.281713','2026-10-25 08:12:49.281713',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,396),(597,'2026-10-25 08:12:49.282800','2026-10-25 08:12:49.282800',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,397),(598,'2026-10-25 08:12:49.283257','2026-10-25 08:12:49.283257',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,398),(599,'2026-10-25 08:12:49.284361','2026-10-25 08:12:49.284361',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,399),(600,'2026-10-25 08:12:49.285282','2026-10-25 08:12:49.285282',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,400),(601,'2026-10-25 17:44:23.085831','2026-10-25 17:44:23.085831',950,NULL,_binary '\0',_binary '','School Fees',1,1,1,401),(602,'2026-10-25 17:44:23.093058','2026-10-25 17:44:23.093058',250,'Every student must pay before schools are reopen.',_binary '',_binary '','Registration Fee',1,1,1,401),(603,'2026-10-25 17:44:23.102401','2026-10-25 17:44:23.102401',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,402),(604,'2026-10-25 17:44:23.107443','2026-10-25 17:44:23.107443',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,403),(605,'2026-10-25 17:44:23.111939','2026-10-25 17:44:23.111939',600,NULL,_binary '\0',_binary '','School Fees',1,1,1,404);
/*!40000 ALTER TABLE `student_fee_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_fee_payment`
--

DROP TABLE IF EXISTS `student_fee_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_fee_payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` double NOT NULL,
  `balance_after` double NOT NULL,
  `method` enum('CARD','CASH','ECOCASH','EFT','MPESA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `paid_at` datetime(6) NOT NULL,
  `reference` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  `received_by` bigint DEFAULT NULL,
  `reversal_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reversed_at` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','REVERSED') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reversed_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2tt4cs76ltcttvd3ujbn0crw` (`created_by`),
  KEY `FKjyvcnjeh5fhm4n5vcx9l4ghth` (`updated_by`),
  KEY `FK4qo0cch88ct49bbhwtxxhm1ox` (`school_id`),
  KEY `FK2f0xtmif6qkdfo9g82jvoo8sg` (`account_id`),
  KEY `FKnghd2slwb7oei7hmncf12jpya` (`received_by`),
  KEY `FKofj9vsce9cvu0p99xhtpf4n0g` (`reversed_by`),
  CONSTRAINT `FK2f0xtmif6qkdfo9g82jvoo8sg` FOREIGN KEY (`account_id`) REFERENCES `student_fee_account` (`id`),
  CONSTRAINT `FK2tt4cs76ltcttvd3ujbn0crw` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK4qo0cch88ct49bbhwtxxhm1ox` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKjyvcnjeh5fhm4n5vcx9l4ghth` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKnghd2slwb7oei7hmncf12jpya` FOREIGN KEY (`received_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKofj9vsce9cvu0p99xhtpf4n0g` FOREIGN KEY (`reversed_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_payment`
--

LOCK TABLES `student_fee_payment` WRITE;
/*!40000 ALTER TABLE `student_fee_payment` DISABLE KEYS */;
INSERT INTO `student_fee_payment` VALUES (1,'2026-10-25 12:21:15.704759','2026-10-25 12:21:15.704759',600,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,303,1,NULL,NULL,NULL,NULL),(2,'2026-10-25 12:34:43.304844','2026-10-25 12:34:43.304844',1200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,2,1,NULL,NULL,NULL,NULL),(3,'2026-10-25 12:55:23.581916','2026-10-25 12:55:23.581916',1200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,11,1,NULL,NULL,'ACTIVE',NULL),(4,'2026-10-25 12:57:24.695909','2026-10-25 12:57:41.994058',300,900,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,18,1,'It was wrong payment','2026-10-25 12:57:41.979805','REVERSED',1),(5,'2026-10-25 12:57:54.121840','2026-10-25 12:57:54.121840',1200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,18,1,NULL,NULL,'ACTIVE',NULL),(6,'2026-10-25 13:14:01.310761','2026-10-25 13:14:01.310761',500,700,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,24,1,NULL,NULL,'ACTIVE',NULL),(7,'2026-10-25 13:14:15.493811','2026-10-25 13:14:15.493811',500,200,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,24,1,NULL,NULL,'ACTIVE',NULL),(8,'2026-10-25 13:14:28.108278','2026-10-25 13:14:28.108278',200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,24,1,NULL,NULL,'ACTIVE',NULL),(9,'2026-10-25 13:49:13.087956','2026-10-25 13:49:13.087956',1200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,303,1,NULL,NULL,'ACTIVE',NULL),(10,'2026-10-25 13:49:24.360706','2026-10-25 13:55:37.925757',1200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,303,1,'Duplicate payment','2026-10-25 13:55:37.921909','REVERSED',1),(11,'2026-10-25 13:50:04.156068','2026-10-25 13:55:32.071449',1200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,303,1,'Duplicate payment','2026-10-25 13:55:32.066692','REVERSED',1),(12,'2026-10-25 13:50:17.656251','2026-10-25 13:55:25.111557',1200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,303,1,'Duplicate payment','2026-10-25 13:55:25.087849','REVERSED',1),(13,'2026-10-25 13:56:09.165679','2026-10-25 13:56:09.165679',1800,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,311,1,NULL,NULL,'ACTIVE',NULL),(14,'2026-10-25 13:56:41.307192','2026-10-25 13:56:41.307192',1700,100,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,317,1,NULL,NULL,'ACTIVE',NULL),(15,'2026-10-25 13:57:26.371871','2026-10-25 13:57:26.371871',1600,200,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,324,1,NULL,NULL,'ACTIVE',NULL),(16,'2026-10-25 14:01:39.394375','2026-10-25 14:01:39.394375',100,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,317,1,NULL,NULL,'ACTIVE',NULL),(17,'2026-10-25 14:01:58.158328','2026-10-25 14:01:58.158328',3000,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,400,1,NULL,NULL,'ACTIVE',NULL),(18,'2026-10-25 14:08:06.345790','2026-10-25 14:08:06.345790',2400,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,335,1,NULL,NULL,'ACTIVE',NULL),(19,'2026-10-25 16:40:32.282658','2026-10-25 16:40:32.282658',200,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,324,1,NULL,NULL,'ACTIVE',NULL),(20,'2026-10-25 18:44:50.553575','2026-10-25 18:44:50.553575',3000,0,'CASH','2026-10-25 12:00:00.000000',NULL,1,NULL,1,404,1,NULL,NULL,'ACTIVE',NULL);
/*!40000 ALTER TABLE `student_fee_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_parent`
--

DROP TABLE IF EXISTS `student_parent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_parent` (
  `student_id` bigint NOT NULL,
  `parent_id` bigint NOT NULL,
  PRIMARY KEY (`student_id`,`parent_id`),
  KEY `FK1uqsk99lie7damnsh9osouodd` (`parent_id`),
  CONSTRAINT `FK1uqsk99lie7damnsh9osouodd` FOREIGN KEY (`parent_id`) REFERENCES `parent` (`id`),
  CONSTRAINT `FK3nulmrwg4cubngtp7nq5lud86` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_parent`
--

LOCK TABLES `student_parent` WRITE;
/*!40000 ALTER TABLE `student_parent` DISABLE KEYS */;
INSERT INTO `student_parent` VALUES (1,1),(2,1);
/*!40000 ALTER TABLE `student_parent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_term_remark`
--

DROP TABLE IF EXISTS `student_term_remark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_term_remark` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `class_teacher_remark` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_teacher_remark_at` datetime(6) DEFAULT NULL,
  `principal_remark` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `principal_remark_at` datetime(6) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `class_teacher_remark_by` bigint DEFAULT NULL,
  `principal_remark_by` bigint DEFAULT NULL,
  `student_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_term_remark` (`student_id`,`term_id`),
  KEY `FKjid3exiay23x3sng3j0kqu14j` (`created_by`),
  KEY `FK59tke5hvk8sk2isbt5n28j4ux` (`updated_by`),
  KEY `FKc9tqi11lj3ybdh6k0idx8hl18` (`school_id`),
  KEY `FK2ju5ysyvii0x9q382vwvsba8e` (`class_teacher_remark_by`),
  KEY `FKb1k5av4vlkdgut494tf96wa66` (`principal_remark_by`),
  KEY `FKiygkseuaq9bl0k24xs28jrgx7` (`term_id`),
  CONSTRAINT `FK2ju5ysyvii0x9q382vwvsba8e` FOREIGN KEY (`class_teacher_remark_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK59tke5hvk8sk2isbt5n28j4ux` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKb1k5av4vlkdgut494tf96wa66` FOREIGN KEY (`principal_remark_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKc9tqi11lj3ybdh6k0idx8hl18` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKiygkseuaq9bl0k24xs28jrgx7` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`),
  CONSTRAINT `FKjid3exiay23x3sng3j0kqu14j` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKtjq8973nuj32bn9g8ch2ytsqv` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_term_remark`
--

LOCK TABLES `student_term_remark` WRITE;
/*!40000 ALTER TABLE `student_term_remark` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_term_remark` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa7mu1bbojwhicjpkg5x89x3m` (`created_by`),
  KEY `FKqwvoue4iubb5cu96hj8cckhwo` (`updated_by`),
  KEY `FKg9ahxfklvfv5o2d8ejweibfo8` (`school_id`),
  CONSTRAINT `FKa7mu1bbojwhicjpkg5x89x3m` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKg9ahxfklvfv5o2d8ejweibfo8` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKqwvoue4iubb5cu96hj8cckhwo` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (1,'2026-10-24 21:44:49.808206','2026-10-24 21:44:49.808206','MT-001','Mathematics',1,1,1),(2,'2026-10-24 21:45:29.018377','2026-10-24 21:45:29.018377','ENG-001','English',1,1,1),(3,'2026-10-24 21:45:43.697227','2026-10-24 21:45:43.697227','SCI-001','Science',1,1,1),(4,'2026-10-25 06:50:47.107772','2026-10-25 06:50:47.107772','SES-001','Sesotho',1,1,1),(5,'2026-10-25 06:50:58.512996','2026-10-25 06:50:58.512996','HIS-001','History',1,1,1),(6,'2026-10-25 06:51:09.867026','2026-10-25 06:51:09.867026','ACC-001','Accounting',1,1,1),(7,'2026-10-25 06:51:20.580641','2026-10-25 06:51:20.580641','BIO-001','Biology',1,1,1),(8,'2026-10-25 06:51:35.087620','2026-10-25 06:51:35.087620','GEO-001','Geography',1,1,1),(9,'2026-10-25 06:51:51.347749','2026-10-25 06:51:51.347749','BUS-001','Business Studies',1,1,1);
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject_class_assignment`
--

DROP TABLE IF EXISTS `subject_class_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject_class_assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `academic_year_id` bigint NOT NULL,
  `school_class_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `teacher_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn4gi5ui7aq8cq09kafu3k21cy` (`created_by`),
  KEY `FKj5a6jvcu96jq6camcdb6o47x5` (`updated_by`),
  KEY `FKre0hr2erm2culyry6oqob2t56` (`school_id`),
  KEY `FK4p1s4s0vncn416n540erfrv1i` (`academic_year_id`),
  KEY `FKq9k3096tkaw5a38v8vab0miao` (`school_class_id`),
  KEY `FKmb1acuh1cg44im8mufiq7br3q` (`subject_id`),
  KEY `FK2faobhlpgsfn4jonppgto9hew` (`teacher_id`),
  CONSTRAINT `FK2faobhlpgsfn4jonppgto9hew` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK4p1s4s0vncn416n540erfrv1i` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year` (`id`),
  CONSTRAINT `FKj5a6jvcu96jq6camcdb6o47x5` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKmb1acuh1cg44im8mufiq7br3q` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`),
  CONSTRAINT `FKn4gi5ui7aq8cq09kafu3k21cy` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKq9k3096tkaw5a38v8vab0miao` FOREIGN KEY (`school_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKre0hr2erm2culyry6oqob2t56` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject_class_assignment`
--

LOCK TABLES `subject_class_assignment` WRITE;
/*!40000 ALTER TABLE `subject_class_assignment` DISABLE KEYS */;
INSERT INTO `subject_class_assignment` VALUES (1,'2026-10-25 07:14:53.644294','2026-10-25 07:14:53.644294',1,1,1,1,1,1,3),(2,'2026-10-25 07:15:11.274527','2026-10-25 07:15:11.274527',1,1,1,1,1,2,7),(3,'2026-10-25 07:15:41.896049','2026-10-25 07:15:41.896049',1,1,1,1,1,3,8),(4,'2026-10-25 07:20:34.440963','2026-10-25 07:20:34.440963',1,1,1,1,1,4,9),(5,'2026-10-25 07:20:50.891364','2026-10-25 07:20:50.891364',1,1,1,1,1,5,28),(6,'2026-10-25 07:21:53.213266','2026-10-25 07:21:53.213266',1,1,1,1,1,6,29),(7,'2026-10-25 07:22:08.308050','2026-10-25 07:22:08.308050',1,1,1,1,1,7,30),(8,'2026-10-25 07:22:27.012200','2026-10-25 07:22:27.012200',1,1,1,1,1,8,7),(9,'2026-10-25 07:22:53.209930','2026-10-25 07:22:53.209930',1,1,1,1,1,9,8),(10,'2026-10-25 07:25:44.448797','2026-10-25 07:25:44.448797',1,1,1,1,2,1,3),(11,'2026-10-25 07:25:50.304425','2026-10-25 07:25:50.304425',1,1,1,1,2,2,7),(12,'2026-10-25 07:25:56.893999','2026-10-25 07:25:56.893999',1,1,1,1,2,3,8),(13,'2026-10-25 07:26:04.378352','2026-10-25 07:26:04.378352',1,1,1,1,2,4,9),(14,'2026-10-25 07:26:10.690739','2026-10-25 07:26:10.690739',1,1,1,1,2,5,28),(15,'2026-10-25 07:26:18.341079','2026-10-25 07:26:18.341079',1,1,1,1,2,6,29),(16,'2026-10-25 07:26:26.115140','2026-10-25 07:26:26.115140',1,1,1,1,2,7,30),(17,'2026-10-25 07:26:35.656407','2026-10-25 07:26:35.656407',1,1,1,1,2,8,31),(18,'2026-10-25 07:26:44.386544','2026-10-25 07:26:44.386544',1,1,1,1,2,9,8),(19,'2026-10-25 07:28:17.206769','2026-10-25 07:28:17.206769',1,1,1,1,3,1,45),(20,'2026-10-25 07:28:24.533204','2026-10-25 07:28:24.533204',1,1,1,1,3,2,44),(21,'2026-10-25 07:28:33.297037','2026-10-25 07:28:33.297037',1,1,1,1,3,3,43),(22,'2026-10-25 07:28:41.172702','2026-10-25 07:28:41.172702',1,1,1,1,3,4,42),(23,'2026-10-25 07:28:48.318530','2026-10-25 07:28:48.318530',1,1,1,1,3,5,41),(24,'2026-10-25 07:28:55.571722','2026-10-25 07:28:55.571722',1,1,1,1,3,6,40),(25,'2026-10-25 07:29:02.165883','2026-10-25 07:29:02.165883',1,1,1,1,3,7,39),(26,'2026-10-25 07:29:10.941640','2026-10-25 07:29:10.941640',1,1,1,1,3,8,38),(27,'2026-10-25 07:29:20.922022','2026-10-25 07:29:20.922022',1,1,1,1,3,9,37),(28,'2026-10-25 07:29:41.302582','2026-10-25 07:29:41.302582',1,1,1,1,4,1,45),(29,'2026-10-25 07:29:47.178024','2026-10-25 07:29:47.178024',1,1,1,1,4,2,44),(30,'2026-10-25 07:29:54.600952','2026-10-25 07:29:54.600952',1,1,1,1,4,3,43),(31,'2026-10-25 07:30:01.462028','2026-10-25 07:30:01.462028',1,1,1,1,4,4,42),(32,'2026-10-25 07:30:08.819336','2026-10-25 07:30:08.819336',1,1,1,1,4,5,41),(33,'2026-10-25 07:30:15.352304','2026-10-25 07:30:15.352304',1,1,1,1,4,6,40),(34,'2026-10-25 07:30:24.769622','2026-10-25 07:30:24.769622',1,1,1,1,4,7,39),(35,'2026-10-25 07:30:32.302826','2026-10-25 07:30:32.302826',1,1,1,1,4,8,38),(36,'2026-10-25 07:30:39.765497','2026-10-25 07:30:39.765497',1,1,1,1,4,9,37),(37,'2026-10-25 07:30:51.704597','2026-10-25 07:30:51.704597',1,1,1,1,5,1,36),(38,'2026-10-25 07:30:59.395552','2026-10-25 07:30:59.395552',1,1,1,1,5,2,35),(39,'2026-10-25 07:31:07.303874','2026-10-25 07:31:07.303874',1,1,1,1,5,3,33),(40,'2026-10-25 07:31:15.770293','2026-10-25 07:31:15.770293',1,1,1,1,5,4,32),(41,'2026-10-25 07:31:22.967185','2026-10-25 07:31:22.967185',1,1,1,1,5,5,31),(42,'2026-10-25 07:31:31.327272','2026-10-25 07:31:37.081270',1,1,1,1,5,6,30),(43,'2026-10-25 07:31:46.576199','2026-10-25 07:31:46.576199',1,1,1,1,5,7,29),(44,'2026-10-25 07:31:56.761452','2026-10-25 07:31:56.761452',1,1,1,1,5,8,28),(45,'2026-10-25 07:32:06.571482','2026-10-25 07:32:06.571482',1,1,1,1,5,9,8),(46,'2026-10-25 07:32:18.187161','2026-10-25 07:32:18.187161',1,1,1,1,6,1,3),(47,'2026-10-25 07:32:23.361526','2026-10-25 07:32:23.361526',1,1,1,1,6,2,7),(48,'2026-10-25 07:32:28.346401','2026-10-25 07:32:28.346401',1,1,1,1,6,3,8),(49,'2026-10-25 07:32:33.987433','2026-10-25 07:32:33.987433',1,1,1,1,6,4,28),(50,'2026-10-25 07:32:42.514034','2026-10-25 07:32:42.514034',1,1,1,1,6,5,30),(51,'2026-10-25 07:32:50.247199','2026-10-25 07:32:50.247199',1,1,1,1,6,6,29),(52,'2026-10-25 07:32:58.781863','2026-10-25 07:32:58.781863',1,1,1,1,6,7,32),(53,'2026-10-25 07:33:05.433669','2026-10-25 07:33:05.433669',1,1,1,1,6,8,34),(54,'2026-10-25 07:33:12.243265','2026-10-25 07:33:12.243265',1,1,1,1,6,9,38);
/*!40000 ALTER TABLE `subject_class_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription`
--

DROP TABLE IF EXISTS `subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `ends_at` datetime(6) DEFAULT NULL,
  `plan` enum('ANNUAL','FREE_TRIAL','MONTHLY','ONE_OFF') COLLATE utf8mb4_unicode_ci NOT NULL,
  `started_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','CANCELLED','EXPIRED','SUSPENDED','TRIAL') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp7f8n66wq23o0v5qpoddhgnxr` (`created_by`),
  KEY `FK95kwb68oybos8b327j6bffqc4` (`updated_by`),
  KEY `FK7mqc5ux1bi7n87axcfm8xd89v` (`school_id`),
  CONSTRAINT `FK7mqc5ux1bi7n87axcfm8xd89v` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK95kwb68oybos8b327j6bffqc4` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKp7f8n66wq23o0v5qpoddhgnxr` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription`
--

LOCK TABLES `subscription` WRITE;
/*!40000 ALTER TABLE `subscription` DISABLE KEYS */;
INSERT INTO `subscription` VALUES (1,'2026-10-24 21:29:53.711070','2026-10-24 21:29:53.711070','2026-11-24 21:29:53.710006','FREE_TRIAL','2026-10-24 21:29:53.710006','TRIAL',NULL,NULL,1);
/*!40000 ALTER TABLE `subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription_request`
--

DROP TABLE IF EXISTS `subscription_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `currency` enum('LSL','USD','ZAR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `note` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plan` enum('ANNUAL','FREE_TRIAL','MONTHLY','ONE_OFF') COLLATE utf8mb4_unicode_ci NOT NULL,
  `review_note` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('APPROVED','CANCELLED','PENDING','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `payment_method_id` bigint DEFAULT NULL,
  `proof_file_id` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkolmgsacpua7fv09qg1gyba4f` (`created_by`),
  KEY `FK768tt2gmhyt6hnu9k4islqbdx` (`updated_by`),
  KEY `FKs5lgfukb4v2wuc7ncewetqps8` (`payment_method_id`),
  KEY `FK2028w6882mm3lgt9c0a9puajv` (`proof_file_id`),
  KEY `FK1xp9xqqxqykocw7j3ammysms9` (`school_id`),
  CONSTRAINT `FK1xp9xqqxqykocw7j3ammysms9` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK2028w6882mm3lgt9c0a9puajv` FOREIGN KEY (`proof_file_id`) REFERENCES `data_file` (`id`),
  CONSTRAINT `FK768tt2gmhyt6hnu9k4islqbdx` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKkolmgsacpua7fv09qg1gyba4f` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKs5lgfukb4v2wuc7ncewetqps8` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription_request`
--

LOCK TABLES `subscription_request` WRITE;
/*!40000 ALTER TABLE `subscription_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscription_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `date_joined` date DEFAULT NULL,
  `employee_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `employment_type` enum('CONTRACT','FULL_TIME','PART_TIME') COLLATE utf8mb4_unicode_ci NOT NULL,
  `qualifications` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `specialization` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_school_employee_number` (`school_id`,`employee_number`),
  UNIQUE KEY `UKi5wqs2ds2vpmfpbcdxi9m2jvr` (`user_id`),
  KEY `FKfamcx7cexhg2kgtokqxsqx73i` (`created_by`),
  KEY `FK62yd1ka2u09n35gbd0q2mr8tp` (`updated_by`),
  CONSTRAINT `FK62yd1ka2u09n35gbd0q2mr8tp` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKfamcx7cexhg2kgtokqxsqx73i` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKpb6g6pahj1mr2ijg92r7m1xlh` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrg46bnmgbcccayv14naymqg3r` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
INSERT INTO `teacher` VALUES (1,'2026-10-24 21:49:24.546297','2026-10-24 21:49:24.546297','2026-01-20','T-0001','FULL_TIME','BSC in Science','Science',1,1,1,3),(2,'2026-10-24 22:07:25.712552','2026-10-24 22:07:25.712552','2026-01-25','T-0002','CONTRACT','BSC in English','English',1,1,1,7),(3,'2026-10-25 08:59:59.000000','2026-10-25 08:59:59.000000','2026-01-01','T-0003','FULL_TIME','B.Ed Mathematics','Mathematics',1,1,1,8),(4,'2026-10-25 08:59:59.000000','2026-10-25 08:59:59.000000','2026-01-01','T-0004','FULL_TIME','B.Ed Biology','Biology',1,1,1,9),(5,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0005','FULL_TIME','Bachelor of Education','Science',1,1,1,28),(6,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0006','FULL_TIME','Bachelor of Education','Sesotho',1,1,1,29),(7,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0007','FULL_TIME','Bachelor of Education','History',1,1,1,30),(8,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0008','FULL_TIME','Bachelor of Education','Accounting',1,1,1,31),(9,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0009','FULL_TIME','Bachelor of Education','Biology',1,1,1,32),(10,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0010','FULL_TIME','Bachelor of Education','Geography',1,1,1,33),(11,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0011','FULL_TIME','Bachelor of Education','Business Studies',1,1,1,34),(12,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0012','FULL_TIME','Bachelor of Education','Mathematics',1,1,1,35),(13,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0013','FULL_TIME','Bachelor of Education','English',1,1,1,36),(14,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0014','FULL_TIME','Bachelor of Education','Science',1,1,1,37),(15,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0015','FULL_TIME','Bachelor of Education','Sesotho',1,1,1,38),(16,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0016','FULL_TIME','Bachelor of Education','History',1,1,1,39),(17,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0017','FULL_TIME','Bachelor of Education','Accounting',1,1,1,40),(18,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0018','FULL_TIME','Bachelor of Education','Biology',1,1,1,41),(19,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0019','FULL_TIME','Bachelor of Education','Geography',1,1,1,42),(20,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0020','FULL_TIME','Bachelor of Education','Business Studies',1,1,1,43),(21,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0021','FULL_TIME','Bachelor of Education','Mathematics',1,1,1,44),(22,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000','2026-01-01','T-0022','FULL_TIME','Bachelor of Education','English',1,1,1,45);
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `term`
--

DROP TABLE IF EXISTS `term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `end_date` datetime(6) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `academic_year_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4h7solj2taksiw38teuc3nggt` (`created_by`),
  KEY `FKnu6lukxl4kcic2gqn7ootqj1` (`updated_by`),
  KEY `FKdbdi7valdcd5jvlktwgnll2wb` (`school_id`),
  KEY `FKsqywphsvdbw4fff2inhkki78y` (`academic_year_id`),
  CONSTRAINT `FK4h7solj2taksiw38teuc3nggt` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKdbdi7valdcd5jvlktwgnll2wb` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKnu6lukxl4kcic2gqn7ootqj1` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKsqywphsvdbw4fff2inhkki78y` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term`
--

LOCK TABLES `term` WRITE;
/*!40000 ALTER TABLE `term` DISABLE KEYS */;
INSERT INTO `term` VALUES (1,'2026-10-24 21:39:52.621170','2026-10-24 21:39:52.621170','2026-03-31 00:00:00.000000','Term 1','2026-01-20 00:00:00.000000',1,1,1,1),(2,'2026-10-24 21:40:49.759757','2026-10-24 21:40:49.759757','2026-06-20 00:00:00.000000','Term 2','2026-04-01 00:00:00.000000',1,1,1,1),(3,'2026-10-24 21:41:14.287527','2026-10-24 21:41:14.287527','2026-09-30 00:00:00.000000','Term 3','2026-08-01 00:00:00.000000',1,1,1,1),(4,'2026-10-24 21:41:40.345242','2026-10-24 21:41:40.345242','2026-11-20 00:00:00.000000','Term 4','2026-10-01 00:00:00.000000',1,1,1,1);
/*!40000 ALTER TABLE `term` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `term_result`
--

DROP TABLE IF EXISTS `term_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term_result` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `calculated_at` datetime(6) NOT NULL,
  `final_mark` double NOT NULL,
  `finalised_at` datetime(6) DEFAULT NULL,
  `grade` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('CALCULATED','FINALISED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `finalised_by` bigint DEFAULT NULL,
  `school_class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_term_result` (`student_id`,`subject_id`,`term_id`),
  KEY `FKtkdlpq7i4yqr38wc56tmnjawe` (`created_by`),
  KEY `FK4otjg03umag9k5mdcw8rmcxrn` (`updated_by`),
  KEY `FKi4cs9vu1o4iek7loeyg1rmjip` (`school_id`),
  KEY `FK9n4vugrecnh89eltot493p3au` (`finalised_by`),
  KEY `FKq2od5k8512hxigdbs0nqx536q` (`school_class_id`),
  KEY `FK2ulevj067cijgitwj39vc56dw` (`subject_id`),
  KEY `FKdkhudcpm0wptck5s70psobvu7` (`term_id`),
  CONSTRAINT `FK2ulevj067cijgitwj39vc56dw` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`),
  CONSTRAINT `FK4otjg03umag9k5mdcw8rmcxrn` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK8jo5e6r65qc9lldemrgw7t2l` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FK9n4vugrecnh89eltot493p3au` FOREIGN KEY (`finalised_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKdkhudcpm0wptck5s70psobvu7` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`),
  CONSTRAINT `FKi4cs9vu1o4iek7loeyg1rmjip` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKq2od5k8512hxigdbs0nqx536q` FOREIGN KEY (`school_class_id`) REFERENCES `school_class` (`id`),
  CONSTRAINT `FKtkdlpq7i4yqr38wc56tmnjawe` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term_result`
--

LOCK TABLES `term_result` WRITE;
/*!40000 ALTER TABLE `term_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `term_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timetable`
--

DROP TABLE IF EXISTS `timetable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timetable` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `day_of_week` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `subject_class_assignment_id` bigint NOT NULL,
  `period_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK44cvq1o7jfemhvmd26xu0fitn` (`created_by`),
  KEY `FKlw8w9rimt4mgyjxv9097g0mvm` (`updated_by`),
  KEY `FK79l0hf3868bfy1pxvprok8vfm` (`school_id`),
  KEY `FK9h64qlfmobwu1kecogsrece4x` (`subject_class_assignment_id`),
  KEY `FKb4c4j9wko821amitn3a8gv7jh` (`period_id`),
  CONSTRAINT `FK44cvq1o7jfemhvmd26xu0fitn` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK79l0hf3868bfy1pxvprok8vfm` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FK9h64qlfmobwu1kecogsrece4x` FOREIGN KEY (`subject_class_assignment_id`) REFERENCES `subject_class_assignment` (`id`),
  CONSTRAINT `FKb4c4j9wko821amitn3a8gv7jh` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
  CONSTRAINT `FKlw8w9rimt4mgyjxv9097g0mvm` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=345 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timetable`
--

LOCK TABLES `timetable` WRITE;
/*!40000 ALTER TABLE `timetable` DISABLE KEYS */;
INSERT INTO `timetable` VALUES (1,'2026-10-25 07:34:15.833226','2026-10-25 07:34:15.833226','SUNDAY',1,1,1,1,1),(2,'2026-10-25 07:34:19.650945','2026-10-25 07:34:19.650945','SUNDAY',1,1,1,1,2),(3,'2026-10-25 07:34:23.997042','2026-10-25 07:34:23.997042','SUNDAY',1,1,1,2,3),(4,'2026-10-25 07:35:21.473870','2026-10-25 07:35:21.473870','SUNDAY',1,1,1,4,4),(5,'2026-10-25 07:36:20.291568','2026-10-25 07:36:20.291568','SUNDAY',1,1,1,3,6),(6,'2026-10-25 07:36:33.667092','2026-10-25 07:36:33.667092','SUNDAY',1,1,1,5,7),(7,'2026-10-25 07:36:39.118721','2026-10-25 07:36:39.118721','SUNDAY',1,1,1,6,8),(8,'2026-10-25 07:36:45.479431','2026-10-25 07:36:45.479431','SUNDAY',1,1,1,7,10),(9,'2026-10-25 07:36:52.104742','2026-10-25 07:36:52.104742','SUNDAY',1,1,1,7,11),(10,'2026-10-25 07:37:02.749722','2026-10-25 07:37:02.749722','SUNDAY',1,1,1,8,12),(11,'2026-10-25 07:39:12.739839','2026-10-25 07:39:12.739839','SUNDAY',1,1,1,11,1),(12,'2026-10-25 07:58:46.657360','2026-10-25 07:58:46.657360','SUNDAY',1,1,1,11,2),(13,'2026-10-25 07:58:54.093684','2026-10-25 07:58:54.093684','SUNDAY',1,1,1,10,3),(14,'2026-10-25 07:58:58.625628','2026-10-25 07:58:58.625628','SUNDAY',1,1,1,10,4),(15,'2026-10-25 08:00:22.242213','2026-10-25 08:00:22.242213','SUNDAY',1,1,1,13,6),(16,'2026-10-25 08:00:26.881295','2026-10-25 08:00:26.881295','SUNDAY',1,1,1,12,7),(17,'2026-10-25 08:00:33.265009','2026-10-25 08:00:33.265009','SUNDAY',1,1,1,12,8),(18,'2026-10-25 08:00:42.334116','2026-10-25 08:00:42.334116','SUNDAY',1,1,1,14,10),(19,'2026-10-25 08:00:49.352679','2026-10-25 08:00:49.352679','SUNDAY',1,1,1,15,11),(20,'2026-10-25 08:00:55.027801','2026-10-25 08:00:55.027801','SUNDAY',1,1,1,15,12),(21,'2026-10-25 08:01:02.099888','2026-10-25 08:01:02.099888','SUNDAY',1,1,1,19,1),(22,'2026-10-25 08:01:05.177140','2026-10-25 08:01:05.177140','SUNDAY',1,1,1,19,2),(23,'2026-10-25 08:01:10.739932','2026-10-25 08:01:10.739932','SUNDAY',1,1,1,20,3),(24,'2026-10-25 08:01:15.882203','2026-10-25 08:01:15.882203','SUNDAY',1,1,1,20,4),(25,'2026-10-25 08:01:19.911699','2026-10-25 08:01:19.911699','SUNDAY',1,1,1,29,1),(26,'2026-10-25 08:01:23.076686','2026-10-25 08:01:23.076686','SUNDAY',1,1,1,29,2),(27,'2026-10-25 08:01:28.014764','2026-10-25 08:01:28.014764','SUNDAY',1,1,1,28,3),(28,'2026-10-25 08:01:32.569361','2026-10-25 08:01:32.569361','SUNDAY',1,1,1,28,4),(29,'2026-10-25 08:01:36.859916','2026-10-25 08:01:36.859916','SUNDAY',1,1,1,37,1),(30,'2026-10-25 08:01:41.228581','2026-10-25 08:01:41.228581','SUNDAY',1,1,1,37,2),(31,'2026-10-25 08:01:45.673134','2026-10-25 08:01:45.673134','SUNDAY',1,1,1,38,3),(32,'2026-10-25 08:01:49.542393','2026-10-25 08:01:49.542393','SUNDAY',1,1,1,38,4),(33,'2026-10-25 08:01:52.715470','2026-10-25 08:01:52.715470','SUNDAY',1,1,1,48,1),(34,'2026-10-25 08:01:55.641325','2026-10-25 08:01:55.641325','SUNDAY',1,1,1,48,2),(35,'2026-10-25 08:01:59.439150','2026-10-25 08:01:59.439150','SUNDAY',1,1,1,49,3),(36,'2026-10-25 08:02:09.620419','2026-10-25 08:02:09.620419','SUNDAY',1,1,1,49,4),(37,'2026-10-25 08:02:15.373229','2026-10-25 08:02:15.373229','SUNDAY',1,1,1,46,6),(38,'2026-10-25 08:02:19.327465','2026-10-25 08:02:19.327465','SUNDAY',1,1,1,46,7),(39,'2026-10-25 08:02:23.630256','2026-10-25 08:02:23.630256','SUNDAY',1,1,1,47,8),(40,'2026-10-25 08:02:29.512529','2026-10-25 08:02:29.512529','SUNDAY',1,1,1,39,6),(41,'2026-10-25 08:02:34.816698','2026-10-25 08:02:34.816698','SUNDAY',1,1,1,39,7),(42,'2026-10-25 08:02:39.623976','2026-10-25 08:02:39.623976','SUNDAY',1,1,1,40,8),(43,'2026-10-25 08:02:45.278871','2026-10-25 08:02:45.278871','SUNDAY',1,1,1,30,6),(44,'2026-10-25 08:02:49.691022','2026-10-25 08:02:49.691022','SUNDAY',1,1,1,22,6),(45,'2026-10-25 08:02:54.223960','2026-10-25 08:02:54.223960','SUNDAY',1,1,1,21,7),(46,'2026-10-25 08:02:57.946362','2026-10-25 08:02:57.946362','SUNDAY',1,1,1,32,7),(47,'2026-10-25 08:03:02.662811','2026-10-25 08:03:02.662811','SUNDAY',1,1,1,30,8),(48,'2026-10-25 08:03:07.223699','2026-10-25 08:03:07.223699','SUNDAY',1,1,1,22,8),(49,'2026-10-25 08:03:12.676014','2026-10-25 08:03:12.676014','SUNDAY',1,1,1,21,10),(50,'2026-10-25 08:03:16.915711','2026-10-25 08:03:16.915711','SUNDAY',1,1,1,23,11),(51,'2026-10-25 08:03:21.093141','2026-10-25 08:03:21.093141','SUNDAY',1,1,1,27,12),(52,'2026-10-25 08:03:25.021253','2026-10-25 08:03:25.021253','SUNDAY',1,1,1,31,10),(53,'2026-10-25 08:03:29.804321','2026-10-25 08:03:29.804321','SUNDAY',1,1,1,31,11),(54,'2026-10-25 08:03:33.504026','2026-10-25 08:03:33.504026','SUNDAY',1,1,1,32,12),(55,'2026-10-25 08:03:37.189469','2026-10-25 08:03:37.189469','SUNDAY',1,1,1,40,10),(56,'2026-10-25 08:03:41.838381','2026-10-25 08:03:41.838381','SUNDAY',1,1,1,41,11),(57,'2026-10-25 08:03:45.921602','2026-10-25 08:03:45.921602','SUNDAY',1,1,1,44,12),(58,'2026-10-25 08:03:50.071584','2026-10-25 08:03:50.071584','SUNDAY',1,1,1,51,10),(59,'2026-10-25 08:03:53.948372','2026-10-25 08:03:53.948372','SUNDAY',1,1,1,52,11),(60,'2026-10-25 08:03:58.888160','2026-10-25 08:03:58.888160','SUNDAY',1,1,1,53,12),(61,'2026-10-25 14:52:36.718726','2026-10-25 14:52:36.718726','MONDAY',1,1,1,1,1),(62,'2026-10-25 14:52:40.541771','2026-10-25 14:52:40.541771','MONDAY',1,1,1,2,2),(63,'2026-10-25 14:52:45.080429','2026-10-25 14:52:45.080429','MONDAY',1,1,1,3,3),(64,'2026-10-25 14:52:49.853617','2026-10-25 14:52:49.853617','MONDAY',1,1,1,4,4),(65,'2026-10-25 14:52:56.191057','2026-10-25 14:52:56.191057','MONDAY',1,1,1,5,6),(66,'2026-10-25 14:53:01.918578','2026-10-25 14:53:01.918578','MONDAY',1,1,1,5,7),(67,'2026-10-25 14:53:08.537400','2026-10-25 14:53:08.537400','MONDAY',1,1,1,6,8),(68,'2026-10-25 14:53:14.095795','2026-10-25 14:53:14.095795','MONDAY',1,1,1,7,10),(69,'2026-10-25 14:53:20.836469','2026-10-25 14:53:20.836469','MONDAY',1,1,1,8,11),(70,'2026-10-25 14:53:25.883867','2026-10-25 14:53:25.883867','MONDAY',1,1,1,8,12),(71,'2026-10-25 14:54:05.596513','2026-10-25 14:54:05.596513','MONDAY',1,1,1,11,1),(72,'2026-10-25 14:54:13.367615','2026-10-25 14:54:13.367615','MONDAY',1,1,1,10,2),(73,'2026-10-25 14:54:17.504566','2026-10-25 14:54:17.504566','MONDAY',1,1,1,10,3),(74,'2026-10-25 14:54:25.713351','2026-10-25 14:54:25.713351','MONDAY',1,1,1,12,4),(75,'2026-10-25 14:54:33.588922','2026-10-25 14:54:33.588922','MONDAY',1,1,1,19,1),(76,'2026-10-25 14:54:38.684284','2026-10-25 14:54:38.684284','MONDAY',1,1,1,19,2),(77,'2026-10-25 14:54:43.025427','2026-10-25 14:54:43.025427','MONDAY',1,1,1,26,3),(78,'2026-10-25 14:54:48.659491','2026-10-25 14:54:48.659491','MONDAY',1,1,1,27,4),(79,'2026-10-25 14:54:52.690087','2026-10-25 14:54:52.690087','MONDAY',1,1,1,29,1),(80,'2026-10-25 14:54:56.931467','2026-10-25 14:54:56.931467','MONDAY',1,1,1,29,2),(81,'2026-10-25 14:55:08.018976','2026-10-25 14:55:08.018976','MONDAY',1,1,1,28,3),(82,'2026-10-25 14:55:12.272015','2026-10-25 14:55:12.272015','MONDAY',1,1,1,28,4),(83,'2026-10-25 14:55:17.996297','2026-10-25 14:55:17.996297','MONDAY',1,1,1,37,1),(84,'2026-10-25 14:55:22.633115','2026-10-25 14:55:22.633115','MONDAY',1,1,1,37,2),(85,'2026-10-25 14:55:26.994218','2026-10-25 14:55:26.994218','MONDAY',1,1,1,38,3),(86,'2026-10-25 14:55:31.561155','2026-10-25 14:55:31.561155','MONDAY',1,1,1,39,4),(87,'2026-10-25 14:55:35.295517','2026-10-25 14:55:35.295517','MONDAY',1,1,1,48,1),(88,'2026-10-25 14:55:38.987028','2026-10-25 14:55:38.987028','MONDAY',1,1,1,48,2),(89,'2026-10-25 14:55:43.610922','2026-10-25 14:55:43.610922','MONDAY',1,1,1,47,3),(90,'2026-10-25 14:55:47.702234','2026-10-25 14:55:47.702234','MONDAY',1,1,1,46,4),(91,'2026-10-25 14:58:37.565151','2026-10-25 14:58:37.565151','MONDAY',1,1,1,13,6),(92,'2026-10-25 14:58:43.178566','2026-10-25 14:58:43.178566','MONDAY',1,1,1,13,7),(93,'2026-10-25 14:59:13.054649','2026-10-25 14:59:13.054649','MONDAY',1,1,1,18,8),(94,'2026-10-25 14:59:19.578860','2026-10-25 14:59:19.578860','MONDAY',1,1,1,17,10),(95,'2026-10-25 14:59:26.797812','2026-10-25 14:59:26.797812','MONDAY',1,1,1,17,11),(96,'2026-10-25 14:59:32.335017','2026-10-25 14:59:32.335017','MONDAY',1,1,1,16,12),(97,'2026-10-25 15:00:43.438814','2026-10-25 15:00:43.438814','MONDAY',1,1,1,20,6),(98,'2026-10-25 15:01:01.109395','2026-10-25 15:01:01.109395','MONDAY',1,1,1,20,7),(99,'2026-10-25 15:01:06.913213','2026-10-25 15:01:06.913213','MONDAY',1,1,1,22,8),(100,'2026-10-25 15:01:17.355842','2026-10-25 15:01:17.355842','MONDAY',1,1,1,25,10),(101,'2026-10-25 15:01:24.891689','2026-10-25 15:01:24.891689','MONDAY',1,1,1,25,11),(102,'2026-10-25 15:01:31.546545','2026-10-25 15:01:31.546545','MONDAY',1,1,1,24,12),(103,'2026-10-25 15:01:37.397852','2026-10-25 15:01:37.397852','MONDAY',1,1,1,32,6),(104,'2026-10-25 15:01:42.114864','2026-10-25 15:01:42.114864','MONDAY',1,1,1,32,7),(105,'2026-10-25 15:01:47.200547','2026-10-25 15:01:47.200547','MONDAY',1,1,1,33,8),(106,'2026-10-25 15:01:54.338876','2026-10-25 15:01:54.338876','MONDAY',1,1,1,36,10),(107,'2026-10-25 15:02:40.637109','2026-10-25 15:02:40.637109','MONDAY',1,1,1,36,11),(108,'2026-10-25 15:02:52.470743','2026-10-25 15:02:52.470743','MONDAY',1,1,1,31,12),(109,'2026-10-25 15:04:43.499950','2026-10-25 15:04:43.499950','MONDAY',1,1,1,40,6),(110,'2026-10-25 15:04:49.889108','2026-10-25 15:04:49.889108','MONDAY',1,1,1,40,7),(111,'2026-10-25 15:04:57.589308','2026-10-25 15:04:57.589308','MONDAY',1,1,1,41,8),(112,'2026-10-25 15:05:07.719766','2026-10-25 15:05:07.719766','MONDAY',1,1,1,44,10),(113,'2026-10-25 15:05:12.748179','2026-10-25 15:05:12.748179','MONDAY',1,1,1,44,11),(114,'2026-10-25 15:05:19.535717','2026-10-25 15:05:19.535717','MONDAY',1,1,1,43,12),(115,'2026-10-25 15:05:26.938324','2026-10-25 15:05:26.938324','MONDAY',1,1,1,54,12),(116,'2026-10-25 15:05:35.050201','2026-10-25 15:05:35.050201','MONDAY',1,1,1,54,11),(117,'2026-10-25 15:05:40.567978','2026-10-25 15:05:40.567978','MONDAY',1,1,1,52,10),(118,'2026-10-25 15:05:57.158418','2026-10-25 15:05:57.158418','MONDAY',1,1,1,49,8),(119,'2026-10-25 15:06:10.527177','2026-10-25 15:06:10.527177','MONDAY',1,1,1,50,7),(120,'2026-10-25 15:06:17.136677','2026-10-25 15:06:17.136677','MONDAY',1,1,1,50,6),(121,'2026-10-25 15:07:41.834601','2026-10-25 15:07:41.834601','TUESDAY',1,1,1,1,1),(122,'2026-10-25 15:07:46.550940','2026-10-25 15:07:46.550940','TUESDAY',1,1,1,11,1),(123,'2026-10-25 15:07:51.257253','2026-10-25 15:07:51.257253','TUESDAY',1,1,1,19,1),(124,'2026-10-25 15:07:55.843711','2026-10-25 15:07:55.843711','TUESDAY',1,1,1,29,1),(125,'2026-10-25 15:07:59.841743','2026-10-25 15:07:59.841743','TUESDAY',1,1,1,37,1),(126,'2026-10-25 15:08:05.289031','2026-10-25 15:08:05.289031','TUESDAY',1,1,1,48,1),(127,'2026-10-25 15:08:15.069806','2026-10-25 15:11:25.108001','TUESDAY',1,1,1,48,2),(128,'2026-10-25 15:08:19.256475','2026-10-25 15:08:19.256475','TUESDAY',1,1,1,46,3),(129,'2026-10-25 15:09:48.453656','2026-10-25 15:09:48.453656','TUESDAY',1,1,1,37,2),(130,'2026-10-25 15:09:54.223102','2026-10-25 15:09:54.223102','TUESDAY',1,1,1,38,3),(131,'2026-10-25 15:09:58.498333','2026-10-25 15:09:58.498333','TUESDAY',1,1,1,38,4),(132,'2026-10-25 15:10:03.749713','2026-10-25 15:10:57.682517','TUESDAY',1,1,1,29,2),(133,'2026-10-25 15:10:07.810705','2026-10-25 15:10:07.810705','TUESDAY',1,1,1,28,3),(135,'2026-10-25 15:10:26.631820','2026-10-25 15:11:07.994119','TUESDAY',1,1,1,28,4),(136,'2026-10-25 15:11:29.505947','2026-10-25 15:11:29.505947','TUESDAY',1,1,1,46,4),(137,'2026-10-25 15:11:37.389468','2026-10-25 15:11:37.389468','TUESDAY',1,1,1,19,2),(138,'2026-10-25 15:11:41.600628','2026-10-25 15:11:41.600628','TUESDAY',1,1,1,20,3),(139,'2026-10-25 15:11:46.097443','2026-10-25 15:11:46.097443','TUESDAY',1,1,1,20,4),(140,'2026-10-25 15:12:19.368706','2026-10-25 15:12:19.368706','TUESDAY',1,1,1,11,2),(141,'2026-10-25 15:12:26.356087','2026-10-25 15:12:26.356087','TUESDAY',1,1,1,12,3),(142,'2026-10-25 15:12:30.810125','2026-10-25 15:12:30.810125','TUESDAY',1,1,1,12,4),(143,'2026-10-25 15:12:43.381480','2026-10-25 15:12:43.381480','TUESDAY',1,1,1,1,2),(144,'2026-10-25 15:12:48.399983','2026-10-25 15:12:48.399983','TUESDAY',1,1,1,2,3),(145,'2026-10-25 15:12:55.128455','2026-10-25 15:12:55.128455','TUESDAY',1,1,1,2,4),(146,'2026-10-25 15:13:05.360415','2026-10-25 15:13:05.360415','TUESDAY',1,1,1,3,6),(147,'2026-10-25 15:13:10.056441','2026-10-25 15:13:10.056441','TUESDAY',1,1,1,3,7),(148,'2026-10-25 15:13:15.661842','2026-10-25 15:13:15.661842','TUESDAY',1,1,1,4,8),(149,'2026-10-25 15:13:21.164037','2026-10-25 15:13:21.164037','TUESDAY',1,1,1,13,6),(150,'2026-10-25 15:13:29.300841','2026-10-25 15:13:29.300841','TUESDAY',1,1,1,13,7),(151,'2026-10-25 15:13:34.627103','2026-10-25 15:13:34.627103','TUESDAY',1,1,1,10,8),(152,'2026-10-25 15:13:40.764068','2026-10-25 15:13:40.764068','TUESDAY',1,1,1,21,6),(153,'2026-10-25 15:13:45.887996','2026-10-25 15:13:45.887996','TUESDAY',1,1,1,21,7),(154,'2026-10-25 15:13:52.813251','2026-10-25 15:13:52.813251','TUESDAY',1,1,1,22,8),(155,'2026-10-25 15:13:59.051804','2026-10-25 15:13:59.051804','TUESDAY',1,1,1,31,6),(156,'2026-10-25 15:14:04.957850','2026-10-25 15:14:04.957850','TUESDAY',1,1,1,31,7),(157,'2026-10-25 15:14:09.744722','2026-10-25 15:14:09.744722','TUESDAY',1,1,1,30,8),(158,'2026-10-25 15:14:17.099049','2026-10-25 15:14:17.099049','TUESDAY',1,1,1,39,6),(159,'2026-10-25 15:14:21.842082','2026-10-25 15:14:21.842082','TUESDAY',1,1,1,39,7),(160,'2026-10-25 15:14:28.058827','2026-10-25 15:14:28.058827','TUESDAY',1,1,1,40,8),(161,'2026-10-25 15:14:33.399963','2026-10-25 15:14:33.399963','TUESDAY',1,1,1,47,6),(162,'2026-10-25 15:14:39.166834','2026-10-25 15:14:39.166834','TUESDAY',1,1,1,47,7),(163,'2026-10-25 15:14:45.150607','2026-10-25 15:14:45.150607','TUESDAY',1,1,1,49,8),(164,'2026-10-25 15:14:56.008552','2026-10-25 15:14:56.008552','TUESDAY',1,1,1,54,10),(165,'2026-10-25 15:15:00.937904','2026-10-25 15:15:00.937904','TUESDAY',1,1,1,54,11),(166,'2026-10-25 15:15:06.180842','2026-10-25 15:15:06.180842','TUESDAY',1,1,1,53,12),(167,'2026-10-25 15:15:13.719069','2026-10-25 15:15:13.719069','TUESDAY',1,1,1,44,10),(168,'2026-10-25 15:15:21.126749','2026-10-25 15:15:21.126749','TUESDAY',1,1,1,44,11),(169,'2026-10-25 15:15:26.551630','2026-10-25 15:15:26.551630','TUESDAY',1,1,1,42,12),(170,'2026-10-25 15:15:32.548952','2026-10-25 15:15:32.548952','TUESDAY',1,1,1,33,10),(171,'2026-10-25 15:15:38.139953','2026-10-25 15:15:38.139953','TUESDAY',1,1,1,33,11),(172,'2026-10-25 15:15:45.130394','2026-10-25 15:15:45.130394','TUESDAY',1,1,1,34,12),(173,'2026-10-25 15:15:51.000457','2026-10-25 15:15:51.000457','TUESDAY',1,1,1,25,10),(174,'2026-10-25 15:15:56.747098','2026-10-25 15:15:56.747098','TUESDAY',1,1,1,25,11),(175,'2026-10-25 15:16:04.669676','2026-10-25 15:16:04.669676','TUESDAY',1,1,1,23,12),(176,'2026-10-25 15:16:34.197772','2026-10-25 15:16:34.197772','TUESDAY',1,1,1,17,10),(177,'2026-10-25 15:16:43.949636','2026-10-25 15:16:43.949636','TUESDAY',1,1,1,16,11),(178,'2026-10-25 15:16:51.774688','2026-10-25 15:16:51.774688','TUESDAY',1,1,1,18,12),(179,'2026-10-25 15:17:00.416840','2026-10-25 15:17:00.416840','TUESDAY',1,1,1,9,10),(180,'2026-10-25 15:17:05.971136','2026-10-25 15:17:05.971136','TUESDAY',1,1,1,4,11),(181,'2026-10-25 15:17:12.445701','2026-10-25 15:17:12.445701','TUESDAY',1,1,1,5,12),(182,'2026-10-25 15:17:22.803029','2026-10-25 15:17:22.803029','WEDNESDAY',1,1,1,9,1),(183,'2026-10-25 15:17:27.206297','2026-10-25 15:17:27.206297','WEDNESDAY',1,1,1,17,1),(184,'2026-10-25 15:17:32.550271','2026-10-25 15:17:32.550271','WEDNESDAY',1,1,1,27,1),(185,'2026-10-25 15:17:37.655382','2026-10-25 15:17:37.655382','WEDNESDAY',1,1,1,35,1),(186,'2026-10-25 15:17:42.701082','2026-10-25 15:17:42.701082','WEDNESDAY',1,1,1,44,1),(187,'2026-10-25 15:17:47.412204','2026-10-25 15:17:47.412204','WEDNESDAY',1,1,1,53,1),(188,'2026-10-25 15:17:52.448109','2026-10-25 15:17:52.448109','WEDNESDAY',1,1,1,53,2),(189,'2026-10-25 15:17:58.547908','2026-10-25 15:17:58.547908','WEDNESDAY',1,1,1,44,2),(190,'2026-10-25 15:18:10.415652','2026-10-25 15:18:10.415652','WEDNESDAY',1,1,1,35,2),(191,'2026-10-25 15:18:18.771739','2026-10-25 15:18:18.771739','WEDNESDAY',1,1,1,27,2),(192,'2026-10-25 15:18:26.528507','2026-10-25 15:18:26.528507','WEDNESDAY',1,1,1,17,2),(193,'2026-10-25 15:18:41.520106','2026-10-25 15:18:41.520106','WEDNESDAY',1,1,1,9,2),(194,'2026-10-25 15:18:47.037160','2026-10-25 15:18:47.037160','WEDNESDAY',1,1,1,8,3),(195,'2026-10-25 15:18:51.733747','2026-10-25 15:18:51.733747','WEDNESDAY',1,1,1,8,4),(196,'2026-10-25 15:18:57.589942','2026-10-25 15:18:57.589942','WEDNESDAY',1,1,1,18,3),(197,'2026-10-25 15:19:02.403575','2026-10-25 15:19:02.403575','WEDNESDAY',1,1,1,18,4),(198,'2026-10-25 15:21:10.440391','2026-10-25 15:21:10.440391','WEDNESDAY',1,1,1,26,3),(199,'2026-10-25 15:21:15.106671','2026-10-25 15:21:15.106671','WEDNESDAY',1,1,1,26,4),(200,'2026-10-25 15:21:20.510979','2026-10-25 15:21:20.510979','WEDNESDAY',1,1,1,36,3),(201,'2026-10-25 15:21:26.132360','2026-10-25 15:21:26.132360','WEDNESDAY',1,1,1,36,4),(202,'2026-10-25 15:21:33.460689','2026-10-25 15:21:33.460689','WEDNESDAY',1,1,1,43,3),(203,'2026-10-25 15:21:38.612595','2026-10-25 15:21:38.612595','WEDNESDAY',1,1,1,43,4),(204,'2026-10-25 15:21:43.263926','2026-10-25 15:21:43.263926','WEDNESDAY',1,1,1,52,3),(205,'2026-10-25 15:21:48.027270','2026-10-25 15:21:48.027270','WEDNESDAY',1,1,1,52,4),(206,'2026-10-25 15:21:55.040479','2026-10-25 15:21:55.040479','WEDNESDAY',1,1,1,54,6),(207,'2026-10-25 15:22:00.631056','2026-10-25 15:22:00.631056','WEDNESDAY',1,1,1,54,7),(208,'2026-10-25 15:22:07.095394','2026-10-25 15:22:07.095394','WEDNESDAY',1,1,1,51,8),(209,'2026-10-25 15:22:13.228097','2026-10-25 15:22:13.228097','WEDNESDAY',1,1,1,42,6),(210,'2026-10-25 15:22:18.756624','2026-10-25 15:22:18.756624','WEDNESDAY',1,1,1,42,7),(211,'2026-10-25 15:22:25.881472','2026-10-25 15:22:25.881472','WEDNESDAY',1,1,1,41,8),(212,'2026-10-25 15:22:31.158909','2026-10-25 15:22:31.158909','WEDNESDAY',1,1,1,32,6),(213,'2026-10-25 15:22:35.671926','2026-10-25 15:22:35.671926','WEDNESDAY',1,1,1,32,7),(214,'2026-10-25 15:22:42.321997','2026-10-25 15:22:42.321997','WEDNESDAY',1,1,1,33,8),(215,'2026-10-25 15:22:47.899133','2026-10-25 15:22:47.899133','WEDNESDAY',1,1,1,24,6),(216,'2026-10-25 15:22:56.111337','2026-10-25 15:22:56.111337','WEDNESDAY',1,1,1,25,7),(217,'2026-10-25 15:23:03.049348','2026-10-25 15:23:03.049348','WEDNESDAY',1,1,1,23,8),(218,'2026-10-25 15:23:11.632779','2026-10-25 15:23:11.632779','WEDNESDAY',1,1,1,10,6),(219,'2026-10-25 15:23:17.245061','2026-10-25 15:23:17.245061','WEDNESDAY',1,1,1,10,7),(220,'2026-10-25 15:23:24.291172','2026-10-25 15:23:24.291172','WEDNESDAY',1,1,1,12,8),(221,'2026-10-25 15:23:29.759588','2026-10-25 15:23:29.759588','WEDNESDAY',1,1,1,3,6),(222,'2026-10-25 15:23:37.750617','2026-10-25 15:23:37.750617','WEDNESDAY',1,1,1,3,7),(223,'2026-10-25 15:23:44.625672','2026-10-25 15:23:44.625672','WEDNESDAY',1,1,1,2,8),(224,'2026-10-25 15:23:49.919799','2026-10-25 15:23:49.919799','WEDNESDAY',1,1,1,1,10),(225,'2026-10-25 15:23:54.222759','2026-10-25 15:23:54.222759','WEDNESDAY',1,1,1,4,11),(226,'2026-10-25 15:23:59.794169','2026-10-25 15:23:59.794169','WEDNESDAY',1,1,1,5,12),(227,'2026-10-25 15:24:08.164635','2026-10-25 15:24:08.164635','WEDNESDAY',1,1,1,14,10),(228,'2026-10-25 15:24:14.216983','2026-10-25 15:24:14.216983','WEDNESDAY',1,1,1,12,11),(229,'2026-10-25 15:24:25.098544','2026-10-25 15:24:25.098544','WEDNESDAY',1,1,1,15,12),(230,'2026-10-25 15:24:35.637908','2026-10-25 15:24:35.637908','WEDNESDAY',1,1,1,24,10),(231,'2026-10-25 15:24:41.182697','2026-10-25 15:24:41.182697','WEDNESDAY',1,1,1,20,11),(232,'2026-10-25 15:24:46.025292','2026-10-25 15:24:46.025292','WEDNESDAY',1,1,1,19,12),(233,'2026-10-25 15:24:50.916859','2026-10-25 15:24:50.916859','WEDNESDAY',1,1,1,28,10),(234,'2026-10-25 15:24:55.774101','2026-10-25 15:24:55.774101','WEDNESDAY',1,1,1,30,11),(235,'2026-10-25 15:25:03.756630','2026-10-25 15:25:03.756630','WEDNESDAY',1,1,1,34,12),(236,'2026-10-25 15:25:12.643470','2026-10-25 15:25:12.643470','WEDNESDAY',1,1,1,37,10),(237,'2026-10-25 15:25:18.794518','2026-10-25 15:25:24.341877','WEDNESDAY',1,1,1,38,11),(238,'2026-10-25 15:25:32.748715','2026-10-25 15:25:32.748715','WEDNESDAY',1,1,1,40,12),(239,'2026-10-25 15:25:40.114703','2026-10-25 15:25:40.114703','WEDNESDAY',1,1,1,48,10),(240,'2026-10-25 15:25:50.632543','2026-10-25 15:25:50.632543','WEDNESDAY',1,1,1,49,11),(241,'2026-10-25 15:26:02.328823','2026-10-25 15:26:02.328823','WEDNESDAY',1,1,1,50,12),(242,'2026-10-25 15:26:32.571283','2026-10-25 15:26:32.571283','FRIDAY',1,1,1,1,1),(243,'2026-10-25 15:26:39.581079','2026-10-25 15:26:39.581079','FRIDAY',1,1,1,2,2),(244,'2026-10-25 15:26:44.513745','2026-10-25 15:26:44.513745','FRIDAY',1,1,1,3,3),(245,'2026-10-25 15:26:49.817497','2026-10-25 15:26:49.817497','FRIDAY',1,1,1,5,4),(246,'2026-10-25 15:26:55.601818','2026-10-25 15:26:55.601818','FRIDAY',1,1,1,15,1),(247,'2026-10-25 15:27:01.389849','2026-10-25 15:27:01.389849','FRIDAY',1,1,1,16,2),(248,'2026-10-25 15:27:07.406215','2026-10-25 15:27:07.406215','FRIDAY',1,1,1,17,3),(249,'2026-10-25 15:27:14.129707','2026-10-25 15:27:14.129707','FRIDAY',1,1,1,18,4),(250,'2026-10-25 15:27:25.303688','2026-10-25 15:27:25.303688','FRIDAY',1,1,1,22,1),(251,'2026-10-25 15:27:29.915704','2026-10-25 15:27:29.915704','FRIDAY',1,1,1,20,2),(252,'2026-10-25 15:27:37.728074','2026-10-25 15:27:37.728074','FRIDAY',1,1,1,19,3),(253,'2026-10-25 15:27:44.873600','2026-10-25 15:27:44.873600','FRIDAY',1,1,1,23,4),(254,'2026-10-25 15:27:54.070296','2026-10-25 15:27:54.070296','FRIDAY',1,1,1,36,1),(255,'2026-10-25 15:27:58.857988','2026-10-25 15:27:58.857988','FRIDAY',1,1,1,35,2),(256,'2026-10-25 15:28:03.956180','2026-10-25 15:28:03.956180','FRIDAY',1,1,1,34,3),(257,'2026-10-25 15:28:09.031307','2026-10-25 15:28:09.031307','FRIDAY',1,1,1,33,4),(258,'2026-10-25 15:28:15.663063','2026-10-25 15:28:15.663063','FRIDAY',1,1,1,37,4),(259,'2026-10-25 15:28:21.021266','2026-10-25 15:28:21.021266','FRIDAY',1,1,1,38,3),(260,'2026-10-25 15:28:26.245625','2026-10-25 15:28:26.245625','FRIDAY',1,1,1,39,2),(261,'2026-10-25 15:28:31.283029','2026-10-25 15:28:31.283029','FRIDAY',1,1,1,40,1),(262,'2026-10-25 15:28:37.815074','2026-10-25 15:28:37.815074','FRIDAY',1,1,1,47,1),(263,'2026-10-25 15:28:41.951430','2026-10-25 15:28:41.951430','FRIDAY',1,1,1,46,2),(264,'2026-10-25 15:28:47.527187','2026-10-25 15:28:54.060369','FRIDAY',1,1,1,49,3),(265,'2026-10-25 15:28:59.248327','2026-10-25 15:28:59.248327','FRIDAY',1,1,1,50,4),(266,'2026-10-25 15:29:06.149686','2026-10-25 15:29:06.149686','FRIDAY',1,1,1,54,6),(267,'2026-10-25 15:29:11.002498','2026-10-25 15:29:11.002498','FRIDAY',1,1,1,44,6),(268,'2026-10-25 15:29:17.004789','2026-10-25 15:29:33.602318','FRIDAY',1,1,1,30,6),(270,'2026-10-25 15:30:06.434469','2026-10-25 15:30:06.434469','FRIDAY',1,1,1,27,6),(271,'2026-10-25 15:30:15.000547','2026-10-25 15:30:15.000547','FRIDAY',1,1,1,6,6),(272,'2026-10-25 15:30:21.470144','2026-10-25 15:30:21.470144','FRIDAY',1,1,1,7,7),(273,'2026-10-25 15:30:27.620585','2026-10-25 15:30:27.620585','FRIDAY',1,1,1,8,8),(274,'2026-10-25 15:30:41.322768','2026-10-25 15:30:41.322768','FRIDAY',1,1,1,10,6),(275,'2026-10-25 15:30:47.858020','2026-10-25 15:30:47.858020','FRIDAY',1,1,1,11,7),(276,'2026-10-25 15:30:52.484541','2026-10-25 15:30:52.484541','FRIDAY',1,1,1,13,8),(277,'2026-10-25 15:31:03.633214','2026-10-25 15:31:03.633214','FRIDAY',1,1,1,24,7),(278,'2026-10-25 15:31:11.943981','2026-10-25 15:31:11.943981','FRIDAY',1,1,1,26,8),(279,'2026-10-25 15:31:23.438633','2026-10-25 15:31:23.438633','FRIDAY',1,1,1,29,7),(280,'2026-10-25 15:31:28.843031','2026-10-25 15:31:28.843031','FRIDAY',1,1,1,28,8),(281,'2026-10-25 15:31:49.210070','2026-10-25 15:31:49.210070','FRIDAY',1,1,1,43,7),(282,'2026-10-25 15:31:54.745599','2026-10-25 15:31:54.745599','FRIDAY',1,1,1,45,8),(283,'2026-10-25 15:32:02.655165','2026-10-25 15:32:23.660571','FRIDAY',1,1,1,51,8),(284,'2026-10-25 15:32:14.097562','2026-10-25 15:32:14.097562','FRIDAY',1,1,1,53,7),(285,'2026-10-25 15:32:41.598295','2026-10-25 15:32:41.598295','THURSDAY',1,1,1,1,1),(286,'2026-10-25 15:32:46.258399','2026-10-25 15:32:46.258399','THURSDAY',1,1,1,1,2),(287,'2026-10-25 15:32:50.682270','2026-10-25 15:32:50.682270','THURSDAY',1,1,1,2,3),(288,'2026-10-25 15:32:55.711849','2026-10-25 15:32:55.711849','THURSDAY',1,1,1,2,4),(289,'2026-10-25 15:33:31.518413','2026-10-25 15:33:31.518413','THURSDAY',1,1,1,3,6),(290,'2026-10-25 15:33:36.487118','2026-10-25 15:33:36.487118','THURSDAY',1,1,1,3,7),(291,'2026-10-25 15:33:46.580422','2026-10-25 15:33:46.580422','THURSDAY',1,1,1,11,1),(292,'2026-10-25 15:33:51.703569','2026-10-25 15:33:51.703569','THURSDAY',1,1,1,11,2),(293,'2026-10-25 15:33:59.112106','2026-10-25 15:33:59.112106','THURSDAY',1,1,1,10,3),(294,'2026-10-25 15:34:04.747407','2026-10-25 15:34:04.747407','THURSDAY',1,1,1,10,4),(295,'2026-10-25 15:34:11.805123','2026-10-25 15:34:11.805123','THURSDAY',1,1,1,13,6),(296,'2026-10-25 15:34:17.887532','2026-10-25 15:34:17.887532','THURSDAY',1,1,1,13,7),(297,'2026-10-25 15:34:25.414876','2026-10-25 15:34:25.414876','THURSDAY',1,1,1,20,1),(298,'2026-10-25 15:34:29.753168','2026-10-25 15:34:29.753168','THURSDAY',1,1,1,20,2),(299,'2026-10-25 15:34:33.788719','2026-10-25 15:34:33.788719','THURSDAY',1,1,1,19,3),(300,'2026-10-25 15:34:39.078421','2026-10-25 15:34:39.078421','THURSDAY',1,1,1,19,4),(301,'2026-10-25 15:34:44.057634','2026-10-25 15:34:44.057634','THURSDAY',1,1,1,21,6),(302,'2026-10-25 15:34:49.355002','2026-10-25 15:34:49.355002','THURSDAY',1,1,1,21,7),(303,'2026-10-25 15:34:54.884378','2026-10-25 15:34:54.884378','THURSDAY',1,1,1,28,1),(304,'2026-10-25 15:35:00.073705','2026-10-25 15:35:00.073705','THURSDAY',1,1,1,28,2),(305,'2026-10-25 15:35:05.142340','2026-10-25 15:35:05.142340','THURSDAY',1,1,1,29,3),(306,'2026-10-25 15:35:10.104090','2026-10-25 15:35:10.104090','THURSDAY',1,1,1,29,4),(307,'2026-10-25 15:35:15.537240','2026-10-25 15:35:15.537240','THURSDAY',1,1,1,31,6),(308,'2026-10-25 15:35:26.869307','2026-10-25 15:35:26.869307','THURSDAY',1,1,1,31,7),(309,'2026-10-25 15:35:31.835634','2026-10-25 15:35:31.835634','THURSDAY',1,1,1,37,1),(310,'2026-10-25 15:35:37.258972','2026-10-25 15:35:37.258972','THURSDAY',1,1,1,37,2),(311,'2026-10-25 15:35:43.919874','2026-10-25 15:35:43.919874','THURSDAY',1,1,1,38,3),(312,'2026-10-25 15:35:49.639187','2026-10-25 15:35:49.639187','THURSDAY',1,1,1,38,4),(313,'2026-10-25 15:35:55.104975','2026-10-25 15:35:55.104975','THURSDAY',1,1,1,39,6),(314,'2026-10-25 15:36:01.287329','2026-10-25 15:36:01.287329','THURSDAY',1,1,1,39,7),(315,'2026-10-25 15:36:06.820515','2026-10-25 15:36:06.820515','THURSDAY',1,1,1,48,1),(316,'2026-10-25 15:36:11.846608','2026-10-25 15:36:11.846608','THURSDAY',1,1,1,48,2),(317,'2026-10-25 15:36:20.035005','2026-10-25 15:36:20.035005','THURSDAY',1,1,1,49,3),(318,'2026-10-25 15:36:25.040686','2026-10-25 15:36:25.040686','THURSDAY',1,1,1,49,4),(319,'2026-10-25 15:36:30.861465','2026-10-25 15:36:30.861465','THURSDAY',1,1,1,46,6),(320,'2026-10-25 15:36:36.120350','2026-10-25 15:36:36.120350','THURSDAY',1,1,1,46,7),(321,'2026-10-25 15:36:42.524324','2026-10-25 15:36:42.524324','THURSDAY',1,1,1,54,10),(322,'2026-10-25 15:36:48.620711','2026-10-25 15:36:48.620711','THURSDAY',1,1,1,54,11),(323,'2026-10-25 15:36:57.810918','2026-10-25 15:36:57.810918','THURSDAY',1,1,1,45,10),(324,'2026-10-25 15:37:03.882068','2026-10-25 15:37:03.882068','THURSDAY',1,1,1,45,11),(325,'2026-10-25 15:37:09.289156','2026-10-25 15:37:09.289156','THURSDAY',1,1,1,36,10),(326,'2026-10-25 15:37:14.653590','2026-10-25 15:37:14.653590','THURSDAY',1,1,1,36,11),(327,'2026-10-25 15:37:21.339008','2026-10-25 15:37:21.339008','THURSDAY',1,1,1,25,10),(328,'2026-10-25 15:37:27.657131','2026-10-25 15:37:27.657131','THURSDAY',1,1,1,25,11),(329,'2026-10-25 15:37:33.104385','2026-10-25 15:37:33.104385','THURSDAY',1,1,1,17,10),(330,'2026-10-25 15:37:38.237959','2026-10-25 15:37:38.237959','THURSDAY',1,1,1,17,11),(331,'2026-10-25 15:37:48.709675','2026-10-25 15:37:48.709675','THURSDAY',1,1,1,8,10),(332,'2026-10-25 15:37:54.781070','2026-10-25 15:37:54.781070','THURSDAY',1,1,1,8,11),(333,'2026-10-25 15:38:04.630714','2026-10-25 15:38:04.630714','THURSDAY',1,1,1,5,8),(334,'2026-10-25 15:38:10.827149','2026-10-25 15:38:10.827149','THURSDAY',1,1,1,6,12),(335,'2026-10-25 15:38:17.199971','2026-10-25 15:38:17.199971','THURSDAY',1,1,1,15,8),(336,'2026-10-25 15:38:23.342321','2026-10-25 15:38:23.342321','THURSDAY',1,1,1,14,12),(337,'2026-10-25 15:38:28.728210','2026-10-25 15:38:28.728210','THURSDAY',1,1,1,22,8),(338,'2026-10-25 15:38:34.255963','2026-10-25 15:38:34.255963','THURSDAY',1,1,1,23,12),(339,'2026-10-25 15:38:40.002098','2026-10-25 15:38:40.002098','THURSDAY',1,1,1,30,8),(340,'2026-10-25 15:38:45.962436','2026-10-25 15:38:45.962436','THURSDAY',1,1,1,33,12),(341,'2026-10-25 15:38:51.738364','2026-10-25 15:38:51.738364','THURSDAY',1,1,1,41,8),(342,'2026-10-25 15:38:58.078922','2026-10-25 15:38:58.078922','THURSDAY',1,1,1,42,12),(343,'2026-10-25 15:39:05.416674','2026-10-25 15:39:05.416674','THURSDAY',1,1,1,47,8),(344,'2026-10-25 15:39:11.239576','2026-10-25 15:39:11.239576','THURSDAY',1,1,1,52,12);
/*!40000 ALTER TABLE `timetable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `date_of_birth` datetime(6) DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `must_change_password` bit(1) NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `failed_login_attempts` int NOT NULL,
  `locked_until` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  KEY `FKdltbr5t0nljpuuo4isxgslt82` (`created_by`),
  KEY `FK2a54xhceitopkkw1hlo3tkv3i` (`updated_by`),
  CONSTRAINT `FK2a54xhceitopkkw1hlo3tkv3i` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKdltbr5t0nljpuuo4isxgslt82` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=173 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2026-10-24 21:29:53.824241','2026-10-24 21:29:53.824241',NULL,'monamane.lebohang45@gmail.com','Lebohang','Monamane',_binary '\0','$2a$10$B8d975Ed1ivtZ6sxC7NTbOqGSXZlPFwoB5n4HrgYpc97AXiMdHkMu','59181664','ACTIVE',NULL,NULL,0,NULL),(2,'2026-10-24 21:48:09.188233','2026-10-24 21:48:09.188233','2000-10-26 00:00:00.000000','bytewisesolutions497@gmail.com','Bytewise','Solutions',_binary '','$2a$10$5RwhoOI8FZPw/B0ASsolMO5vsu6ve3aSLW4bCKuWTKz/WPE6Uq6ga','55667788','ACTIVE',1,1,0,NULL),(3,'2026-10-24 21:49:24.451861','2026-10-24 21:49:24.451861','2000-10-24 00:00:00.000000','mahalabet7@gmail.com','Mahala','Bet',_binary '','$2a$10$Urc2ToespTj9A81tXlMHKOqIgsOXH/qMAoE4NtMyg/eg6h6GbDhQC','66778844','ACTIVE',1,1,0,NULL),(4,'2026-10-24 21:50:12.655142','2026-10-24 21:50:12.655142','2000-10-28 00:00:00.000000','playingunits476@gmail.com','Finance','Officer',_binary '','$2a$10$h4xutf03wweQZ.x/uu.S2O.NGWoNxWMiNyU3kZU9.6usEScesBZ9.','66554433','ACTIVE',1,1,0,NULL),(5,'2026-10-24 21:51:10.622682','2026-10-24 21:51:10.622682',NULL,'lebomona71@gmail.com','Lebo','Mona',_binary '','$2a$10$5IhfPTgAXQ4aHroKYi.Dm.7lB.j17eCDU.n6Kp6wyw1JHCAeUyMi.','55446633','ACTIVE',1,1,0,NULL),(6,'2026-10-24 21:51:56.296469','2026-10-24 21:51:56.296469',NULL,'lebohangmonamane3@gmail.com','Malebohang','Mona',_binary '','$2a$10$G7i4O7vFy2Pn1KKrHlPiSu0wTD6PPbTlN8ZJpfQVQGdG1Tc.JJ5Ca','57667788','ACTIVE',1,1,0,NULL),(7,'2026-10-24 22:07:25.665914','2026-10-24 22:07:25.665914','2026-10-25 00:00:00.000000','communityhealthreferral@gmail.com','Community','Referral',_binary '','$2a$10$mHVZ5XPo3PSDzHi3L9GHf.BMUJ9wQg3g4/FO06XzBQQ8gavzDVT/m','675748383','ACTIVE',1,1,0,NULL),(8,'2026-10-25 08:59:59.000000','2026-10-25 08:59:59.000000',NULL,'test.teacher1@tsoinyane.test','Naledi','Mokoena',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000001','ACTIVE',1,1,0,NULL),(9,'2026-10-25 08:59:59.000000','2026-10-25 08:59:59.000000',NULL,'test.teacher2@tsoinyane.test','Kabelo','Thaba',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000002','ACTIVE',1,1,0,NULL),(28,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher03@tsoinyane.test','Mpho','Ndlovu',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000003','ACTIVE',1,1,0,NULL),(29,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher04@tsoinyane.test','Palesa','Molefe',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000004','ACTIVE',1,1,0,NULL),(30,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher05@tsoinyane.test','Kea','Mokoena',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000005','ACTIVE',1,1,0,NULL),(31,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher06@tsoinyane.test','Thabo','Mahlangu',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000006','ACTIVE',1,1,0,NULL),(32,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher07@tsoinyane.test','Lerato','Maseko',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000007','ACTIVE',1,1,0,NULL),(33,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher08@tsoinyane.test','Kamohelo','Dube',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58000008','ACTIVE',1,1,0,NULL),(34,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher09@tsoinyane.test','Boitumelo','Motaung',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000009','ACTIVE',1,1,0,NULL),(35,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher10@tsoinyane.test','Neo','Molefe',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000010','ACTIVE',1,1,0,NULL),(36,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher11@tsoinyane.test','Refilwe','Mokoena',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000011','ACTIVE',1,1,0,NULL),(37,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher12@tsoinyane.test','Tshepo','Molefe',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000012','ACTIVE',1,1,0,NULL),(38,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher13@tsoinyane.test','Noluthando','Khumalo',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000013','ACTIVE',1,1,0,NULL),(39,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher14@tsoinyane.test','Sello','Radebe',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000014','ACTIVE',1,1,0,NULL),(40,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher15@tsoinyane.test','Masechaba','Phiri',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000015','ACTIVE',1,1,0,NULL),(41,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher16@tsoinyane.test','Lesedi','Mokoena',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000016','ACTIVE',1,1,0,NULL),(42,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher17@tsoinyane.test','Kagiso','Molefe',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000017','ACTIVE',1,1,0,NULL),(43,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher18@tsoinyane.test','Mpho','Mokone',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000018','ACTIVE',1,1,0,NULL),(44,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher19@tsoinyane.test','Nthabiseng','Motaung',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000019','ACTIVE',1,1,0,NULL),(45,'2026-10-25 09:01:07.000000','2026-10-25 09:01:07.000000',NULL,'test.teacher20@tsoinyane.test','Lindelani','Nkosi',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldLJZdL17lhWy','58000020','ACTIVE',1,1,0,NULL),(46,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student001@tsoinyane.test','Test','Student 001',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100001','ACTIVE',1,1,0,NULL),(47,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student002@tsoinyane.test','Test','Student 002',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100002','ACTIVE',1,1,0,NULL),(48,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student003@tsoinyane.test','Test','Student 003',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100003','ACTIVE',1,1,0,NULL),(49,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student004@tsoinyane.test','Test','Student 004',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100004','ACTIVE',1,1,0,NULL),(50,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student005@tsoinyane.test','Test','Student 005',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100005','ACTIVE',1,1,0,NULL),(51,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student006@tsoinyane.test','Test','Student 006',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100006','ACTIVE',1,1,0,NULL),(52,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student007@tsoinyane.test','Test','Student 007',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100007','ACTIVE',1,1,0,NULL),(53,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student008@tsoinyane.test','Test','Student 008',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100008','ACTIVE',1,1,0,NULL),(54,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student009@tsoinyane.test','Test','Student 009',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100009','ACTIVE',1,1,0,NULL),(55,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student010@tsoinyane.test','Test','Student 010',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100010','ACTIVE',1,1,0,NULL),(56,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student011@tsoinyane.test','Test','Student 011',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100011','ACTIVE',1,1,0,NULL),(57,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student012@tsoinyane.test','Test','Student 012',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100012','ACTIVE',1,1,0,NULL),(58,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student013@tsoinyane.test','Test','Student 013',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100013','ACTIVE',1,1,0,NULL),(59,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student014@tsoinyane.test','Test','Student 014',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100014','ACTIVE',1,1,0,NULL),(60,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student015@tsoinyane.test','Test','Student 015',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100015','ACTIVE',1,1,0,NULL),(61,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student016@tsoinyane.test','Test','Student 016',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100016','ACTIVE',1,1,0,NULL),(62,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student017@tsoinyane.test','Test','Student 017',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100017','ACTIVE',1,1,0,NULL),(63,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student018@tsoinyane.test','Test','Student 018',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100018','ACTIVE',1,1,0,NULL),(64,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student019@tsoinyane.test','Test','Student 019',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100019','ACTIVE',1,1,0,NULL),(65,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student020@tsoinyane.test','Test','Student 020',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100020','ACTIVE',1,1,0,NULL),(66,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student021@tsoinyane.test','Test','Student 021',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100021','ACTIVE',1,1,0,NULL),(67,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student022@tsoinyane.test','Test','Student 022',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100022','ACTIVE',1,1,0,NULL),(68,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student023@tsoinyane.test','Test','Student 023',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100023','ACTIVE',1,1,0,NULL),(69,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student024@tsoinyane.test','Test','Student 024',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100024','ACTIVE',1,1,0,NULL),(70,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student025@tsoinyane.test','Test','Student 025',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100025','ACTIVE',1,1,0,NULL),(71,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student026@tsoinyane.test','Test','Student 026',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100026','ACTIVE',1,1,0,NULL),(72,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student027@tsoinyane.test','Test','Student 027',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100027','ACTIVE',1,1,0,NULL),(73,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student028@tsoinyane.test','Test','Student 028',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100028','ACTIVE',1,1,0,NULL),(74,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student029@tsoinyane.test','Test','Student 029',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100029','ACTIVE',1,1,0,NULL),(75,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student030@tsoinyane.test','Test','Student 030',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100030','ACTIVE',1,1,0,NULL),(76,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student031@tsoinyane.test','Test','Student 031',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100031','ACTIVE',1,1,0,NULL),(77,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student032@tsoinyane.test','Test','Student 032',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100032','ACTIVE',1,1,0,NULL),(78,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student033@tsoinyane.test','Test','Student 033',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100033','ACTIVE',1,1,0,NULL),(79,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student034@tsoinyane.test','Test','Student 034',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100034','ACTIVE',1,1,0,NULL),(80,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student035@tsoinyane.test','Test','Student 035',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100035','ACTIVE',1,1,0,NULL),(81,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student036@tsoinyane.test','Test','Student 036',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100036','ACTIVE',1,1,0,NULL),(82,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student037@tsoinyane.test','Test','Student 037',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100037','ACTIVE',1,1,0,NULL),(83,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student038@tsoinyane.test','Test','Student 038',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100038','ACTIVE',1,1,0,NULL),(84,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student039@tsoinyane.test','Test','Student 039',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100039','ACTIVE',1,1,0,NULL),(85,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student040@tsoinyane.test','Test','Student 040',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100040','ACTIVE',1,1,0,NULL),(86,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student041@tsoinyane.test','Test','Student 041',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100041','ACTIVE',1,1,0,NULL),(87,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student042@tsoinyane.test','Test','Student 042',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100042','ACTIVE',1,1,0,NULL),(88,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student043@tsoinyane.test','Test','Student 043',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100043','ACTIVE',1,1,0,NULL),(89,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student044@tsoinyane.test','Test','Student 044',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100044','ACTIVE',1,1,0,NULL),(90,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student045@tsoinyane.test','Test','Student 045',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100045','ACTIVE',1,1,0,NULL),(91,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student046@tsoinyane.test','Test','Student 046',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100046','ACTIVE',1,1,0,NULL),(92,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student047@tsoinyane.test','Test','Student 047',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100047','ACTIVE',1,1,0,NULL),(93,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student048@tsoinyane.test','Test','Student 048',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100048','ACTIVE',1,1,0,NULL),(94,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student049@tsoinyane.test','Test','Student 049',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100049','ACTIVE',1,1,0,NULL),(95,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student050@tsoinyane.test','Test','Student 050',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100050','ACTIVE',1,1,0,NULL),(96,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student051@tsoinyane.test','Test','Student 051',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100051','ACTIVE',1,1,0,NULL),(97,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student052@tsoinyane.test','Test','Student 052',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100052','ACTIVE',1,1,0,NULL),(98,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student053@tsoinyane.test','Test','Student 053',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100053','ACTIVE',1,1,0,NULL),(99,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student054@tsoinyane.test','Test','Student 054',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100054','ACTIVE',1,1,0,NULL),(100,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student055@tsoinyane.test','Test','Student 055',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100055','ACTIVE',1,1,0,NULL),(101,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student056@tsoinyane.test','Test','Student 056',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100056','ACTIVE',1,1,0,NULL),(102,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student057@tsoinyane.test','Test','Student 057',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100057','ACTIVE',1,1,0,NULL),(103,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student058@tsoinyane.test','Test','Student 058',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100058','ACTIVE',1,1,0,NULL),(104,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student059@tsoinyane.test','Test','Student 059',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100059','ACTIVE',1,1,0,NULL),(105,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student060@tsoinyane.test','Test','Student 060',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100060','ACTIVE',1,1,0,NULL),(106,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student061@tsoinyane.test','Test','Student 061',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100061','ACTIVE',1,1,0,NULL),(107,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student062@tsoinyane.test','Test','Student 062',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100062','ACTIVE',1,1,0,NULL),(108,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student063@tsoinyane.test','Test','Student 063',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100063','ACTIVE',1,1,0,NULL),(109,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student064@tsoinyane.test','Test','Student 064',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100064','ACTIVE',1,1,0,NULL),(110,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student065@tsoinyane.test','Test','Student 065',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100065','ACTIVE',1,1,0,NULL),(111,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student066@tsoinyane.test','Test','Student 066',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100066','ACTIVE',1,1,0,NULL),(112,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student067@tsoinyane.test','Test','Student 067',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100067','ACTIVE',1,1,0,NULL),(113,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student068@tsoinyane.test','Test','Student 068',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100068','ACTIVE',1,1,0,NULL),(114,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student069@tsoinyane.test','Test','Student 069',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100069','ACTIVE',1,1,0,NULL),(115,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student070@tsoinyane.test','Test','Student 070',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100070','ACTIVE',1,1,0,NULL),(116,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student071@tsoinyane.test','Test','Student 071',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100071','ACTIVE',1,1,0,NULL),(117,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student072@tsoinyane.test','Test','Student 072',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100072','ACTIVE',1,1,0,NULL),(118,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student073@tsoinyane.test','Test','Student 073',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100073','ACTIVE',1,1,0,NULL),(119,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student074@tsoinyane.test','Test','Student 074',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100074','ACTIVE',1,1,0,NULL),(120,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student075@tsoinyane.test','Test','Student 075',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100075','ACTIVE',1,1,0,NULL),(121,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student076@tsoinyane.test','Test','Student 076',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100076','ACTIVE',1,1,0,NULL),(122,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student077@tsoinyane.test','Test','Student 077',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100077','ACTIVE',1,1,0,NULL),(123,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student078@tsoinyane.test','Test','Student 078',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100078','ACTIVE',1,1,0,NULL),(124,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student079@tsoinyane.test','Test','Student 079',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100079','ACTIVE',1,1,0,NULL),(125,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student080@tsoinyane.test','Test','Student 080',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100080','ACTIVE',1,1,0,NULL),(126,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student081@tsoinyane.test','Test','Student 081',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100081','ACTIVE',1,1,0,NULL),(127,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student082@tsoinyane.test','Test','Student 082',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100082','ACTIVE',1,1,0,NULL),(128,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student083@tsoinyane.test','Test','Student 083',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100083','ACTIVE',1,1,0,NULL),(129,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student084@tsoinyane.test','Test','Student 084',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100084','ACTIVE',1,1,0,NULL),(130,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student085@tsoinyane.test','Test','Student 085',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100085','ACTIVE',1,1,0,NULL),(131,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student086@tsoinyane.test','Test','Student 086',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100086','ACTIVE',1,1,0,NULL),(132,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student087@tsoinyane.test','Test','Student 087',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100087','ACTIVE',1,1,0,NULL),(133,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student088@tsoinyane.test','Test','Student 088',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100088','ACTIVE',1,1,0,NULL),(134,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student089@tsoinyane.test','Test','Student 089',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100089','ACTIVE',1,1,0,NULL),(135,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student090@tsoinyane.test','Test','Student 090',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100090','ACTIVE',1,1,0,NULL),(136,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student091@tsoinyane.test','Test','Student 091',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100091','ACTIVE',1,1,0,NULL),(137,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student092@tsoinyane.test','Test','Student 092',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100092','ACTIVE',1,1,0,NULL),(138,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student093@tsoinyane.test','Test','Student 093',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100093','ACTIVE',1,1,0,NULL),(139,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student094@tsoinyane.test','Test','Student 094',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100094','ACTIVE',1,1,0,NULL),(140,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student095@tsoinyane.test','Test','Student 095',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100095','ACTIVE',1,1,0,NULL),(141,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student096@tsoinyane.test','Test','Student 096',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100096','ACTIVE',1,1,0,NULL),(142,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student097@tsoinyane.test','Test','Student 097',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100097','ACTIVE',1,1,0,NULL),(143,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student098@tsoinyane.test','Test','Student 098',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100098','ACTIVE',1,1,0,NULL),(144,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student099@tsoinyane.test','Test','Student 099',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100099','ACTIVE',1,1,0,NULL),(145,'2026-10-25 09:02:30.000000','2026-10-25 09:02:30.000000',NULL,'test.student100@tsoinyane.test','Test','Student 100',_binary '','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','58100100','ACTIVE',1,1,0,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL,
  `role` enum('FINANCE_OFFICER','PARENT','PRINCIPAL','SCHOOL_ADMIN','STUDENT','SYSTEM_ADMIN','TEACHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`,`role`),
  CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'SCHOOL_ADMIN'),(2,'PRINCIPAL'),(3,'TEACHER'),(4,'FINANCE_OFFICER'),(5,'STUDENT'),(6,'PARENT'),(7,'TEACHER'),(8,'TEACHER'),(9,'TEACHER'),(28,'TEACHER'),(29,'TEACHER'),(30,'TEACHER'),(31,'TEACHER'),(32,'TEACHER'),(33,'TEACHER'),(34,'TEACHER'),(35,'TEACHER'),(36,'TEACHER'),(37,'TEACHER'),(38,'TEACHER'),(39,'TEACHER'),(40,'TEACHER'),(41,'TEACHER'),(42,'TEACHER'),(43,'TEACHER'),(44,'TEACHER'),(45,'TEACHER'),(46,'STUDENT'),(47,'STUDENT'),(48,'STUDENT'),(49,'STUDENT'),(50,'STUDENT'),(51,'STUDENT'),(52,'STUDENT'),(53,'STUDENT'),(54,'STUDENT'),(55,'STUDENT'),(56,'STUDENT'),(57,'STUDENT'),(58,'STUDENT'),(59,'STUDENT'),(60,'STUDENT'),(61,'STUDENT'),(62,'STUDENT'),(63,'STUDENT'),(64,'STUDENT'),(65,'STUDENT'),(66,'STUDENT'),(67,'STUDENT'),(68,'STUDENT'),(69,'STUDENT'),(70,'STUDENT'),(71,'STUDENT'),(72,'STUDENT'),(73,'STUDENT'),(74,'STUDENT'),(75,'STUDENT'),(76,'STUDENT'),(77,'STUDENT'),(78,'STUDENT'),(79,'STUDENT'),(80,'STUDENT'),(81,'STUDENT'),(82,'STUDENT'),(83,'STUDENT'),(84,'STUDENT'),(85,'STUDENT'),(86,'STUDENT'),(87,'STUDENT'),(88,'STUDENT'),(89,'STUDENT'),(90,'STUDENT'),(91,'STUDENT'),(92,'STUDENT'),(93,'STUDENT'),(94,'STUDENT'),(95,'STUDENT'),(96,'STUDENT'),(97,'STUDENT'),(98,'STUDENT'),(99,'STUDENT'),(100,'STUDENT'),(101,'STUDENT'),(102,'STUDENT'),(103,'STUDENT'),(104,'STUDENT'),(105,'STUDENT'),(106,'STUDENT'),(107,'STUDENT'),(108,'STUDENT'),(109,'STUDENT'),(110,'STUDENT'),(111,'STUDENT'),(112,'STUDENT'),(113,'STUDENT'),(114,'STUDENT'),(115,'STUDENT'),(116,'STUDENT'),(117,'STUDENT'),(118,'STUDENT'),(119,'STUDENT'),(120,'STUDENT'),(121,'STUDENT'),(122,'STUDENT'),(123,'STUDENT'),(124,'STUDENT'),(125,'STUDENT'),(126,'STUDENT'),(127,'STUDENT'),(128,'STUDENT'),(129,'STUDENT'),(130,'STUDENT'),(131,'STUDENT'),(132,'STUDENT'),(133,'STUDENT'),(134,'STUDENT'),(135,'STUDENT'),(136,'STUDENT'),(137,'STUDENT'),(138,'STUDENT'),(139,'STUDENT'),(140,'STUDENT'),(141,'STUDENT'),(142,'STUDENT'),(143,'STUDENT'),(144,'STUDENT'),(145,'STUDENT');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_school`
--

DROP TABLE IF EXISTS `user_school`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_school` (
  `user_id` bigint NOT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`school_id`),
  KEY `FKks1nruv2dct07u0rdshfqi72g` (`school_id`),
  CONSTRAINT `FKks1nruv2dct07u0rdshfqi72g` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKmyjs8jnfhemn4vnfypdcla7nk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_school`
--

LOCK TABLES `user_school` WRITE;
/*!40000 ALTER TABLE `user_school` DISABLE KEYS */;
INSERT INTO `user_school` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(28,1),(29,1),(30,1),(31,1),(32,1),(33,1),(34,1),(35,1),(36,1),(37,1),(38,1),(39,1),(40,1),(41,1),(42,1),(43,1),(44,1),(45,1),(46,1),(47,1),(48,1),(49,1),(50,1),(51,1),(52,1),(53,1),(54,1),(55,1),(56,1),(57,1),(58,1),(59,1),(60,1),(61,1),(62,1),(63,1),(64,1),(65,1),(66,1),(67,1),(68,1),(69,1),(70,1),(71,1),(72,1),(73,1),(74,1),(75,1),(76,1),(77,1),(78,1),(79,1),(80,1),(81,1),(82,1),(83,1),(84,1),(85,1),(86,1),(87,1),(88,1),(89,1),(90,1),(91,1),(92,1),(93,1),(94,1),(95,1),(96,1),(97,1),(98,1),(99,1),(100,1),(101,1),(102,1),(103,1),(104,1),(105,1),(106,1),(107,1),(108,1),(109,1),(110,1),(111,1),(112,1),(113,1),(114,1),(115,1),(116,1),(117,1),(118,1),(119,1),(120,1),(121,1),(122,1),(123,1),(124,1),(125,1),(126,1),(127,1),(128,1),(129,1),(130,1),(131,1),(132,1),(133,1),(134,1),(135,1),(136,1),(137,1),(138,1),(139,1),(140,1),(141,1),(142,1),(143,1),(144,1),(145,1);
/*!40000 ALTER TABLE `user_school` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'school_management_db'
--

--
-- Dumping routines for database 'school_management_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-07 14:50:08
