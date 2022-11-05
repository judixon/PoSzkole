package com.tomekw.poszkole.mailbox.mailboxThread;

import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.mailbox.mailboxMessage.MailboxMessage;

import javax.persistence.*;
import java.util.List;

@Entity
public class MailboxThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<MailboxMessage> mailboxMessageList;

    @ManyToMany
    private Mailbox mailbox;

}
