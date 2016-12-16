package hr.modulit.services;

import hr.modulit.dto.PasswordData;
import hr.modulit.enums.TokenValidationStatus;
import hr.modulit.exceptions.InvalidAccountIdAuthenticationException;
import hr.modulit.exceptions.InvalidConfirmedPasswordException;
import hr.modulit.exceptions.InvalidOldPasswordException;
import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.Token;
import hr.modulit.persistence.repos.AccountRepository;
import hr.modulit.persistence.repos.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import static hr.modulit.enums.TokenValidationStatus.*;
import static java.util.UUID.randomUUID;

@Service
public class AccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    public Account getAccountByEmail(final String email) {
        return accountRepository.findByEmail(email);
    }

    public Token generateNewVerificationToken(final String existingToken) {
        Token verificationToken = getToken(existingToken);
        verificationToken.updateToken(randomUUID().toString());
        verificationToken = tokenRepository.save(verificationToken);
        return verificationToken;
    }

    public void createVerificationToken(Account account, String token) {
        generateToken(account, token);
    }

    public void createPasswordToken(Account account, String token) {
        generateToken(account, token);
    }

    private void generateToken(Account account, String token) {
        final Token inToken = new Token(token, account);
        tokenRepository.save(inToken);
    }

    protected boolean emailExist(String email) {
        Account account = accountRepository.findByEmail(email);
        return account != null;
    }

    public void authenticateAccountUserWithToken(String id, String token) {
        Token passwordToken = getToken(token);
        final Account account = passwordToken.getAccount();
        validateAccountId(id, account.getId().toString());

        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getEmail());
        final Authentication auth = new UsernamePasswordAuthenticationToken(account, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void validateAccountId(String id, String accountId) {
        if(!accountId.equals(id))
            throw new InvalidAccountIdAuthenticationException();
    }

    public TokenValidationStatus validateToken(String token) {
        final Token validatingToken = getToken(token);
        if (validatingToken == null)
            return INVALID_TOKEN;

        if (isTokenExpired(validatingToken)) {
            tokenRepository.delete(validatingToken);
            return EXPIRED_TOKEN;
        }

        return VALID_TOKEN;
    }

    private boolean isTokenExpired(Token validationToken) {
        return validationToken.getExpiryDate().before(Calendar.getInstance().getTime());
    }

    public void enableAccount(String token) {
        Token verificationToken = getToken(token);
        final Account account = verificationToken.getAccount();
        account.setEnabled(true);
        accountRepository.save(account);
    }

    private Token getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void validatePasswords(Account account, PasswordData passwordData) {
        if(!passwordEncoder.matches(account.getPassword(), passwordData.getOldPassword()))
            throw new InvalidOldPasswordException();

        if(!passwordData.getNewPassword().equals(passwordData.getConfirmedPassword()))
            throw new InvalidConfirmedPasswordException();
    }

    public void changeAccountPassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
}
