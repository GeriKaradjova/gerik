package com.ram.controller;

import com.ram.dto.AddressDTO;
import com.ram.dto.OrderDTO;
import com.ram.exception.OrderServiceException;
import com.ram.model.request.OrderDetailsRequestModel;
import com.ram.model.response.OperationStatusModel;
import com.ram.model.ui.AddressesRest;
import com.ram.model.ui.OrderRest;
import com.ram.service.AddressService;
import com.ram.service.OrderService;
import com.ram.utils.ErrorMessages;
import com.ram.utils.RequestOperationName;
import com.ram.utils.RequestOperationResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = {"http://localhost:8080","http://localhost:8081"})
public class OrderController
{
	@Autowired
	private OrderService orderService;

	@Autowired
	private AddressService addressService;

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE},
			produces = { MediaType.APPLICATION_JSON_VALUE})
	//@CrossOrigin(origins = {"*", "http://localhost:9090", "http://localhost:9090", "http://localhost:9091"})
	public OrderRest createOrder(@RequestBody OrderDetailsRequestModel orderDetails) throws Exception
	{
		ModelMapper modelMapper = new ModelMapper();
		OrderDTO orderDTO = modelMapper.map(orderDetails, OrderDTO.class);

		OrderDTO createdOrderDTO = orderService.createOrder(orderDTO);

		OrderRest orderRest = modelMapper.map(createdOrderDTO, OrderRest.class);
		return orderRest;
	}

	
	@ApiOperation(value="Get Order Details Web Service EndPoint",
			notes="This Web Service EndPoint returns Order Details, Need to pass public order id in the URL")
	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Bearer JWT Token",paramType="header")})
	@GetMapping(path = "/{id}",
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OrderRest getOrder(@PathVariable("id") String orderId) throws Exception {
		OrderDTO orderDTO = orderService.getOrderByOrderId(orderId);
		ModelMapper modelMapper = new ModelMapper();
		OrderRest orderRest = modelMapper.map(orderDTO, OrderRest.class);
		return orderRest;
	}

	@PutMapping(path = "/{id}",
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OrderRest updateOrder(@PathVariable("id") String orderId, @RequestBody OrderDetailsRequestModel orderDetails) throws Exception
	{
		ModelMapper modelMapper = new ModelMapper();
		OrderDTO orderDTO = modelMapper.map(orderDetails, OrderDTO.class);
		
		OrderDTO updatedOrderDTO = orderService.updateOrder(orderId, orderDTO);
		
		OrderRest orderRest = modelMapper.map(updatedOrderDTO, OrderRest.class);
		return orderRest;
	}

	@DeleteMapping(path = "/{id}")
	public OperationStatusModel deleteOrder(@PathVariable("id") String orderId)
	{
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		operationStatusModel.setOperationName(RequestOperationName.DELETE.name());

		orderService.deleteOrder(orderId);

		operationStatusModel.setOperationResult(RequestOperationResult.SUCCESS.name());
		return operationStatusModel;
	}

//	@GetMapping(path = {"/"}, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
//	public List<OrderRest> getOrders(@RequestParam(value = "page", defaultValue = "0") int page,
//			@RequestParam(value = "limit", defaultValue = "3") int limit)
//	{
//		List<OrderRest> orderRestList = new ArrayList<OrderRest>();
//
//		List<OrderDTO> orderDTOList = orderService.getOrders(page, limit);
//		if (orderDTOList != null && !orderDTOList.isEmpty())
//		{
//			java.lang.reflect.Type orderRestListType = new TypeToken<List<OrderRest>>(){}.getType();
//			ModelMapper modelMapper = new ModelMapper();
//			orderRestList = modelMapper.map(orderDTOList, orderRestListType);
//		}
//
//		return orderRestList;
//	}
}
