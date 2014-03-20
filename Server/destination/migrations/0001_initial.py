# encoding: utf8
from django.db import models, migrations


class Migration(migrations.Migration):
    
    dependencies = []

    operations = [
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('background_description', models.CharField(max_length=500, null=True),), ('history_introduction', models.CharField(max_length=500, null=True),), ('geograpyic_info', models.CharField(max_length=500, null=True),), ('transportation_info', models.CharField(max_length=1000, null=True),), ('proper_travel_time', models.CharField(max_length=500, null=True),), ('visa_info', models.CharField(max_length=500, null=True),), ('attention', models.CharField(max_length=2000, null=True),), ('travel_advice', models.CharField(max_length=2000, null=True),), ('image_path', models.CharField(max_length=200, null=True),), ('country_name', models.CharField(max_length=30),)],
            bases = (models.Model,),
            options = {u'abstract': False},
            name = 'Country',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('background_description', models.CharField(max_length=500, null=True),), ('history_introduction', models.CharField(max_length=500, null=True),), ('geograpyic_info', models.CharField(max_length=500, null=True),), ('transportation_info', models.CharField(max_length=1000, null=True),), ('proper_travel_time', models.CharField(max_length=500, null=True),), ('visa_info', models.CharField(max_length=500, null=True),), ('attention', models.CharField(max_length=2000, null=True),), ('travel_advice', models.CharField(max_length=2000, null=True),), ('image_path', models.CharField(max_length=200, null=True),), ('provence_name', models.CharField(max_length=30),), ('related_country', models.ForeignKey(to=u'destination.Country', to_field=u'id'),)],
            bases = (models.Model,),
            options = {u'abstract': False},
            name = 'Provence',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('background_description', models.CharField(max_length=500, null=True),), ('history_introduction', models.CharField(max_length=500, null=True),), ('geograpyic_info', models.CharField(max_length=500, null=True),), ('transportation_info', models.CharField(max_length=1000, null=True),), ('proper_travel_time', models.CharField(max_length=500, null=True),), ('visa_info', models.CharField(max_length=500, null=True),), ('attention', models.CharField(max_length=2000, null=True),), ('travel_advice', models.CharField(max_length=2000, null=True),), ('image_path', models.CharField(max_length=200, null=True),), ('city_name', models.CharField(max_length=30),), ('related_provence', models.ForeignKey(to=u'destination.Provence', to_field=u'id'),)],
            bases = (models.Model,),
            options = {u'abstract': False},
            name = 'City',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('background_description', models.CharField(max_length=500, null=True),), ('history_introduction', models.CharField(max_length=500, null=True),), ('geograpyic_info', models.CharField(max_length=500, null=True),), ('transportation_info', models.CharField(max_length=1000, null=True),), ('proper_travel_time', models.CharField(max_length=500, null=True),), ('visa_info', models.CharField(max_length=500, null=True),), ('attention', models.CharField(max_length=2000, null=True),), ('travel_advice', models.CharField(max_length=2000, null=True),), ('image_path', models.CharField(max_length=200, null=True),), ('spot_name', models.CharField(max_length=30),), ('related_city', models.ForeignKey(to=u'destination.City', to_field=u'id'),), ('ticket', models.CharField(max_length=500, null=True),)],
            bases = (models.Model,),
            options = {u'abstract': False},
            name = 'ScenerySpot',
        ),
    ]
