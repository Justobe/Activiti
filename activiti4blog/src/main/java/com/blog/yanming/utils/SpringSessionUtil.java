package com.blog.yanming.utils;

import com.blog.yanming.entity.User;

import javax.servlet.http.HttpSession;

/**
 * Created by YanMing on 2017/7/13.
 */
public class SpringSessionUtil {

    public static final String USER = "user";

    public static void setSession(HttpSession session, User user)
    {
        session.setAttribute(USER, user);
    }

    public static User getSession(HttpSession session) {
        Object attribute = session.getAttribute(USER);
        return attribute == null ? null : (User) attribute;
    }
}
