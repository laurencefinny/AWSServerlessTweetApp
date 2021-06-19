package com.tweetapp.lambda.user.handler;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.dto.UsersDto;
import com.tweetapp.lambda.requests.UserRequest;
import com.tweetapp.lambda.response.UserResponse;

public class CreateUserLambda {

	public APIGatewayProxyResponseEvent createUser(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException {
		UserResponse response = new UserResponse();
		ObjectMapper mapper = new ObjectMapper();
		UserRequest userRequest = mapper.readValue(request.getBody(), UserRequest.class);
		UsersDto userDto = userRequest.getUserDto();
		try {
			DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
			Table table = dynamoDB.getTable(System.getenv("USERS_TABLE"));
			Item item = new Item().withPrimaryKey("loginId", userDto.getLoginId())
					.withString("firstName", userDto.getFirstName()).withString("lastName", userDto.getLastName())
					.with("emailId", userDto.getEmailId())
					.withString("contactNumber", userDto.getContactNumber());
			table.putItem(item);
			response.setStatusMessage("SUCCESS");

		} catch (Exception e) {
			// TODO: handle exception
			response.setStatusMessage("User Already Exists");
		}
		Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*"); //
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(headers).withBody(jsonString);

	}
}
