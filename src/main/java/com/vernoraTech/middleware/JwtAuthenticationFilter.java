package com.vernoraTech.middleware;

import com.vernoraTech.service.JwtTokenService;
import com.vernoraTech.service.AuthService;
import com.vernoraTech.entity.UserCredentials;
import com.vernoraTech.repository.UserCredentialsRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserCredentialsRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                   UserCredentialsRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenService.validateToken(jwt)) {
                String email = jwtTokenService.getEmailFromToken(jwt);
                Long userId = jwtTokenService.getUserIdFromToken(jwt);

                Optional<UserCredentials> userOpt = userRepository.findByEmail(email);

                if (userOpt.isPresent() && userOpt.get().getIsActive()) {
                    UserCredentials user = userOpt.get();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    null,
                                    null // add roles later if needed
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    request.setAttribute("currentUserId", userId);
                    request.setAttribute("currentUserEmail", email);
                    request.setAttribute("currentUser", user);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
