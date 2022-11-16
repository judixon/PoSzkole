package com.tomekw.poszkole.mailbox;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailboxRepository extends CrudRepository<Mailbox, Long> {
}
