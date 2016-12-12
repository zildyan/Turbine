package hr.modulit.persistence.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
public class User extends Account {

    @Column(name = "USER_TEST")
    private Integer userTest;

}
