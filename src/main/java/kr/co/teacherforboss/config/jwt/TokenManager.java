package kr.co.teacherforboss.config.jwt;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private final RedissonClient redissonClient;
    private static final String REFRESH_TOKEN_MAP = "RefreshTokenMap";
    private static final String ACCESS_TOKEN_DENIED_MAP = "AccessTokenDeniedMap";

    public void addRefreshToken(String key, String value) {
        RMap<String, String> tokenMap = redissonClient.getMap(REFRESH_TOKEN_MAP);
        tokenMap.put(key, value);
    }

    public String getRefreshToken(String key) {
        RMap<String, String> tokenMap = redissonClient.getMap(REFRESH_TOKEN_MAP);
        return tokenMap.get(key);
    }

    public void deleteRefreshToken(String key) {
        RMap<String, String> tokenMap = redissonClient.getMap(REFRESH_TOKEN_MAP);
        tokenMap.remove(key);
    }

    public void addBlackListAccessToken(String token) {
        RMap<String, String> map = redissonClient.getMap(ACCESS_TOKEN_DENIED_MAP);
        map.put(token, "logged out");
    }

    public boolean existBlackListAccessToken(String token) {
        RMap<String, String> map = redissonClient.getMap(ACCESS_TOKEN_DENIED_MAP);
        return map.containsKey(token);
    }
}
