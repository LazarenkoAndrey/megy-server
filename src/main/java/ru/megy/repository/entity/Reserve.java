package ru.megy.repository.entity;

import ru.megy.repository.type.ItemTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "FS_RESERVE")
public class Reserve {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqReserveId")
    @SequenceGenerator(name = "seqReserveId", sequenceName = "SEQ_RESERVE_ID")
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BACKUP_ID")
    @NotNull
    private Backup backup;

    @Column(name = "VERSION_ID")
    @NotNull
    private Long versionsId;

    @Column(name = "LAST_VERSION_ID")
    @NotNull
    private Long lastVersionsId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    @NotNull
    private ItemTypeEnum type;

    @Column(name = "NAME")
    @NotNull
    private String name;

    @Column(name = "PATH")
    private String path;

    @Column(name = "SIZE_BYTE")
    @NotNull
    private Long sizeByte;

    @Column(name = "LENGTH")
    @NotNull
    private Integer length;

    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date lastModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID")
    private Store store;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Backup getBackup() {
        return backup;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    public Long getVersionsId() {
        return versionsId;
    }

    public void setVersionsId(Long versionsId) {
        this.versionsId = versionsId;
    }

    public Long getLastVersionsId() {
        return lastVersionsId;
    }

    public void setLastVersionsId(Long lastVersionsId) {
        this.lastVersionsId = lastVersionsId;
    }

    public ItemTypeEnum getType() {
        return type;
    }

    public void setType(ItemTypeEnum type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        if(path!=null && path.length()==0) {
            return null;
        } else {
            return path;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Long getSizeByte() {
        return sizeByte;
    }

    public void setSizeByte(Long sizeByte) {
        this.sizeByte = sizeByte;
    }

    @Override
    public String toString() {
        return "Reserve{" +
                "id=" + id +
                ", backup=" + backup.getId() +
                ", versionsId=" + versionsId +
                ", lastVersionsId=" + lastVersionsId +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", sizeByte=" + sizeByte +
                ", length=" + length +
                ", lastModified=" + lastModified +
                ", store=" + store +
                '}';
    }
}
