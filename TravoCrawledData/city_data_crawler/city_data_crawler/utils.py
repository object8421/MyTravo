#-*- coding:utf-8 -*-
import uuid
import urllib2

from city_data_crawler import settings
def image_url_filter(url_list):
    result_list = []
    for url in url_list :
        if "720x400"  in url:
            result_list.append(url)
    return result_list

def save_image(url_list):
    '''save the image in the url_list to IMAGE_PATH ,
    return a string of image name ,splited by ;''' 
    image_string = ''
    for url in url_list:
        req = urllib2.Request(url = url)
        file_name = str(uuid.uuid1())
        image_string = image_string + file_name +'.jpg;'
        
        file_path = settings.CITY_IMAGE_PATH + file_name +'.jpg'
        image_data = urllib2.urlopen(req).read()
        with open(file_path, "wb") as download_data:     
            download_data.write(image_data)
        download_data.close()
        print '%s.jpg 抓取成功！'%file_name
    return image_string

def scrape_str(info_list):
    '''scrape a list to utf-8 string'''
    scraped_str = ''
    for info in info_list:
        scraped_str += info
    scraped_str = scraped_str.encode('utf-8')
    print scraped_str
    return scraped_str



        


