CREATE TABLE `account` (
  `character_id` int(11) NOT NULL,
  `character_name` varchar(255) DEFAULT NULL,
  `character_owner_hash` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `faction` varchar(20) DEFAULT NULL,
  `ded` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ship` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) DEFAULT NULL,
  `type` varchar(80) DEFAULT NULL,
  `type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_UNIQ` (`name`,`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `solar_system` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `security` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `outcome` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system_id` int(11) DEFAULT NULL,
  `ship_id` int(11) DEFAULT NULL,
  `start` datetime NOT NULL,
  `end` datetime DEFAULT NULL,
  `faction` tinyint(4) NOT NULL,
  `escalation` tinyint(4) NOT NULL,
  `bounty_value` bigint(20) DEFAULT '0',
  `reward_value` bigint(20) DEFAULT NULL,
  `loot_value` bigint(20) DEFAULT '0',
  `account_id` int(11) NOT NULL,
  `site_id` int(11) DEFAULT NULL,
  `site_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_outcome_site_idx` (`site_id`),
  KEY `fk_outcome_account_idx` (`account_id`),
  KEY `fk_outcome_ship` (`ship_id`),
  KEY `fk_outcome_system` (`system_id`),
  CONSTRAINT `fk_outcome_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`character_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_ship` FOREIGN KEY (`ship_id`) REFERENCES `ship` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_site` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_system` FOREIGN KEY (`system_id`) REFERENCES `solar_system` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `loot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL DEFAULT '1',
  `name` varchar(100) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` double NOT NULL,
  `outcome_id` bigint(20),
  PRIMARY KEY (`id`),
  KEY `fk_loot_outcome_idx` (`outcome_id`),
  CONSTRAINT `fk_loot_outcome` FOREIGN KEY (`outcome_id`) REFERENCES `outcome` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `type_translation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_id` int(11) NOT NULL,
  `language` varchar(2) DEFAULT NULL,
  `name` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_type_lang` (`type_id`,`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `price` (
  `type_id` int(11) NOT NULL,
  `value` double NOT NULL,
  `created` datetime NOT NULL,
  PRIMARY KEY (`type_id`),
  KEY `IDX_DATE` (`created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

