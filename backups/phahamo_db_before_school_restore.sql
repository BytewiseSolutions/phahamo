-- MySQL dump 10.13  Distrib 9.5.0, for macos26.1 (arm64)
--
-- Host: localhost    Database: phahamo_db
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `absence_alert_log`
--

LOCK TABLES `absence_alert_log` WRITE;
/*!40000 ALTER TABLE `absence_alert_log` DISABLE KEYS */;
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
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `academic_year`
--

LOCK TABLES `academic_year` WRITE;
/*!40000 ALTER TABLE `academic_year` DISABLE KEYS */;
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
  `audience_type` enum('ALL','CLASS','ROLE','USER') NOT NULL,
  `body` text NOT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `publish_at` datetime(6) NOT NULL,
  `status` enum('DRAFT','PUBLISHED') NOT NULL,
  `target_role` enum('FINANCE_OFFICER','PARENT','PRINCIPAL','SCHOOL_ADMIN','STUDENT','SYSTEM_ADMIN','TEACHER') DEFAULT NULL,
  `title` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annual_fee`
--

LOCK TABLES `annual_fee` WRITE;
/*!40000 ALTER TABLE `annual_fee` DISABLE KEYS */;
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
  `name` varchar(255) NOT NULL,
  `type` enum('CONTINUOUS','EXAM') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assessment`
--

LOCK TABLES `assessment` WRITE;
/*!40000 ALTER TABLE `assessment` DISABLE KEYS */;
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
  `status` enum('ABSENT','EXCUSED','LATE','PRESENT') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `action` enum('ACADEMIC_YEAR_CREATED','ACADEMIC_YEAR_DELETED','ACADEMIC_YEAR_UPDATED','ATTENDANCE_CHANGED','FAILED_LOGIN','FEE_STRUCTURE_CREATED','FEE_STRUCTURE_DELETED','FEE_STRUCTURE_UPDATED','HOLIDAY_CREATED','HOLIDAY_DELETED','HOLIDAY_UPDATED','LOGIN','LOGOUT','PAYMENT_RECORDED','PAYMENT_REVERSED','RESULT_CHANGED_AFTER_FINALISATION','STUDENT_FEE_CLASS_CHANGED','STUDENT_STATUS_CHANGED','TERM_CREATED','TERM_DELETED','TERM_UPDATED','TIMETABLE_CREATED','TIMETABLE_DELETED','TIMETABLE_PUBLISHED','TIMETABLE_UPDATED','USER_ACCOUNT_UNLOCKED','USER_CREATED','USER_DEACTIVATED','USER_ROLE_CHANGED') NOT NULL,
  `summary` varchar(1000) NOT NULL,
  `target_id` bigint DEFAULT NULL,
  `target_type` varchar(60) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
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
  `token` varchar(512) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd8f94e6nes93c15hn3lmijlld` (`token`),
  KEY `FK7sdeh2ggmxlkoj5u55giwx4p8` (`created_by`),
  KEY `FKj3ibrrvdn5a1qy14ktnu1dhnd` (`updated_by`),
  CONSTRAINT `FK7sdeh2ggmxlkoj5u55giwx4p8` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKj3ibrrvdn5a1qy14ktnu1dhnd` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blacklisted_token`
--

LOCK TABLES `blacklisted_token` WRITE;
/*!40000 ALTER TABLE `blacklisted_token` DISABLE KEYS */;
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
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfcb8bduvqfb7wdkpiwj1kdj9r` (`created_by`),
  KEY `FK7esh30cv5f50hbvprnwrrktv5` (`updated_by`),
  CONSTRAINT `FK7esh30cv5f50hbvprnwrrktv5` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKfcb8bduvqfb7wdkpiwj1kdj9r` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `description` varchar(500) DEFAULT NULL,
  `mandatory` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_item`
--

LOCK TABLES `fee_item` WRITE;
/*!40000 ALTER TABLE `fee_item` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_structure`
--

LOCK TABLES `fee_structure` WRITE;
/*!40000 ALTER TABLE `fee_structure` DISABLE KEYS */;
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
  `grade` varchar(255) NOT NULL,
  `max_score` double NOT NULL,
  `min_score` double NOT NULL,
  `remark` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `status` enum('CANCELLED','COMPLETED','MISSED','SCHEDULED','SUBSTITUTED') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lesson`
--

LOCK TABLES `lesson` WRITE;
/*!40000 ALTER TABLE `lesson` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `body` text NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `edited_at` datetime(6) DEFAULT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `sent_at` datetime(6) NOT NULL,
  `subject` varchar(200) NOT NULL,
  `thread_id` varchar(36) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
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
  `invited_at` datetime(6) DEFAULT NULL,
  `login_enabled` tinyint(1) NOT NULL DEFAULT '1',
  `relationship` enum('FATHER','GUARDIAN','MOTHER','OTHER') NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2er3k7pagjgspesr4bn7a2aa3` (`user_id`),
  KEY `FKk4k6gnc6lxl1xmccdweqoisnq` (`created_by`),
  KEY `FKemnkag2r5j9pw1ig5ixc3w3ot` (`updated_by`),
  KEY `FKb4ivdsgti4js74ipm3beexxtq` (`school_id`),
  CONSTRAINT `FKb4ivdsgti4js74ipm3beexxtq` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKemnkag2r5j9pw1ig5ixc3w3ot` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKk4k6gnc6lxl1xmccdweqoisnq` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKl4w905h1321m2ide56ov5efb0` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent`
