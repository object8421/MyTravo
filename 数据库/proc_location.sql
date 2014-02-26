use travo;

delimiter $$
##############################################
DROP PROCEDURE IF EXISTS sp_sync_location; $$
CREATE PROCEDURE sp_sync_location(IN _user_id INT, IN begin_time DATETIME,
		IN max_qty INT)
BEGIN
	DECLARE MAXINT INT DEFAULT ~0 >> 33;
	IF max_qty = 0 THEN
		SET max_qty = MAXINT;
	END IF;
	IF begin_time IS NULL THEN
		SET begin_time = '0000-00-00 00:00:00';
	END IF;
	SELECT time, longitude, latitude, address FROM location
	WHERE user_id = _user_id AND time > begin_time LIMIT max_qty;
END;$$
