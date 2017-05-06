package com.a1337.kt.musiq;


public class Constants {

	public static final String CONSUMER_KEY 	= "29a7ad5321907bdd3156510a89944021";
	public static final String CONSUMER_SECRET 	= "5d822a4c5a957ab67e0b0a5d76a2e3952589b3e8";

//	public static final String SCOPE 			= "https://www.google.com/m8/feeds/";
	public static final String REQUEST_URL 		= "https://api.4shared.com/v1_2/oauth/initiate";
	public static final String ACCESS_URL 		= "https://api.4shared.com/v1_2/oauth/token";
	public static final String AUTHORIZE_URL 	= "https://api.4shared.com/v1_2/oauth/authorize";

	public static final String API_REQUEST2		= "https://api.4shared.com/v1_2/user";
	public static final String API_REQUEST 		= "https://search.4shared.com/v1_2/files?query=big%20bang%20loser&type=mp3&sort=size,desc&limit=10&addFields=id3";
	public static final String FS_LOGOUT		= "http://www.4shared.com/web/logout";
	
	public static final String ENCODING 		= "UTF-8";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

}
