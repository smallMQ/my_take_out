import com.smallmq.ReggieApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ReggieApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @org.junit.Test
    public void test() {
        stringRedisTemplate.opsForValue().set("test", "test");
        System.out.println(stringRedisTemplate.opsForValue().get("test"));
    }
}
