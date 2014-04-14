# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html

class CityDataCrawlerPipeline(object):
    def process_item(self, item, spider):
        return item
    
    def open_spider(self,spider):
    	self.conn = MySQLdb.connect(host='127.0.0.1', user = 'root', \
        passwd = '123456', db = 'scene_data', charset = 'utf8')
        self.cursor = self.conn.cursor()