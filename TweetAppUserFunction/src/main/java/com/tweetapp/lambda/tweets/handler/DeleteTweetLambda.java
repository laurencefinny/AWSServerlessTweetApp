package com.tweetapp.lambda.tweets.handler;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.response.TweetResponse;

public class DeleteTweetLambda {
	public APIGatewayProxyResponseEvent deleteTweet(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException {
		TweetResponse response = new TweetResponse();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> pathParameters = request.getPathParameters();
		String username = pathParameters.get("username");
		String tweetId = pathParameters.get("id");
		try {
			DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
			Table table = dynamoDB.getTable(System.getenv("TWEETS_TABLE"));
			DeleteItemOutcome outcome = table.deleteItem("tweetId", tweetId);
			response.setStatusMessage("SUCCESS");

		} catch (Exception e) {
			// TODO: handle exception
			response.setStatusMessage("FAILURE");
		}
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonString);

	}
}
