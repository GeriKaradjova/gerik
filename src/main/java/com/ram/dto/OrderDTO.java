package com.ram.dto;

import java.io.Serializable;

public class OrderDTO implements Serializable
{
	private static final long serialVersionUID = 1L;
	private long id;
	private String orderId;
	private long userId;
	private float price;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getOrderId()
	{
		return orderId;
	}

	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

	public void setUserId( long userId)
	{
		this.userId = userId;
	}

	public long getUserId()
	{
		return userId;
	}

	public float getPrice()
	{
		return price;
	}

	public void setPrice(float price)
	{
		this.price = price;
	}

}
