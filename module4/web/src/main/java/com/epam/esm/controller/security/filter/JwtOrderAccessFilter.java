package com.epam.esm.controller.security.jwt.filter;

import com.epam.esm.controller.rest.handler.ApiError;
import com.epam.esm.controller.security.JwtAuthenticationException;
import com.epam.esm.controller.security.jwt.JwtTokenProvider;
import com.epam.esm.domain.User;
import com.epam.esm.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * JwtOrderAccessFilter
 *
 * @author alex
 * @version 1.0
 * @since 11.05.22
 */
@Component
@AllArgsConstructor
@Slf4j
public class JwtOrderAccessFilter extends OncePerRequestFilter {

    private static final String USERS = "/users/";

    private static final String ORDERS = "/orders/";

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws ServletException, IOException {

        log.info("IN doFilterInternal");
        String uri = req.getRequestURI();
        if (uri.contains(USERS) && uri.contains(ORDERS)) {
            log.info("In JwtOrderAccessFilter - user trying to access order, uri: {}", uri);

            User user = getUserFromRequest(req);
            boolean haveAccessToOrder = isUserHaveAccessToOrder(user, uri);

            if (!haveAccessToOrder) {
                throw new JwtAuthenticationException("User tries to access not owned order");
            }
        }
        chain.doFilter(req, resp);
    }

    private boolean isUserHaveAccessToOrder(User user, String uri) {
        boolean isUserOrder = user.getId().equals(getUserIdFromUri(uri));
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));

        return isUserOrder || isAdmin;
    }

    private User getUserFromRequest(HttpServletRequest req) {
        String token = jwtTokenProvider.resolveToken(req);
        String username = jwtTokenProvider.getUsername(token);
        return userService.findByUsername(username);
    }

    private Long getUserIdFromUri(String uri) {
        String part = uri.replace(USERS, "");
        return Long.parseLong(part.substring(0, part.indexOf('/')));
    }
}