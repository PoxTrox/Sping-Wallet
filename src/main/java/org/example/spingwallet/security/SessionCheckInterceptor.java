package org.example.spingwallet.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.model.UserRole;
import org.example.spingwallet.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class SessionCheckInterceptor implements HandlerInterceptor {

    private final Set<String> UNAUTHORIZED_ENDPOINTS = Set.of("/", "/login", "/register");
    private final Set<String>ADMIN_ENDPOINTS = Set.of("/users", "/reposts");
    private final UserService userService;

    public SessionCheckInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String endPoint = request.getServletPath();

        if (UNAUTHORIZED_ENDPOINTS.contains(endPoint)) {
            return true;
        }
        HttpSession currentUserSession = request.getSession(false);

        if (currentUserSession == null) {
            response.sendRedirect("/login");
            return false;
        }

        UUID userId = (UUID) (currentUserSession.getAttribute("user_id"));
        User user = userService.getById(userId);

        if (!user.isActive()) {
            currentUserSession.invalidate();
            response.sendRedirect("/login");
        }
        // first way
//        if(ADMIN_ENDPOINTS.contains(endPoint) && user.getRole() != UserRole.ADMIN) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.getWriter().write("You are not allowed to access this resource.");
//                    return false;
//        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if(handlerMethod.getMethod().isAnnotationPresent(RequireAdminRole.class ) && user.getRole() != UserRole.ADMIN) {


            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("You are not allowed to access this resource.");
                    return false;

        }

        return true;

    }
}
