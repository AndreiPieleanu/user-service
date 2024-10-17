package s6.userservice.servicelayer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import s6.userservice.configuration.AccessToken;
import s6.userservice.servicelayer.customexceptions.InvalidAccessTokenException;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccessTokenEncoderDecoder implements IAccessTokenEncoder, IAccessTokenDecoder {
    private final Key key;
    public AccessTokenEncoderDecoder(@Value("${jwt.secret}") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    @Override
    public AccessToken decode(String accessTokenEncoded) {
        try {
            var jwt = Jwts.parserBuilder().setSigningKey(key).build().parse(accessTokenEncoded);
            Claims claims = (Claims) jwt.getBody();

            String role = claims.get("role", String.class);

            return AccessToken
                    .builder()
                    .role(role)
                    .subject(claims.getSubject())
                    .userId(claims.get("userId", Integer.class))
                    .build();

        }
        catch (JwtException exception){
            throw new InvalidAccessTokenException(exception.getMessage());
        }
    }

    @Override
    public String encode(AccessToken accessToken) {
        Map<String, Object> claimsMap = new HashMap<>();
        if(accessToken.getRole() != null) {
            claimsMap.put("role", accessToken.getRole());
        }
        if(accessToken.getUserId() != null){
            claimsMap.put("userId", accessToken.getUserId());
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(accessToken.getSubject())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(3, ChronoUnit.HOURS)))
                .addClaims(claimsMap)
                .signWith(key)
                .compact();
    }
}
