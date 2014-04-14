# Scrapy settings for city_data_crawler project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'city_data_crawler'

SPIDER_MODULES = ['city_data_crawler.spiders']
NEWSPIDER_MODULE = 'city_data_crawler.spiders'

# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'city_data_crawler (+http://www.yourdomain.com)'
IMAGE_PATH = '/Users/EthanWu/Desktop/travo_data_picture/'
CITY_IMAGE_PATH = IMAGE_PATH+'city/'