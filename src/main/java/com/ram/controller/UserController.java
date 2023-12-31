package com.ram.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.ram.dto.AddressDTO;
import com.ram.dto.UserDTO;
import com.ram.exception.UserServiceException;
import com.ram.model.request.UserDetailsRequestModel;
import com.ram.model.response.OperationStatusModel;
import com.ram.model.ui.AddressesRest;
import com.ram.model.ui.UserRest;
import com.ram.service.AddressService;
import com.ram.service.UserService;
import com.ram.utils.ErrorMessages;
import com.ram.utils.RequestOperationName;
import com.ram.utils.RequestOperationResult;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:8080","http://localhost:8081"})
public class UserController
{
	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	//@CrossOrigin(origins = {"*", "http://localhost:9090", "http://localhost:9090", "http://localhost:9091"})
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
	{
		if (userDetails.getEmail().isEmpty())
		{
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELDS.getErrorMessage());
		}

		ModelMapper modelMapper = new ModelMapper();
		UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);

		UserDTO createdUserDTO = userService.createUser(userDTO);

		return modelMapper.map(createdUserDTO, UserRest.class);
	}

	
	@ApiOperation(value="Get User Details Web Service EndPoint",
			notes="This Web Service EndPoint returns User Details, Need to pass public user id in the URL")
	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Bearer JWT Token",paramType="header")})
	@GetMapping(path = "/{id}",
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest getUser(@PathVariable("id") String userId)
	{
		UserDTO userDTO = userService.getUserByUserId(userId);
		ModelMapper modelMapper = new ModelMapper();
		UserRest userRest = modelMapper.map(userDTO, UserRest.class);
		return userRest;
	}

	@PutMapping(path = "/{id}",
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest updateUser(@PathVariable("id") String userId, @RequestBody UserDetailsRequestModel userDetails) throws Exception
	{
		if (userDetails.getEmail().isEmpty())
		{
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELDS.getErrorMessage());
		}

		ModelMapper modelMapper = new ModelMapper();
		UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);
		
		UserDTO updatedUserDTO = userService.updateUser(userId, userDTO);
		
		UserRest userRest = modelMapper.map(updatedUserDTO, UserRest.class);
		return userRest;
	}

	@DeleteMapping(path = "/{id}")
	public OperationStatusModel deleteUser(@PathVariable("id") String userId)
	{
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		operationStatusModel.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(userId);

		operationStatusModel.setOperationResult(RequestOperationResult.SUCCESS.name());
		return operationStatusModel;
	}

	@GetMapping(path = {"/"}, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "3") int limit)
	{
		List<UserRest> userRestList = new ArrayList<UserRest>();

		List<UserDTO> userDTOList = userService.getUsers(page, limit);
		System.out.println("asd");
		if (userDTOList != null && !userDTOList.isEmpty())
		{
			java.lang.reflect.Type userRestListType = new TypeToken<List<UserRest>>(){}.getType();
			ModelMapper modelMapper = new ModelMapper();
			userRestList = modelMapper.map(userDTOList, userRestListType);
		}

		return userRestList;
	}

	@GetMapping(path = "/{id}/addresses",
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,"application/hal+json" })
	public Resources<AddressesRest> getUserAddresses(@PathVariable("id") String userId)
	{
		List<AddressesRest> addressRestList = new ArrayList<>();

		List<AddressDTO> addressDTOList = addressService.getAddresses(userId);

		if (addressDTOList != null && !addressDTOList.isEmpty())
		{
			java.lang.reflect.Type AddressesRestListType = new TypeToken<List<AddressesRest>>(){}.getType();
			ModelMapper modelMapper = new ModelMapper();
			addressRestList = modelMapper.map(addressDTOList, AddressesRestListType);
			
			for (AddressesRest addressesRest : addressRestList)
			{
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId,addressesRest.getAddressId())).withSelfRel();	
				Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
				
				addressesRest.add(addressLink);
				addressesRest.add(userLink);
			}
		}

		return new Resources<>(addressRestList);
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}",
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,"application/hal+json" })
	public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId)
	{

		AddressesRest addressesRest = null;

		AddressDTO addressDTO = addressService.getAddress(userId, addressId);

		if (addressDTO != null)
		{
			ModelMapper modelMapper = new ModelMapper();
			addressesRest = modelMapper.map(addressDTO, AddressesRest.class);
			
			//Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();	
			//Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
			//Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");	
			
			
			Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId,addressId)).withSelfRel();	
			Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
			Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");	
			
			addressesRest.add(addressLink);
			addressesRest.add(userLink);
			addressesRest.add(addressesLink);
		}
		
		return new Resource<>(addressesRest);
	}

}
