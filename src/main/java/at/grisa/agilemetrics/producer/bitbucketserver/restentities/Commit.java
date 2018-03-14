package at.grisa.agilemetrics.producer.bitbucketserver.restentities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commit {
    private String id;
    private String message;
    private User author;
    private Date authorTimestamp;
    private User committer;
    private Date committerTimestamp;

    public Commit() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getAuthorTimestamp() {
        return authorTimestamp;
    }

    public void setAuthorTimestamp(Date authorTimestamp) {
        this.authorTimestamp = authorTimestamp;
    }

    public User getCommitter() {
        return committer;
    }

    public void setCommitter(User committer) {
        this.committer = committer;
    }

    public Date getCommitterTimestamp() {
        return committerTimestamp;
    }

    public void setCommitterTimestamp(Date committerTimestamp) {
        this.committerTimestamp = committerTimestamp;
    }
}
