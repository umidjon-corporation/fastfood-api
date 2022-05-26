package com.project.fastfoodapi.utils;

import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JWTHelper {

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;


    public static String creatJWT(Map<String, Object> claims, String issuer, String secretKey) {

        JwtBuilder builder = Jwts.builder();

        final Long userId = (Long) claims.get(TokenClaims.USER_ID.getKey());
        long timeMillis = System.currentTimeMillis();
        Date issued = new Date(timeMillis);
        Date expire = new Date(timeMillis+7200000);
        
        final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        final Key signingKey = new SecretKeySpec(apiKeySecretBytes, SIGNATURE_ALGORITHM.getJcaName());


        builder.setId(userId.toString()).setIssuedAt(issued).setExpiration(expire).setIssuer(issuer).addClaims(claims)
                .signWith(SIGNATURE_ALGORITHM, signingKey);
        return builder.compact();
    }

    public static void checkJwt(Key secretKey, String token){
        Jwts.parser().setSigningKey(secretKey).parse(token);
    }

    public static Map<String, Object> getClaims(Key secretKey, String token){
        Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return jws.getBody();
    }


}

