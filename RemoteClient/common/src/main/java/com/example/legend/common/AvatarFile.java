package com.example.legend.common;

import java.io.Serializable;

/**
 * @author Legend
 * @data by on 19-5-16.
 * @description
 */
public class AvatarFile implements Serializable {
    private int icon;
    private String avatarHeading;
    private String avatarSubheading;
    private String path;
    private String type;

    public AvatarFile(int icon, String avatarHeading, String avatarSubheading, String path, String type) {
        this.icon = icon;
        this.avatarHeading = avatarHeading;
        this.avatarSubheading = avatarSubheading;
        this.path = path;
        this.type = type;
    }

    public String getHeading() {
        return this.avatarHeading;
    }

    public String getPath() {
        return this.path;
    }

    public String getSubheading() {
        return this.avatarSubheading;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getType() {
        return this.type;
    }
}

