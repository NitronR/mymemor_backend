package com.mymemor.mymemor;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int MAX_SUGGESTION_LIST_LENGTH = 5;
    public static final int MAX_SEARCH_PAGE_SIZE = 10;
    public static final String COOKIES_NAME = "USER_ID";

    private static Map<String, String> javaToJsonName = new HashMap<>();

    static {
        javaToJsonName.put("profilePicURL", "profile_pic_url");
        javaToJsonName.put("schoolName", "school");
        javaToJsonName.put("collegeName", "college");
        javaToJsonName.put("currentCity", "current_city");
    }

    public static String getJsonName(String javaName) {
        return javaToJsonName.get(javaName);
    }
}
