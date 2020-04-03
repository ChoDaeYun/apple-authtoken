package com.appletoken.main;

import java.io.FileReader;
import java.security.PrivateKey;
import java.util.Date;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;

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
     * 서비스 앱 ID 
     */
    private static String CLIENT_ID = "";
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
    private static String generateJWT() throws Exception {
      if (pKey == null) {
          pKey = getPrivateKey();
      }
      String token = Jwts.builder().setHeaderParam(JwsHeader.KEY_ID, KEY_ID)
              .setIssuer(TEAM_ID)
              .setAudience("https://appleid.apple.com")
              .setSubject(CLIENT_ID)
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
    public static void authToken(String authorizationCode) throws Exception{
        
        String token = generateJWT();
        HttpResponse<String> response = Unirest.post(AUTH_TOKEN)
                  .header("Content-Type", "application/x-www-form-urlencoded")
          .field("client_id", CLIENT_ID)
          .field("client_secret", token)
          .field("grant_type", "authorization_code")
          .field("code", authorizationCode)
              .asString();
        // 200 - OK , 400 - Bad Request 
        System.out.println(response.getStatus());      
        System.out.println(response.getBody());
    }
}
