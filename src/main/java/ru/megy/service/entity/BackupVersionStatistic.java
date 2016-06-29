package ru.megy.service.entity;

public class BackupVersionStatistic {
    private String name;
    private Long value;
    private Long valueFile;
    private Long valueDir;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getValueFile() {
        return valueFile;
    }

    public void setValueFile(Long valueFile) {
        this.valueFile = valueFile;
    }

    public Long getValueDir() {
        return valueDir;
    }

    public void setValueDir(Long valueDir) {
        this.valueDir = valueDir;
    }
}
