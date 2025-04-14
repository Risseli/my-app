package com.example.application.data;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Workout extends AbstractEntity {

    private String name;
    private LocalDateTime date;
    private Integer duration;
    private String comment;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

}
