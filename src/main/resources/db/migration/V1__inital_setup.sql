CREATE TABLE `account` (
  `character_id` int(11) NOT NULL,
  `character_name` varchar(255) DEFAULT NULL,
  `character_owner_hash` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
