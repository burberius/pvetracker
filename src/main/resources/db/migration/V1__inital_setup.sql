---
-- ========================================================================
-- Eve Online PvE Tracker
-- ------------------------------------------------------------------------
-- Copyright (C) 2017 Jens Oberender <j.obi@troja.net>
-- ------------------------------------------------------------------------
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
-- ========================================================================
---
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
  `type` varchar(10) NOT NULL,
  `faction` varchar(15) DEFAULT NULL,
  `ded` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `outcome` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system` varchar(45) NOT NULL,
  `ship` varchar(45) NOT NULL,
  `start` datetime NOT NULL,
  `end` datetime DEFAULT NULL,
  `faction` tinyint(4) NOT NULL,
  `escalation` tinyint(4) NOT NULL,
  `bounty_value` double DEFAULT '0',
  `loot_value` double DEFAULT '0',
  `account_id` int(11) NOT NULL,
  `site_id` int(11),
  `site_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_outcome_site_idx` (`site_id`),
  KEY `fk_outcome_account_idx` (`account_id`),
  CONSTRAINT `fk_outcome_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`character_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_outcome_site` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `loot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL DEFAULT '1',
  `name` varchar(150) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` double NOT NULL,
  `outcome_id` bigint(20),
  PRIMARY KEY (`id`),
  KEY `fk_loot_outcome_idx` (`outcome_id`),
  CONSTRAINT `fk_loot_outcome` FOREIGN KEY (`outcome_id`) REFERENCES `outcome` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
