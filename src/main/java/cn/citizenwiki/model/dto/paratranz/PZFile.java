package cn.citizenwiki.model.dto.paratranz;

import java.time.ZonedDateTime;

public class PZFile {
    private int id;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime modifiedAt;
    private String name;
    private int project;
    private String format;
    private int total;
    private int translated;
    private int disputed;
    private int checked;
    private int reviewed;
    private int hidden;
    private int locked;
    private int words;
    private String hash;
    private String folder;
    private Progress progress;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ZonedDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTranslated() {
        return translated;
    }

    public void setTranslated(int translated) {
        this.translated = translated;
    }

    public int getDisputed() {
        return disputed;
    }

    public void setDisputed(int disputed) {
        this.disputed = disputed;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public int getReviewed() {
        return reviewed;
    }

    public void setReviewed(int reviewed) {
        this.reviewed = reviewed;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", modifiedAt=" + modifiedAt +
                ", name='" + name + '\'' +
                ", project=" + project +
                ", format='" + format + '\'' +
                ", total=" + total +
                ", translated=" + translated +
                ", disputed=" + disputed +
                ", checked=" + checked +
                ", reviewed=" + reviewed +
                ", hidden=" + hidden +
                ", locked=" + locked +
                ", words=" + words +
                ", hash='" + hash + '\'' +
                ", folder='" + folder + '\'' +
                ", progress=" + progress +
                '}';
    }
}
