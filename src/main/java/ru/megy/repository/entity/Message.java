package ru.megy.repository.entity;

import ru.megy.repository.type.MessageStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "FS_MESSAGE")
public class Message {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqMessageId")
    @SequenceGenerator(name = "seqMessageId", sequenceName = "SEQ_MESSAGE_ID")
    @Column(name = "ID")
    private Long id;

    @Column(name = "STATUS")
    @NotNull
    private MessageStatusEnum status;

    @Column(name = "SUBJECT")
    @NotNull
    private String subject;

    @Column(name = "TEXT")
    @NotNull
    private String text;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createdDate;

    @Column(name = "SENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentDate;

    @Column(name = "RESEND_COUNTER")
    @NotNull
    private Long resendCounter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageStatusEnum getStatus() {
        return status;
    }

    public void setStatus(MessageStatusEnum status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Long getResendCounter() {
        return resendCounter;
    }

    public void setResendCounter(Long resendCounter) {
        this.resendCounter = resendCounter;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", status=" + status +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", createdDate=" + createdDate +
                ", sentDate=" + sentDate +
                ", resendCounter=" + resendCounter +
                '}';
    }
}
