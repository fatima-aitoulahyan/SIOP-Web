package com.example.backend_siop.security.apikey;

import com.example.backend_siop.common.exception.ResourceNotFoundException;
import com.example.backend_siop.utilisateur.entity.Utilisateur;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor // ← Ce constructeur sera utilisé par SecurityConfig
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-Key";
    private static final String INTEGRATION_PATH = "/api/integration";

    private final UtilisateurRepository utilisateurRepository;
    private final String validApiKey;
    private final String systemUserEmail;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (!requestUri.startsWith(INTEGRATION_PATH)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader(API_KEY_HEADER);

        if (providedKey == null || providedKey.isBlank()) {
            log.warn("Tentative d'accès à l'API d'intégration sans clé API.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Clé API manquante.\"}");
            return;
        }

        if (!validApiKey.equals(providedKey)) {
            log.warn("Tentative d'accès à l'API d'intégration avec une clé API invalide.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Clé API invalide.\"}");
            return;
        }

        try {
            Utilisateur systemUser = utilisateurRepository.findByEmail(systemUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur système introuvable"));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            systemUser,
                            null,
                            systemUser.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Authentification API Key réussie pour l'utilisateur système : {}", systemUserEmail);

        } catch (ResourceNotFoundException e) {
            log.error("L'utilisateur système n'existe pas en base.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Configuration interne manquante.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}