package com.gofluent.backend.config;

import org.springframework.security.config.Customizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthProvider userAuthProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                // PUBLIC
                .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()

                // ADMIN
                .requestMatchers(HttpMethod.GET, "/levels").hasAnyRole("USER", "ADMIN")  // ← ADAUGĂ ASTA
                .requestMatchers(HttpMethod.GET, "/levels/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/lessons").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/lessons/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/lessons/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/levels/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/exercises/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/exercises/**").hasRole("ADMIN")
                                // MCQ - ordinea e importantă!
                .requestMatchers(HttpMethod.GET, "/mcq/test/**").hasAnyRole("USER", "ADMIN")  // ← ADAUGĂ ACEASTĂ LINIE PRIMUL
                .requestMatchers(HttpMethod.POST, "/mcq/test/submit").hasRole("USER")         // ← ȘI ACEASTA PENTRU SUBMIT
                .requestMatchers(HttpMethod.GET, "/mcq", "/mcq/**").hasRole("ADMIN")           // ← ACEASTĂ RĂMÂNE
                .requestMatchers(HttpMethod.POST, "/mcq").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/mcq/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/mcq/**").hasRole("ADMIN")
// ← ADAUGĂ ACESTE LINII PENTRU LANGUAGES ADMIN
                .requestMatchers(HttpMethod.POST, "/admin/languages").hasRole("ADMIN")        // ← ADAUGĂ ASTA
                .requestMatchers(HttpMethod.PUT, "/admin/languages/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/admin/languages/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/admin/languages/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/admin/languages/**").hasRole("ADMIN")



                                // USER
                .requestMatchers(HttpMethod.GET, "/lessons/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/exercises/by-lesson/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/exercises/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/progress/lesson/*/exercise/*/complete").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/progress", "/progress/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/user/me").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/languages").hasAnyRole("USER", "ADMIN") // ← ADAUGĂ
                .requestMatchers(HttpMethod.GET, "/languages/my-languages").hasRole("USER") // ← ADAUGĂ
                .requestMatchers(HttpMethod.POST, "/languages/start-learning/*").hasRole("USER") // ← ADAUGĂ
                // fallback
                .anyRequest().authenticated()
        )


                // 👇 Move JWT filter AFTER authorization rules
                .addFilterAfter(new JwtAuthFilter(userAuthProvider), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // dacă trimiți cookie sau Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
