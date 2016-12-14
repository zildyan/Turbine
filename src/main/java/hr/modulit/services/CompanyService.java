package hr.modulit.services;

import hr.modulit.dto.AccountData;
import hr.modulit.exceptions.EmailExistsException;
import hr.modulit.persistence.models.Company;
import hr.modulit.persistence.repos.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static hr.modulit.persistence.builders.AccountBuilder.anAccount;

@Service
public class CompanyService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AccountService accountService;

    @Transactional
    public Company registerNewCompanyAccount(AccountData accountData) throws EmailExistsException {

        if (accountService.emailExist(accountData.getEmail()))
            throw new EmailExistsException(accountData.getEmail());

        Company newCompany = anAccount()
                .withName(accountData.getName())
                .withPassword(encoder.encode(accountData.getPassword()))
                .withEmail(accountData.getEmail())
                .buildForCompany();

        return companyRepository.save(newCompany);
    }
}
