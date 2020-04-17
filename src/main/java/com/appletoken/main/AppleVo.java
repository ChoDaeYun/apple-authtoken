package com.appletoken.main;

public class AppleVo {
    /**
     * Api호출 리턴 상태 코드 200 정상 400 오류 
     */
    private int statusCode; 
    
    private String firstName;
    private String lastName;    
    private String email;
    private String socialId;
    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String id_token;
    
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSocialId() {
        return socialId;
    }
    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }
    public String getAccess_token() {
        return access_token;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public String getToken_type() {
        return token_type;
    }
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    public String getExpires_in() {
        return expires_in;
    }
    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
    public String getRefresh_token() {
        return refresh_token;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    public String getId_token() {
        return id_token;
    }
    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
    
    
}
