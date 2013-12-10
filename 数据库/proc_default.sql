use travo;

delimiter $$
##############################################
DROP PROCEDURE IF EXISTS sp_update_token; $$
CREATE PROCEDURE sp_update_token(IN _user_id int, IN _token char(32))
BEGIN
	UPDATE user SET token = _token WHERE user_id = _user_id;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_verify_token; $$
CREATE PROCEDURE sp_verify_token(IN _token char(32), IN valid_day mediumint,
		OUT _user_id int)
BEGIN
	DECLARE last_login_date date DEFAULT NULL;
	SET _user_id = 0;
	SELECT user_id INTO _user_id FROM user
	WHERE token = _token;
	
	IF _user_id != 0 THEN #else, user not found
		#Get last login date
		SELECT DATE(time) INTO last_login_date FROM login_record
		WHERE user_id = _user_id
		ORDER BY time DESC LIMIT 1;
		IF to_days(last_login_date) + valid_day <= to_days(curdate()) THEN
			SET _user_id = -1; #Overdate
		END IF;
	END IF;
END;$$
