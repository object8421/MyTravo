use travo;

delimiter $$
##############################################
DROP PROCEDURE IF EXISTS sp_register; $$
CREATE PROCEDURE sp_register(IN _nickname varchar(16), IN _token char(32),
		IN _email varchar(25), IN _password varchar(16),
		IN _qq_user_id char(32), IN _sina_user_id varchar(20))
BEGIN
	INSERT INTO user(email, password, nickname, token, qq_user_id, sina_user_id,register_time)
	VALUES(_email, _password, _nickname, _token, _qq_user_id, _sina_user_id,getdate());
	
	SELECT LAST_INSERT_ID();
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_record_login; $$
CREATE PROCEDURE sp_record_login(IN _user_id int, IN _ip char(15))
BEGIN
	INSERT INTO login_record(user_id, ip) VALUES(_user_Id, _ip);
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_travo_login; $$
CREATE PROCEDURE sp_travo_login(IN _email varchar(25), IN _password varchar(16),
		IN _token char(32), OUT code INT)
BEGIN
	DECLARE password varchar(16) DEFAULT NULL;
	DECLARE _user_id INT DEFAULT 0;

	SELECT user_id, u.password INTO _user_id, password
	FROM user u WHERE email = _email;
	
	IF _user_id = 0 THEN
		SET code = -1;
	ELSE
		IF NOT password = _password THEN
			SET code = -2;
		ELSE
			UPDATE user SET token = _token WHERE user_id = _user_id;
			CALL sp_get_user(_user_id);
		END IF;
	END IF;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_qq_login; $$
CREATE PROCEDURE sp_qq_login(IN _qq_user_id char(32), IN _token char(32),
		OUT code INT)
BEGIN 
	DECLARE _user_id INT DEFAULT 0;
	SELECT user_id INTO _user_id
	FROM user WHERE qq_user_id = _qq_user_id;
	IF _user_id = 0 THEN
		SET code = -1;
	ELSE
		UPDATE user SET token = _token WHERE user_id = _user_id;
		CALL sp_get_user(_user_id);
	END IF;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_sina_login; $$
CREATE PROCEDURE sp_sina_login(IN _sina_user_id varchar(20), IN _token char(32),
		OUT code INT)
BEGIN 
	DECLARE _user_id INT DEFAULT 0;
	SELECT user_id INTO _user_id
	FROM user WHERE sina_user_id = _sina_user_id;
	IF _user_id = 0 THEN
		SET code = -1;
	ELSE
		UPDATE user SET token = _token WHERE user_id = _user_id;
		CALL sp_get_user(_user_id);
	END IF;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_bind_open_user; $$
CREATE PROCEDURE sp_bind_open_user(IN _user_id int, IN _user_type varchar(4),
		IN _open_id CHAR(32), OUT code INT)
BEGIN
	DECLARE open_id CHAR(32) DEFAULT NULL;

	CASE _user_type
		WHEN 'sina' THEN
		BEGIN
			SELECT sina_user_id INTO open_id FROM user WHERE sina_user_id = _open_id;
			IF NOT open_id IS NULL THEN
				SET code = -1;
			ELSE
				UPDATE user SET sina_user_id = _open_id WHERE user_id = _user_id;
			END IF;
		END;
		WHEN 'qq' THEN
		BEGIN
			SELECT qq_user_id INTO open_id FROM user WHERE qq_user_id = _open_id;
			IF NOT open_id IS NULL THEN
				SET code = -1;
			ELSE
				UPDATE user SET qq_user_id = _open_id WHERE user_id = _user_id;
			END IF;
		END;
	END CASE;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_update_email; $$
CREATE PROCEDURE sp_update_email(IN _user_id int, IN _email VARCHAR(25),
		IN _password VARCHAR(16), OUT code TINYINT)
BEGIN
	DECLARE password VARCHAR(16) DEFAULT NULL;
	SELECT u.password INTO password FROM user u WHERE u.user_id = _user_id;
	IF NOT password IS NULL AND password NOT LIKE _password THEN
		SET code = -1;
	ELSE
		UPDATE user SET email = _email, password = _password WHERE user_id = _user_id;
	END IF;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_update_user; $$
CREATE PROCEDURE sp_update_user(IN _user_id int, IN _nickname varchar(16),
		IN _signature varchar(70), IN _face_path char(24))
BEGIN
	IF _nickname IS NOT NULL THEN
		UPDATE user SET nickname = _nickname WHERE user_id = _user_id;
	END IF;
	IF _signature IS NOT NULL THEN
		UPDATE user SET signature = _signature WHERE user_id = _user_id;
	END IF;
	IF _face_path IS NOT NULL THEN
		UPDATE user SET face_path = _face_path WHERE user_id = _user_id;
	END IF;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_get_user; $$
CREATE PROCEDURE sp_get_user(IN _user_id int)
BEGIN
	SELECT user_id, token, email, qq_user_id, sina_user_id, nickname, 
		register_time, signature, account, 
		is_location_public, is_info_public,
		travel_qty, scenic_point_qty, favorite_travel_qty,
		achievement_qty, follower_qty FROM user
	WHERE user_id = _user_id;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_update_password; $$
CREATE PROCEDURE sp_update_password(IN _email varchar(25), IN old_pass varchar(16),
		IN new_pass varchar(16), OUT _user_id int)
BEGIN
	DECLARE password VARCHAR(16) DEFAULT NULL;
	SELECT u.password, u.user_id INTO password, _user_id FROM user u
	WHERE u.email = _email;
	
	IF password IS NULL THEN
		SET _user_id = -1;
	ELSE IF password != old_pass THEN
			SET _user_id = -2;
		 ELSE
			UPDATE user SET password=new_pass WHERE user_id = _user_id;
		 END IF;
	END IF;
END;$$

