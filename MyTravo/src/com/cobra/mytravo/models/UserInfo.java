package com.cobra.mytravo.models;

import java.io.Serializable;

public class UserInfo extends BaseType implements Serializable
{
	private String phone;
	private String mobile;
	private String qq;
	private String name;
	private int age;
	private String sex;
	private String address;
	private String address2;
	private String native_place;
	private String degree;
	private String job;
	public String getPhone()
	{
		return phone;
	}
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public String getMobile()
	{
		return mobile;
	}
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}
	public String getQq()
	{
		return qq;
	}
	public void setQq(String qq)
	{
		this.qq = qq;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getAge()
	{
		return age;
	}
	public void setAge(int age)
	{
		this.age = age;
	}
	public String getSex()
	{
		return sex;
	}
	public void setSex(String sex)
	{
		this.sex = sex;
	}
	public String getAddress()
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	public String getAddress2()
	{
		return address2;
	}
	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}
	public String getNative_place()
	{
		return native_place;
	}
	public void setNative_place(String native_place)
	{
		this.native_place = native_place;
	}
	public String getDegree()
	{
		return degree;
	}
	public void setDegree(String degree)
	{
		this.degree = degree;
	}
	public String getJob()
	{
		return job;
	}
	public void setJob(String job)
	{
		this.job = job;
	}
}
