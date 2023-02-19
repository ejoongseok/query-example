INSERT INTO person(id,first_name,last_name) VALUES (1,'John','Doe');
INSERT INTO person(id,first_name,last_name) VALUES (2,'Doe','Won');
INSERT INTO address(id,person_id,state,city,street,zip_code)
VALUES (1,1,'CA', 'Los Angeles', 'Standford Ave', '90001');
INSERT INTO address(id,person_id,state,city,street,zip_code)
VALUES (2,2,'CA', 'Los Angeles', 'Standford Ave', '90002');