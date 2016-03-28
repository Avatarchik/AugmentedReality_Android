package com.freedom.augmentedreality.app;

/**
 * Created by hienbx94 on 3/6/16.
 */
public class AppConfig {
    public static String baseURL = "http://192.168.0.100:3000";
//    public static String baseURL = "http://10.42.0.1:3000";

    public static String URL_LOGIN = baseURL + "/api/sessions";
    public static String URL_REGISTER = baseURL + "/api/users";

    public static String URL_MARKERS = baseURL + "/api/markers";

    public static String URL_CREATE_MARKER  = baseURL + "/api/markers";


}

