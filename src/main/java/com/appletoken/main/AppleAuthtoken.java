package com.appletoken.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.appletoken.main.AppleAuthUtil;

@SpringBootApplication
public class AppleAuthtoken  {

    
	public static void main(String[] args) throws Exception {
	    /**
	     * Apple 에서 전달된 authorization_code 
	     */
	    String authorizationCode = "";
	    AppleAuthUtil.authToken(authorizationCode);
	    
	    // id_token 
	    String refreshToken = "";
	    AppleAuthUtil.authRefreshToken(refreshToken);
	}
}
