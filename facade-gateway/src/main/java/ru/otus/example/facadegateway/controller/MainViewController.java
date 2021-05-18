package ru.otus.example.facadegateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.otus.example.facadegateway.model.AuthInfo;

@Controller
public class MainViewController {

    @GetMapping("/")
    public String mainPage(Model model) {
        return "index";
    }

    @GetMapping("/authinfo")
    @ResponseBody
    public AuthInfo authinfo() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();

        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthenticated(securityContext.getAuthentication().isAuthenticated());
        authInfo.setUsername(userDetails.getUsername());

        return authInfo;
    }
}
