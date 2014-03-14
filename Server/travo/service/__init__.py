import  userservice
from travo.models import UserInfo, Travel, Note, Location
from travo.rc import *

def sync_state(token):
	user = userservice.get_user(token)
	result = {RSP_CODE : RC_SUCESS}
	result['user_info'] = UserInfo.lm_time(user) 
	result['travel'] = Travel.lm_time(user) 
	result['note'] = Note.lm_time(user) 
	#result['location'] = Location.objects.filter(user=user).latest('lm_time')
	return result

