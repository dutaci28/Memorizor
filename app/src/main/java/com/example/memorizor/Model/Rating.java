package com.example.memorizor.Model;

public class Rating {
    private String courseId;
    private String ratingId;
    private int value;

    public Rating() {
    }

    public Rating(String courseId, String ratingId, int value) {
        this.courseId = courseId;
        this.ratingId = ratingId;
        this.value = value;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
