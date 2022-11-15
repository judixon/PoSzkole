package com.tomekw.poszkole.mailbox.mailboxmessage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailboxMessageRepository extends CrudRepository<MailboxMessage,Long> {
}
