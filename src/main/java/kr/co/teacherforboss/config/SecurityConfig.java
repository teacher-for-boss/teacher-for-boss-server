package kr.co.teacherforboss.config;

import kr.co.teacherforboss.config.jwt.JwtAccessDeniedHandler;
import kr.co.teacherforboss.config.jwt.JwtAuthenticationEntryPoint;
import kr.co.teacherforboss.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf((csrfConfig) ->
						csrfConfig.disable()
				)
				.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//				.headers((headerConfig) ->
//						headerConfig.frameOptions(frameOptionsConfig ->
//								frameOptionsConfig.disable()
//						)
//				)
				.authorizeHttpRequests((authorizeRequests) ->
						authorizeRequests
								.requestMatchers("/", "/temp/**", "/login/**").permitAll()
//								.requestMatchers("/posts/**", "/api/v1/posts/**").hasRole(Role.USER.name())
//								.requestMatchers("/admins/**", "/api/v1/admins/**").hasRole(Role.ADMIN.name())
								.anyRequest().authenticated()
				)
				.exceptionHandling((exceptionConfig) ->
						exceptionConfig.authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler)
				)
				.apply(new JwtSecurityConfig(tokenProvider));

		return http.build();
	}
}
