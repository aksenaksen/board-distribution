package mini.board.hotarticle.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ArticleLikeCountRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "hot-article::article::%s::like-count";


    public void createOrUpdate(Long articleId, Long likeCount, Duration ttl) {
        String key = generateKey(articleId);
        redisTemplate.opsForValue().set(key, String.valueOf(likeCount), ttl);
    }

    public Long read(Long articleId){
        String result = redisTemplate.opsForValue().get(generateKey(articleId));
        return result == null ? 0L : Long.parseLong(result);
    }



    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }


}
