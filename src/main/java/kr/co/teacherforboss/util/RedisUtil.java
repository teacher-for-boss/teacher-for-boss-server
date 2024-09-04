package kr.co.teacherforboss.util;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisUtil {

	private final RedisTemplate<String, String> redisTemplate;

	public static final String PREFIX_REFRESH_TOKEN = "REFRESH_TOKEN:";
	public static final String PREFIX_LOGOUT = "LOGOUT:";
	public static final String PREFIX_EMAIL = "EMAIL:";

	public String getData(String key) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return valueOperations.get(key);
	}

	public void setData(String key, String value) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(key, value);
	}

	public void setDataExpire(String key, String value, long duration) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(key, value, Duration.ofMillis(duration));
	}

	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

	public boolean existsData(String key) {
		return redisTemplate.hasKey(key);
	}

	public void addPostId(String key, String value) {
		ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
		valueOperations.append(key, value);
	}

	public void addViewCountInRedis(Long postId) {
		String key = postId +"post";
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		if (getData(key) == null) {
			valueOperations.set(key, "0");
			setOperations.add("keyList", key.replace("post",""));
		}
		if (Boolean.FALSE.equals(setOperations.isMember("keyList", getData(key)))) {
			setOperations.add("keyList", key.replace("post",""));
		}
		valueOperations.increment(key);
	}

	public List<String> deleteViewCountInRedis() {
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        return setOperations.pop("keyList", setOperations.size("keyList"));
	}

	public String getAndDeleteData(String key) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return valueOperations.getAndDelete(key+"post");
	}

	public void updateData(String key, String value) {
		RedisScript script = RedisScript.of("return redis.call('SET', KEYS[1], ARGV[1], 'KEEPTTL')");
		redisTemplate.execute(script, Collections.singletonList(key), value);
	}
}
