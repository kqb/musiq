package com.a1337.kt.musiq


object Constants {

    val CONSUMER_KEY = "29a7ad5321907bdd3156510a89944021"
    val CONSUMER_SECRET = "5d822a4c5a957ab67e0b0a5d76a2e3952589b3e8"

    //	public static final String SCOPE 			= "https://www.google.com/m8/feeds/";
    val REQUEST_URL = "https://api.4shared.com/v1_2/oauth/initiate"
    val ACCESS_URL = "https://api.4shared.com/v1_2/oauth/token"
    val AUTHORIZE_URL = "https://api.4shared.com/v1_2/oauth/authorize"

    val API_REQUEST2 = "https://api.4shared.com/v1_2/user"
    val API_REQUEST_FILES = "https://search.4shared.com/v1_2/files"
    val API_DOWNLOAD_FILES = "https://api.4shared.com/v1_2/files"
    val FS_LOGOUT = "http://www.4shared.com/web/logout"

    val ENCODING = "UTF-8"

    val OAUTH_CALLBACK_SCHEME = "x-oauthflow"
    val OAUTH_CALLBACK_HOST = "callback"
    val OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST

    val ACTION_HANDLE_RESULTS = "ACTION_HANDLE_RESULTS"
    val ACTION_HANDLE_MEDIA = "ACTION_HANDLE_MEDIA"


    val QUERY_RESULT = "queryResult"
    val REQUEST_URL_ALIAS = "REQUEST_URL"
    val REQUEST_ACTION_ALIAS = "REQUEST_ACTION"

    val FILE_NAME = "FILE_NAME"


    val OAUTH_ERROR = "OAUTH_ERROR"
    val ACTION_RELOGIN = "ACTION_RELOGIN"


}
