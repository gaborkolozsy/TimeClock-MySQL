CREATE SCHEMA `TimeClock`;

CREATE TABLE `TimeClock`.`Developer` (
  `Developer_id` INT NOT NULL AUTO_INCREMENT,
  `First_name` VARCHAR(45) NULL,
  `Last_name` VARCHAR(45) NULL,
  PRIMARY KEY (`Developer_id`));

INSERT INTO Developer (First_name, Last_name) VALUES ('Gabor', 'Kolozsy');

CREATE TABLE `TimeClock`.`Job` (
  `Job_id` INT NOT NULL AUTO_INCREMENT,
  `Branch` VARCHAR(45) NULL,
  `Project` VARCHAR(45) NULL,
  `Package` VARCHAR(45) NULL,
  `Class` VARCHAR(45) NULL,
  `Job_number` VARCHAR(45) NULL,
  `Start_at` DATETIME NULL,
  `End_at` DATETIME NULL,
  `To_time` TIME(0) NULL,
  `Status` VARCHAR(45) NULL,
  `Comment` VARCHAR(45) NULL,
  `Developer_id` VARCHAR(45) NULL,
  `In_part` INT(11) NULL,
  PRIMARY KEY (`Job_id`));

CREATE TABLE `TimeClock`.`Pay` (
  `Pay_id` INT NOT NULL AUTO_INCREMENT,
  `Pay` DOUBLE NULL,
  `Currency` VARCHAR(45) NULL,
  PRIMARY KEY (`Job_id`));
