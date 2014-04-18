# Scrapy settings for travo_crawler project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'travo_crawler'

SPIDER_MODULES = ['travo_crawler.spiders']
NEWSPIDER_MODULE = 'travo_crawler.spiders'

# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'travo_crawler (+http://www.yourdomain.com)'
LOG_LEVEL = 'INFO'
# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'provence_data_crawler (+http://www.yourdomain.com)'
DATABASE = {
	'HOST':'127.0.0.1',
	'USER':'root',
	'PASSWORD':'123456',
	'DBNAME':'scene_data',
	'CHARSET':'utf8',
}

ITEM_PIPELINES = {
	
	'travo_crawler.pipelines.TravoCrawlerPipeline':100,
}

TIME_FORMAT = '%Y-%m-%d %X'



#=============================oss config ============================
OSS_DOMAIN = 'oss.aliyuncs.com'
OSS_ID = 'ZtI6J33H7O9dWyP5'
OSS_KEY = '6XubAUt6JFWQ7yi9w5MPQkKLHtcFe3'
OSS_BUCKET = 'crawled-pic'

#=============================local config ============================
IMAGE_PATH = '/Users/EthanWu/Desktop/travo_data_picture/'
COUNTRY_IMAGE_PATH = IMAGE_PATH + 'country/'
PROVINCE_IMAGE_PATH = IMAGE_PATH +'province/'
CITY_IMAGE_PATH = IMAGE_PATH + 'city/'
SCENERY_SPOT_IMAGE_PATH = IMAGE_PATH + 'scenery_spot/'


#=============================ftp config ============================
FTP_SERVER = '172.16.12.125'
FTP_PORT = 21
FTP_USER = 'rew'
FTP_BASE_PATH = '/travo_data_picture'
FTP_COUNTRY_PATH = FTP_BASE_PATH + '/country'
FTP_PROVINCE_PATH = FTP_BASE_PATH + '/province'
FTP_CITY_PATH = FTP_BASE_PATH + '/city'
FTP_SCENERY_PATH = FTP_BASE_PATH +'/scenery_spot'