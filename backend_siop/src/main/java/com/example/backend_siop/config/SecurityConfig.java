package com.example.backend_siop.config;

import com.example.backend_siop.security.UserDetailsServiceImpl;
import com.example.backend_siop.security.apikey.ApiKeyAuthenticationFilter;
import com.example.backend_siop.security.jwt.JwtAuthFilter;
import com.example.backend_siop.security.jwt.JwtTokenProvider;
import com.example.backend_siop.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurRepository utilisateurRepository;

    @Value("${integration.api-key}")
    private String validApiKey;

    @Value("${integration.system.email}")
    private String systemUserEmail;

    /**
     * Encodeur des mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider d'authentification basé sur UserDetailsService
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * Filtre JWT
     */
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(
                jwtTokenProvider,
                userDetailsService
        );
    }

    /**
     * Filtre d'authentification par API Key
     */
    @Bean
    public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter() {
        return new ApiKeyAuthenticationFilter(
                utilisateurRepository,
                validApiKey,
                systemUserEmail
        );
    }

    /**
     * Configuration principale de Spring Security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Désactivation CSRF car API REST stateless
                .csrf(csrf -> csrf.disable())

                // Pas de session HTTP
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Autorisations
                .authorizeHttpRequests(auth -> auth

                        // API d'intégration
                        .requestMatchers("/api/integration/**")
                        .permitAll()

                        // Requêtes CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Authentification publique
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/activer-compte",
                                "/api/auth/mot-de-passe-oublie",
                                "/api/auth/reinitialiser-mot-de-passe"
                        )
                        .permitAll()

                        // Tout le reste nécessite une authentification
                        .anyRequest()
                        .authenticated()
                )

                // AuthenticationProvider
                .authenticationProvider(authenticationProvider())

                /*
                 * IMPORTANT :
                 *
                 * On place les deux filtres avant le filtre standard
                 * UsernamePasswordAuthenticationFilter.
                 *
                 * On ne fait PAS :
                 *
                 * addFilterBefore(apiKeyAuthenticationFilter(), JwtAuthFilter.class)
                 *
                 * car JwtAuthFilter est un filtre personnalisé et Spring
                 * Security 7 ne lui attribue pas automatiquement un ordre
                 * utilisable comme référence.
                 */
                .addFilterBefore(
                        apiKeyAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterBefore(
                        jwtAuthFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}