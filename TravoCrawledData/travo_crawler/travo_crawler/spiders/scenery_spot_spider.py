#-*- coding:utf-8 -*-
from scrapy.selector import Selector
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule
import MySQLdb
from travo_crawler.items import ScenerySpotDataCrawlerItem
from travo_crawler import settings
from travo_crawler import utils

class ScenerySpotSpiderSpider(CrawlSpider):
    name = 'scenery_spot_spider'
    allowed_domains = ['qunar.com']
    start_urls = ['http://travel.qunar.com/place/sitemap/china']
    data_count = 0
    rules = (
        #Rule(SgmlLinkExtractor(allow=r'travel\.qunar\.com/place/province'), callback='parse_item', follow=True),
        Rule(SgmlLinkExtractor(allow=r'travel\.qunar\.com/place/poi/[a-z]+-\d{6}$'), callback='parse_item', follow=True),
        )

    def parse_item(self, response):
        sel = Selector(response)
        i = ScenerySpotDataCrawlerItem()
        i['item_type'] = 'scenery_spot'
        i['crawled_url'] = response.url
        i['name'] = sel.xpath('//div[@class="b_title clrfix"]/h2[@class="tit"]/text()')[0].extract().encode('UTF-8')
        #try:
        i['related_province'] = sel.xpath('//div[@class="e_crumbs"]/a[4]/text()')[0].extract().encode('UTF-8')
        i['related_city'] = sel.xpath('//div[@class="e_crumbs"]/a[5]/text()')[0].extract().encode('UTF-8')
        print i['name']
        print i['related_city']
        print i['related_province']
        print '==================图片链接======================'
        #image_url_list = sel.xpath('//img[@data_beacon="poi_pic"]/@src').extract()
        image_url_list = sel.xpath('//img/@src').extract()
        image_url_list = utils.image_url_filter(image_url_list,'1024x683')
        valid_url = ''
        for url in image_url_list:
            valid_url += url +';'
            print url
        i['image_url'] = valid_url
        print i['image_url']
        #print '%s -- %s -- %s' % (i['name'],i['related_city'],i['related_province'])
        print '------------------------------------------简介----------------------------------------------'
        try:
            i['brief_description'] = sel.xpath('//div[@class="e_db_content_box"]//div[@class="content"]//p/text()')[0].extract().encode('utf8')
        except IndexError:
            i['brief_description'] = "暂无"
        print i['brief_description']
        print '------------------------------------------门票----------------------------------------------'
        try:
            i['ticket_info']  = sel.xpath\
            ('//div[@class="b_detail_section b_detail_ticket"]/div[@class="e_db_content_box e_db_content_dont_indent"]//p/text()')[0].extract().encode('utf8')
        except IndexError:
            i['ticket_info'] = "暂无"
        print i['ticket_info']
        print '------------------------------------------旅游时间----------------------------------------------'
        try:
            i['proper_travel_time'] = sel.xpath\
            ('//div[@class="b_detail_section b_detail_travelseason"]/div[@class="e_db_content_box e_db_content_dont_indent"]//p/text()')[0].extract().encode('utf8')
        except IndexError:
            i['proper_travel_time'] = "暂无"
        print i['proper_travel_time']
        print '------------------------------------------交通信息----------------------------------------------'
        try:
            i['transportation_info'] = sel.xpath\
            ('//div[@class="b_detail_section b_detail_traffic"]/div[@class="e_db_content_box e_db_content_dont_indent"]//p/text()')[0].extract().encode('utf8')
        except IndexError:
            i['transportation_info'] = "暂无"
        print i['transportation_info']
        i['last_update_time'] = utils.get_current_time()
        self.data_count += 1
        print "抓取第：%d 条数据。"%self.data_count
        return i

    def __init__(self):
        CrawlSpider.__init__(self)
        print settings.DATABASE['HOST']
        conn = MySQLdb.connect(host = settings.DATABASE['HOST'], user = settings.DATABASE['USER'], \
            passwd = settings.DATABASE['PASSWORD'], db = settings.DATABASE['DBNAME'], charset = settings.DATABASE['CHARSET'])
        cursor = conn.cursor()
        cursor.execute("SELECT crawled_url FROM des_city")
        parent_url_list = cursor.fetchall()
        for url in parent_url_list:
            #print url[0]
            self.start_urls.append(url[0]+'/jingdian')
        for url in self.start_urls:
            print url