package hr.modulit.utils;


import javax.servlet.http.HttpServletRequest;

public class TurbineUtil {

    public static String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
