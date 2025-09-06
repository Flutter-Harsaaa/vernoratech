// Authentication Response DTO
package com.vernoraTech.dto;


public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private Boolean emailVerified;
    private String message;
    private boolean success;
    
    public AuthResponse(String token, String email, Boolean emailVerified,Boolean success, String message) {
        this.token = token;
        this.email = email;
        this.emailVerified = emailVerified;
        this.success=success;
        this.message = message;
    }
    
    // Getters and Setters
    
    public boolean isSuccess() {return success;}

	public void setSuccess(boolean success) {this.success = success;}
	public String getToken() { return token; }
	public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}