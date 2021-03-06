package hr.modulit.services;

import hr.modulit.dto.AccountData;
import hr.modulit.exceptions.EmailExistsException;
import hr.modulit.persistence.models.User;
import hr.modulit.persistence.repos.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static hr.modulit.persistence.builders.AccountBuilder.anAccount;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    @Transactional
    public User registerNewUserAccount(AccountData accountData) throws EmailExistsException {

        if (accountService.emailExist(accountData.getEmail()))
            throw new EmailExistsException(accountData.getEmail());

        User newUser = anAccount()
                .withName(accountData.getName())
                .withPassword(encoder.encode(accountData.getPassword()))
                .withEmail(accountData.getEmail())
                .buildForUser();

        return userRepository.save(newUser);
    }

}
