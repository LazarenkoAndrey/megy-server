package ru.megy.mvc.objects;


import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RepoVO {
    @NotEmpty
    @Size(min = 1, max = 3999)
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
