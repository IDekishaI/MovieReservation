package com.idekishai.moviereservation.auth.config;

import com.idekishai.moviereservation.auth.entities.User;
import com.idekishai.moviereservation.auth.enums.Role;
import com.idekishai.moviereservation.auth.repositories.UserRepository;
import com.idekishai.moviereservation.auth.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        if (oAuth2User == null)
            throw new RuntimeException("OAuth User is Null");

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null)
            throw new RuntimeException("email is Null");

        boolean isNewUser = !userRepository.existsById(email);

        User user;

        if (isNewUser) {
            user = userRepository.save(new User(email, Role.USER));
            log.info("New user registered via Google: {}", email);
        } else {
            user = userRepository.findById(email).orElseThrow();
            log.info("Existing user logged in via Google: {}", email);
        }

        String jwt = jwtService.generateToken(email, name, user.getRole().name());

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + jwt + "\"}");
    }
}
