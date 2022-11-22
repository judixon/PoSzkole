package com.tomekw.poszkole.user.parent;

import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.payment.Payment;
import com.tomekw.poszkole.user.User;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.userrole.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode()
public class Parent extends User {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Student> studentList;

    @OneToMany(mappedBy = "parentOfStudent", cascade = CascadeType.PERSIST)
    private List<Payment> paymentList;

    private BigDecimal wallet;
    private BigDecimal debt;

    public Parent(String name, String surname, String email, String telephoneNumber, String username, String password, Mailbox mailbox, List<UserRole> roles, List<Student> studentList, List<Payment> paymentList, BigDecimal wallet, BigDecimal debt) {
        super(name, surname, email, telephoneNumber, username, password, mailbox, roles);
        this.studentList = studentList;
        this.paymentList = paymentList;
        this.wallet = wallet;
        this.debt = debt;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "ID=" + super.getId() +
                super.toString() +
                ", wallet=" + wallet +
                ", debt=" + debt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (super.equals(o)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
