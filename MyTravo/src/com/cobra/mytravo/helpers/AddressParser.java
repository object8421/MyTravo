package com.cobra.mytravo.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.android.gms.maps.model.LatLng;

public class AddressParser
{
	public static String parse(double latitude, double longitude) 	
	{
		HttpClient httpclient;
		String url = "http://maps.google.com/maps/api/geocode/xml?latlng=" +
				latitude+"," + longitude +
				"&language=zh-CN&sensor=false";
		httpclient = new HttpClient();
		InputStream input = null;
		String str;
		GetMethod getMethod = new GetMethod(url);
		int statusCode = 0;
		try
		{
			statusCode = httpclient.executeMethod(getMethod);
		} catch (HttpException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if(statusCode == HttpStatus.SC_OK)
		{
			try
			{
				input = getMethod.getResponseBodyAsStream();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}  
	    Document document=null;
		try
		{
			document = builder.parse(input);
		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}  
	    Element element = document.getDocumentElement();
	    
	    NodeList resultNodes = element.getElementsByTagName("result");
	    Element resultElement = (Element) resultNodes.item(0); 
	    NodeList childList = resultElement.getChildNodes();
	    Node node = null;
	    for(int i = 0 ; i < childList.getLength(); i++)
	    {
	    	if(childList.item(i).getNodeName().equals("formatted_address"))
	    	{
	    		node = childList.item(i);
	    	}
	    }
		return node.getFirstChild().getNodeValue();
	}
//	
//	public static void main(String[] args)
//	{
//		AddressParser.parse(new LatLng(36, 7));
//	}
}
