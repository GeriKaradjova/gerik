package com.ram.service;

import com.ram.dto.OrderDTO;

import java.util.List;

public interface OrderService
{
	public OrderDTO createOrder(OrderDTO orderDTO);
	public OrderDTO getOrder(long id) throws Exception;
	public OrderDTO getOrderByOrderId(String orderId) throws Exception;
	public OrderDTO updateOrder(String orderId,OrderDTO orderDTO);
	public void deleteOrder(String orderId);
	public List<OrderDTO> getOrders(int page, int limit);
	
}
