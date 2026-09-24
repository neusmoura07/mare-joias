package br.com.marejoias.identity.security;

import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Extrai o token do cabeçalho da requisição
        var token = this.recoverToken(request);
        
        if (token != null) {
            // 2. Valida o token e resgata o username (email) que guardamos nele
            var username = tokenService.validateToken(token);

            if (!username.isEmpty()) {
                // 3. Busca o usuário no banco de dados para pegar as permissões (roles) atualizadas
                UserDetails user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

                // 4. Cria o "crachá" de autenticação oficial do Spring
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                
                // 5. Salva a autenticação no contexto dessa requisição específica
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // 6. Passa a requisição adiante (seja autenticada ou não, o SecurityConfig vai decidir se barra ou não depois)
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}