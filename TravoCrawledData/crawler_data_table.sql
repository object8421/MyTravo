CREATE TABLE IF NOT EXISTS `des_country` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `crawled_url` text,
  `background_description` text,
  `history_introduction` text,
  `geograpyic_info` text,
  `transportation_info` text,
  `proper_travel_time` text,
  `visa_info` text,
  `attention` text,
  `travel_advice` text,
  `image_path` text,
  `country_name` varchar(40) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `des_province` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `crawled_url` text,
  `background_description` text,
  `history_introduction` text,
  `geograpyic_info` text,
  `transportation_info` text,
  `proper_travel_time` text,
  `visa_info` text,
  `attention` text,
  `travel_advice` text,
  `image_path` text,
  `image_name` text,
  `country_id` int(11) NOT NULL,
  `province_name` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  index(`country_id`),
  CONSTRAINT `related_country` FOREIGN KEY (`country_id`) REFERENCES `des_country` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `des_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `crawled_url` text,
  `city_name` varchar(45) NOT NULL,
  `background_description` text DEFAULT NULL,
  `history_introduction` text DEFAULT NULL,
  `geograpyic_info` text DEFAULT NULL,
  `transportation_info` text DEFAULT NULL,
  `proper_travel_time` text DEFAULT NULL,
  `visa_info` text DEFAULT NULL,
  `attention` text DEFAULT NULL,
  `travel_advice` text DEFAULT NULL,
  `image_path` text DEFAULT NULL,
  `image_name` text DEFAULT NULL,
  `province_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  index (`province_id`),
  CONSTRAINT `related_province` FOREIGN KEY (`province_id`) REFERENCES `des_province` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `des_scenery_spot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `crawled_url` text,
  `spot_name` varchar(45) DEFAULT NULL,
  `ticket` text,
  `background_description` text,
  `history_introduction` text,
  `geograpyic_info` text,
  `transportation_info` text,
  `proper_travel_time` text,
  `visa_info` text,
  `attention` text,
  `travel_advice` text,
  `image_path` text,
  `city_id` int(11) NOT NULL,
  `image_name` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  index(`city_id`),
  CONSTRAINT `related_city` FOREIGN KEY (`city_id`) REFERENCES `des_city` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

