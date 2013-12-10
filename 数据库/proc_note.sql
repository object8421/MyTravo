use travo;

delimiter $$
##############################################
DROP PROCEDURE IF EXISTS sp_new_note; $$
CREATE PROCEDURE sp_new_note(IN _user_id INT, IN _travel_id INT,
		IN _content VARCHAR(2048), IN _create_time DATETIME,
		IN _image_path CHAR(24), OUT _note_id INT)
BEGIN
	DECLARE __user_id INT DEFAULT 0;
	SELECT user_id INTO __user_id FROM travel WHERE travel_id = _travel_id;
	IF __user_id = 0 THEN
		SET _note_id = -1;	#找不到游记
	ELSE IF __user_id != _user_id THEN
		SET _note_id = -2;	#游记不属于该用户
	ELSE
		SELECT note_id INTO _note_id FROM note
		WHERE user_id = _user_id AND travel_id = _travel_id AND create_time = _create_time;
	
		IF _note_id > 0 THEN
			SET _note_id = -3;  #note重复
		ELSE
			INSERT INTO note
			(user_id, travel_id, create_time, content, image_path)
			VALUES(_user_id, _travel_id, _create_time, _content, _image_path);
	
			SELECT LAST_INSERT_ID() INTO _note_id;
		END IF;
	END IF;
	END IF;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_sync_note; $$
CREATE PROCEDURE sp_sync_note(IN _user_id INT, IN begin_time DATETIME,
		IN max_qty INT)
BEGIN
	SELECT note_id, user_id, travel_id, create_time, content, comment_qty,
			vote_qty, image_path
	FROM note
	WHERE user_id = _user_id AND lm_time > begin_time AND is_deleted = FALSE
	LIMIT max_qty;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_get_note; $$
CREATE PROCEDURE sp_get_note(IN _travel_id INT, IN _user_id INT,
		OUT code INT)
BEGIN
	DECLARE user_id INT DEFAULT 0;
	DECLARE is_public TINYINT;
	DECLARE is_deleted TINYINT;

	SELECT t.user_id, t.is_public, t.is_deleted INTO user_id, is_public, is_deleted
	FROM travel t
	WHERE t.travel_id = _travel_id;
	
	IF user_id = 0 OR is_deleted = 1 THEN
		#游记不存在
		SET code = -1;
	ELSE IF is_public = 0 AND _user_id != user_id THEN
			#权限不足
			SET code = -2;
		 ELSE
			SELECT note_id, user_id, travel_id, create_time, content, comment_qty,
					vote_qty, image_path
			FROM note n
			WHERE n.user_id = user_id AND n.travel_id = _travel_id AND
					is_deleted = FALSE;
		END IF;
	END IF;
END;$$
	