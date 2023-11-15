package com.ram.service.impl;

import com.ram.dto.AddressDTO;
import com.ram.dto.OrderDTO;
import com.ram.entity.AddressEntity;
import com.ram.entity.OrderEntity;
import com.ram.exception.OrderServiceException;
import com.ram.repository.AddressRepository;
import com.ram.repository.OrderRepository;
import com.ram.service.OrderService;
import com.ram.utils.ErrorMessages;
import com.ram.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService
{

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	Utils utils;


	@Override
	public OrderDTO createOrder(OrderDTO orderDTO)
	{

		ModelMapper modelMapper = new ModelMapper();
		OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);

		String publicOrderId = utils.generateUserId(20);
		orderEntity.setOrderId(publicOrderId);

		long userId = orderDTO.getUserId();
		orderEntity.setUserId(userId);

		float orderPrice = orderDTO.getPrice();
		orderEntity.setPrice(orderPrice);


		OrderEntity storedOrderEntity = orderRepository.save(orderEntity);

		return modelMapper.map(storedOrderEntity, OrderDTO.class);
	}

	public OrderDTO getOrder(long id) throws Exception {
		OrderEntity orderEntity = orderRepository.findById(id);
		if (orderEntity == null)
		{
			throw new Exception(String.valueOf(id));
		}
		OrderDTO returnOrderDTO = new OrderDTO();
		BeanUtils.copyProperties(orderEntity, returnOrderDTO);
		return returnOrderDTO;
	}

	@Override
	public OrderDTO getOrderByOrderId(String orderId) throws Exception {
		OrderEntity orderEntityByOrderId = orderRepository.findByOrderId(orderId);

		if (orderEntityByOrderId == null)
		{
			throw new Exception(orderId);
		}

		ModelMapper modelMapper = new ModelMapper();

		return modelMapper.map(orderEntityByOrderId, OrderDTO.class);
	}

	@Override
	public OrderDTO updateOrder(String orderId, OrderDTO orderDTO)
	{
		OrderEntity orderEntityfromDB = orderRepository.findByOrderId(orderId);

		if (orderEntityfromDB == null)
		{
			throw new OrderServiceException(ErrorMessages.RECORD_NOT_FOUND.getErrorMessage());
		}

		ModelMapper modelMapper = new ModelMapper();	

		OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);
		orderEntity.setId(orderEntityfromDB.getId());
		orderEntity.setOrderId(orderEntityfromDB.getOrderId());
		orderEntity.setUserId(orderEntityfromDB.getUserId());

		OrderEntity updatedOrderEntity = orderRepository.save(orderEntity);

		return modelMapper.map(updatedOrderEntity, OrderDTO.class);
	}

	@Override
	public void deleteOrder(String orderId)
	{
		OrderEntity orderEntityByOrderId = orderRepository.findByOrderId(orderId);

		if (orderEntityByOrderId == null)
		{
			throw new OrderServiceException(ErrorMessages.RECORD_NOT_FOUND.getErrorMessage());
		}
		orderRepository.delete(orderEntityByOrderId);

	}

	@Override
	public List<OrderDTO> getOrders(int page, int limit)
	{
		List<OrderDTO> orderDTOList = new ArrayList<OrderDTO>();

		if (page > 0)
		{
			page = page - 1;
		}

		Pageable pageable = PageRequest.of(page, limit);
		Page<OrderEntity> ordersPage = orderRepository.findAll(pageable);

		List<OrderEntity> orderEntityList = ordersPage.getContent();

		for (OrderEntity orderEntity : orderEntityList)
		{
			
			ModelMapper modelMapper = new ModelMapper();
			OrderDTO orderDTO = modelMapper.map(orderEntity, OrderDTO.class);
			orderDTOList.add(orderDTO);
		}

		return orderDTOList;
	}

}
