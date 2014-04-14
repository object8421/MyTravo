#-*- coding:utf-8 -*-
from scrapy.selector import Selector
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule
from travo_crawler.items import CountryDataCrawlerItem
from travo_crawler import utils
import urllib2
from lxml import etree


class CountrySpiderSpider(CrawlSpider):
    name = 'country_spider'
    allowed_domains = ['qunar.com']
    start_urls = ['http://travel.qunar.com/place/']

    rules = (
        Rule(SgmlLinkExtractor(allow=r'travel\.qunar\.com/place/country/[a-z]+-\d{6}$'), callback='parse_item', follow=True),
    )

    def parse_item(self, response):
        sel = Selector(response)
        i = CountryDataCrawlerItem()
        i['item_type'] = 'country'
        i['crawled_url'] = response.url
        print response.url
        print i['item_type']
        i['name'] = sel.xpath('//div[@class="b_title clrfix"]/div[@class="tit"]/text()').extract()[0].encode('UTF-8')
        print i['name']
        image_url_list = sel.xpath('//img/@src').extract()
        image_url_list = utils.image_url_filter(image_url_list,'720x400')
        i['image_url'] = utils.get_image_url_string(image_url_list)
        print i['image_url']
        #i['image_path'] = 'test'
        i['image_path'] = utils.save_image_to_oss(image_url_list)
        print 'image_path'
        brief_info_list = sel.xpath('//div[@class="countbox"]//text()').extract()
        i['brief_description'] = utils.scrape_str(brief_info_list)
        print i['brief_description']
        #抓取详细信息
        detail_info_source = urllib2.urlopen(response.url+'/zhinan').read()
        detail_info_page = etree.HTML(detail_info_source.lower())
        detail_info_list = detail_info_page.xpath("//div[@class='b_g_cont']//text()")
        i['detail_description'] = utils.scrape_str(detail_info_list)
        print i['detail_description']
        i['last_update_time'] = utils.get_current_time()
        print i['last_update_time']

        return i
