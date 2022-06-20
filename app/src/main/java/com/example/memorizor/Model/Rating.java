package com.example.memorizor.Model;

public class Rating {
    private String courseId;
    private String ratingId;
    private String userId;
    private int value;

    public Rating() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Rating(String courseId, String ratingId, String userId, int value) {
        this.courseId = courseId;
        this.ratingId = ratingId;
        this.userId = userId;
        this.value = value;
    }
}
