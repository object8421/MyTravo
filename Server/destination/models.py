from django.db import models

class BasicModel(models.Model):
	background_description = models.CharField(max_length=500,null=True)
	history_introduction = models.CharField(max_length=500,null=True)
	geograpyic_info = models.CharField(max_length=500,null=True)
	transportation_info = models.CharField(max_length=1000,null=True)
	proper_travel_time = models.CharField(max_length=500,null=True)
	visa_info = models.CharField(max_length=500,null=True)
	attention = models.CharField(max_length=2000,null=True)
	travel_advice = models.CharField(max_length=2000,null=True)
	image_path = models.CharField(max_length=200,null=True)
	class Meta:
		abstract = True

class Country(BasicModel):
	country_name = models.CharField(max_length=30)
	

class Provence(BasicModel):
	provence_name = models.CharField(max_length=30)
	related_country = models.ForeignKey(Country)
	
class City(BasicModel):
	city_name = models.CharField(max_length=30)
	related_provence = models.ForeignKey(Provence)
	

class ScenerySpot(BasicModel):
	spot_name = models.CharField(max_length=30)
	related_city = models.ForeignKey(City)
	ticket = models.CharField(max_length=500,null=True)
	
	


