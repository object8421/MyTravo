#-*- coding:utf-8 -*-
from scrapy.selector import Selector
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule
from travo_crawler.items import TravoCrawlerItem,ProvinceDataCrawlerItem
import urllib2
from travo_crawler import utils
from lxml import etree

class ProvinceSpiderSpider(CrawlSpider):
    name = 'province_spider'
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

        i = ProvinceDataCrawlerItem()
        i['item_type'] = 'province'
        #抓取原网页的链接
        i['crawled_url'] = response.url

        #抓取省份的名称
        i['name'] = sel.xpath('//div[@class="b_title clrfix"]/div[@class="tit"]/text()').extract()[0].encode('UTF-8')
        print i['name']
        i['related_country'] = '中国'
        #抓取图片链接
        #image_url_list = sel.xpath('//img/@src').extract()
        image_url_list = sel.xpath('//img/@src').extract()

        print '==================图片抽取链接======================'
        image_url_list = utils.image_url_filter(image_url_list,'720x400')
        valid_url = ''
        for url in image_url_list:
            valid_url += url +';'
            print url
        print '============================================'
        i['image_path'] = utils.save_image_to_oss(image_url_list)
        i['image_url'] = valid_url
        print '开始获取简介信息'
        brief_info_list = sel.xpath('//div[@class="countbox"]//text()').extract()
        i['brief_description'] = utils.scrape_str(brief_info_list)
        print i['brief_description']
        print '简介信息获取完毕。'
        #抓取详细信息
        print '开始获取详细信息'
        detail_info_source = urllib2.urlopen(response.url+'/zhinan').read()
        detail_info_page = etree.HTML(detail_info_source.lower())
        detail_info_list = detail_info_page.xpath("//div[@class='b_g_cont']//text()")
        i['detail_description'] = utils.scrape_str(detail_info_list)
        print i['detail_description']
        print '详细信息获取完毕。'
        i['last_update_time'] = utils.get_current_time()
        print i['last_update_time']
        #=============================old version========================================
        #抓取省份的概述
        # print '开始获取省份概述'
        # deestinatino_info_source = urllib2.urlopen(response.url+'/du?typeId=401').read()
        # deestinatino_info_page = etree.HTML(deestinatino_info_source.lower())
        # deestinatino_info_list = deestinatino_info_page.xpath("//div[@class='e_content']//text()")
        # i['province_description'] = utils.scrape_str(deestinatino_info_list)
        # print i['province_description']
        # print '省份信息获取完毕！'
        # #抓取旅游时机信息
        # proper_time_source = urllib2.urlopen(response.url+'/du?typeId=415').read()
        # proper_time_page = etree.HTML(proper_time_source.lower())
        # proper_time_list = proper_time_page.xpath("//div[@class='e_content']//text()")
        # i['proper_travel_time'] = utils.scrape_str(proper_time_list)
        # print i['proper_travel_time']
        # #抓取交通信息
        # transportation_info_source = urllib2.urlopen(response.url+'/du?typeId=407').read()
        # transportation_info_page = etree.HTML(transportation_info_source.lower())
        # transporation_info_list = transportation_info_page.xpath("//div[@class='e_content']//text()")
        # i['transportation_info'] = utils.scrape_str(transporation_info_list)
        # print i['transportation_info']
        # #抓取旅行贴士
        # travel_tips_source = urllib2.urlopen(response.url+'/du?typeId=406').read()
        # travel_tips_page = etree.HTML(travel_tips_source.lower())
        # travel_tips_list = travel_tips_page.xpath("//div[@class='e_content']//text()")
        # i['travel_advice'] = utils.scrape_str(travel_tips_list)
        # print i['travel_advice']
        # i['related_country'] = 1
        
        # self.data_count = self.data_count+1
        # print self.data_count
        return i

