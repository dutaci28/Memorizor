package com.example.memorizor.Model;

public class Course {
    private String courseId;
    private String description;
    private String imageUrl;
    private String price;
    private String publisher;
    private String title;


    public Course() {
    }

    public Course(String courseId, String description, String imageUrl, String price, String publisher, String title) {
        this.courseId = courseId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.publisher = publisher;
        this.title = title;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
