package com.example.utils;

/**
 * 常用常量属性工具类
 */
public class Const {
    // JWT黑名单前缀
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data";
    public static final String FLOW_LIMIT_COUNT = "flow:limit:count";
    public static final String FLOW_LIMIT_BLOCK = "flow:limit:block";

    //    限流过滤器顺序级别
    public static final int ORDER_LIMIT = -101;
    //    跨域请求过滤顺序级别
    public static final int ORDER_CORS = -102;
    // 状态码：200
    public static final int STATUS_CODE_200 = 200;
    // 状态码：400
    public static final int STATUS_CODE_400 = 400;
    // 状态码：401
    public static final int STATUS_CODE_401 = 401;
    // 状态码：403
    public static final int STATUS_CODE_403 = 403;
    // 状态码：500
    public static final int STATUS_CODE_500 = 500;

}