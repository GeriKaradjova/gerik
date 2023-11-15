package com.ram.model.request;

import java.util.List;

public class OrderDetailsRequestModel
{
	private long userId;
	private float price;

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
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
