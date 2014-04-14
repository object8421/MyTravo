#-*- coding:utf-8 -*-
from scrapy.selector import Selector
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule
from travo_crawler.items import TravoCrawlerItem,CityDataCrawlerItem
import urllib2
from travo_crawler import utils
from lxml import etree

class CitySpiderSpider(CrawlSpider):
    name = 'city_spider'
    allowed_domains = ['qunar.com']
    allowed_domains = ['qunar.com']
    start_urls = ['http://travel.qunar.com/place/sitemap/china']
    data_count = 0

    rules = (
        #Rule(SgmlLinkExtractor(allow=r'travel\.qunar\.com/place/province'), callback='parse_item', follow=True),
        Rule(SgmlLinkExtractor(allow=r'travel\.qunar\.com/place/city/[a-z]+-\d{6}$'), callback='parse_item', follow=True),
    )

    def parse_item(self, response):
        sel = Selector(response)
        i = CityDataCrawlerItem()
        i['item_type'] = 'city'
        i['crawled_url'] = response.url
        i['name'] = sel.xpath('//div[@class="b_title clrfix"]/div[@class="tit"]/text()').extract()[0].encode('UTF-8')
        try:
            i['related_province'] = sel.xpath('//div[@class="e_crumbs"]/a[4]/@title').extract()[0].encode('UTF-8')
        except IndexError:
            print 'related province data may out of index'
        else:    
            print '%s --- %s.'%(i['name'],i['related_province'])
        #抓取图片链接
        #image_url_list = sel.xpath('//img/@src').extract()
        image_url_list = sel.xpath('//img/@src').extract()
        print '====================图片链接===================='
        image_url_list = utils.image_url_filter(image_url_list,'720x400')
        valid_url = ''
        for url in image_url_list:
            valid_url = valid_url + url +';'
            print url
        print '============================================'
        i['image_path'] = utils.save_image_to_oss(image_url_list)
        i['image_url'] = valid_url
        #抓取简介信息
        brief_info_list = sel.xpath('//div[@class="countbox"]//text()').extract()
        i['brief_description'] = utils.scrape_str(brief_info_list)
        #抓取详细信息
        detail_info_source = urllib2.urlopen(response.url+'/zhinan').read()
        detail_info_page = etree.HTML(detail_info_source.lower())
        detail_info_list = detail_info_page.xpath("//div[@class='b_g_cont']//text()")
        i['detail_description'] = utils.scrape_str(detail_info_list)
        
        i['last_update_time'] = utils.get_current_time();
        print i['last_update_time']
        print response.url
        self.data_count += 1
        print "抓取第：%d 条数据。"%self.data_count
        return i
