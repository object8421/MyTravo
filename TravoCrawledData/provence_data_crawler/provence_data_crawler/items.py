#-*- coding:utf-8 -*-
# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field

class ProvenceDataCrawlerItem(Item):

    province_name = Field()
    related_country = Field()
    province_description = Field()
    #以字符串形式存储文件名，包含多个文件名，以；隔开
    image_path = Field()
    #图片的抓取路径，以；隔开
    image_url = Field()
    crawled_url = Field()
    proper_travel_time = Field()
    transportation_info = Field()
    travel_advice = Field()

    background_description = Field()
    history_introduction = Field()
    geographic_info = Field()
    attention = Field()
    useful_info = Field()

