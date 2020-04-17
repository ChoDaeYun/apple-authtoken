package com.appletoken.main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.appletoken.main.AppleAuthUtil;

@SpringBootApplication
public class AppleAuthtoken  {

    
	public static void main(String[] args) throws Exception {
	    
	    /**
	     * Apple 에서 전달된 authorization_code  IOS
	     */
	    String authorizationCode = "";
	    AppleVo applevo1 = AppleAuthUtil.authToken(authorizationCode , false);	    
        /**
         * 발급된 리플래쉬 토큰 유효성 검사  1일 1회 ?  
         *  가입 시 사용 불필요 	    
         */
	    AppleVo applevo2 = AppleAuthUtil.authRefreshToken(applevo1.getRefresh_token(), false);
	    
	    
	    
        
        
        /**
         * Web 로그인의 경우 리턴받는 정보
         * code  Apple 에서 전달 되는 Authorization code
         * state Web 에서 전달한 State 정보  
         * user scope 에서 전달 요청한 정보 name email 
         */
        
        String code = "";
        String state = "";
        String user = "";
        
        /**
         * Apple에서 리턴된 authorization_code WEB
         */
        AppleVo applevo3 = AppleAuthUtil.authToken(code,true);
        
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj;
         
        try {
            jsonObj = (JSONObject) jsonParser.parse(user);
            jsonObj = (JSONObject) jsonObj.get("name");
            applevo3.setFirstName(jsonObj.get("firstName").toString());
            applevo3.setLastName(jsonObj.get("lastName").toString());
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        /**
         * 발급된 리플래쉬 토큰 유효성 검사  1일 1회 ?       
         * 가입 시 사용 불필요    
         */
        AppleVo applevo4 = AppleAuthUtil.authRefreshToken(applevo3.getRefresh_token(), false);
	}
}
