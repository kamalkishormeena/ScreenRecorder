package com.app.kk.screenrecorder;

public class Item {
    public String vidImage, vidTitle, vidDuration, vidSize;

    public Item(String vidImage, String title, String duration, String size) {
        this.vidImage = vidImage;
        this.vidTitle = title;
        this.vidDuration = duration;
        this.vidSize = size;
    }

    public String getVidImage() {
        return vidImage;
    }

    public String getVidTitle() {
        return vidTitle;
    }

    public String getVidDuration() {
        return vidDuration;
    }

    public String getVidSize() {
        return vidSize;
    }

}


