package com.topcit.aims.aims.dto.request;

public class RefreshTokenRequest {
    private String refreshToken;
    public String getRefreshToken(){
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
