package me.barion.capstoneprojectbarion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    //사용자 정보 관리
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername("defaultUser")
                .password(passwordEncoder().encode("qwer1234")) // 고정된 login_code 설정 (암호화)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
    // 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // BCrypt 포함
    }

    //인증과정 처리
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {

                String loginCode = authentication.getCredentials().toString();

                UserDetails user = userDetailsService.loadUserByUsername("defaultUser");

                // 입력된 비밀번호와 저장된 비밀번호 비교
                if (passwordEncoder.matches(loginCode, user.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(user, loginCode, user.getAuthorities());
                }

                // 인증 실패 시 예외 처리
                throw new AuthenticationException("Invalid login code") {};
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**", // Swagger API 문서 경로
                                "/swagger-ui/**",  // Swagger UI 리소스 경로
                                "/swagger-ui.html" // Swagger UI HTML 페이지
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .httpBasic(httpBasic -> httpBasic.realmName("MyApp"))
                .formLogin(form -> form.permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
