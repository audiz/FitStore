package ru.otus.example.facadegateway.model;

import lombok.Data;

@Data
public class AuthInfo {
    private boolean authenticated;
    private String username;
}
