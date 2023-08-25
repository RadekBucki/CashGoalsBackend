package pl.cashgoals.notification.business.model;

import lombok.*;
import pl.cashgoals.notification.business.service.source.Source;
import pl.cashgoals.user.persistence.model.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Notification implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    private Template template;
    private Locale locale;
    private User user;
    private String email;
    private Map<String, String> variables;
    private List<Source> source;
}
