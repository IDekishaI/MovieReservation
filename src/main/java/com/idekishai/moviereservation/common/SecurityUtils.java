package com.idekishai.moviereservation.common;

import com.idekishai.moviereservation.auth.dtos.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class SecurityUtils {
    public static UserPrincipal getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new RuntimeException("No authentication found");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal == null)
            throw new RuntimeException("User not found in Authentication");

        return userPrincipal;
    }
    public static String getCurrentUserEmail() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal.email();
    }
    public static String getCurrentUserName() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal.name();
    }
}
