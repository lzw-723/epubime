package fun.lzwi.epubime.epub;

import java.util.List;

public class Metadata {
    private String title;
    private String creator;
    private final List<String> contributors;
    private String publisher;
    private String identifier;
    private final List<String> subjects;
    private String date;
    private String language;
    private String description;
    private String rights;
    private String type;

    public Metadata() {
        contributors = new java.util.ArrayList<>();
        subjects = new java.util.ArrayList<>();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String format;
    private String source;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void addSubject(String subject) {
        this.subjects.add(subject);
    }

    public String getSubject() {
        return subjects.get(0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContributor() {
        return contributors.get(0);
    }

    public List<String> getContributors() {
        return contributors;
    }

    public void addContributor(String contributor) {
        this.contributors.add(contributor);
    }
}
