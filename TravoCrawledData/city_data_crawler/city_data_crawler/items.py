#-*- coding:utf-8 -*-
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field

class CityDataCrawlerItem(Item):
    city_name = Field()
    related_province = Field()
    province_description = Field()
    #以字符串形式存储文件名，包含多个文件名，以；隔开
    image_path = Field()
    #图片的抓取路径，以；隔开
    image_url = Field()
    crawled_url = Field()
    proper_travel_time = Field()
    transportation_info = Field()
    travel_advice = Field()

    #摘要信息
    brief_description = Field()
    #详细信息
    detail_description = Field()
    history_introduction = Field()
    geographic_info = Field()
    attention = Field()
    useful_info = Field()