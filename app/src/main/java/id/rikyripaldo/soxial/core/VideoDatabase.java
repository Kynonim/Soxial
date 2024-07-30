package id.rikyripaldo.soxial.core;

import androidx.annotation.NonNull;

public class VideoDatabase {

    private String profileUrl;
    private String videoUrl;
    private String userName;
    private String userDeskripsi;

    public VideoDatabase () {}

    public VideoDatabase(@NonNull String profileUrl, String videoUrl, String userName, String userDeskripsi) {
        this.profileUrl = profileUrl;
        this.videoUrl = videoUrl;
        this.userName = userName;
        this.userDeskripsi = userDeskripsi;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDeskripsi() {
        return userDeskripsi;
    }
}