package com.minhadieta.dietaapi.config;

import com.minhadieta.dietaapi.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager; // Importação adicionada
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Importação adicionada
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy; // Importação adicionada
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration; // Import Novo
import org.springframework.web.cors.CorsConfigurationSource; // Import Novo
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Import Novo

import java.util.Arrays; // Import Novo
import java.util.List; // Import Novo

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter; //Injete o filtro

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para APIs REST sem sessões baseadas em cookies
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Garante que as sessões são stateless (sempre importante com JWT)
                .authorizeHttpRequests(authorize -> authorize

                        //Endpoints públicos
                        .requestMatchers("/api/auth/login").permitAll() // Permite acesso público ao endpoint de login
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers("/api/users/{userId}/diet-profiles/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN") // Somente ADMIN pode listar todos os usuários
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("USER", "ADMIN") // USER ou ADMIN podem ver um usuário
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN") // Somente ADMIN pode deletar usuários

                        // Todas as outras requisições precisam ser autenticadas
                        .anyRequest().authenticated()
                )
                // Adicione o filtro JWT antes do filtro padrão de autenticação de usuário/senha
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite a origem do seu Front-end Angular
        configuration.setAllowedOrigins(List.of("*"));//http://localhost:4200
        // Permite os métodos HTTP comuns
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite cabeçalhos (Authorization é essencial para o JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Bean para expor o AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
