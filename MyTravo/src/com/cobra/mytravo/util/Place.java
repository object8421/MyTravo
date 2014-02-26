package com.cobra.mytravo.util;

public class Place
{
	private String name;
	
	private String address;
	
	private String reference;
	
	private String latitude;
	
	private String longitude;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getReference()
	{
		return reference;
	}

	public void setReference(String reference)
	{
		this.reference = reference;
	}
	
	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}

	public Place(String name, String address, String reference)
	{
		this.name = name;
		this.address = address;
		this.reference = reference;
	}
}
