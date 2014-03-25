SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `travo` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;

USE `travo`;

CREATE  TABLE IF NOT EXISTS `travo`.`user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `email` VARCHAR(25) NULL  ,
  `token` CHAR(32) NOT NULL ,
  `qq_user_id` CHAR(32) NULL,
  `sina_user_id` VARCHAR(20) NULL,
  `password` VARCHAR(16) NULL ,
  `register_time` DATETIME NOT NULL ,
  `nickname` VARCHAR(16) NOT NULL ,
  `face_path` CHAR(24) NULL DEFAULT NULL ,
  `signature` VARCHAR(70) NULL DEFAULT NULL ,
  `account` INT(11) NOT NULL DEFAULT 0 ,
  `travel_qty` INT(11) NOT NULL DEFAULT 0 ,
  `scenic_point_qty` INT(11) NOT NULL DEFAULT 0 ,
  `achievement_qty` INT(11) NOT NULL DEFAULT 0 ,
  `follower_qty` INT(11) NOT NULL DEFAULT 0 ,
  `favorite_travel_qty` INT(11) NOT NULL DEFAULT 0 ,
  `is_location_public` TINYINT(1) NOT NULL DEFAULT FALSE,
  `is_info_public` TINYINT(1) NOT NULL DEFAULT FALSE,
  `lm_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  UNIQUE INDEX `nickname_UNIQUE` (`nickname` ASC),
  UNIQUE INDEX `token_UNIQUE` (`token` ASC),
  UNIQUE INDEX `qq_user_id_UNIQUE` (`qq_user_id` ASC),
  UNIQUE INDEX `sina_user_id_UNIQUE` (`sina_user_id` ASC))
  
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`user_info` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `phone` CHAR(12) NULL DEFAULT NULL ,
  `mobile` CHAR(11) NULL DEFAULT NULL ,
  `qq`   VARCHAR(12) NULL DEFAULT NULL ,
  `sina_blog` VARCHAR(25) NULL DEFAULT NULL,
  `name` CHAR(4) NULL DEFAULT NULL ,
  `age` TINYINT(3) UNSIGNED NULL DEFAULT NULL ,
  `sex` ENUM('男','女') NULL DEFAULT NULL ,
  `address` INT(11) NULL DEFAULT NULL ,
  `address2` INT(11) NULL DEFAULT NULL ,
  `native_place` INT(11) NULL DEFAULT NULL ,
  `degree` CHAR(6) NULL DEFAULT NULL ,
  `job` VARCHAR(15) NULL DEFAULT NULL ,
  `lm_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) ,
  INDEX `fk_address_idx` (`address` ASC) ,
  INDEX `fk_address2_idx` (`address2` ASC) ,
  INDEX `fk_native_place_idx` (`native_place` ASC) ,
  CONSTRAINT `fk_address`
    FOREIGN KEY (`address` )
    REFERENCES `travo`.`address` (`id` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_address2`
    FOREIGN KEY (`address2` )
    REFERENCES `travo`.`address` (`id` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_native_province`
    FOREIGN KEY (`native_place` )
    REFERENCES `travo`.`province` (`id` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_contact_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`province` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` CHAR(3) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`city` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(15) NOT NULL ,
  `area_code` CHAR(4) NOT NULL ,
  `province_id` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_province_idx` (`province_id` ASC) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  CONSTRAINT `fk_province`
    FOREIGN KEY (`province_id` )
    REFERENCES `travo`.`province` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`district` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(15) NOT NULL ,
  `zip` CHAR(6) NOT NULL ,
  `city_id` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_city_idx` (`city_id` ASC) ,
  CONSTRAINT `fk_city`
    FOREIGN KEY (`city_id` )
    REFERENCES `travo`.`city` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`address` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `address` VARCHAR(20) NOT NULL ,
  `district_id` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_district_idx` (`district_id` ASC) ,
  CONSTRAINT `fk_district`
    FOREIGN KEY (`district_id` )
    REFERENCES `travo`.`district` (`id` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`follow` (
  `id` INT(11) NULL,
  `active` INT(11) NOT NULL ,
  `passive` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `action` ENUM('0','1') NOT NULL ,
  PRIMARY KEY (`active`, `passive`, `time`) ,
  INDEX `fk_active_user_idx` (`active` ASC) ,
  INDEX `fk_passive_user_idx` (`passive` ASC) ,
  CONSTRAINT `fk_active_user`
    FOREIGN KEY (`active` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_passive_user`
    FOREIGN KEY (`passive` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`login_record` (
  `user_id` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `ip` CHAR(15) NOT NULL ,
  PRIMARY KEY (`user_id`, `time`) ,
  INDEX `fk_user_idx` (`user_id` ASC) ,
  CONSTRAINT `fk_login_record_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`location` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(45) NULL,
  `longitude` FLOAT(17,14) NOT NULL ,
  `latitude` FLOAT(17,14) NOT NULL ,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`scenic_point` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `longitude` FLOAT(17,14) NOT NULL ,
  `latitude` FLOAT(17,14) NOT NULL ,
  `price` DECIMAL(7,2) NULL DEFAULT NULL ,
  `description` VARCHAR(300) NULL DEFAULT NULL ,
  `scenic_area_id` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_scenic_area_idx` (`scenic_area_id` ASC) ,
  CONSTRAINT `fk_sp_scenic_area`
    FOREIGN KEY (`scenic_area_id` )
    REFERENCES `travo`.`scenic_area` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`scenic_area` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(300) NULL DEFAULT NULL ,
  `price` DECIMAL(7,2) NULL DEFAULT NULL ,
  `address_id` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_address_idx` (`address_id` ASC) ,
  CONSTRAINT `fk_sa_address`
    FOREIGN KEY (`address_id` )
    REFERENCES `travo`.`address` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`scenic_point_info` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `content` VARCHAR(300) NULL DEFAULT NULL ,
  `image_path` CHAR(25) NULL DEFAULT NULL ,
  `image_explain` VARCHAR(300) NULL DEFAULT NULL ,
  `scenic_point_id` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_scenic_point_idx` (`scenic_point_id` ASC) ,
  CONSTRAINT `fk_spi_scenic_point`
    FOREIGN KEY (`scenic_point_id` )
    REFERENCES `travo`.`scenic_point` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`travel` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(11) NOT NULL ,
  `title` VARCHAR(45) NOT NULL ,
  `cover_path` VARCHAR(24) NULL ,
  `destination` VARCHAR(45) NOT NULL ,
  `begin_date` DATE NOT NULL ,
  `end_date` DATE NULL DEFAULT NULL ,
  `average_spend` VARCHAR(20) NULL DEFAULT NULL ,
  `description` VARCHAR(4096) NULL DEFAULT NULL ,
  `create_time` DATETIME NOT NULL ,
  `comment_qty` INT(11) NOT NULL DEFAULT 0 ,
  `vote_qty` INT(11) NOT NULL DEFAULT 0 ,
  `favorite_qty` INT(11) NOT NULL DEFAULT 0 ,
  `read_times` INT(11) NOT NULL DEFAULT 0 ,
  `is_public` TINYINT(1) NOT NULL DEFAULT true ,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT false ,
  `lm_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) ,
  INDEX `fk_user_idx` (`user_id` ASC) ,
  CONSTRAINT `fk_travel_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`note` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL ,
  `travel_id` INT(11) NOT NULL ,
  `location_id` INT(11) NULL,
  `create_time` DATETIME NOT NULL,
  `content` VARCHAR(2048) NULL DEFAULT NULL ,
  `image_path` CHAR(24) NULL DEFAULT NULL,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT false ,
  `lm_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) ,
  INDEX `fk_user_idx` (`user_id` ASC) ,
  INDEX `fk_travel_idx` (`travel_id` ASC) ,
  INDEX `fk_location_idx` (`location_id` ASC),
  CONSTRAINT `fk_note_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_note_travel`
    FOREIGN KEY (`travel_id` )
    REFERENCES `travo`.`travel` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_note_location`
	FOREIGN KEY (`location_id`)
	REFERENCES `travo`.`location` (`id`)
	ON DELETE NO ACTION
	ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`note_comment` (
  `note_id` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `commenter` INT(11) NOT NULL ,
  `content` VARCHAR(200) NOT NULL ,
  PRIMARY KEY (`note_id`, `time`) ,
  INDEX `fk_note_idx` (`note_id` ASC) ,
  INDEX `fk_commenter_idx` (`commenter` ASC) ,
  CONSTRAINT `fk_nc_note`
    FOREIGN KEY (`note_id` )
    REFERENCES `travo`.`note` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_nc_commenter`
    FOREIGN KEY (`commenter` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`note_vote` (
  `note_id` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `voter` INT(11) NOT NULL ,
  PRIMARY KEY (`note_id`, `time`) ,
  INDEX `fk_note_idx` (`note_id` ASC) ,
  INDEX `fk_voter_idx` (`voter` ASC) ,
  CONSTRAINT `fk_nv_note`
    FOREIGN KEY (`note_id` )
    REFERENCES `travo`.`note` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_nv_voter`
    FOREIGN KEY (`voter` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`travel_plan` (
  `user_id` INT(11) NOT NULL ,
  `publish_time` DATETIME NOT NULL,
  `title` VARCHAR(70) NOT NULL ,
  `begin_date` DATE NOT NULL ,
  `end_date` DATE NOT NULL ,
  `destination` VARCHAR(45) NOT NULL ,
  `transport` VARCHAR(45) NULL DEFAULT NULL ,
  `publisher_name` VARCHAR(10) NULL DEFAULT NULL ,
  `publisher_sex` ENUM('男','女') NULL DEFAULT NULL ,
  `publisher_age` TINYINT(3) UNSIGNED NULL DEFAULT NULL ,
  `publisher_member` VARCHAR(12) NULL DEFAULT NULL ,
  `hope_sex` ENUM('男','女') NULL DEFAULT NULL ,
  `hope_age` VARCHAR(12) NULL DEFAULT NULL ,
  `hope_member` VARCHAR(12) NULL DEFAULT NULL ,
  `mobile` CHAR(11) NULL DEFAULT NULL ,
  `qq` VARCHAR(12) NULL DEFAULT NULL ,
  `conment` VARCHAR(200) NULL DEFAULT NULL ,
  `lm_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `publish_time`) ,
  INDEX `fk_user_idx` (`user_id` ASC) ,
  CONSTRAINT `fk_tp_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`achievement` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NOT NULL ,
  `scenic_point_qty` SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`user_scenic_point` (
  `user_id` INT(11) NOT NULL ,
  `scenic_point_id` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`user_id`, `scenic_point_id`, `time`) ,
  INDEX `fk_user_idx` (`user_id` ASC) ,
  INDEX `fk_scenic_point_idx` (`scenic_point_id` ASC) ,
  CONSTRAINT `fk_usp_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usp_scenic_point`
    FOREIGN KEY (`scenic_point_id` )
    REFERENCES `travo`.`scenic_point` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`achievement_scenic_point` (
  `achievement_id` INT(11) NOT NULL ,
  `scenic_point_id` INT(11) NOT NULL ,
  PRIMARY KEY (`achievement_id`, `scenic_point_id`) ,
  INDEX `fk_achievement_idx` (`achievement_id` ASC) ,
  INDEX `fk_scenic_point_idx` (`scenic_point_id` ASC) ,
  CONSTRAINT `fk_asp_achievement`
    FOREIGN KEY (`achievement_id` )
    REFERENCES `travo`.`achievement` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_asp_scenic_point`
    FOREIGN KEY (`scenic_point_id` )
    REFERENCES `travo`.`scenic_point` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`user_achievement` (
  `user_id` INT(11) NOT NULL ,
  `achievement_id` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`user_id`, `achievement_id`) ,
  INDEX `fk_user_idx` (`user_id` ASC) ,
  INDEX `fk_achievement_idx` (`achievement_id` ASC) ,
  CONSTRAINT `fk_ua_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ua_achievement`
    FOREIGN KEY (`achievement_id` )
    REFERENCES `travo`.`achievement` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`travel_comment` (
  `id` INT(11) NULL,
  `travel_id` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `commenter` INT(11) NOT NULL ,
  `content` VARCHAR(200) NOT NULL ,
  PRIMARY KEY (`travel_id`, `time`) ,
  INDEX `fk_travel_idx` (`travel_id` ASC) ,
  INDEX `fk_commenter_idx` (`commenter` ASC) ,
  CONSTRAINT `fk_tc_travel`
    FOREIGN KEY (`travel_id` )
    REFERENCES `travo`.`travel` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_tc_commenter`
    FOREIGN KEY (`commenter` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`favorite_travel` (
  `user_id` INT(11) NOT NULL ,
  `travel_id` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`user_id`, `travel_id`) ,
  INDEX `fk_user_idx` (`user_id` ASC) ,
  INDEX `fk_travel_idx` (`travel_id` ASC) ,
  CONSTRAINT `fk_ft_user`
    FOREIGN KEY (`user_id` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ft_travel`
    FOREIGN KEY (`travel_id` )
    REFERENCES `travo`.`travel` (`id` )
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`travel_read_log` (
  `id` INT(11) NULL,
  `travel_id` INT(11) NOT NULL,
  `reader`  INT(11) NOT NULL,
  `time`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(`travel_id`, `time`),
  INDEX `fk_travel_idx` (`travel_id` ASC),
  INDEX `fk_reader_idx` (`reader` ASC),
  CONSTRAINT `fk_trl_travel`
    FOREIGN KEY (`travel_id` )
    REFERENCES `travo`.`travel` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_trl_reader`
    FOREIGN KEY (`reader` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE  TABLE IF NOT EXISTS `travo`.`travel_vote` (
  `travel_id` INT(11) NOT NULL ,
  `voter` INT(11) NOT NULL ,
  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`travel_id`, `voter`) ,
  INDEX `fk_travel_idx` (`travel_id` ASC) ,
  INDEX `fk_voter_idx` (`voter` ASC) ,
  CONSTRAINT `fk_tv_travel`
    FOREIGN KEY (`travel_id` )
    REFERENCES `travo`.`travel` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_tv_voter`
    FOREIGN KEY (`voter` )
    REFERENCES `travo`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


CREATE  TABLE IF NOT EXISTS `travo`.`error_log` (
  `time` TIMESTAMP NOT NULL ,
  `position` VARCHAR(45) NOT NULL ,
  `message` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`time`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

-- -----------------------------------------------------
-- Placeholder table for view `travo`.`complete_address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `travo`.`complete_address` (`address_id` INT, `province` INT, `city` INT, `district` INT, `address` INT, `area_code` INT, `zip` INT);


USE `travo`;

-- -----------------------------------------------------
-- View `travo`.`complete_address`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `travo`.`complete_address`;
USE `travo`;
CREATE  OR REPLACE VIEW `travo`.`complete_address` AS
SELECT a.id, p.name AS province, c.name AS city, d.name AS district, address, c.area_code, d.zip
FROM province p
JOIN city AS c
	ON p.id = c.province_id
JOIN district AS d
	ON c.id = d.city_id
RIGHT JOIN address a
	ON a.district_id = d.id
;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
