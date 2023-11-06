package org.eclipse.dirigible.components.command;

import com.google.gson.annotations.SerializedName;

public enum CommandOS {
    @SerializedName("unix")
    UNIX,
    @SerializedName("linux")
    LINUX,
    @SerializedName("mac")
    MAC,
    @SerializedName("windows")
    WINDOWS
}