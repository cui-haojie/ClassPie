package org.example.classpiserver.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.classpiserver.config.AuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_ACCOUNT = "authAccount";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthProperties authProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!authProperties.isEnabled()) {
            return true;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"未登录或登录已过期\"}");
            return false;
        }
        try {
            String account = jwtService.parseAccount(auth.substring(7).trim());
            request.setAttribute(ATTR_ACCOUNT, account);
            return true;
        } catch (Exception ex) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"登录凭证无效\"}");
            return false;
        }
    }
}
