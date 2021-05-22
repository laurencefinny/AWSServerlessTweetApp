package com.tweetapp.lambda.requests;

import com.tweetapp.lambda.dto.TweetsDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TweetRequest {

	private TweetsDto tweet;
}
