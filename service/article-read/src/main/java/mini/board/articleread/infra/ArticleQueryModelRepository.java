package mini.board.articleread.infra;

import kuke.board.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticleQueryModelRepository {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String KEY_FORMAT = "article-read::article::%s";
    private final RedisTemplate<Object, Object> redisTemplate;

    public void create(ArticleQueryModel articleQueryModel, Duration ttl){
        redisTemplate.opsForValue()
                .set(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel), ttl);
    }

    public void update(ArticleQueryModel articleQueryModel){
        redisTemplate.opsForValue().setIfPresent(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel));
    }

    public void delete(Long articleId) {
        stringRedisTemplate.delete(generateKey(articleId));
    }

    public Optional<ArticleQueryModel> read(Long articleId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(generateKey(articleId))
        ).map(json -> DataSerializer.deserialize(json, ArticleQueryModel.class));
    }

    private String generateKey(ArticleQueryModel articleQueryModel) {
        return generateKey(articleQueryModel.getArticleId());
    }
    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }

}
