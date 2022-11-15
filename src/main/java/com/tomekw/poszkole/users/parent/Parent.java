package com.tomekw.poszkole.users.parent;


import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.payments.Payment;
import com.tomekw.poszkole.users.User;
import com.tomekw.poszkole.users.userrole.UserRole;
import com.tomekw.poszkole.users.student.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;



@Entity
@NoArgsConstructor
@Data
public class Parent extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
                "id=" + id +
                ", studentList=" + studentList.stream().map(student -> student.getId()+" "+student.getName()+" "+ student.getSurname()) +
                ", paymentList=" + paymentList.stream().map(payment -> payment.getId() + " "+ payment.getCost()) +
                ", wallet=" + wallet +
                ", debt=" + debt +
                '}';
    }
}
