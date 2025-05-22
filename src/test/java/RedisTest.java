import com.software.redisapi.RedisFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class RedisTest {
    private Jedis jedis;
    private String pwd = "Ljp200503052613";

    @Test
    void test2(){
        RedisFactory.getRedis().set("a","30");
        System.out.println(RedisFactory.getRedis().get("a"));
    }
    @AfterEach
    void shut() {
        if (jedis != null) jedis.close();
    }
}
