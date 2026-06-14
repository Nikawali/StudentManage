package org.example.utils;

import com.alibaba.fastjson2.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WriteJson {
    public static void writeJson(
            HttpServletResponse resp,
            Object obj)
            throws IOException {

        resp.setContentType(
                "application/json;charset=UTF-8");

        resp.getWriter().write(
                JSON.toJSONString(obj));
    }
}
