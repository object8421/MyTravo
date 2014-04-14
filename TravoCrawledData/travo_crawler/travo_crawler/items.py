#-*- coding:utf-8 -*-
# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field
from oss.oss_api import *
class TravoCrawlerItem(Item):
    name = Field()
    #以字符串形式存储文件名，包含多个文件名，以；隔开
    image_path = Field()
    #图片的抓取路径，以；隔开
    image_url = Field()
    #抓取该Item的源链接
    crawled_url = Field()

    brief_description = Field()
    detail_description = Field()
    proper_travel_time = Field()
    transportation_info = Field()
    history_introduction = Field()
    travel_advice = Field()
    attention = Field()
    useful_info = Field()
    last_update_time = Field()

class CountryDataCrawlerItem(TravoCrawlerItem):
    item_type = Field()


class ProvinceDataCrawlerItem(TravoCrawlerItem):
    item_type = Field()
    province_name = Field()
    related_country = Field()
    province_description = Field()
    background_description = Field()

class CityDataCrawlerItem(TravoCrawlerItem):
    item_type = Field()
    city_name = Field()
    related_province = Field()
    province_description = Field()
    #摘要信息
    brief_description = Field()
    #详细信息
    detail_description = Field()
    
    geographic_info = Field()


class ScenerySpotDataCrawlerItem(TravoCrawlerItem):
    item_type = 'scenery_spot'