DROP DATABASE IF EXISTS computer_store;

CREATE DATABASE computer_store;

USE computer_store;

-- Create table users with encoding utf-8
-- Check Spring security documentation for more info
-- https://docs.spring.io/spring-security/site/docs/4.0.1.RELEASE/reference/html/appendix-schema.html

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(50) NOT NULL,
  password CHAR(60) NOT NULL,
  first_name VARCHAR(30) DEFAULT NULL,
  last_name VARCHAR(30) DEFAULT NULL,
  country VARCHAR(50) DEFAULT NULL,
  address VARCHAR(150) DEFAULT NULL,
  phone_number VARCHAR(20) DEFAULT NULL,
  enabled TINYINT NOT NULL,
  verified_profile TINYINT NOT NULL,
  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Enter some test users
-- The test password is 'test'
-- You could create your own password from here https://www.bcryptcalculator.com/

INSERT INTO users 
(email, password, first_name, last_name, country, address, phone_number, enabled, verified_profile)
VALUES 
('abv@abv.bg','$2a$10$xEbmetHDZXteGarC57W/h.bMCpBADz/k9ENbRhiXnHlLpyOIG4FEK','Мартин','Атанасов', null,null,null,1,1),
('manager@abv.bg','$2a$10$xEbmetHDZXteGarC57W/h.bMCpBADz/k9ENbRhiXnHlLpyOIG4FEK','инж Киров',null, null,null,null,1,1),
('admin@abv.bg','$2a$10$xEbmetHDZXteGarC57W/h.bMCpBADz/k9ENbRhiXnHlLpyOIG4FEK','the BOSS',null, null,null,null,1,1);

-- Create table authorities 

CREATE TABLE authorities (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  authority varchar(50) NOT NULL,
  constraint fk_authorities_users foreign key(id) references users(id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
create unique index ix_auth_username on authorities (id, authority);

-- Insert base roles

INSERT INTO authorities (authority)
VALUES 
('ROLE_CUSTOMER'),('ROLE_MANAGER'),('ROLE_ADMIN');

CREATE TABLE users_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  
  PRIMARY KEY (user_id, role_id),
  
  KEY FK_ROLE_idx (role_id),
  
  CONSTRAINT FK_USER_05 FOREIGN KEY (user_id) 
  REFERENCES users (id) 
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  
  CONSTRAINT FK_ROLE FOREIGN KEY (role_id) 
  REFERENCES authorities (id) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Set authorities to the base users

INSERT INTO users_roles (user_id,role_id)
VALUES 
(1, 1),
(2, 1),
(2, 2),
(3, 1),
(3, 2),
(3, 3)




