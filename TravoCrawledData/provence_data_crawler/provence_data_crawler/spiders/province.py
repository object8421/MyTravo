#-*- coding:utf-8 -*-
from scrapy.selector import Selector
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule
from provence_data_crawler.items import ProvenceDataCrawlerItem
from provence_data_crawler import utils
from provence_data_crawler import settings
import urllib2
from lxml import etree

class ProvinceSpider(CrawlSpider):
    name = 'province'
    allowed_domains = ['qunar.com']
    start_urls = ['http://travel.qunar.com/place/sitemap/china']
    data_count = 0
    #http://travel.qunar.com/place/province/anhui-298253
    rules = (
        #Rule(SgmlLinkExtractor(allow=r'travel\.qunar\.com/place/province'), callback='parse_item', follow=True),
        Rule(SgmlLinkExtractor(allow=r'travel\.qunar\.com/place/province/[a-z]+-\d{6}$'), callback='parse_item', follow=True),
    )

    def parse_item(self, response):
        sel = Selector(response)

        i = ProvenceDataCrawlerItem()
        #抓取原网页的链接
        i['crawled_url'] = response.url

        #抓取省份的名称
        i['province_name'] = sel.xpath('//span[@class="e_cover_title"]/text()').extract()[0].encode('UTF-8')
        print i['province_name']

        #抓取图片链接
        #image_url_list = sel.xpath('//img/@src').extract()
        image_url_list = sel.xpath('//img/@src').extract()
        for url in image_url_list:
            print url

        print '========================================'
        image_url_list = utils.image_url_filter(image_url_list)
        valid_url = ''
        for url in image_url_list:
            valid_url = valid_url + url +';'
            print url
        print '============================================'
        i['image_path'] = utils.save_image_to_ftp(image_url_list)
        i['image_url'] = valid_url
        #抓取省份的概述
        deestinatino_info_source = urllib2.urlopen(response.url+'/du?typeId=401').read()
        deestinatino_info_page = etree.HTML(deestinatino_info_source.lower())
        deestinatino_info_list = deestinatino_info_page.xpath("//div[@class='e_content']//text()")
        i['province_description'] = utils.scrape_str(deestinatino_info_list)
        print i['province_description']

        #抓取旅游时机信息
        proper_time_source = urllib2.urlopen(response.url+'/du?typeId=415').read()
        proper_time_page = etree.HTML(proper_time_source.lower())
        proper_time_list = proper_time_page.xpath("//div[@class='e_content']//text()")
        i['proper_travel_time'] = utils.scrape_str(proper_time_list)
        print i['proper_travel_time']
        #抓取交通信息
        transportation_info_source = urllib2.urlopen(response.url+'/du?typeId=407').read()
        transportation_info_page = etree.HTML(transportation_info_source.lower())
        transporation_info_list = transportation_info_page.xpath("//div[@class='e_content']//text()")
        i['transportation_info'] = utils.scrape_str(transporation_info_list)
        print i['transportation_info']
        #抓取旅行贴士
        travel_tips_source = urllib2.urlopen(response.url+'/du?typeId=406').read()
        travel_tips_page = etree.HTML(travel_tips_source.lower())
        travel_tips_list = travel_tips_page.xpath("//div[@class='e_content']//text()")
        i['travel_advice'] = utils.scrape_str(travel_tips_list)
        print i['travel_advice']
        i['related_country'] = 1
        
        self.data_count = self.data_count+1
        print self.data_count
        return i



    

