package com.appletoken.main;

import java.io.FileReader;
import java.security.PrivateKey;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class AppleAuthUtil {
    
    /**
     * authorization_code 인증시 사용되는 Apple API URL
     */
    private static String AUTH_TOKEN = "https://appleid.apple.com/auth/token";
    
    
    private static String AUTH_KEYS = "https://appleid.apple.com/auth/keys";
    
    /**
     * Apple에 등록된 TEAM ID
     */
    private static String TEAM_ID = "";
    
    /**
     * Apple 에서 발급된 KEY ID ( .p8 파일 발급시 확인  ) 
     */
    private static String KEY_ID = "";
    
    /**
     * Apple 에서 다운로드 한 key파일
     */
    private static String P8_FILEPATH = "";
    
    /**
     * 서비스 앱 ID IOS
     */
    private static String CLIENT_ID = "";
    
    /**
     * 서비스 앱 ID , WEB ,ANDROID 
     */
    private static String CLIENT_WBE_ID = "";
    
    private static PrivateKey pKey;
        
    /**
     * Privatekey 가져오기 
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKey() throws Exception {        
         
        final PEMParser pemParser = new PEMParser(new FileReader(P8_FILEPATH));
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        final PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        final PrivateKey pKey = converter.getPrivateKey(object);
        return pKey;
    }
    
    /**
     * Apple API 사용시 사용자 인증 JWT 생성 
     * @return
     * @throws Exception
     */
    private static String generateJWT(Boolean webBool) throws Exception {
      if (pKey == null) {
          pKey = getPrivateKey();
      }
      String clientId = CLIENT_ID;
      if(webBool == true) {
          clientId = CLIENT_WBE_ID;
      }
      String token = Jwts.builder().setHeaderParam(JwsHeader.KEY_ID, KEY_ID)
              .setIssuer(TEAM_ID)
              .setAudience("https://appleid.apple.com")
              .setSubject(clientId)
              .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 10)))
              .setIssuedAt(new Date(System.currentTimeMillis()))
              .signWith(pKey,SignatureAlgorithm.ES256)
              .compact();
      return token;
    }
        
    /**
     * apple 로그인시 전달된 authorization code 인증 하기 
     * @param authorizationCode
     * @return
     * @throws Exception
     */
    public static AppleVo authToken(String authorizationCode,Boolean webBool) throws Exception{
        
        String clientId = CLIENT_ID;
        if(webBool == true) {
            clientId = CLIENT_WBE_ID;
        }
        AppleVo appleVo = new AppleVo();
        String token = generateJWT(webBool);
        HttpResponse<String> response = Unirest.post(AUTH_TOKEN)
                  .header("Content-Type", "application/x-www-form-urlencoded")
          .field("client_id", clientId)
          .field("client_secret", token)
          .field("grant_type", "authorization_code")
          .field("code", authorizationCode)
              .asString();
        
        // 200 - OK , 400 - Bad Request
        appleVo.setStatusCode(response.getStatus());
        try {
            appleVo = new Gson().fromJson(response.getBody(),AppleVo.class);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        appleVo.setStatusCode(response.getStatus());
        //System.out.println(ToStringBuilder.reflectionToString(appleVo));  
        if(appleVo.getStatusCode() == 200) {
            DecodedJWT jwt = JWT.decode(appleVo.getId_token());
            Date now = new Date();
            long nowTime = now.getTime();
            long expTime = jwt.getClaim("exp").asLong()*1000;
            if(expTime < nowTime) {
                appleVo.setStatusCode(400);
            }else {
                appleVo.setEmail(jwt.getClaim("email").asString());
                appleVo.setSocialId(jwt.getClaim("sub").asString());
            }
        }
        return appleVo;
    }
    
    
    /**
     * refreshToken 검증  1일 1회 검증 필요 (Apple 권고 사항 ?.... ) 
     * @param refreshToken
     * @throws Exception
     */
    public static AppleVo authRefreshToken(String refreshToken ,Boolean webBool) throws Exception{
        
        String token = generateJWT(webBool);
        String clientId = CLIENT_ID;
        if(webBool == true) {
            clientId = CLIENT_WBE_ID;
        }
        HttpResponse<String> response = Unirest.post(AUTH_TOKEN)
                  .header("Content-Type", "application/x-www-form-urlencoded")
              .field("client_id", clientId)
              .field("client_secret", token)
              .field("grant_type", "refresh_token")
              .field("refresh_token", refreshToken)
              .asString();
        // 200 - OK , 400 - Bad Request 
//        System.out.println(response.getStatus());      
//        System.out.println(response.getBody());
        AppleVo appleVo = new AppleVo();
        try {
            appleVo = new Gson().fromJson(response.getBody(),AppleVo.class);
        }catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        appleVo.setStatusCode(response.getStatus());
        return appleVo;
    }
}
