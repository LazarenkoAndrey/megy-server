package ru.megy.repository;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Message;
import ru.megy.repository.type.MessageStatusEnum;

import java.util.stream.Stream;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Stream<Message> findAllByStatus(MessageStatusEnum status);
    Page<Message> findAll(Pageable pageable);
}
