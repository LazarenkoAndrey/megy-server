package ru.megy.repository.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "FS_BACKUP_VERSION")
public class BackupVersion {
    public final static long MAX_VERSION = 9999999999L;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqBackupVersionId")
    @SequenceGenerator(name = "seqBackupVersionId", sequenceName = "SEQ_BACKUP_VERSION_ID")
    @Column(name = "ID")
    private Long id;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "BACKUP_ID")
    @NotNull
    private Backup backup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Backup getBackup() {
        return backup;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    @Override
    public String toString() {
        return "BackupVersion{" +
                "id=" + id +
                ", backup=" + backup.getId() +
                ", createdDate=" + createdDate +
                '}';
    }
}
