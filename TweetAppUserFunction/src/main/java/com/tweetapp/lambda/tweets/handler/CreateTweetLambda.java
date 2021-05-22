package com.tweetapp.lambda.tweets.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.dto.Reply;
import com.tweetapp.lambda.dto.TweetsDto;
import com.tweetapp.lambda.requests.TweetRequest;
import com.tweetapp.lambda.response.TweetResponse;

public class CreateTweetLambda {
	public APIGatewayProxyResponseEvent createTweet(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException {
		TweetResponse response = new TweetResponse();
		ObjectMapper mapper = new ObjectMapper();
		TweetRequest tweetRequest = mapper.readValue(request.getBody(), TweetRequest.class);
		TweetsDto tweet = tweetRequest.getTweet();
		List<Reply> replies = new ArrayList<Reply>();
		Map<String, String> pathParameters = request.getPathParameters();
		String userTweetId = pathParameters.get("username");
		try {
			DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
			Table table = dynamoDB.getTable(System.getenv("TWEETS_TABLE"));
			Item item = new Item().withPrimaryKey("tweetId", UUID.randomUUID().toString())
					.with("tweet", tweet.getTweet()).with("userTweetId", userTweetId).withList("reply", replies)
					.withLong("like", 0l).with("dateOfPost", new Date().toString());
			table.putItem(item);
			response.setStatusMessage("SUCCESS");

		} catch (Exception e) {
			// TODO: handle exception
			response.setStatusMessage("User Already Exists");
		}
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonString);

	}
}
