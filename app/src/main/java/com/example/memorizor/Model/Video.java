package com.example.memorizor.Model;

public class Video {
    private String hostCourseId;
    private String videoId;
    private Long videoIndex;
    private String videoUrl;

    public Video() {
    }

    public Video(String hostCourseId, String videoId, Long videoIndex, String videoUrl) {
        this.hostCourseId = hostCourseId;
        this.videoId = videoId;
        this.videoIndex = videoIndex;
        this.videoUrl = videoUrl;
    }

    public String getHostCourseId() {
        return hostCourseId;
    }

    public void setHostCourseId(String hostCourseId) {
        this.hostCourseId = hostCourseId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Long getVideoIndex() {
        return videoIndex;
    }

    public void setVideoIndex(Long videoIndex) {
        this.videoIndex = videoIndex;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "Video{" +
                "hostCourseId='" + hostCourseId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", videoIndex=" + videoIndex +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
