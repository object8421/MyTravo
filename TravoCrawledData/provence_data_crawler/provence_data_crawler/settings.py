# Scrapy settings for provence_data_crawler project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'provence_data_crawler'

SPIDER_MODULES = ['provence_data_crawler.spiders']
NEWSPIDER_MODULE = 'provence_data_crawler.spiders'
LOG_LEVEL = 'INFO'
# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'provence_data_crawler (+http://www.yourdomain.com)'
DATABASE = {
	'HOST':'127.0.0.1',
	'USER':'root',
	'PASSWORD':'123456',
	'DBNAME':'scene_data',
	'CHARSET':'UTF-8',
}

ITEM_PIPELINES = {
	
	'provence_data_crawler.pipelines.ProvenceDataCrawlerPipeline':100,
}

IMAGE_PATH = '/Users/EthanWu/Desktop/travo_data_picture/'
PROVINCE_IMAGE_PATH = IMAGE_PATH+'province/'

FTP_SERVER = '172.16.12.125'
FTP_PORT = 21
FTP_USER = 'rew'
FTP_BASE_PATH = '/travo_data_picture'
FTP_COUNTRY_PATH = FTP_BASE_PATH + '/country'
FTP_PROVINCE_PATH = FTP_BASE_PATH + '/province'
FTP_CITY_PATH = FTP_BASE_PATH + '/city'
FTP_SCENERY_PATH = FTP_BASE_PATH +'/scenery_spot'