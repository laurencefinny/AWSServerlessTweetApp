package com.tweetapp.lambda.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	private Date dateReplied;

	public Reply(String userId, String replied, String dateReplied) throws ParseException {
		super();
		this.userId = userId;
		this.replied = replied;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD/MM/YY");
		this.dateReplied = simpleDateFormat.parse(dateReplied) ;
	}
	
	
}
