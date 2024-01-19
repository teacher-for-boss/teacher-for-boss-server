package kr.co.teacherforboss.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    private final TokenManager tokenManager;

    @Value("${jwt.secret}")
    private String secretKey;

    public static final String TOKEN_PREFIX = "Bearer";
    private static final String KEY_ROLES = "roles";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = (long) 1000 * 60 * 60 * 24; // 24시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = (long) 1000 * 60 * 60 * 24 * 30; // 1달

    public AuthResponseDTO.TokenResponseDTO createTokenResponse(String email, Role role) {
        List<String> roles = getRolesByUserType(role);

        Claims claims = Jwts.claims().setSubject(email);
        claims.put(KEY_ROLES, roles);

        String accessToken = generateToken(claims, ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = generateToken(claims, REFRESH_TOKEN_EXPIRE_TIME);

        tokenManager.addRefreshToken(email, refreshToken);
        return new AuthResponseDTO.TokenResponseDTO(email, accessToken, refreshToken);
    }

    private List<String> getRolesByUserType(Role role) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + role.name());

        return roles;
    }

    public AuthResponseDTO.TokenResponseDTO recreateAccessToken(String refreshToken) {
        if(!this.validateToken(refreshToken)) {
            throw new AuthHandler(ErrorStatus.TOKEN_TIME_OUT);
        }

        Claims claims = parseClaims(refreshToken);

        String email = claims.getSubject();
        String findToken = tokenManager.getRefreshToken(email);

        if(!refreshToken.equals(findToken)) {
            throw new AuthHandler(ErrorStatus.JWT_REFRESH_TOKEN_NOT_FOUND);
        }

        String accessToken = generateToken(claims, ACCESS_TOKEN_EXPIRE_TIME);

        return AuthResponseDTO.TokenResponseDTO.builder()
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateToken(Claims claims, Long expiredTime) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + expiredTime);

        Key key = Keys.hmacShaKeyFor(this.secretKey.getBytes());

        return Base64.getEncoder().encodeToString(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(expired)
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact().getBytes()
        );
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    public Authentication getAuthentication(String token) {
        String username = this.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean isAccessTokenDenied(String accessToken) {
        return tokenManager.existBlackListAccessToken(accessToken);
    }

    public String resolveTokenFromRequest(String token) {
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthHandler(ErrorStatus.TOKEN_TIME_OUT);
        } catch (SignatureException e) {
            throw new AuthHandler(ErrorStatus.JWT_TOKEN_WRONG_TYPE);
        } catch (MalformedJwtException e) {
            throw new AuthHandler(ErrorStatus.JWT_TOKEN_MALFORMED);
        }
    }
}
