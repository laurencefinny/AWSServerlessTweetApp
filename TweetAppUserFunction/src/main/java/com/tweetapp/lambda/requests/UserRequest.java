package com.tweetapp.lambda.requests;

import java.io.Serializable;

import com.tweetapp.lambda.dto.UsersDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8300957195016445421L;
	
	UsersDto userDto;
}

