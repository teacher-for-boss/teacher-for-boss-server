package kr.co.teacherforboss.config;

import kr.co.teacherforboss.config.jwt.JwtFilter;
import kr.co.teacherforboss.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig implements
		SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {
	private final TokenProvider tokenProvider;

	@Override
	public void init(HttpSecurity builder) throws Exception {

	}

	@Override
	public void configure(HttpSecurity http) {
		JwtFilter customFilter = new JwtFilter(tokenProvider);
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}
}

