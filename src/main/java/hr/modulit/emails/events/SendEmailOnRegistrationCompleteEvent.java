package hr.modulit.emails.events;

import hr.modulit.persistence.models.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SendEmailOnRegistrationCompleteEvent extends ApplicationEvent {

    @Getter
    private Account account;

    @Getter
    private String appUrl;

    public SendEmailOnRegistrationCompleteEvent(final Account account, final String appUrl) {
        super(account);
        this.account = account;
        this.appUrl = appUrl;
    }
}
