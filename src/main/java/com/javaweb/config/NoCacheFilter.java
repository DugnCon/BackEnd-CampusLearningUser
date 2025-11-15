package com.javaweb.config;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse res) {
            res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private, max-age=0");
            res.setHeader("Pragma", "no-cache");
            res.setHeader("Expires", "0");
        }
        chain.doFilter(request, response);
    }
}
