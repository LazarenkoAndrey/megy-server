package ru.megy.repository.entity;

import ru.megy.repository.type.ItemTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "FS_ITEM")
public class Item {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqItemId")
    @SequenceGenerator(name = "seqItemId", sequenceName = "SEQ_ITEM_ID")
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "VERSION_ID", nullable = false)
    private SnapshotVersion versions;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    @NotNull
    private ItemTypeEnum type;

    @Column(name = "NAME")
    @NotNull
    private String name;

    @Column(name = "PATH")
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Item parent;

    @OneToMany(mappedBy = "parent")
    private Set<Item> childes;

    @Column(name = "SIZE_BYTE")
    @NotNull
    private Long sizeByte;

    @Column(name = "LENGTH")
    @NotNull
    private Integer length;

    @Column(name = "SHA512")
    private String sha512;

    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date lastModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SnapshotVersion getVersions() {
        return versions;
    }

    public void setVersions(SnapshotVersion versions) {
        this.versions = versions;
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

    public Item getParent() {
        return parent;
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public Long getSizeByte() {
        return sizeByte;
    }

    public void setSizeByte(Long sizeByte) {
        this.sizeByte = sizeByte;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getSha512() {
        return sha512;
    }

    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Set<Item> getChildes() {
        return childes;
    }

    public void setChildes(Set<Item> childes) {
        this.childes = childes;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", versions=" + versions.getId() +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", sizeByte=" + sizeByte +
                ", length=" + length +
                ", sha512='" + sha512 + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }
}
