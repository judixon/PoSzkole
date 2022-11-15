package com.tomekw.poszkole.mailbox.mailboxmessage;


import com.tomekw.poszkole.mailbox.mailboxthread.MailboxThread;
import com.tomekw.poszkole.users.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "mailbox_thread_id")
    private MailboxThread mailboxThread;

    @OneToOne
    private User sender;

    public MailboxMessage(String title, String messageBody, LocalDateTime creationTime, MailboxThread mailboxThread, User sender) {
        this.title = title;
        this.messageBody = messageBody;
        this.creationTime = creationTime;
        this.mailboxThread = mailboxThread;
        this.sender = sender;
    }
}
