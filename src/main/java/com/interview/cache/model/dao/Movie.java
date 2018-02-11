package com.interview.cache.model.dao;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Document
public class Movie implements Serializable {
    @Id
    private String id;
    @Min(1)
    @Max(255)
    private int rank;
    @NotEmpty
    private String title;
    @Min(1878)
    @Max(2100)
    private int year;

    public Movie() {
    }

    public Movie(int rank, String title, int year) {
        this.rank = rank;
        this.title = title;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return Movie.class.getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", rank=" + rank +
                ", title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}
