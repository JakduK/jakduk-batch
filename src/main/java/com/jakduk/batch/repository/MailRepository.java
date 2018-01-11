package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.Mail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MailRepository extends MongoRepository<Mail, String> {
    Optional<Mail> findOneById(String id);
}
