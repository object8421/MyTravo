#-*- coding:utf-8 -*-

# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
from provence_data_crawler import settings
import MySQLdb
class ProvenceDataCrawlerPipeline(object):
    data_count = 0
    def process_item(self, item, spider):
        if self.is_exist(item['province_name']):
            print '%s 相关数据已存在。'%(item['province_name'])
            return item
        self.cursor.execute("""insert into des_province 
        	(id,crawled_url,province_name,country_id,background_description,
        	transportation_info,proper_travel_time,travel_advice,image_path,image_name)
         values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)""",\
        (0,item['crawled_url'],item['province_name'],int(item['related_country']),item['province_description'],\
            item['transportation_info'],item['proper_travel_time'],item['travel_advice'],\
            item['image_url'],item['image_path']));  
        self.conn.commit()
        print '%s 相关数据插入成功！'%(item['province_name'])
        self.data_count = self.data_count+1
        return item

    def open_spider(self, spider):
        #self.conn = MySQLdb.connect(host=settings.DATABASE['HOST'], user = settings.DATABASE['USER'], \
            #passwd = settings.DATABASE['PASSWORD'], db = settings.DATABASE.['DBNAME'], charset = settings.DATABASE['CHARSET'])
        self.conn = MySQLdb.connect(host='127.0.0.1', user = 'root', \
            passwd = '123456', db = 'scene_data', charset = 'utf8')
        self.cursor = self.conn.cursor()
        sql_script = str(open('../crawler_data_table.sql').read())
        sql_list = sql_script.split(';')
        sql_list.remove(sql_list[-1])
        for sql in sql_list:
            self.cursor.execute(sql)
        print '数据库连接正常'

    def close_spider(self,spider):
        self.conn.close()
        print '抓取完毕,共抓取了 %d 条数据。'%self.data_count

    def is_exist(self,row_name):
    	#有待改进，table name 未加入
        self.cursor.execute\
        ('select * from des_province where province_name = %s',(row_name) )
        row_count  = int(self.cursor.rowcount)
        if row_count == 0:
            return False
        else:
            return True

