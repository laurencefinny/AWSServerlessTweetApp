package com.tweetapp.lambda.response;

import java.io.Serializable;
import java.util.List;

import com.tweetapp.lambda.dto.TweetsDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TweetResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3452495145437447139L;
	private List<TweetsDto> tweetsDto;
	private String statusMessage;
}
