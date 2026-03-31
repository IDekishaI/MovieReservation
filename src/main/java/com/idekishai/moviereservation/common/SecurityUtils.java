package com.idekishai.moviereservation.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class SecurityUtils {
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new RuntimeException("No authentication found");

        String email = (String) authentication.getPrincipal();
        if (email == null)
            throw new RuntimeException("Email not found in Authentication");

        return email;
    }
}
