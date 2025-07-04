package mini.board.hotarticle.infra;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
public class ArticleCreatedTimeRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "hot-article::article::%s::created-time";

    public void createOrUpdate(Long articleId, LocalDateTime createdTime , Duration ttl) {
        String key = generateKey(articleId);
        redisTemplate.opsForValue().set(key,
                String.valueOf(createdTime.toInstant(ZoneOffset.UTC).toEpochMilli()), ttl);
    }

    public void delete(Long articleId) {
        String key = generateKey(articleId);
        redisTemplate.delete(key);
    }

    public LocalDateTime read(Long articleId){
        String result = redisTemplate.opsForValue().get(generateKey(articleId));
        if(result == null) {
            return null;
        }
        return
                LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Long.parseLong(result)), ZoneOffset.UTC);
    }


    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }
}
