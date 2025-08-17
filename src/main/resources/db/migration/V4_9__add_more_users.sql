INSERT INTO users
(email, password, first_name, last_name, country, address, phone_number, customer_id, attempts, enabled, verified_profile)
VALUES
('manager@gmail.com','$2a$10$xEbmetHDZXteGarC57W/h.bMCpBADz/k9ENbRhiXnHlLpyOIG4FEK','Manager', 'Only', null,null,null,null,0,1,1),
('admin@gmail.com','$2a$10$xEbmetHDZXteGarC57W/h.bMCpBADz/k9ENbRhiXnHlLpyOIG4FEK','Admin','Only', null,null,null,null,0,1,1);


INSERT INTO users_roles (user_id,role_id)
VALUES
(4, 2),
(5, 3);
