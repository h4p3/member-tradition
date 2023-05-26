package com.h4p3.constant;

public class MemberConstants {

    /**
     * token request 请求头
     */
    public static final String REQUEST_HEAD_AUTH = "Auth";
    /**
     * token 前缀
     */
    public static final String BEARER = "Bearer ";

    /**
     * 登录前缀KEY
     */
    public static final String LOGIN_KEY = "login_user_key";

    /**
     * 刷新token差集
     */
    public static final long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    /**
     * 登录失效时间
     */
    public static final long LOGIN_EXPIRE_TIME = 30;

}
