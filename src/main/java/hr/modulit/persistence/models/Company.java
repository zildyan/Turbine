package hr.modulit.persistence.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COMPANIES")
@Data
@NoArgsConstructor
public class Company extends Account {

    @Column(name = "COMPANY_TEST")
    private Integer companyTest;

}
