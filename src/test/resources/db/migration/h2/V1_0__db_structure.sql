CREATE TABLE `account` (
  `character_id` int NOT NULL,
  `character_name` varchar(255) DEFAULT NULL,
  `character_owner_hash` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`character_id`)
);

CREATE TABLE `site` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `faction` varchar DEFAULT NULL,
  `ded` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE (`name`)
);

CREATE TABLE `ship` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(80) DEFAULT NULL,
  `type` varchar(80) DEFAULT NULL,
  `type_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`name`,`type_id`)
);

CREATE TABLE `solar_system` (
  `id` int NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `security` double DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `outcome` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `system_id` int DEFAULT NULL,
  `ship_id` int DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `faction` tinyint NOT NULL,
  `escalation` tinyint NOT NULL,
  `bounty_value` bigint DEFAULT '0',
  `reward_value` bigint DEFAULT NULL,
  `loot_value` bigint DEFAULT '0',
  `account_id` int NOT NULL,
  `site_id` int DEFAULT NULL,
  `site_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_outcome_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`character_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_ship` FOREIGN KEY (`ship_id`) REFERENCES `ship` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_site` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_system` FOREIGN KEY (`system_id`) REFERENCES `solar_system` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE `loot` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `count` int NOT NULL DEFAULT '1',
  `name` varchar(100) NOT NULL,
  `type_id` int NOT NULL,
  `money` double NOT NULL,
  `outcome_id` bigint,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_loot_outcome` FOREIGN KEY (`outcome_id`) REFERENCES `outcome` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE `type_translation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_id` int NOT NULL,
  `language` varchar(2) DEFAULT NULL,
  `name` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `price` (
  `type_id` int NOT NULL,
  `money` double NOT NULL,
  `created` datetime NOT NULL,
  PRIMARY KEY (`type_id`)
);
