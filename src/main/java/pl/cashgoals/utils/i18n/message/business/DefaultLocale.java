package pl.cashgoals.utils.i18n.message.business;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DefaultLocale implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        if (Locale.getDefault().getLanguage().equals("test")) {
            return;
        }
        Locale.setDefault(Locale.ENGLISH);
    }
}
