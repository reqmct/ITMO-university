package ru.itmo.wp.form;

import javax.persistence.Lob;
import javax.validation.constraints.*;

public class PostCredentials {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 60)
    private String title;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 65000)
    @Lob
    private String text;
    @NotNull
    @NotBlank
    @Pattern(regexp = "\\s*[a-z]*(\\s+[a-z]+)*\\s*",
            message = "должно состоять из маленьких латинских букв или пробелов")
    private String tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
