package com.tweetapp.lambda.tweets.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.dto.Reply;
import com.tweetapp.lambda.dto.TweetsDto;
import com.tweetapp.lambda.response.TweetResponse;

public class ReadAllTweetsByUserLambda {
	public APIGatewayProxyResponseEvent readAllUserTweets(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException, ParseException {
		TweetResponse response = new TweetResponse();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> pathParameters = request.getPathParameters();
		String userTweetId = pathParameters.get("username");
		try {
			AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
			ScanResult scan = dynamoDB.scan(new ScanRequest().withTableName(System.getenv("TWEETS_TABLE")));
			List<TweetsDto> collect = scan.getItems().stream()
					.filter(item -> item.get("userTweetId").getS().equals(userTweetId))
					.map(item -> new TweetsDto(item.get("tweet").getS(), item.get("userTweetId").getS(),
							item.get("tweetId").getS(), Long.valueOf(item.get("like").getN()),
							item.get("reply").getL().stream().map(e -> {
								com.tweetapp.lambda.dto.Reply reply = null;
								try {
									reply = Reply(e.getM().get("replied").getS(), e.getM().get("userId").getS(),
											e.getM().get("dateReplied").getS());
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								return reply;
							}).collect(Collectors.toList()), item.get("dateOfPost").getS(),
							item.get("dateOfPost").getS()))
					.collect(Collectors.toList());
			response.setTweetsDto(collect);
			response.setStatusMessage("SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			response.setStatusMessage("FAIURE");
		}
		Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*"); //
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(headers).withBody(jsonString);

	}

	private Reply Reply(String s, String s2, String s3) throws ParseException {
		// TODO Auto-generated method stub
		Reply reply = new Reply();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		reply.setUserId(s2);
		reply.setReplied(s);
		reply.setDateReplied(s3);
		return reply;
	}
}
