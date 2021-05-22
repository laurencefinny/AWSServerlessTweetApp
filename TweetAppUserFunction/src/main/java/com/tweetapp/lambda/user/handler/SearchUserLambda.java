package com.tweetapp.lambda.user.handler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.dto.UsersDto;
import com.tweetapp.lambda.response.UserResponse;

public class SearchUserLambda {
	public APIGatewayProxyResponseEvent searchUsers(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException {
		UserResponse response = new UserResponse();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> pathParameters = request.getPathParameters();
		String userTweetId = pathParameters.get("username");
		try {
			AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
			ScanResult scan = dynamoDB.scan(new ScanRequest().withTableName(System.getenv("USERS_TABLE")));
			List<UsersDto> collect = scan.getItems().stream().filter(item -> item.get("loginId").getS().equals(userTweetId))
					.map(item -> new UsersDto(item.get("loginId").getS(), item.get("firstName").getS(),
							item.get("lastName").getS(), item.get("emailId").getS(), "password",
							item.get("contactNumber").getS()))
					.collect(Collectors.toList());
			response.setUsersDto(collect);
			response.setStatusMessage("SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			response.setStatusMessage("FAILURE");
		}
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonString);

	}
}
