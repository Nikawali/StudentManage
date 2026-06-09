package org.example.servlet;

import org.example.entity.User;
import org.example.exception.BusinessesException;
import org.example.service.UserService;
import org.example.servicrImpl.UserServiceImpl;
import org.example.utils.LoginResult;
import org.example.utils.Result;
import org.example.utils.WriteJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private UserService userService =
            new UserServiceImpl();

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers",
                "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "3600");

        req.setCharacterEncoding("UTF-8");

        try {

            String action =
                    req.getParameter("action");


            Result<?> result;

            switch (action) {

                case "login":

                    result =
                            Result.success(
                                    "登录成功",
                                    login(req)
                            );
                    break;

                default:

                    result =
                            Result.fail(
                                    400,
                                    "未知操作"
                            );
            }

            WriteJson.writeJson(
                    resp,
                    result
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            "服务器内部异常"
                    )
            );
        }
    }

    private LoginResult<?> login(
            HttpServletRequest req) {

        User user =
                buildUser(req);

        return userService.login(user);
    }

    private User buildUser(
            HttpServletRequest req) {

        User user = new User();

        user.setUserId(
                Long.parseLong(
                        req.getParameter("userId")
                )
        );

        user.setPassword(
                req.getParameter("password")
        );

        return user;
    }
}