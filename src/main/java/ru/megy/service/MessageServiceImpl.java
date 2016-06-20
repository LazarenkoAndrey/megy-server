package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.megy.repository.MessageRepository;
import ru.megy.repository.entity.Message;
import ru.megy.repository.type.MessageStatusEnum;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
@ConfigurationProperties("message")
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender ;
    @Autowired
    private MessageRepository messageRepository;
    @NotNull
    private String mailSubject;
    @NotNull
    private String mailTo;
    @NotNull
    private String mailFrom;
    @NotNull
    private Integer mailRersendMax;

    @Transactional
    @Override
    public List<Message> getMessageList(int top) {
        Pageable pageable = new PageRequest(0, top, new Sort(Sort.Direction.DESC, "createdDate"));
        Page<Message> pages;

        pages = messageRepository.findAll(pageable);

        List<Message> messageList = new ArrayList<>();
        pages.forEach( message -> messageList.add(message));

        return messageList;
    }

    @Transactional
    @Override
    public Long addMessage(String text) {
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setResendCounter(0L);
        message.setStatus(MessageStatusEnum.CREATE);
        message.setSubject(mailSubject + " / " + getLocalName());
        message.setText(text);

        message = messageRepository.save(message);

        return message.getId();
    }

    @Transactional
    @Override
    public void doSending() {
        try(Stream<Message> messageStream = messageRepository.findAllByStatus(MessageStatusEnum.CREATE)) {
            messageStream.forEach(message -> {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(mailFrom);
                mailMessage.setTo(mailTo);
                mailMessage.setSubject(message.getSubject());
                mailMessage.setText(message.getText());

                try {
                    message.setResendCounter(message.getResendCounter() + 1);
                    javaMailSender.send(mailMessage);
                    message.setStatus(MessageStatusEnum.SENT);
                    message.setSentDate(new Date());
                } catch (MailException ex) {
                    logger.error("javaMailSender.send", ex);
                    if (message.getResendCounter() > mailRersendMax) {
                        message.setStatus(MessageStatusEnum.ERROR);
                    }
                }

                messageRepository.save(message);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("sleep", e);
                }
            });
        }
    }

    private String getLocalName() {
        try {
            InetAddress addr = null;
            addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            logger.error("getLocalHost", e);
        }
        return "unknown";
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public Integer getMailRersendMax() {
        return mailRersendMax;
    }

    public void setMailRersendMax(Integer mailRersendMax) {
        this.mailRersendMax = mailRersendMax;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }
}
