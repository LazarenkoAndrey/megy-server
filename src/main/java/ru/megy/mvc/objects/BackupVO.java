package ru.megy.mvc.objects;


import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BackupVO {
    @NotEmpty
    @Size(min = 1, max = 3999)
    private String path;

    @NotNull
    private Long repoId;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
    }
}
