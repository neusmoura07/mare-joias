package br.com.marejoias.identity.service;

import br.com.marejoias.identity.domain.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Lê a senha mestra do JWT lá do nosso application.yml
    @Value("${api.security.token.secret}")
    private String secret;


    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("marejoias-api") // Quem emitiu o token
                    .withSubject(user.getUsername()) // O dado principal que vamos guardar (o login)
                    .withExpiresAt(generateExpirationDate()) // Quando expira
                    .sign(algorithm); // Assinatura com a nossa chave secreta
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Descriptografa o token e devolve o email do usuário se for válido e não estiver expirado.
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("marejoias-api")
                    .build()
                    .verify(token) // Se o token for falso ou expirado, quebra aqui
                    .getSubject(); // Devolve o email do usuário
        } catch (JWTVerificationException exception) {
            return ""; // Retorna string vazia caso o token seja inválido
        }
    }

    private Instant generateExpirationDate() {
        // Token expira em 2 horas. Usando o fuso de Brasília (-03:00)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}