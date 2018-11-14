create database if not exists etlapp;

create table if not exists etlapp.games (product_id varchar(255) primary key,
 product_name varchar(255),
 product_category varchar(255),
 product_price varchar(255),
 product_image_url varchar(255));

ALTER TABLE etlapp.games CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
SET NAMES 'UTF8';
SET CHARSET 'UTF8';