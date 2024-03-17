DROP DATABASE IF EXISTS computer_store;

CREATE DATABASE computer_store;

USE computer_store;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(50) NOT NULL,
  password CHAR(68) NOT NULL,
  first_name VARCHAR(30) DEFAULT NULL,
  last_name VARCHAR(30) DEFAULT NULL,
  country VARCHAR(50) DEFAULT NULL,
  address VARCHAR(150) DEFAULT NULL,
  phone_number VARCHAR(20) DEFAULT NULL,
  enabled TINYINT NOT NULL,
  verified_profile TINYINT NOT NULL,
  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

INSERT INTO users 
(email,password,first_name,last_name,country,address,phone_number,enabled,verified_profile)
VALUES 
('abv@abv.bg','{bcrypt}$2a$10$qeS0HEh7urweMojsnwNAR.vcXJeXR1UcMRZ2WcGQl9YeuspUdgF.q','Martin','Atanasov', null,null,null,1,1),
('manager@abv.bg','{bcrypt}$2a$10$qeS0HEh7urweMojsnwNAR.vcXJeXR1UcMRZ2WcGQl9YeuspUdgF.q','Manager',null, null,null,null,1,1),
('admin@abv.bg','{bcrypt}$2a$10$qeS0HEh7urweMojsnwNAR.vcXJeXR1UcMRZ2WcGQl9YeuspUdgF.q','Admin',null, null,null,null,1,1);

CREATE TABLE authorities (
  email varchar(50) NOT NULL,
  authority varchar(50) NOT NULL,
  UNIQUE KEY authorities4_idx_1 (email, authority),
  CONSTRAINT authorities4_ibfk_1 FOREIGN KEY (email) REFERENCES users (email)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;