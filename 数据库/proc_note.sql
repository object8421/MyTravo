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
CREATE PROCEDURE sp_get_note(IN _user_id INT, IN _travel_id INT,
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
##############################################
DROP PROCEDURE IF EXISTS sp_delete_note;$$
CREATE PROCEDURE sp_delete_note(IN _user_id INT, IN _note_id INT, OUT code INT)
BEGIN
	DECLARE user_id INT DEFAULT 0;
	SET code = 0;
	SELECT n.user_id INTO user_id
	FROM note n
	WHERE _note_id = note_id;
	
	IF user_id = 0 THEN
		#找不到该Note
		SET code = -1;
	ELSE IF user_id != _user_id THEN
			#不是该用户
			SET code = -2;
		 ELSE
			#删除动作
			UPDATE note
			SET is_deleted = TRUE
			WHERE note_id = _note_id;
		 END IF;
	END IF;
END;$$
##############################################
DROP PROCEDURE IF EXISTS sp_update_note;$$
CREATE PROCEDURE sp_update_note(IN _user_id INT, IN _note_id INT,
		IN _content VARCHAR(2048), IN _image_path CHAR(24), OUT code INT)
BEGIN
	DECLARE user_id INT DEFAULT 0;
	SET code = 0;
	SELECT n.user_id INTO user_id
	FROM note n
	WHERE note_id = _note_id;

	IF user_id = 0 THEN
		#找不到该Note
		SET code = -1;
	ELSE IF user_id != _user_id THEN
			#不是该用户
			SET code = -2;
		 ELSE
			#更新动作（之所以用这么多判断是为了少一次更新操作）
			IF _content IS NOT NULL AND _image_path IS NOT NULL THEN
				#两项数据都被修改
				UPDATE note
				SET content = _content, image_path = _image_path
				WHERE note_id = _note_id;
			ELSE IF _content IS NOT NULL THEN
				#之修改了其中的一项
				UPDATE note
				SET content = _content
				WHERE note_id = _note_id;
				ELSE IF _image_path IS NOT NULL THEN
					UPDATE note
					SET image_path = _image_path
					WHERE note_id = _note_id;
					END IF;
			    END IF;
			END IF;
		 END IF;
	END IF;
END
		