package com.tweetapp.lambda.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public class Reply {

	@DynamoDBAttribute
	private String userId;

	@DynamoDBAttribute
	private String replied;

	@DynamoDBAttribute
	private String dateReplied;
		
}
