package cn.citizenwiki.model.dto.paratranz;

import java.util.Objects;

public class PZTranslation {
    private Integer id;
    private String key;
    private String original;
    private String translation;
    private Integer stage;
    private String context;

    // Getter and Setter methods
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "TranslationEntry{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", original='" + original + '\'' +
                ", translation='" + translation + '\'' +
                ", stage=" + stage +
                ", context='" + context + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PZTranslation that = (PZTranslation) o;
        return Objects.equals(id, that.id) && Objects.equals(key, that.key) && Objects.equals(original, that.original) && Objects.equals(translation, that.translation) && Objects.equals(stage, that.stage) && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, original, translation, stage, context);
    }
}
