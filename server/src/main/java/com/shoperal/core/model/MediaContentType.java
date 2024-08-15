package com.shoperal.core.model;

public enum MediaContentType {
    // Video formats
    MPEG_4("video/mp4", ".mp4"), 
    FLASH("video/x-flv", ".flv"), 
    MOBILE_3GP("video/3gpp", ".3gp"),
    AV_INTERLEAVE("video/x-msvideo", ".avi"), 
    QUICK_TIME("video/quicktime", ".mov"),
    WINDOWS_MEDIA("video/x-ms-wmv", ".wmv"),

    // Image formats
    JPEG("image/jpeg", ".jpeg"), 
    PNG("image/png", ".png"), 
    GIF("image/gif", ".gif"), 
    TIFF("image/x-tiff", ".tiff");

    private String contentType;
    private String extension;

    public String getMIMEType() {
        return this.contentType;
    }

    public String getFileExtension() {
        return this.extension;
    }

    MediaContentType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }
}
