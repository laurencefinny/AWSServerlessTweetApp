package com.tweetapp.lambda.tweets.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.dto.TweetsDto;
import com.tweetapp.lambda.requests.TweetRequest;
import com.tweetapp.lambda.response.TweetResponse;

public class UpdateTweetHandler {
	private static final Logger logger = LoggerFactory.getLogger(UpdateTweetHandler.class);

	public APIGatewayProxyResponseEvent updateTweet(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException {
		TweetResponse response = new TweetResponse();
		ObjectMapper mapper = new ObjectMapper();
		TweetRequest tweetRequest = mapper.readValue(request.getBody(), TweetRequest.class);
		TweetsDto tweet = tweetRequest.getTweet();
		try {
			Map<String, AttributeValue> attributeValues = new HashMap<>();
			attributeValues.put("tweet", new AttributeValue().withS(tweet.getTweet()));
			attributeValues.put("tweetId", new AttributeValue().withS(tweet.getTweetId()));
			AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
			UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(System.getenv("TWEETS_TABLE"))
					.addKeyEntry("tweetId", new AttributeValue().withS(tweet.getTweetId()))
					.addAttributeUpdatesEntry("tweet",
							new AttributeValueUpdate().withValue(new AttributeValue().withS(tweet.getTweet())));
			UpdateItemResult updateItemResult = dynamoDB.updateItem(updateItemRequest);
			response.setStatusMessage("SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setStatusMessage("Failure");
		}
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonString);

	}
}
