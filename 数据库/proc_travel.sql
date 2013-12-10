use travo;

delimiter $$

##############################################
DROP PROCEDURE IF EXISTS sp_new_travel; $$
CREATE PROCEDURE sp_new_travel(IN _user_id INT, IN _title VARCHAR(45),
		IN _dest VARCHAR(45), IN _begin_date Date, IN _create_time DATETIME,
		IN _end_date Date, IN _cover_path VARCHAR(24), IN _avg_spend VARCHAR(20), 
		IN _description VARCHAR(4096), IN _is_public BOOL, OUT _travel_id INT)
BEGIN
	#check if has dup travel
	SET _travel_id = 0;
	SELECT travel_id into _travel_id FROM travel
	WHERE user_id = _user_id AND create_time = _create_time;
	
	IF _travel_id != 0 THEN
		SET _travel_id = -1;
	ELSE
		IF _is_public IS NULL THEN
			SET _is_public = True;
		END IF;
		INSERT INTO travel
		(user_id, title, destination, begin_date, end_date, cover_path,
		average_spend, description, create_time, is_public)
		VALUES(_user_id, _title, _dest, _begin_date, _end_date, _cover_path, 
				_avg_spend, _description, _create_time, _is_public);
		#get new travel_id
		SELECT LAST_INSERT_ID() INTO _travel_id;
	END IF;
END;$$

##############################################
DROP PROCEDURE IF EXISTS sp_sync_travel; $$
CREATE PROCEDURE sp_sync_travel(IN _user_id INT, IN begin_time DATETIME)
BEGIN
	IF begin_time IS NULL THEN
		SET begin_time = '1970-00-00 00:00:00';
	END IF;
	SELECT travel_id, title, destination, begin_date, end_date, average_spend,
			create_time, comment_qty, vote_qty, favorite_qty, read_times, is_public,
			is_deleted, description, cover_path FROM travel
	WHERE user_id = _user_id AND lm_time > begin_time;
END;$$

##############################################
DROP PROCEDURE IF EXISTS sp_get_travel; $$
CREATE PROCEDURE sp_get_travel(IN _travel_id INT)
BEGIN
	SELECT travel_id, title, destination, begin_date, end_date, average_spend,
			create_time, comment_qty, vote_qty, favorite_qty, read_times, is_public,
			is_deleted, description FROM travel
	WHERE travel_id = _travel_id AND is_public = TRUE AND is_deleted = FALSE;
END;$$

##############################################
DROP PROCEDURE IF EXISTS sp_get_cover; $$
CREATE PROCEDURE sp_get_cover(IN _travel_id INT, IN _user_id INT,
		OUT _cover_path varchar(24), OUT code INT)
BEGIN
	DECLARE user_id INT DEFAULT 0;
	DECLARE is_public BOOL;
	DECLARE is_deleted BOOL;

	SELECT t.user_id, t.is_public, t.is_deleted, t.cover_path
	INTO user_id, is_public, is_deleted, _cover_path
	FROM travel t
	WHERE t.travel_id = _travel_id;

	IF user_id = 0 OR is_deleted IS TRUE THEN
		#游记不存在
		SET code = -1;
	ELSE IF is_public IS FALSE AND _user_id != user_id THEN
		#权限不足
		SET code = -2;
		END IF;
	END IF;
END;$$

