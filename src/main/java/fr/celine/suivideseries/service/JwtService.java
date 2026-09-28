package fr.celine.suivideseries.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String genererToken(UserDetails utilisateur) {
        Instant maintenant =  Instant.now();
        return Jwts.builder()
                .subject(utilisateur.getUsername())
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plusMillis(expiration)))
                .signWith(cleDeSignature())
                .compact();
    }

    public String extraireUsername(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    public boolean tokenValide(String token, UserDetails utilisateur) {
        String username = extraireUsername(token);
        return username.equals(utilisateur.getUsername()) && !tokenExpire(token);
    }

    public boolean tokenExpire(String token) {
        Instant expirationToken = extraireClaim(token, Claims::getExpiration).toInstant();
        return expirationToken.isBefore(Instant.now());
    }

    private <T> T extraireClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(cleDeSignature())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    private SecretKey cleDeSignature() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
