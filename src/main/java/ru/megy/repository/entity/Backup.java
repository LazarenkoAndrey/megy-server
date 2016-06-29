package ru.megy.repository.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FS_BACKUP")
public class Backup {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqBackupId")
    @SequenceGenerator(name = "seqBackupId", sequenceName = "SEQ_BACKUP_ID")
    @Column(name = "ID")
    private Long id;

    @Column(name = "PATH")
    @NotNull
    private String path;

    @ManyToOne
    @JoinColumn(name = "REPO_ID")
    @NotNull
    private Repo repo;

    @OneToOne
    @JoinColumn(name = "LAST_VERSION_ID")
    private BackupVersion lastVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Repo getRepo() {
        return repo;
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public BackupVersion getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(BackupVersion lastVersion) {
        this.lastVersion = lastVersion;
    }

    @Override
    public String toString() {
        return "Backup{" +
                "id=" + id +
                ", repo=" + repo.getId() +
                ", path='" + path + '\'' +
                ", lastVersion=" + (lastVersion !=null ? lastVersion.getId() : "null") +
                '}';
    }
}
