package com.wistron.javacodebase.User.dto;

public class LoginResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String account;
    private String name;

    public LoginResponse(boolean success, String message, Long userId, String account, String name) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.account = account;
        this.name = name;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }
}
