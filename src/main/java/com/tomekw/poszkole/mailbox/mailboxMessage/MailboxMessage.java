package com.tomekw.poszkole.mailbox.mailboxMessage;


import com.tomekw.poszkole.users.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class MailboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String messageBody;
    private LocalDateTime creationTime;

    @ManyToMany
    @JoinTable(name = "mailbox_message_receivers",
    joinColumns = @JoinColumn(name = "mailbox_message_id",referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "receiver_id",referencedColumnName = "id"))
    private List<User> receivers;

    @OneToOne
    private User sender;

    public MailboxMessage(String title, String messageBody, LocalDateTime creationTime, List<User> receivers, User sender) {
        this.title = title;
        this.messageBody = messageBody;
        this.creationTime = creationTime;
        this.receivers = receivers;
        this.sender = sender;
    }
}
