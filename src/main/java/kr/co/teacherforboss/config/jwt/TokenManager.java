package kr.co.teacherforboss.config.jwt;

import kr.co.teacherforboss.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static kr.co.teacherforboss.config.jwt.JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME;
import static kr.co.teacherforboss.util.RedisUtil.PREFIX_LOGOUT;
import static kr.co.teacherforboss.util.RedisUtil.PREFIX_REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private final RedisUtil redisUtil;

    public void addRefreshToken(String key, String value) {
        redisUtil.setDataExpire(PREFIX_REFRESH_TOKEN + key, value, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String getRefreshToken(String key) {
        return redisUtil.getData(PREFIX_REFRESH_TOKEN + key);
    }

    public void deleteRefreshToken(String key) {
        redisUtil.deleteData(PREFIX_REFRESH_TOKEN + key);
    }

    public void addBlackListAccessToken(String token, String email) {
        redisUtil.setData(PREFIX_LOGOUT + token, email);
    }

    public boolean existBlackListAccessToken(String token) {
        return redisUtil.existsData(PREFIX_LOGOUT + token);
    }
}
