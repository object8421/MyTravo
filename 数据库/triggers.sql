USE travo;

DELIMITER $$
##################################
### trigger in user
##################################
DROP TRIGGER IF EXISTS bins_user;
$$
CREATE TRIGGER bins_user
BEFORE INSERT ON user FOR EACH ROW
BEGIN
	IF NEW.register_time LIKE '0000-00-00 00:00:00' THEN
		SET NEW.register_time = NOW();
	END IF;
end;
$$
DROP TRIGGER IF EXISTS ains_user;
$$
CREATE TRIGGER ains_user
AFTER INSERT ON user FOR EACH ROW
BEGIN
	INSERT INTO user_info(user_id)
	VALUES(new.user_id);
END;
$$
##################################
### trigger in note_comment
##################################
DROP TRIGGER IF EXISTS ains_note_comment;
$$
CREATE TRIGGER ains_note_comment
AFTER INSERT ON note_comment FOR EACH ROW
BEGIN
	UPDATE note
	SET comment_qty = comment_qty + 1
	WHERE note_id = new.note_id;
end
$$

DROP TRIGGER IF EXISTS adel_note_comment;
$$
CREATE TRIGGER adel_note_comment 
AFTER DELETE ON note_comment FOR EACH ROW
BEGIN
	UPDATE note
	SET comment_qty = comment_qty - 1
	WHERE note_id = old.note_id;
end
$$
##################################
### trigger in note
##################################
DROP TRIGGER IF EXISTS bins_note;
$$
CREATE TRIGGER bins_note
BEFORE INSERT ON note FOR EACH ROW
BEGIN
	IF NEW.create_time IS NULL THEN
		SET NEW.create_time = NOW();
	END IF;
END;
$$
##################################
### trigger in travel
##################################
DROP TRIGGER IF EXISTS ains_travel
$$
CREATE TRIGGER ains_travel
AFTER INSERT ON travel FOR EACH ROW
BEGIN
	UPDATE user
	SET travel_qty = travel_qty + 1
	WHERE user_id = new.user_id;
end
$$
##################################
### trigger in travel_comment
##################################
DROP TRIGGER IF EXISTS ains_travel_comment;
$$
CREATE TRIGGER ains_travel_comment 
AFTER INSERT ON travel_comment FOR EACH ROW
BEGIN
	UPDATE travel
	SET comment_qty = comment_qty + 1
	WHERE travel_id = new.travel_id;
end
$$

DROP TRIGGER IF EXISTS adel_travel_comment;
$$
CREATE TRIGGER adel_travel_comment
AFTER DELETE ON travel_comment FOR EACH ROW
BEGIN
	UPDATE travel
	SET comment_qty = comment_qty - 1
	WHERE travel_id = old.travel_id;
end
$$
##################################
### trigger in note_vote
##################################
DROP TRIGGER IF EXISTS ains_note_vote;
$$
CREATE TRIGGER ains_note_vote
AFTER INSERT ON note_vote FOR EACH ROW
BEGIN
	UPDATE note
	SET vote_qty = vote_qty + 1
	WHERE note_id = new.note_id;
end
$$

DROP TRIGGER IF EXISTS adel_note_vote;
$$
CREATE TRIGGER adel_note_vote 
AFTER DELETE ON note_vote FOR EACH ROW
BEGIN
	UPDATE note
	SET vote_qty = vote_qty - 1
	WHERE note_id = old.note_id;
end
$$
##################################
### trigger in travel_vote
##################################
DROP TRIGGER IF EXISTS ains_travel_vote;
$$
CREATE TRIGGER ains_travel_vote 
AFTER INSERT ON travel_vote FOR EACH ROW
BEGIN
	UPDATE travel
	SET vote_qty = vote_qty + 1
	WHERE travel_id = new.travel_id;
end
$$

DROP TRIGGER IF EXISTS adel_travel_vote;
$$
CREATE TRIGGER adel_travel_vote
AFTER DELETE ON travel_vote FOR EACH ROW
BEGIN
	UPDATE travel
	SET vote_qty = vote_qty - 1
	WHERE travel_id = old.travel_id;
end
$$

