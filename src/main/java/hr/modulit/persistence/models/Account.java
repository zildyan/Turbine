package hr.modulit.persistence.models;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "ACCOUNTS")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ACCOUNT_ID", unique = true, nullable = false)
    protected Long id;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @Column(name = "PASSWORD", nullable = false, length = 60)
    protected String password;

    @Email
    @Column(name = "EMAIL", nullable = false)
    protected String email;

    @Column(name = "ROLE", nullable = false)
    protected String role;

    @Column(name = "ENABLED", nullable = false)
    @ColumnDefault(value = "false")
    protected boolean enabled;

    @CreatedDate
    @Column(name = "DATE_CREATED", nullable = false)
    protected Date dateCreated;

}
