---
-- ====================================================
-- Eve Online PvE Tracker
-- ----------------------------------------------------
-- Copyright (C) 2017 Jens Oberender <j.obi@troja.net>
-- ----------------------------------------------------
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public
-- License along with this program.  If not, see
-- <http://www.gnu.org/licenses/gpl-3.0.html>.
-- ====================================================
---
-- MySQL dump 10.13  Distrib 5.7.19, for Linux (x86_64)
--
-- Host: localhost    Database: pve
-- ------------------------------------------------------
-- Server version	5.7.19-0ubuntu0.17.04.1

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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `character_id` int(11) NOT NULL,
  `character_name` varchar(255) DEFAULT NULL,
  `character_owner_hash` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (91522382,'Burbone Mijory','ywDT01YdK2Xpaco2rV94fgY7sTo=','2017-10-14 19:14:09','2017-10-14 21:49:20','JX32JqXcX7wmsbXG3N16aDNE5K991E3GhlJyPZvYgeXheS8rHIUQfqiuxVGxxRHy0');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loot`
--

DROP TABLE IF EXISTS `loot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `loot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL DEFAULT '1',
  `name` varchar(150) NOT NULL,
  `typeId` int(11) NOT NULL,
  `value` double NOT NULL,
  `outcome_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_loot_outcome_idx` (`outcome_id`),
  CONSTRAINT `fk_loot_outcome` FOREIGN KEY (`outcome_id`) REFERENCES `outcome` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loot`
--