##################################
### trigger in follow
##################################
DROP TRIGGER IF EXISTS bins_follow;
$$
CREATE TRIGGER bins_follow
BEFORE INSERT ON follow FOR EACH ROW
BEGIN
	DECLARE _action ENUM('0', '1') DEFAULT NULL;

	#过滤不合法的数据
	IF NOT new.action IN ('0', '1') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'action must be "0" or "1"';
	end if;
	
	#同种操作不能连续进行
	SELECT action INTO _action
	FROM follow
	WHERE active = new.active AND passive = new.passive
	ORDER BY time DESC
	LIMIT 1;
	
	IF _action IS NOT NULL AND _action = new.action THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'can not insert same action continuously';
	end if;
end
$$
DROP TRIGGER IF EXISTS ains_follow;
$$
CREATE TRIGGER ains_follow
AFTER INSERT ON follow FOR EACH ROW
BEGIN
	IF new.action = '1' THEN
		UPDATE user
		SET follower_qty = follower_qty + 1
		WHERE user_id = new.passive;
	ELSE IF new.action = '0' THEN
		UPDATE user
		SET follower_qty = follower_qty - 1
		WHERE user_id = new.passive;
		END IF;
	END IF;
end
$$
##################################
### trigger in achievement_scenic_point
##################################
DROP TRIGGER IF EXISTS ains_achievement_scenic_point;
$$
CREATE TRIGGER ains_achievement_scenic_point 
AFTER INSERT ON achievement_scenic_point FOR EACH ROW
BEGIN
	UPDATE achievement
	SET scenic_point_qty = scenic_point_qty + 1
	WHERE achievement_id = new.achievement_id;
end
$$

DROP TRIGGER IF EXISTS adel_achievement_scenic_point;
$$
CREATE TRIGGER adel_achievement_scenic_point
AFTER DELETE ON achievement_scenic_point FOR EACH ROW
BEGIN
	UPDATE achievement
	SET scenic_point_qty = scenic_point_qty - 1
	WHERE achievement_id = old.achievement_id;
end
$$
##################################
### trigger in travel_plan
##################################
DROP TRIGGER IF EXISTS bins_travel_plan;
$$
CREATE TRIGGER bins_travel_plan
BEFORE INSERT ON travel_plan FOR EACH ROW
BEGIN
	IF NEW.publish_time IS NULL THEN
		SET NEW.publish_time = NOW();
	END IF;
END;
$$
DROP trigger IF EXISTS ains_travel_plan;
$$
CREATE TRIGGER ains_travel_plan
AFTER INSERT ON travel_plan FOR EACH ROW
BEGIN
	##将能用到的信息如publish_name,publish_sex,mobile等信息保存到contact中。

	#从contact中选出数据
	DECLARE c_name char(4) DEFAULT NULL;
	DECLARE c_sex ENUM('男','女') DEFAULT NULL;
	DECLARE c_mobile char(11) DEFAULT NULL;
	DECLARE c_age tinyint DEFAULT NULL;
	DECLARE c_qq  VARCHAR(12) DEFAULT NULL;
	
	SELECT name, age, sex, mobile, qq INTO c_name, c_age, c_sex, c_mobile, c_qq
	FROM user_info
	WHERE user_id = new.user_id;
	
	#查看new中有没有能用的数据
	if c_name is null and new.publisher_name is not null and CHAR_LENGTH(new.publisher_name) <= 4 then
		SET c_name = new.publisher_name;
	end if;

	if c_sex is null and new.publisher_sex is not null then
		SET c_sex = new.publisher_sex;
	end if;
	
	if c_age is null and new.publisher_age is not null then
		SET c_age = new.publisher_age;
	end if;
	
	if c_mobile is null and new.mobile is not null then
		set c_mobile = new.mobile;
	end if;
    if c_qq is null and new.qq is not null then
		set c_qq = new.qq;
    end if;

	#将新数据写入contact
	UPDATE user_info
	SET name = c_name, age = c_age, sex = c_sex, mobile = c_mobile
	WHERE user_id = new.user_id;
