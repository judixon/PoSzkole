package com.tomekw.poszkole.mailbox.mailboxthread;

import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.mailbox.mailboxmessage.MailboxMessage;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
public class MailboxThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "mailboxThread")
    private List<MailboxMessage> mailboxMessageList;

    @ManyToMany(mappedBy = "mailboxThreadList")
    private List<Mailbox> mailboxList;

    public MailboxThread(List<MailboxMessage> mailboxMessageList, List<Mailbox> mailboxList) {
        this.mailboxMessageList = mailboxMessageList;
        this.mailboxList = mailboxList;
    }
}
