# encoding: utf8
from django.db import models, migrations


class Migration(migrations.Migration):
    
    dependencies = []

    operations = [
        migrations.CreateModel(
            fields = [('time', models.DateTimeField(serialize=False, primary_key=True),), ('position', models.CharField(max_length=45),), ('message', models.CharField(max_length=45),)],
            bases = (models.Model,),
            options = {u'db_table': u'error_log', u'managed': False},
            name = 'ErrorLog',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('province', models.CharField(max_length=3, blank=True),), ('city', models.CharField(max_length=15, blank=True),), ('district', models.CharField(max_length=15, blank=True),), ('address', models.CharField(max_length=20),), ('area_code', models.CharField(max_length=4, blank=True),), ('zip', models.CharField(max_length=6, blank=True),)],
            bases = (models.Model,),
            options = {u'db_table': u'complete_address', u'managed': False},
            name = 'CompleteAddress',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('name', models.CharField(max_length=20),), ('scenic_point_qty', models.IntegerField(),)],
            bases = (models.Model,),
            options = {u'db_table': u'achievement', u'managed': False},
            name = 'Achievement',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('name', models.CharField(unique=True, max_length=3),)],
            bases = (models.Model,),
            options = {u'db_table': u'province', u'managed': False},
            name = 'Province',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),)],
            bases = (models.Model,),
            options = {},
            name = 'MyModel',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('name', models.CharField(unique=True, max_length=15),), ('area_code', models.CharField(max_length=4),), ('province', models.ForeignKey(to=u'travo.Province', to_field='id'),)],
            bases = (models.Model,),
            options = {u'db_table': u'city', u'managed': False},
            name = 'City',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('name', models.CharField(max_length=15),), ('zip', models.CharField(max_length=6),), ('city', models.ForeignKey(to=u'travo.City', to_field='id'),)],
            bases = (models.Model,),
            options = {u'db_table': u'district', u'managed': False},
            name = 'District',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('address', models.CharField(max_length=20),), ('district', models.ForeignKey(to=u'travo.District', to_field='id'),)],
            bases = (models.Model,),
            options = {u'db_table': u'address', u'managed': False},
            name = 'Address',
        ),
        migrations.CreateModel(
            fields = [(u'mymodel_ptr', models.OneToOneField(auto_created=True, primary_key=True, to_field=u'id', serialize=False, to=u'travo.MyModel'),), ('address', models.CharField(max_length=45, null=True),), ('longitude', models.FloatField(),), ('latitude', models.FloatField(),)],
            bases = (u'travo.mymodel',),
            options = {u'db_table': u'location', u'managed': False},
            name = 'Location',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('name', models.CharField(max_length=45),), ('description', models.CharField(max_length=300, blank=True),), ('price', models.DecimalField(null=True, max_digits=7, decimal_places=2, blank=True),), ('address', models.ForeignKey(to=u'travo.Address', to_field='id'),)],
            bases = (models.Model,),
            options = {u'db_table': u'scenic_area', u'managed': False},
            name = 'ScenicArea',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('name', models.CharField(max_length=45),), ('longitude', models.FloatField(),), ('latitude', models.FloatField(),), ('price', models.DecimalField(null=True, max_digits=7, decimal_places=2, blank=True),), ('description', models.CharField(max_length=300, blank=True),), ('scenic_area', models.ForeignKey(to=u'travo.ScenicArea', to_field='id'),)],
            bases = (models.Model,),
            options = {u'db_table': u'scenic_point', u'managed': False},
            name = 'ScenicPoint',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('achievement', models.ForeignKey(to=u'travo.Achievement', to_field='id'),), ('scenic_point', models.ForeignKey(to=u'travo.ScenicPoint', to_field='id'),)],
            bases = (models.Model,),
            options = {u'db_table': u'achievement_scenic_point', u'managed': False},
            name = 'AchievementScenicPoint',
        ),
        migrations.CreateModel(
            fields = [('id', models.IntegerField(serialize=False, primary_key=True),), ('content', models.CharField(max_length=300, blank=True),), ('image_path', models.CharField(max_length=25, blank=True),), ('image_explain', models.CharField(max_length=300, blank=True),), ('scenic_point', models.ForeignKey(to=u'travo.ScenicPoint', to_field='id'),)],
            bases = (models.Model,),
            options = {u'db_table': u'scenic_point_info', u'managed': False},
            name = 'ScenicPointInfo',
        ),
        migrations.CreateModel(
            fields = [(u'mymodel_ptr', models.OneToOneField(auto_created=True, primary_key=True, to_field=u'id', serialize=False, to=u'travo.MyModel'),)],
            bases = (u'travo.mymodel',),
            options = {},
            name = 'SyncModel',
        ),
        migrations.CreateModel(
            fields = [(u'mymodel_ptr', models.OneToOneField(auto_created=True, primary_key=True, to_field=u'id', serialize=False, to=u'travo.MyModel'),), ('email', models.CharField(unique=True, max_length=25),), ('token', models.CharField(unique=True, max_length=32),), ('qq_user_id', models.CharField(default=None, unique=True, max_length=32),), ('sina_user_id', models.CharField(default=None, unique=True, max_length=20),), ('password', models.CharField(max_length=16, blank=True),), ('register_time', models.DateTimeField(auto_now_add=True),), ('nickname', models.CharField(unique=True, max_length=16),), ('face_path', models.CharField(default=None, max_length=24),), ('signature', models.CharField(max_length=70),), ('account', models.IntegerField(default=0),), ('travel_qty', models.IntegerField(default=0),), ('scenic_point_qty', models.IntegerField(default=0),), ('achievement_qty', models.IntegerField(default=0),), ('follower_qty', models.IntegerField(default=0),), ('favorite_travel_qty', models.IntegerField(default=0),), ('is_location_public', models.IntegerField(default=False),), ('is_info_public', models.IntegerField(default=True),), ('lm_time', models.DateTimeField(),)],
            bases = (u'travo.mymodel',),
            options = {u'db_table': u'user', u'managed': False},
            name = 'User',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('active', models.ForeignKey(to=u'travo.User', db_column=u'active', to_field=u'mymodel_ptr'),), ('passive', models.ForeignKey(to=u'travo.User', db_column=u'passive', to_field=u'mymodel_ptr'),), ('time', models.DateTimeField(auto_now_add=True),), ('action', models.CharField(max_length=1),)],
            bases = (models.Model,),
            options = {u'db_table': u'follow', u'managed': False},
            name = 'Follow',
        ),
        migrations.CreateModel(
            fields = [(u'mymodel_ptr', models.OneToOneField(auto_created=True, primary_key=True, to_field=u'id', serialize=False, to=u'travo.MyModel'),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('time', models.DateTimeField(),), ('ip', models.CharField(max_length=15),)],
            bases = (u'travo.mymodel',),
            options = {u'unique_together': set([(u'user', u'time',)]), u'db_table': u'login_record', u'managed': False},
            name = 'LoginRecord',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('time', models.DateTimeField(),), ('ip', models.CharField(max_length=15),), ('method', models.CharField(max_length=8),), ('path', models.CharField(max_length=25),), ('http_agent', models.CharField(max_length=128),), ('rsp_code', models.IntegerField(),)],
            bases = (models.Model,),
            options = {u'db_table': u'trans', u'managed': False},
            name = 'Trans',
        ),
        migrations.CreateModel(
            fields = [(u'syncmodel_ptr', models.OneToOneField(auto_created=True, to_field=u'mymodel_ptr', to=u'travo.SyncModel'),), ('id', models.AutoField(serialize=False, primary_key=True),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('title', models.CharField(max_length=45),), ('cover_path', models.CharField(max_length=24, null=True),), ('destination', models.CharField(max_length=45),), ('begin_date', models.DateField(null=True),), ('end_date', models.DateField(null=True, blank=True),), ('average_spend', models.CharField(max_length=20, null=True),), ('description', models.CharField(max_length=4096, null=True),), ('create_time', models.DateTimeField(),), ('comment_qty', models.IntegerField(default=0),), ('vote_qty', models.IntegerField(default=0),), ('favorite_qty', models.IntegerField(default=0),), ('read_times', models.IntegerField(default=0),), ('is_public', models.IntegerField(default=True),), ('is_deleted', models.IntegerField(default=False),), ('lm_time', models.DateTimeField(),)],
            bases = (u'travo.syncmodel',),
            options = {u'db_table': u'travel', u'managed': False},
            name = 'Travel',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('travel', models.ForeignKey(to=u'travo.Travel', to_field='id'),), ('time', models.DateTimeField(auto_now_add=True),)],
            bases = (models.Model,),
            options = {u'db_table': u'favorite_travel', u'managed': False},
            name = 'FavoriteTravel',
        ),
        migrations.CreateModel(
            fields = [(u'syncmodel_ptr', models.OneToOneField(auto_created=True, to_field=u'mymodel_ptr', to=u'travo.SyncModel'),), ('id', models.AutoField(serialize=False, primary_key=True),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('travel', models.ForeignKey(to=u'travo.Travel', to_field='id'),), ('location', models.OneToOneField(null=True, to_field=u'mymodel_ptr', to=u'travo.Location'),), ('create_time', models.DateTimeField(),), ('photo_time', models.DateTimeField(null=True),), ('content', models.CharField(max_length=2048, blank=True),), ('image_path', models.CharField(max_length=24, null=True),), ('is_deleted', models.IntegerField(default=False),), ('lm_time', models.DateTimeField(),)],
            bases = (u'travo.syncmodel',),
            options = {u'db_table': u'note', u'managed': False},
            name = 'Note',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('note', models.ForeignKey(to=u'travo.Note', to_field='id'),), ('time', models.DateTimeField(),), ('commenter', models.ForeignKey(to=u'travo.User', db_column=u'commenter', to_field=u'mymodel_ptr'),), ('content', models.CharField(max_length=200),)],
            bases = (models.Model,),
            options = {u'db_table': u'note_comment', u'managed': False},
            name = 'NoteComment',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('note', models.ForeignKey(to=u'travo.Note', to_field='id'),), ('time', models.DateTimeField(),), ('voter', models.ForeignKey(to=u'travo.User', db_column=u'voter', to_field=u'mymodel_ptr'),)],
            bases = (models.Model,),
            options = {u'db_table': u'note_vote', u'managed': False},
            name = 'NoteVote',
        ),
        migrations.CreateModel(
            fields = [(u'mymodel_ptr', models.OneToOneField(auto_created=True, primary_key=True, to_field=u'id', serialize=False, to=u'travo.MyModel'),), ('travel', models.ForeignKey(to=u'travo.Travel', to_field='id'),), ('time', models.DateTimeField(),), ('commenter', models.ForeignKey(to=u'travo.User', db_column=u'commenter', to_field=u'mymodel_ptr'),), ('content', models.CharField(max_length=200),)],
            bases = (u'travo.mymodel',),
            options = {u'unique_together': set([(u'travel', u'time',)]), u'db_table': u'travel_comment', u'managed': False},
            name = 'TravelComment',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('publish_time', models.DateTimeField(),), ('title', models.CharField(max_length=70),), ('begin_date', models.DateField(),), ('end_date', models.DateField(),), ('destination', models.CharField(max_length=45),), ('transport', models.CharField(max_length=45, blank=True),), ('publisher_name', models.CharField(max_length=10, blank=True),), ('publisher_sex', models.CharField(max_length=1, blank=True),), ('publisher_age', models.IntegerField(null=True, blank=True),), ('publisher_member', models.CharField(max_length=12, blank=True),), ('hope_sex', models.CharField(max_length=1, blank=True),), ('hope_age', models.CharField(max_length=12, blank=True),), ('hope_member', models.CharField(max_length=12, blank=True),), ('mobile', models.CharField(max_length=11, blank=True),), ('qq', models.CharField(max_length=12, blank=True),), ('conment', models.CharField(max_length=200, blank=True),), ('lm_time', models.DateTimeField(),)],
            bases = (models.Model,),
            options = {u'db_table': u'travel_plan', u'managed': False},
            name = 'TravelPlan',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('travel', models.ForeignKey(to=u'travo.Travel', to_field='id'),), ('reader', models.ForeignKey(to=u'travo.User', db_column=u'reader', to_field=u'mymodel_ptr'),), ('time', models.DateTimeField(auto_now_add=True),)],
            bases = (models.Model,),
            options = {u'db_table': u'travel_read_log', u'managed': False},
            name = 'TravelReadLog',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('travel', models.ForeignKey(to=u'travo.Travel', to_field='id'),), ('voter', models.ForeignKey(to=u'travo.User', db_column=u'voter', to_field=u'mymodel_ptr'),), ('time', models.DateTimeField(auto_now_add=True),)],
            bases = (models.Model,),
            options = {u'db_table': u'travel_vote', u'managed': False},
            name = 'TravelVote',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('achievement', models.ForeignKey(to=u'travo.Achievement', to_field='id'),), ('time', models.DateTimeField(),)],
            bases = (models.Model,),
            options = {u'db_table': u'user_achievement', u'managed': False},
            name = 'UserAchievement',
        ),
        migrations.CreateModel(
            fields = [(u'syncmodel_ptr', models.OneToOneField(auto_created=True, to_field=u'mymodel_ptr', to=u'travo.SyncModel'),), ('user', models.ForeignKey(primary_key=True, to_field=u'mymodel_ptr', serialize=False, to=u'travo.User'),), ('phone', models.CharField(max_length=12, null=True),), ('mobile', models.CharField(max_length=11, null=True),), ('qq', models.CharField(max_length=12, null=True),), ('sina_blog', models.CharField(max_length=25, null=True),), ('name', models.CharField(max_length=4, null=True),), ('age', models.IntegerField(null=True, blank=True),), ('sex', models.CharField(max_length=1, null=True),), ('address', models.ForeignKey(db_column=u'address', to_field='id', to=u'travo.Address', null=True),), ('address2', models.ForeignKey(db_column=u'address2', to_field='id', to=u'travo.Address', null=True),), ('native_place', models.ForeignKey(db_column=u'native_place', to_field='id', to=u'travo.Province', null=True),), ('degree', models.CharField(max_length=6, null=True),), ('job', models.CharField(max_length=15, null=True),), ('lm_time', models.DateTimeField(),)],
            bases = (u'travo.syncmodel',),
            options = {u'db_table': u'user_info', u'managed': False},
            name = 'UserInfo',
        ),
        migrations.CreateModel(
            fields = [(u'id', models.AutoField(verbose_name=u'ID', serialize=False, auto_created=True, primary_key=True),), ('user', models.ForeignKey(to=u'travo.User', to_field=u'mymodel_ptr'),), ('scenic_point', models.ForeignKey(to=u'travo.ScenicPoint', to_field='id'),), ('time', models.DateTimeField(),)],
            bases = (models.Model,),
            options = {u'db_table': u'user_scenic_point', u'managed': False},
            name = 'UserScenicPoint',
        ),
    ]