--

LOCK TABLES `parent` WRITE;
/*!40000 ALTER TABLE `parent` DISABLE KEYS */;
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
  `token` varchar(64) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `details` varchar(2000) NOT NULL,
  `label` varchar(255) NOT NULL,
  `sort_order` int NOT NULL,
  `type` enum('CARD','CASH','ECOCASH','EFT','MPESA') NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbg104x5rtyc47w2fv4gqbwp7p` (`created_by`),
  KEY `FK2lydyj5svk6m6kt1pys0f5jr8` (`updated_by`),
  CONSTRAINT `FK2lydyj5svk6m6kt1pys0f5jr8` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbg104x5rtyc47w2fv4gqbwp7p` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `name` varchar(255) NOT NULL,
  `start_time` time NOT NULL,
  `type` varchar(20) NOT NULL DEFAULT 'LESSON',
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcqkr0eqjwuo8f46xrle2rnrr1` (`created_by`),
  KEY `FKon89sa08kdum2skkbetunes4x` (`updated_by`),
  KEY `FK97qls3bc7g0tdkv0mbgm7a08x` (`school_id`),
  CONSTRAINT `FK97qls3bc7g0tdkv0mbgm7a08x` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKcqkr0eqjwuo8f46xrle2rnrr1` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKon89sa08kdum2skkbetunes4x` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `period`
--

LOCK TABLES `period` WRITE;
/*!40000 ALTER TABLE `period` DISABLE KEYS */;
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
  `currency` enum('LSL','USD','ZAR') NOT NULL,
  `plan` enum('ANNUAL','FREE_TRIAL','MONTHLY','ONE_OFF') NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_price_plan_currency` (`plan`,`currency`),
  KEY `FKg5bkuv3hlfummqxx9ncy2b8lp` (`created_by`),
  KEY `FKrxg9sry8k4auq0gs21flwaxsw` (`updated_by`),
  CONSTRAINT `FKg5bkuv3hlfummqxx9ncy2b8lp` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrxg9sry8k4auq0gs21flwaxsw` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `batch_id` varchar(36) NOT NULL,
  `new_status` enum('ACTIVE','COMPLETED','GRADUATED','INACTIVE','TRANSFERRED','WITHDRAWN') NOT NULL,
  `previous_status` enum('ACTIVE','COMPLETED','GRADUATED','INACTIVE','TRANSFERRED','WITHDRAWN') NOT NULL,
  `promoted_at` datetime(6) NOT NULL,
  `reason` varchar(1000) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `reason` varchar(1000) DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `address` varchar(255) NOT NULL,
  `contact_email` varchar(255) NOT NULL,
  `contact_phone` varchar(255) NOT NULL,
  `currency` enum('LSL','USD','ZAR') NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') NOT NULL,
  `term_structure` enum('FOUR_QUARTER','ONE_TERM','THREE_TERM','TWO_TERM') NOT NULL,
  `timetable_published` tinyint(1) NOT NULL DEFAULT '0',
  `timetable_published_at` datetime(6) DEFAULT NULL,
  `timezone` varchar(255) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `logo_file_id` bigint DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school`
--

LOCK TABLES `school` WRITE;
/*!40000 ALTER TABLE `school` DISABLE KEYS */;
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
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school_class`
--

LOCK TABLES `school_class` WRITE;
/*!40000 ALTER TABLE `school_class` DISABLE KEYS */;
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
  `name` varchar(255) NOT NULL,
  `recurring` bit(1) NOT NULL,
  `start_date` date NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrv25r7e8pwjrgrlc40ca02vub` (`created_by`),
  KEY `FKmpopqwuqempgshd9b37m8txui` (`updated_by`),
  KEY `FKru7fodwmn6sgbcngimkeohlhb` (`school_id`),
  CONSTRAINT `FKmpopqwuqempgshd9b37m8txui` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKru7fodwmn6sgbcngimkeohlhb` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`),
  CONSTRAINT `FKrv25r7e8pwjrgrlc40ca02vub` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school_holiday`
--

LOCK TABLES `school_holiday` WRITE;
/*!40000 ALTER TABLE `school_holiday` DISABLE KEYS */;
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
  `admission_number` varchar(255) NOT NULL,
  `date_of_birth` datetime(6) NOT NULL,
  `gender` enum('FEMALE','MALE') NOT NULL,
  `status` enum('ACTIVE','COMPLETED','GRADUATED','INACTIVE','TRANSFERRED','WITHDRAWN') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_account`
