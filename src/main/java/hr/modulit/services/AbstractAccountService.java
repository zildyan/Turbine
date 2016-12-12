package hr.modulit.services;

import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.VerificationToken;
import hr.modulit.persistence.repos.AccountRepository;
import hr.modulit.persistence.repos.VerificationTokenRepository;

import java.util.Calendar;

import static hr.modulit.Constants.TOKEN_EXPIRED;
import static hr.modulit.Constants.TOKEN_INVALID;
import static hr.modulit.Constants.TOKEN_VALID;
import static java.util.UUID.randomUUID;

public abstract class AbstractAccountService {

    public abstract AccountRepository getAccountRepository();

    public abstract VerificationTokenRepository getTokenRepository();

    public void createVerificationTokenForUser(Account account, String token) {
        final VerificationToken verificationToken = new VerificationToken(token, account);
        getTokenRepository().save(verificationToken);
    }

    public VerificationToken generateNewVerificationToken(final String existingToken) {
        VerificationToken verificationToken = getTokenRepository().findByToken(existingToken);
        verificationToken.updateToken(randomUUID().toString());
        verificationToken = getTokenRepository().save(verificationToken);
        return verificationToken;
    }

    protected boolean emailExist(String email) {
        Account account = getAccountRepository().findByEmail(email);
        return account != null;
    }

    protected Long getAccountIdFromToken(String token){
        VerificationToken verificationToken = getTokenRepository().findByToken(token);
        if(verificationToken != null){
            Account account = verificationToken.getAccount();
            if(account != null)
                return account.getId();
        }

        return null;
    }

    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = getTokenRepository().findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            getTokenRepository().delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        return TOKEN_VALID;
    }
}
