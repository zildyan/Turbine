package hr.modulit.persistence.repos;

import hr.modulit.persistence.models.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByEmail(String email);

}
