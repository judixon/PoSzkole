package com.tomekw.poszkole.mailbox;

import com.tomekw.poszkole.mailbox.mailboxThread.MailboxThread;
import com.tomekw.poszkole.users.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Mailbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "mailbox", fetch = FetchType.LAZY)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "mailbox_threads",
            joinColumns = @JoinColumn(name = "mailbox_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "mailbox_thread_id",referencedColumnName = "id")
    )
    private List<MailboxThread> mailboxThreadList;

    public Mailbox(User owner, List<MailboxThread> mailboxThreadList) {
        this.owner = owner;
        this.mailboxThreadList = mailboxThreadList;
    }
}
