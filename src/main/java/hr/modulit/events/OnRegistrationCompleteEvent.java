package hr.modulit.events;

import hr.modulit.persistence.models.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@SuppressWarnings("serial")
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    @Getter
    private Account account;

    @Getter
    private String confirmationUrl;

    public OnRegistrationCompleteEvent(final Account account, final String confirmationUrl) {
        super(account);
        this.account = account;
        this.confirmationUrl = confirmationUrl;
    }
}
