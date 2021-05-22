package com.tweetapp.lambda.tweets.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.lambda.dto.Reply;
import com.tweetapp.lambda.dto.TweetsDto;
import com.tweetapp.lambda.response.TweetResponse;

public class ReadAllTweetsLambda {
	public APIGatewayProxyResponseEvent readTweets()
			throws JsonMappingException, JsonProcessingException, ParseException {
		TweetResponse response = new TweetResponse();

		ObjectMapper mapper = new ObjectMapper();
		List<Reply> replies = new ArrayList<Reply>();
		try {
			AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
			ScanResult scan = dynamoDB.scan(new ScanRequest().withTableName(System.getenv("TWEETS_TABLE")));
			List<TweetsDto> collect = scan.getItems().stream()
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
		String jsonString = mapper.writeValueAsString(response);
		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonString);

	}

	private Reply Reply(String s, String s2, String s3) throws ParseException {
		// TODO Auto-generated method stub
		Reply reply = new Reply();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		reply.setUserId(s2);
		reply.setReplied(s);
		Date date1 = null;
		try {
			if (s3 != null && !(StringUtils.isNullOrEmpty(s3))) {
				date1 = dateFormatter.parse(s3);
			} else {
				date1 = dateFormatter.parse("Thu May 27 18:34:09 UTC 2021");
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			date1 = dateFormatter.parse("Thu May 28 18:34:09 UTC 2021");
		}
		reply.setDateReplied(date1);
		return reply;
	}
}
