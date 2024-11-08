package eu.groeller.datastreamserver.service.security;

import eu.groeller.datastreamserver.configuration.security.JwtConfig;
import eu.groeller.datastreamserver.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtConfig jwtConfig;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration().toMillis());

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("id", String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .compact();
    }

    public String validateTokenAndGetEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
