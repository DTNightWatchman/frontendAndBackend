package com.example.demo.constant;

/**
 * @Author: YT
 * @Description:
 * @DateTime: 2022/9/27$ - 19:02
 */
public class UserConstant {
    public static String USER_INFO = "userInfo";

    // 用户角色
    public static int NORMAL_USER = 0;
    public static int ADMIN_USER = 1;

    // 推荐用户缓存时间
    public static int REDIS_RECOMMEND_USER_TIME_OUT = 30000;

    public static String REDIS_PRE_STRING = "findFriend:user:recommend:";

    public static String REDIS_PRECACHE_JOB_DO_CACHE_LOCK = "findFriend:precachejob:docache:lock";
}
