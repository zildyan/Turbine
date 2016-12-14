package hr.modulit.services;

import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.Token;
import hr.modulit.persistence.repos.AccountRepository;
import hr.modulit.persistence.repos.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import static hr.modulit.Constants.TOKEN_EXPIRED;
import static hr.modulit.Constants.TOKEN_INVALID;
import static hr.modulit.Constants.TOKEN_VALID;
import static java.util.UUID.randomUUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenRepository tokenRepository;

    public Account getAccountByVerificationToken(final String token) {
        Token verificationToken = tokenRepository.findByToken(token);
        if(verificationToken != null)
            return verificationToken.getAccount();

        return null;
    }

    public Account getAccountByEmail(final String email) {
        return accountRepository.findByEmail(email);
    }

    public void createVerificationTokenForAccount(Account account, String token) {
        final Token verificationToken = new Token(token, account);
        tokenRepository.save(verificationToken);
    }

    public Token generateNewVerificationToken(final String existingToken) {
        Token token = tokenRepository.findByToken(existingToken);
        token.updateToken(randomUUID().toString());
        token = tokenRepository.save(token);
        return token;
    }

    protected boolean emailExist(String email) {
        Account account = accountRepository.findByEmail(email);
        return account != null;
    }

    public String validateVerificationToken(String token) {
        final Token verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        return TOKEN_VALID;
    }

    public void enableAccount(String token) {
        final Account account = getAccountByVerificationToken(token);
        account.setEnabled(true);
        accountRepository.save(account);
    }

    public void createPasswordResetToken(String token, Account account) {
        final Token passwordToken = new Token(token, account);
        tokenRepository.save(passwordToken);
    }
}
