package ru.megy.util.objects;

import ru.megy.repository.type.ItemTypeEnum;

import java.util.Date;
import java.util.Set;

public class Item {
    private ItemTypeEnum type;
    private String name;
    private String path;
    private Item parent;
    private Set<Item> childes;
    private Long sizeByte;
    private Integer length;
    private String sha512;
    private Date lastModified;

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