end
$$
##################################
### trigger in user_scenic_point 
##################################
DROP TRIGGER IF EXISTS ains_user_scenic_point;
$$
CREATE TRIGGER ains_user_scenic_point
AFTER INSERT ON user_scenic_point FOR EACH ROW
BEGIN
	DECLARE done int default 0;
	DECLARE got_sp_count	int;	#用户已经去过的某个成就中包含的景点数量
	DECLARE need_sp_count	int;    #某个成就中包含的所有景点数量
	DECLARE userid	int default -1;	#用来检查用户是否已经获得了某个成就
	DECLARE may_achie_id	int;
	#加入新的景点后有可能获得的所有成就
	DECLARE may_achie CURSOR for select achievement_id 
									from achievement_scenic_point
									where scenic_point_id = new.scenic_point_id;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

	OPEN may_achie;
 
 	REPEAT
 		FETCH may_achie INTO may_achie_id;
		IF NOT done THEN 
			
			#对每一个可能获得的成就，检查用户是否能获得这个成就
			SELECT COUNT(usp.scenic_point_id) INTO got_sp_count
			FROM user_scenic_point usp, achievement_scenic_point asp
			WHERE usp.user_id = new.user_id AND
				  usp.scenic_point_id = asp.scenic_point_id AND
					asp.achievement_id = may_achie_id;

			SELECT COUNT(scenic_point_id) INTO need_sp_count
			FROM achievement_scenic_point
			WHERE achievement_id = may_achie_id;

			IF got_sp_count = need_sp_count THEN
				#用户可以获得这个成就
				#但要先检查他是否已经获得了这个成就
				SELECT user_id INTO userid
				FROM user_achievement
				WHERE user_id = new.user_id AND achievement_id = may_achie_id;

				if userid = -1 then	
					#之前没有获得，现在给他
					INSERT INTO user_achievement
					VALUES(new.user_id, may_achie_id, now());
					SET done = 0;
				else 
					set userid = -1;
				end if;
			end if;
		end if;
 	UNTIL done END REPEAT;
 
 	CLOSE may_achie;
	##对应的user.scenic_point_qty++ 
	UPDATE user
	SET scenic_point_qty = scenic_point_qty + 1
	WHERE user_id = new.user_id;
	
end
$$

DROP TRIGGER IF EXISTS adel_user_scenic_point;
$$
CREATE TRIGGER adel_user_scenic_point
AFTER DELETE ON user_scenic_point FOR EACH ROW
BEGIN
	UPDATE user
	SET scenic_point_qty = scenic_point_qty - 1
	WHERE user_id = old.user_id;
end
$$
##################################
### trigger in user_achievement 
##################################
DROP TRIGGER IF EXISTS ains_user_achievement;
$$
CREATE TRIGGER ains_user_achievement
AFTER INSERT ON user_achievement FOR EACH ROW
BEGIN
	UPDATE user
	SET achievement_qty = achievement_qty + 1
	WHERE user_id = new.user_id;
end
$$

DROP TRIGGER IF EXISTS adel_user_achievement;
$$
CREATE TRIGGER adel_user_achievement
AFTER DELETE ON user_achievement FOR EACH ROW
BEGIN
	UPDATE user
	SET achievement_qty = achievement_qty - 1
	WHERE user_id = old.user_id;
end
$$
##################################
### trigger in favorite_travel 
##################################
DROP TRIGGER IF EXISTS ains_favorite_travel;
$$
CREATE TRIGGER ains_favorite_travel
AFTER INSERT ON favorite_travel FOR EACH ROW
BEGIN
	UPDATE travel 
	SET favorite_qty = favorite_qty + 1 
	WHERE travel_id = new.travel_id; 
	UPDATE user
	SET favorite_travel_qty = favorite_travel_qty + 1
	WHERE user_id = new.user_id;
end
$$

DROP TRIGGER IF EXISTS adel_favorite_travel;
$$
CREATE TRIGGER adel_favorite_travel
AFTER DELETE ON favorite_travel FOR EACH ROW
BEGIN
	UPDATE travel 
	SET favorite_qty = favorite_qty - 1 
	WHERE travel_id = old.travel_id; 
	UPDATE user
	SET favorite_travel_qty = favorite_travel_qty - 1
	WHERE user_id = old.user_id;
end
$$

show triggers;

