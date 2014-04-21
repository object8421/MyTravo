#-*- coding:utf-8 -*-
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
import MySQLdb
import datetime
class TravoCrawlerPipeline(object):
	data_count = 0

	def __init__(self):
		pass

	def open_spider(self, spider):
		#self.conn = MySQLdb.connect(host=settings.DATABASE['HOST'], user = settings.DATABASE['USER'], \
		#passwd = settings.DATABASE['PASSWORD'], db = settings.DATABASE['DBNAME'], charset = settings.DATABASE['CHARSET'])
		self.conn = MySQLdb.connect(host='127.0.0.1', user = 'root', \
		 	passwd = '123456', db = 'scene_data', charset = 'utf8')
		self.cursor = self.conn.cursor()
		sql_script = str(open('../crawler_data_table.sql').read())
		sql_list = sql_script.split(';')
		sql_list.remove(sql_list[-1])
		for sql in sql_list:
			self.cursor.execute(sql)
		print '数据库连接正常'
		self.start_time = datetime.datetime.now()
		print "开始时间：%s"%datetime.datetime.now()


	def close_spider(self,spider):
		self.conn.close()
		self.end_time = datetime.datetime.now()
		delt_time_min = (self.start_time - self.end_time).seconds//60
		delt_time_sec = (self.start_time - self.end_time).seconds%60
		print '抓取完毕,共抓取了 %d 条数据。'%self.data_count
		print "结束时间：%s"%datetime.datetime.now()
		print '共计用时：%s 分 %s 秒。'%(delt_time_min,delt_time_sec)
	def process_item(self, item, spider):
		if item['item_type'] == 'country':
			print '检测到数据类型为country，准备处理'
			self.process_country_data(item, spider)
		if item['item_type'] == 'province':
			print '检测到数据类型为province，准备处理'
			self.process_province_data(item, spider)
		if item['item_type'] == 'city':
			print '检测到数据类型为city，准备处理'
			self.process_city_data(item, spider)
		if item['item_type'] == 'scenery_spot':
			print '检测到数据类型为scenery_spot，准备处理'
			self.process_scenery_spot_data(item, spider)

	def process_country_data(self, item, spider):
		# if self.is_exist('des_country','country_name',item['name']):
		# 	print '%s 相关数据已存在。'%(item['name'])
		# 	return item
		print '开始将%s数据插入数据库'%item['name']
		self.cursor.execute("""insert into des_country
			(id,country_name,crawled_url,brief_information,detail_information,
				image_path,image_url,last_update_time)
			values(%s,%s,%s,%s,%s,%s,%s,%s)""",\
			(0,item['name'],item['crawled_url'],item['brief_description'],item['detail_description'],\
				item['image_path'],item['image_url'],item['last_update_time']))
		self.conn.commit()
		print '%s (国家)相关数据插入成功！'%(item['name'])
		self.data_count += 1
		return item

	def process_province_data(self,item,spider):
		# if self.is_exist('des_province',item['name']):
		# 	print '%s 相关数据已存在。'%(item['province_name'])
		# 	return item
		print '开始将%s数据插入数据库'%item['name']
		self.cursor.execute("""insert into des_province
			(id,province_name,related_country,crawled_url,brief_information,detail_information,
			image_path,image_url,last_update_time)
			values(%s,%s,%s,%s,%s,%s,%s,%s,%s)""",\
			(0,item['name'],item['related_country'],item['crawled_url'],item['brief_description'],item['detail_description'],\
				item['image_path'],item['image_url'],item['last_update_time']))
		self.conn.commit()
		print '%s (省区)相关数据插入成功！'%(item['name'])
		self.data_count += 1
		print '共抓取了第 %d 条数据。'%self.data_count
		return item


	def process_city_data(self,item,spider):
		print '开始将%s(城市)数据插入数据库'%item['name']
		self.cursor.execute("""insert into des_city
			(id,city_name,related_province,crawled_url,brief_information,detail_information,
			image_path,image_url,last_update_time)
			values(%s,%s,%s,%s,%s,%s,%s,%s,%s)""",\
			(0,item['name'],item['related_province'],item['crawled_url'],item['brief_description'],item['detail_description'],\
				item['image_path'],item['image_url'],item['last_update_time']))
		self.conn.commit()
		print '%s (城市)相关数据插入成功！'%(item['name'])
		self.data_count += 1
		print '共抓取了第 %d 条数据。'%self.data_count
		return item

	def process_scenery_spot_data(self, item, spider):
		print '开始将%s(景点)数据插入数据库。'%item['name']
		self.cursor.execute("""insert into des_scenery_spot
			(id,spot_name,related_province,related_city,crawled_url,image_url,brief_information,
				ticket,proper_travel_time,transportation_info,last_update_time)
			values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)""",\
			(0,item['name'],item['related_province'],item['related_city'],item['crawled_url'],item['image_url'],\
				item['brief_description'],item['ticket_info'],item['proper_travel_time'],item['transportation_info'],item['last_update_time']))
		self.conn.commit()
		print '%s (城市)相关数据插入成功！'%(item['name'])
		self.data_count += 1
		print '共抓取了第 %d 条数据。'%self.data_count
		return item

	def is_exist(self,table_name,row_name,row_value):
		#有待改进，table name 未加入
		self.cursor.execute\
		('select * from %s where %s = %s'%(table_name,row_name,row_value))
		row_count  = int(self.cursor.rowcount)
		if row_count == 0:
			return False
		else:
			return True