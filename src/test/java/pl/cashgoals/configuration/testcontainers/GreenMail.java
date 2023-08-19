package pl.cashgoals.configuration.testcontainers;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

public class GreenMail {
    private static GreenMailExtension INSTANCE;
    private GreenMail() {
    }

    public static GreenMailExtension getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GreenMailExtension(ServerSetupTest.SMTP)
                    .withConfiguration(
                            GreenMailConfiguration.aConfig()
                                    .withUser("admin", "admin")
                    )
                    .withPerMethodLifecycle(false);
        }
        return INSTANCE;
    }


}
