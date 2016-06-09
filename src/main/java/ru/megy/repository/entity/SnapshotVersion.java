package ru.megy.repository.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "FS_SNAPSHOT_VERSION")
public class SnapshotVersion {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seqSnapshotVersionId")
    @SequenceGenerator(name = "seqSnapshotVersionId", sequenceName = "SEQ_SNAPSHOT_VERSION_ID")
    @Column(name = "ID")
    private Long id;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "REPO_ID")
    @NotNull
    private Repo repo;

    @Column(name = "CALC_SHA512")
    @NotNull
    private Boolean calcSha512;

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

    public Repo getRepo() {
        return repo;
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public Boolean getCalcSha512() {
        return calcSha512;
    }

    public void setCalcSha512(Boolean calcSha512) {
        this.calcSha512 = calcSha512;
    }


    @Override
    public String toString() {
        return "SnapshotVersion{" +
                "id=" + id +
                ", repo=" + repo.getId() +
                ", calcSha512=" + calcSha512 +
                ", createdDate=" + createdDate +
                '}';
    }
}
