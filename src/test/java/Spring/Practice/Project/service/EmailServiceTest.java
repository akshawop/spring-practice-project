package Spring.Practice.Project.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.akshawop.journalApp.JournalApplication;
import me.akshawop.journalApp.service.EmailService;

@SpringBootTest(classes = JournalApplication.class)
public class EmailServiceTest {
    @Autowired
    private EmailService service;

    @Test
    void testSendMail() {
        service.sendEmail("avishekshaw2005@gmail.com", "Testing email service in journal app", "Hello bro ;)");
    }
}
