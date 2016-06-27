package ru.megy.repository.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FS_REPO")
public class Repo {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqRepoId")
    @SequenceGenerator(name = "seqRepoId", sequenceName = "SEQ_REPO_ID")
    @Column(name = "ID")
    private Long id;

    @Column(name = "PATH")
    @NotNull
    private String path;

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

    @Override
    public String toString() {
        return String.format("Repository[id=%d, path='%s']", id, path);
    }
}
