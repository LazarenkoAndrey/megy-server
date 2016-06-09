package ru.megy.repository.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

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

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SnapshotVersion> SnapshotVersions;

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

    public Set<SnapshotVersion> getSnapshotVersions() {
        return SnapshotVersions;
    }

    public void setSnapshotVersions(Set<SnapshotVersion> snapshotVersions) {
        this.SnapshotVersions = snapshotVersions;
    }

    @Override
    public String toString() {
        return String.format("Repository[id=%d, path='%s']", id, path);
    }
}
