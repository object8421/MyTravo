#-*- coding:utf-8 -*-
import uuid
import urllib2
from ftplib import FTP
from provence_data_crawler import settings
def image_url_filter(url_list):
    result_list = []
    for url in url_list :
        if "1024x683"  in url:
            result_list.append(url)
    return result_listdef image_url_filter(url_list):
    result_list = []
    for url in url_list :
        if "1024x683"  in url:
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
        
        file_path = settings.PROVINCE_IMAGE_PATH + file_name +'.jpg'
        image_data = urllib2.urlopen(req).read()
        with open(file_path, "wb") as download_data:     
            download_data.write(image_data)
        download_data.close()
        print '%s.jpg 抓取成功！'%file_name
    return image_string

def scrape_str(list):
    '''scrape a list to utf-8 string'''
    scraped_str = ''
    for str in list:
        scraped_str = scraped_str + str
    scraped_str = scraped_str.encode('utf-8')
    return scraped_str

def get_ftp():
    ftp = FTP()
    ftp.connect(settings.FTP_SERVER,settings.FTP_PORT)
    ftp.login(settings.FTP_USER)
    return ftp

def save_image_to_ftp(url_list):
    ftp = get_ftp()
    ftp.cwd(settings.FTP_PROVINCE_PATH)
    image_string = ''
    for url in url_list:
        req = urllib2.Request(url = url)
        file_name = str(uuid.uuid1()) + '.jpg'
        image_string = image_string + file_name +';'
        
        file_path = settings.PROVINCE_IMAGE_PATH + file_name
        image_data = urllib2.urlopen(req).read()
        with open(file_path, "wb") as download_data:     
            download_data.write(image_data)
        download_data.close()
        ftp.storbinary('STOR '+file_name,open(file_path,'rb'))

    print '保存 %s 到 ftp %s 成功。'%(file_name,settings.FTP_SERVER)
    return image_string

        


