package com.tweetapp.lambda.tweets.handler;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.dto.TweetsDto;
import com.tweetapp.lambda.requests.TweetRequest;
import com.tweetapp.lambda.response.TweetResponse;

public class UpdateTweetHandler {
	public APIGatewayProxyResponseEvent updateTweet(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException {
		TweetResponse response = new TweetResponse();
		ObjectMapper mapper = new ObjectMapper();
		TweetRequest tweetRequest = mapper.readValue(request.getBody(), TweetRequest.class);
		TweetsDto tweet = tweetRequest.getTweet();
		try {
			DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
			TweetsDto load = dynamoDBMapper.load(TweetsDto.class, tweet.getTweetId());
			load.setTweet(tweet.getTweet());
			dynamoDBMapper.save(load);
			response.setStatusMessage("SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			response.setStatusMessage("Failure");
		}
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonString);

	}
}
