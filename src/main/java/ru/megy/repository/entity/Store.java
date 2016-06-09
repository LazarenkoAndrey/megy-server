package ru.megy.repository.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FS_STORE")
public class Store {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqStoreId")
    @SequenceGenerator(name = "seqStoreId", sequenceName = "SEQ_STORE_ID")
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BACKUP_ID")
    @NotNull
    private Backup backup;

    @Column(name = "PATH")
    @NotNull
    private String path;

    @Column(name = "SIZE_BYTE")
    @NotNull
    private Long sizeByte;

    @Column(name = "SHA512")
    @NotNull
    private String sha512;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSizeByte() {
        return sizeByte;
    }

    public void setSizeByte(Long sizeByte) {
        this.sizeByte = sizeByte;
    }

    public String getSha512() {
        return sha512;
    }

    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Backup getBackup() {
        return backup;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", path=" + path +
                ", sizeByte=" + sizeByte +
                ", sha512='" + sha512 + '\'' +
                '}';
    }
}