LOCK TABLES `loot` WRITE;
/*!40000 ALTER TABLE `loot` DISABLE KEYS */;
/*!40000 ALTER TABLE `loot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `outcome`
--

DROP TABLE IF EXISTS `outcome`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `outcome` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system` varchar(45) NOT NULL,
  `ship` varchar(45) NOT NULL,
  `start` datetime NOT NULL,
  `end` datetime DEFAULT NULL,
  `faction` tinyint(4) NOT NULL,
  `escalation` tinyint(4) NOT NULL,
  `bountyValue` double DEFAULT '0',
  `lootValue` double DEFAULT '0',
  `account_id` int(11) NOT NULL,
  `site_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_outcome_site_idx` (`site_id`),
  KEY `fk_outcome_account_idx` (`account_id`),
  CONSTRAINT `fk_outcome_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`character_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_site` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outcome`
--

LOCK TABLES `outcome` WRITE;
/*!40000 ALTER TABLE `outcome` DISABLE KEYS */;
/*!40000 ALTER TABLE `outcome` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` VALUES (1,'1','inital setup','SQL','V1__inital_setup.sql',1036837196,'pve','2017-10-14 17:05:15',48,1);
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site`
--

DROP TABLE IF EXISTS `site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `type` varchar(10) NOT NULL,
  `faction` varchar(15) DEFAULT NULL,
  `ded` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site`
--

LOCK TABLES `site` WRITE;
/*!40000 ALTER TABLE `site` DISABLE KEYS */;
INSERT INTO `site` VALUES (1,'Angel Burrow','ANOMALY','ANGEL_CARTEL',0),(2,'Angel Den','ANOMALY','ANGEL_CARTEL',0),(3,'Angel Forlorn Den','ANOMALY','ANGEL_CARTEL',0),(4,'Angel Forlorn Hideaway','ANOMALY','ANGEL_CARTEL',0),(5,'Angel Forlorn Hub','ANOMALY','ANGEL_CARTEL',0),(6,'Angel Forlorn Rally Point','ANOMALY','ANGEL_CARTEL',0),(7,'Angel Forsaken Den','ANOMALY','ANGEL_CARTEL',0),(8,'Angel Forsaken Hideaway','ANOMALY','ANGEL_CARTEL',0),(9,'Angel Forsaken Hub','ANOMALY','ANGEL_CARTEL',0),(10,'Angel Forsaken Rally Point','ANOMALY','ANGEL_CARTEL',0),(11,'Angel Haven','ANOMALY','ANGEL_CARTEL',0),(12,'Angel Hidden Den','ANOMALY','ANGEL_CARTEL',0),(13,'Angel Hidden Hideaway','ANOMALY','ANGEL_CARTEL',0),(14,'Angel Hidden Hub','ANOMALY','ANGEL_CARTEL',0),(15,'Angel Hidden Rally Point','ANOMALY','ANGEL_CARTEL',0),(16,'Angel Hideaway','ANOMALY','ANGEL_CARTEL',0),(17,'Angel Hub','ANOMALY','ANGEL_CARTEL',0),(18,'Angel Port','ANOMALY','ANGEL_CARTEL',0),(19,'Angel Rally Point','ANOMALY','ANGEL_CARTEL',0),(20,'Angel Refuge','ANOMALY','ANGEL_CARTEL',0),(21,'Angel Sanctum','ANOMALY','ANGEL_CARTEL',0),(22,'Angel Yard','ANOMALY','ANGEL_CARTEL',0),(23,'Central Angel Command Center','DATA','ANGEL_CARTEL',0),(24,'Central Angel Data Mining Site','DATA','ANGEL_CARTEL',0),(25,'Central Angel Sparking Transmitter','DATA','ANGEL_CARTEL',0),(26,'Central Angel Survey Site','DATA','ANGEL_CARTEL',0),(27,'Local Angel Backup Server','DATA','ANGEL_CARTEL',0),(28,'Local Angel Data Processing Center','DATA','ANGEL_CARTEL',0),(29,'Local Angel Data Terminal','DATA','ANGEL_CARTEL',0),(30,'Local Angel Mainframe','DATA','ANGEL_CARTEL',0),(31,'Local Angel Minor Shipyard','DATA','ANGEL_CARTEL',0),(32,'Local Angel Production Installation','DATA','ANGEL_CARTEL',0),(33,'Local Angel Shattered Life-Support Unit','DATA','ANGEL_CARTEL',0),(34,'Local Angel Virus Test Site','DATA','ANGEL_CARTEL',0),(35,'Regional Angel Backup Server','DATA','ANGEL_CARTEL',0),(36,'Regional Angel Command Center','DATA','ANGEL_CARTEL',0),(37,'Regional Angel Data Fortress','DATA','ANGEL_CARTEL',0),(38,'Regional Angel Data Mining Site','DATA','ANGEL_CARTEL',0),(39,'Regional Angel Data Processing Center','DATA','ANGEL_CARTEL',0),(40,'Regional Angel Data Terminal','DATA','ANGEL_CARTEL',0),(41,'Regional Angel Mainframe','DATA','ANGEL_CARTEL',0),(42,'Regional Angel Secure Server','DATA','ANGEL_CARTEL',0),(43,'Angel Domination Fleet Staging Point 2+3','ESCALATION','ANGEL_CARTEL',0),(44,'Angel Mineral Acquisition Outpost Part 2','ESCALATION','ANGEL_CARTEL',0),(45,'Angel Owned Station','ESCALATION','ANGEL_CARTEL',0),(46,'Angel Powergrid','ESCALATION','ANGEL_CARTEL',0),(47,'Blue Pill','ESCALATION','ANGEL_CARTEL',0),(48,'Chasing the Dragon','ESCALATION','ANGEL_CARTEL',0),(49,'Domination Surveillance Squad','ESCALATION','ANGEL_CARTEL',0),(50,'Operation Spring Cleaning','ESCALATION','ANGEL_CARTEL',0),(51,'Pioneers Peril','ESCALATION','ANGEL_CARTEL',0),(52,'Salvation Angel\'s Shipment','ESCALATION','ANGEL_CARTEL',0),(53,'Special Forces','ESCALATION','ANGEL_CARTEL',0),(54,'The Big Blue','ESCALATION','ANGEL_CARTEL',0),(55,'The Nuclear Small Arms Project','ESCALATION','ANGEL_CARTEL',0),(56,'Toxic Waste Scandal!','ESCALATION','ANGEL_CARTEL',0),(57,'Angel Chemical Lab','GAS','ANGEL_CARTEL',0),(58,'Angel Gas Processing Site','GAS','ANGEL_CARTEL',0),(59,'Arctic Fox Nebula','GAS','ANGEL_CARTEL',0),(60,'Boisterous Nebula','GAS','ANGEL_CARTEL',0),(61,'Cardinal Nebula','GAS','ANGEL_CARTEL',0),(62,'Crystal Nebula','GAS','ANGEL_CARTEL',0),(63,'Eagle Nebula','GAS','ANGEL_CARTEL',0),(64,'Elohim Sooth Sayer Distribution Base','GAS','ANGEL_CARTEL',0),(65,'Elohim Sooth Sayer Production Facility','GAS','ANGEL_CARTEL',0),(66,'Elohim X-Instinct Distribution Base','GAS','ANGEL_CARTEL',0),(67,'Elohim X-Instinct Production Facility','GAS','ANGEL_CARTEL',0),(68,'Flame Nebula','GAS','ANGEL_CARTEL',0),(69,'Ghost Nebula','GAS','ANGEL_CARTEL',0),(70,'Glistening Nebula','GAS','ANGEL_CARTEL',0),(71,'Murky Nebula','GAS','ANGEL_CARTEL',0),(72,'Pipe Nebula','GAS','ANGEL_CARTEL',0),(73,'Rapture Nebula','GAS','ANGEL_CARTEL',0),(74,'Red Dragonfly Nebula','GAS','ANGEL_CARTEL',0),(75,'Saintly Nebula','GAS','ANGEL_CARTEL',0),(76,'Snowy Owl Nebula','GAS','ANGEL_CARTEL',0),(77,'Vaporous Nebula','GAS','ANGEL_CARTEL',0),(78,'Wispy Nebula','GAS','ANGEL_CARTEL',0),(79,'Small Angel Covert Research Facility','GHOST','ANGEL_CARTEL',0),(80,'Standard Angel Covert Research Facility','GHOST','ANGEL_CARTEL',0),(81,'Crumbling Angel Abandoned Colony','RELIC','ANGEL_CARTEL',0),(82,'Crumbling Angel Antiquated Outpost','RELIC','ANGEL_CARTEL',0),(83,'Crumbling Angel Crystal Quarry','RELIC','ANGEL_CARTEL',0),(84,'Crumbling Angel Excavation','RELIC','ANGEL_CARTEL',0),(85,'Crumbling Angel Explosive Debris','RELIC','ANGEL_CARTEL',0),(86,'Crumbling Angel Mining Installation','RELIC','ANGEL_CARTEL',0),(87,'Crumbling Angel Solar Harvesters','RELIC','ANGEL_CARTEL',0),(88,'Crumbling Angel Stone Formation','RELIC','ANGEL_CARTEL',0),(89,'Decayed Angel Collision Site','RELIC','ANGEL_CARTEL',0),(90,'Decayed Angel Excavation','RELIC','ANGEL_CARTEL',0),(91,'Decayed Angel Lone Vessel','RELIC','ANGEL_CARTEL',0),(92,'Decayed Angel Mass Grave','RELIC','ANGEL_CARTEL',0),(93,'Decayed Angel Mining Installation','RELIC','ANGEL_CARTEL',0),(94,'Decayed Angel Particle Accelerator','RELIC','ANGEL_CARTEL',0),(95,'Decayed Angel Quarry','RELIC','ANGEL_CARTEL',0),(96,'Decayed Angel Rock Formations','RELIC','ANGEL_CARTEL',0),(97,'Ruined Angel Crystal Quarry','RELIC','ANGEL_CARTEL',0),(98,'Ruined Angel Monument Site','RELIC','ANGEL_CARTEL',0),(99,'Ruined Angel Science Outpost','RELIC','ANGEL_CARTEL',0),(100,'Ruined Angel Temple Site','RELIC','ANGEL_CARTEL',0),(101,'Angel Annex','SIGNATURE','ANGEL_CARTEL',0),(102,'Angel Base','SIGNATURE','ANGEL_CARTEL',0),(103,'Angel Cartel Naval Shipyard','SIGNATURE','ANGEL_CARTEL',10),(104,'Angel Cartel Occupied Mining Colony','SIGNATURE','ANGEL_CARTEL',4),(105,'Angel Creo-Corp Mining','SIGNATURE','ANGEL_CARTEL',2),(106,'Angel Domination Fleet Staging Point','SIGNATURE','ANGEL_CARTEL',0),(107,'Angel Fortress','SIGNATURE','ANGEL_CARTEL',0),(108,'Angel Hideout','SIGNATURE','ANGEL_CARTEL',0),(109,'Angel Lookout','SIGNATURE','ANGEL_CARTEL',0),(110,'Angel Military Complex','SIGNATURE','ANGEL_CARTEL',0),(111,'Angel Military Operations Complex','SIGNATURE','ANGEL_CARTEL',7),(112,'Angel Mineral Acquisition Outpost','SIGNATURE','ANGEL_CARTEL',0),(113,'Angel Outpost','SIGNATURE','ANGEL_CARTEL',0),(114,'Angel Provincial HQ','SIGNATURE','ANGEL_CARTEL',0),(115,'Angel Repurposed Outpost','SIGNATURE','ANGEL_CARTEL',3),(116,'Angel Vigil','SIGNATURE','ANGEL_CARTEL',0),(117,'Angel Watch','SIGNATURE','ANGEL_CARTEL',0),(118,'ANGEL_CARTEL\'s Red Light District','SIGNATURE','ANGEL_CARTEL',5),(119,'Bloated Ruins','SIGNATURE','ANGEL_CARTEL',0),(120,'Cartel Prisoner Retention','SIGNATURE','ANGEL_CARTEL',8),(121,'Digital Matrix','SIGNATURE','ANGEL_CARTEL',0),(122,'Digital Network','SIGNATURE','ANGEL_CARTEL',0),(123,'Minecore Harvester Depot','SIGNATURE','ANGEL_CARTEL',0),(124,'Minmatar Contracted Bio-Farm','SIGNATURE','ANGEL_CARTEL',1),(125,'Minor Angel Annex','SIGNATURE','ANGEL_CARTEL',0),(126,'Provisional Angel Outpost','SIGNATURE','ANGEL_CARTEL',0),(127,'Wispy Ruins','SIGNATURE','ANGEL_CARTEL',0);
/*!40000 ALTER TABLE `site` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-10-15 22:06:03
