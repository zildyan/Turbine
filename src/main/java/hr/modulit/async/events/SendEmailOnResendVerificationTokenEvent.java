package hr.modulit.async.events;

import hr.modulit.persistence.models.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SendEmailOnResendVerificationTokenEvent extends ApplicationEvent {

    @Getter
    private Account account;

    @Getter
    private String existingToken;

    @Getter
    private String appUrl;

    public SendEmailOnResendVerificationTokenEvent(final Account account, final String appUrl) {
        super(account);
        this.account = account;
        this.appUrl = appUrl;
    }
}
