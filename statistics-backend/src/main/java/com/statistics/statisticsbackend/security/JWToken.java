package com.statistics.statisticsbackend.security;

import com.statistics.statisticsbackend.models.Role;
import io.jsonwebtoken.*;
import lombok.Data;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Data
public class JWToken {

    public static final String JWT_ATTRIBUTE_NAME = "jwt";
    private static final String JWT_ROLE_CLAIM = "role";
    private static final String JWT_IPADDRESS_CLAIM = "ipa";

    private static final String JWT_ISSUER_CLAIM = "iss";
    private static final String JWT_USERNAME_CLAIM = "sub";
    private static final String JWT_ACCOUNTID_CLAIM = "id";

    private String username;
    private Long userId;
    private Role role;
    private String ipAddress;

    private void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public JWToken(String username, Long userId, Role role) {
        this.username = username;
        this.userId = userId;
        this.role = role;
    }

    //encodes the JWToken
    public String encode(String issuer, int expiration, String passPhrase) {
        Key key = getKey(passPhrase);
        return Jwts.builder()
                .subject(this.username)
                .id(this.userId.toString())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .claim(JWT_ROLE_CLAIM, this.role.name())
                .claim(JWT_IPADDRESS_CLAIM, this.ipAddress != null ? this.ipAddress : "1.1.1.1")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    //generate a key to sign the  JWToken with.
    private static Key getKey(String passPhrase) {
        byte[] hmacKey = passPhrase.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(hmacKey, SignatureAlgorithm.HS512.getJcaName());
    }

    //decode the JWToken.
    public static JWToken decode(String token, String issuer, String passPhrase)
            throws ExpiredJwtException, MalformedJwtException {
        //validate the tokens and extract the claims
        Key key = getKey(passPhrase);
        Jws<Claims> jws = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
        Claims claims = jws.getBody();
        //check the issuer claim
        if (!claims.getIssuer().equals(issuer)) {
            throw new MalformedJwtException("Invalid Issuer");
        }
        String roleString = claims.get(JWT_ROLE_CLAIM).toString();
        Role role = Role.valueOf(roleString);
        //build a token from the extracted claims
        JWToken jwToken = new JWToken(
                claims.getSubject(),
                Long.valueOf(claims.getId()),
                role
        );
        jwToken.setIpAddress(claims.get(JWT_IPADDRESS_CLAIM).toString());
        return jwToken;
    }
}
