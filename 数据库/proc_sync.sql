use travo;

delimiter $$
##############################################
DROP PROCEDURE IF EXISTS sp_get_sync_status; $$
CREATE PROCEDURE sp_get_sync_status(IN _user_id INT)
BEGIN
	SELECT lm_time FROM user WHERE user_id = _user_id
	ORDER BY lm_time DESC LIMIT 1;
	SELECT lm_time FROM user_info WHERE user_id = _user_id
	ORDER BY lm_time DESC LIMIT 1;

	SELECT time FROM location WHERE user_id = _user_id
	ORDER BY time DESC LIMIT 1;
	
	SELECT lm_time FROM note WHERE user_id = _user_id
	ORDER BY lm_time DESC LIMIT 1;
	SELECT lm_time FROM travel WHERE user_id = _user_id
	ORDER BY lm_time DESC LIMIT 1;
	SELECT lm_time FROM travel_plan WHERE user_id = _user_id
	ORDER BY lm_time DESC LIMIT 1;
END;$$

