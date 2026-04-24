package Spring.Practice.Project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.akshawop.journalApp.JournalApplication;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.service.RedisService;

@SpringBootTest(classes = JournalApplication.class)
public class RedisTests {
    @Autowired
    private RedisService redis;

    @Test
    void testRedis() {
        User user = User.builder().email("xyz").username("abc").password("adfs").build();
        redis.set("name", user, 30l);
        User newUser = redis.get("name", User.class);
        assertEquals(user, newUser);
        assertEquals(user.getUsername(), newUser.getUsername());
    }
}