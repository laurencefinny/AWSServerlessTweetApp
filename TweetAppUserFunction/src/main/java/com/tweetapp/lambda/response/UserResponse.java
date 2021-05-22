package com.tweetapp.lambda.response;

import java.io.Serializable;
import java.util.List;

import com.tweetapp.lambda.dto.UsersDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5432683200958075030L;

	private List<UsersDto> usersDto;

	private String statusMessage;
}
