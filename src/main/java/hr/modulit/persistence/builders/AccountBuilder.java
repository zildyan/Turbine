package hr.modulit.persistence.builders;

import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.Company;
import hr.modulit.persistence.models.User;

import static hr.modulit.Constants.COMPANY_ROLE;
import static hr.modulit.Constants.USER_ROLE;

public final class AccountBuilder {

    private String name;
    private String password;
    private String email;

    private AccountBuilder() {
    }

    public static AccountBuilder anAccount() {
        return new AccountBuilder();
    }

    public AccountBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AccountBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public AccountBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public User buildForUser() {
        User user = (User) buildAccount();
        user.setRole(USER_ROLE);
        return user;
    }

    public Company buildForCompany() {
        Company company = (Company) buildAccount();
        company.setRole(COMPANY_ROLE);
        return company;
    }

    private Account buildAccount() {
        Account account = new Account();
        account.setName(name);
        account.setPassword(password);
        account.setEmail(email);
        return account;
    }

}
