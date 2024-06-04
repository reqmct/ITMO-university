package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MessageServlet extends HttpServlet {
    private static class Pair {
        public final String user;
        public final String text;

        Pair(final String user, final String text) {
            this.user = user;
            this.text = text;
        }
    }

    List<Pair> usersText = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession();
        OutputStream outputStream = response.getOutputStream();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        switch (uri) {
            case "/message/auth":
                String paramUser = request.getParameter("user");
                if (paramUser != null && !paramUser.isBlank()) {
                    session.setAttribute("user", paramUser);
                } else {
                    paramUser = "";
                }
                outputStream.write(new Gson().toJson(paramUser).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                break;
            case "/message/findAll":
                outputStream.write(new Gson().toJson(usersText).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                break;
            case "/message/add":
                String paramText = request.getParameter("text");
                if (session.getAttribute("user") != null) {
                    String attributeUser = (String) session.getAttribute("user");
                    usersText.add(new Pair(attributeUser, paramText));
                }
                break;
        }
    }
}