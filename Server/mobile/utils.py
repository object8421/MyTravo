from json import JSONEncoder
from django.db import models

class MyJsonEncoder(JSONEncoder):
	def default(self, o):
		if isinstance(o, models.Model):
			return o.dict()
		return JSONEncoder.default(self, o)
