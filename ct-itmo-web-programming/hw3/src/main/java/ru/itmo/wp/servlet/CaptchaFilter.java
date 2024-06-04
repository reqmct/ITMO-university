package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.util.Random;

public class CaptchaFilter extends HttpFilter {
    private final String path = "C:/ITMO/web/hw3/src/main/webapp/static";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession();
        if (session.getAttribute("captcha_passed") == null &&
                !(uri.equals("/img/captcha.png") || uri.equals("/css/captcha.css") ||
                        uri.equals("/favicon.ico"))) {
            if (request.getMethod().equals("POST")) {
                String inputNumber = request.getParameter("input_number");
                String expectedNumber = (String) session.getAttribute("expected_number");
                if (expectedNumber != null && expectedNumber.equals(inputNumber)) {
                    session.setAttribute("captcha_passed", "ok");
                    response.sendRedirect(uri);
                }
            }
            generateCaptcha(session);
            File captcha = new File(path, "captcha.html");
            if (captcha.isFile()) {
                response.setContentType(getServletContext().getMimeType(captcha.getName()));
                try (OutputStream outputStream = response.getOutputStream()) {
                    Files.copy(captcha.toPath(), outputStream);
                }
            }
        } else {
            super.doFilter(request, response, chain);
        }
    }

    private void generateCaptcha(HttpSession session) throws IOException {
        File img = new File(path, "/img/captcha.png");
        int captcha_number = new Random().nextInt(900) + 100;
        session.setAttribute("expected_number", String.valueOf(captcha_number));
        FileOutputStream fileOutputStream = new FileOutputStream(img);
        fileOutputStream.write(ImageUtils.toPng(String.valueOf(captcha_number)));
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
