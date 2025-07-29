package org.milestone.wdpt6.ticketplatform.ticket_platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/tickets/create", "/tickets/*/edit").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/tickets/*/edit", "/tickets/*/delete", "/user/create", "/user/*/edit").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/tickets/*/editStato", "/user/editStato").hasAnyAuthority("ADMIN", "OPERATORE")
                .requestMatchers("/tickets", "/tickets/*").hasAnyAuthority("OPERATORE", "ADMIN")
                .requestMatchers("/user/editStato").hasAuthority("OPERATORE")
                .requestMatchers("/user/create", "/user/*/edit").hasAuthority("ADMIN")
                .requestMatchers("/**").permitAll())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    DatabaseUserDetailsService userDetailsService() {
        return new DatabaseUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

}