--

LOCK TABLES `student_fee_account` WRITE;
/*!40000 ALTER TABLE `student_fee_account` DISABLE KEYS */;
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
  `reason` varchar(500) DEFAULT NULL,
  `type` enum('DISCOUNT','SCHOLARSHIP','WAIVER','WRITEOFF') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_adjustment`
--

LOCK TABLES `student_fee_adjustment` WRITE;
/*!40000 ALTER TABLE `student_fee_adjustment` DISABLE KEYS */;
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
  `description` varchar(500) DEFAULT NULL,
  `from_annual_fee` bit(1) NOT NULL,
  `mandatory` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_line`
--

LOCK TABLES `student_fee_line` WRITE;
/*!40000 ALTER TABLE `student_fee_line` DISABLE KEYS */;
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
  `method` enum('CARD','CASH','ECOCASH','EFT','MPESA') NOT NULL,
  `paid_at` datetime(6) NOT NULL,
  `reference` varchar(100) DEFAULT NULL,
  `reversal_reason` varchar(500) DEFAULT NULL,
  `reversed_at` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','REVERSED') DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  `received_by` bigint DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_payment`
--

LOCK TABLES `student_fee_payment` WRITE;
/*!40000 ALTER TABLE `student_fee_payment` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_parent`
--

LOCK TABLES `student_parent` WRITE;
/*!40000 ALTER TABLE `student_parent` DISABLE KEYS */;
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
  `class_teacher_remark` varchar(2000) DEFAULT NULL,
  `class_teacher_remark_at` datetime(6) DEFAULT NULL,
  `principal_remark` varchar(2000) DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject_class_assignment`
--

LOCK TABLES `subject_class_assignment` WRITE;
/*!40000 ALTER TABLE `subject_class_assignment` DISABLE KEYS */;
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
  `plan` enum('ANNUAL','FREE_TRIAL','MONTHLY','ONE_OFF') NOT NULL,
  `started_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','CANCELLED','EXPIRED','SUSPENDED','TRIAL') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription`
--

LOCK TABLES `subscription` WRITE;
/*!40000 ALTER TABLE `subscription` DISABLE KEYS */;
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
  `currency` enum('LSL','USD','ZAR') NOT NULL,
  `note` varchar(2000) DEFAULT NULL,
  `plan` enum('ANNUAL','FREE_TRIAL','MONTHLY','ONE_OFF') NOT NULL,
  `review_note` varchar(2000) DEFAULT NULL,
  `status` enum('APPROVED','CANCELLED','PENDING','REJECTED') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `employee_number` varchar(255) NOT NULL,
  `employment_type` enum('CONTRACT','FULL_TIME','PART_TIME') NOT NULL,
  `qualifications` varchar(255) DEFAULT NULL,
  `specialization` varchar(255) DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
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
  `name` varchar(255) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term`
--

LOCK TABLES `term` WRITE;
/*!40000 ALTER TABLE `term` DISABLE KEYS */;
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
  `grade` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `status` enum('CALCULATED','FINALISED') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `day_of_week` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timetable`
--

LOCK TABLES `timetable` WRITE;
/*!40000 ALTER TABLE `timetable` DISABLE KEYS */;
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
  `email` varchar(255) NOT NULL,
  `failed_login_attempts` int NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `locked_until` datetime(6) DEFAULT NULL,
  `must_change_password` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  KEY `FKdltbr5t0nljpuuo4isxgslt82` (`created_by`),
  KEY `FK2a54xhceitopkkw1hlo3tkv3i` (`updated_by`),
  CONSTRAINT `FK2a54xhceitopkkw1hlo3tkv3i` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKdltbr5t0nljpuuo4isxgslt82` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
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
  `role` enum('FINANCE_OFFICER','PARENT','PRINCIPAL','SCHOOL_ADMIN','STUDENT','SYSTEM_ADMIN','TEACHER') NOT NULL,
  PRIMARY KEY (`user_id`,`role`),
  CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_school`
--

LOCK TABLES `user_school` WRITE;
/*!40000 ALTER TABLE `user_school` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_school` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'phahamo_db'
--

--
-- Dumping routines for database 'phahamo_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-07 14:50:00
