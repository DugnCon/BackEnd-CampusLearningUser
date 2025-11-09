package com.javaweb.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
<<<<<<< HEAD
=======

>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
<<<<<<< HEAD
@Order(Ordered.HIGHEST_PRECEDENCE) // Đặt độ ưu tiên cao nhất
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
=======
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

<<<<<<< HEAD
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5004");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600"); // Cache preflight 1 giờ
=======
        // CHO PHÉP TẤT CẢ TRONG DEV (ngrok + localhost)
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin") != null
                ? request.getHeader("Origin")
                : "*");

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, Content-Type, Accept, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}