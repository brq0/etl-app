create database if not exists etlapp;

CREATE TABLE if not exists `etlapp`.`producers` (
  `id` int NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_polish_ci;

CREATE TABLE if not exists `etlapp`.`pegi_codes` (
  `id` INT NOT NULL,
  `img_url` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_polish_ci;

CREATE TABLE if not exists `etlapp`.`categories` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_polish_ci;

CREATE TABLE `etlapp`.`games` (
  `id` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `category_id` INT NOT NULL,
  `price` VARCHAR(255) NOT NULL,
  `img_url` VARCHAR(255) NOT NULL,
  `position` INT NOT NULL,
  `description` LONGTEXT NOT NULL,
  `producer_id` INT NOT NULL,
  `release_date` DATE NOT NULL,
  `pegi_code_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `category_id`
    FOREIGN KEY (`category_id`)
    REFERENCES `etlapp`.`categories` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `producer_id`
    FOREIGN KEY (`producer_id`)
    REFERENCES `etlapp`.`producers` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `pegi_code_id`
    FOREIGN KEY (`pegi_code_id`)
    REFERENCES `etlapp`.`pegi_codes` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_polish_ci;


ALTER TABLE etlapp.games CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
SET NAMES 'UTF8';
SET CHARSET 'UTF8';
