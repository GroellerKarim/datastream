package eu.groeller.datastreamserver.service.security;

import eu.groeller.datastreamserver.configuration.security.JwtConfig;
import eu.groeller.datastreamserver.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtConfig jwtConfig;

    public String generateToken(User user) {
        log.debug("Generating Token for user [id: {}]", user.getId());
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
        log.debug("Validating authentication token...");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        val subject = claims.getSubject();
        log.debug("Validated Token for user [id: {}]", subject);
        return subject;
    }
}
