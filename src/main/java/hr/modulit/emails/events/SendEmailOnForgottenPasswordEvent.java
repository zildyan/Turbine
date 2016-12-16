package hr.modulit.emails.events;

import hr.modulit.persistence.models.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SendEmailOnForgottenPasswordEvent extends ApplicationEvent {

    @Getter
    private Account account;

    @Getter
    private String appUrl;

    public SendEmailOnForgottenPasswordEvent(Account account, String appUrl) {
        super(account);
        this.account = account;
        this.appUrl = appUrl;
    }
}
