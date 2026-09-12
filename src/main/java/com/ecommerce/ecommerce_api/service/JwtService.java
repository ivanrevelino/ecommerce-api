package com.ecommerce.ecommerce_api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ecommerce.ecommerce_api.models.User;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class JwtService {

    Dotenv dotenv = Dotenv.load();
    String key = dotenv.get("JWT_SECRET");

    public String generateToken(User user) {
    Instant now = Instant.now();
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            return JWT.create()
                    .withIssuedAt(now)
                    .withIssuer("auth-api")
                    .withIssuedAt(now)
                    .withExpiresAt(now.plus(1, ChronoUnit.HOURS))
                    .withSubject(user.getUsername())
                    .withClaim("role", user.getRoles().name())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            log.error("Error while creating token{}", exception.getMessage());
            return null;
        }
    }

    public String verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token invalido ou expirado");
        }
    }

    public Instant expirationTime() {
        return Instant.now().plus(1, ChronoUnit.HOURS);
    }
}
