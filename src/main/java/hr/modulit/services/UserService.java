package hr.modulit.services;

import hr.modulit.dto.UserData;
import hr.modulit.exceptions.EmailExistsException;
import hr.modulit.persistence.models.User;
import hr.modulit.persistence.repos.AccountRepository;
import hr.modulit.persistence.repos.UserRepository;
import hr.modulit.persistence.repos.VerificationTokenRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static hr.modulit.persistence.builders.AccountBuilder.anAccount;

@Data
@Service
public class UserService extends AbstractAccountService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;


    @Transactional
    public User registerNewUserAccount(UserData userData) throws EmailExistsException {

        if (emailExist(userData.getEmail()))
            throw new EmailExistsException(userData.getEmail());

        User newUser = anAccount()
                .withName(userData.getName())
                .withPassword(encoder.encode(userData.getPassword()))
                .withEmail(userData.getEmail())
                .buildForUser();

        return userRepository.save(newUser);
    }

    public User getUser(final String token) {
        final Long accountId = getAccountIdFromToken(token);
        if (accountId != null)
            return userRepository.findOne(accountId);

        return null;
    }
}
