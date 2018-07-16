package com.citu.litenote.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MimeTypesUtilities {

    public static String TXT = "text/plain";
    public static String PDF = "application/pdf";
    public static String[] IMAGE = new String[]{
            "image/jpeg",
            "image/bmp",
            "image/gif",
            "image/png",
    };
    public static String[] WORD = new String[]{
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
            "application/vnd.ms-word.document.macroEnabled.12",
            "pplication/vnd.ms-word.template.macroEnabled.12"
    };
    public static String[] EXCEL = new String[]{
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
            "pplication/vnd.ms-word.document.macroEnabled.12",
            "application/vnd.ms-word.template.macroEnabled.12"
    };
    public static String[] POWERPOINT = new String[]{
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.template",
            "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
            "application/vnd.ms-powerpoint.addin.macroEnabled.12",
            "application/vnd.ms-powerpoint.presentation.macroEnabled.12",
            "application/vnd.ms-powerpoint.template.macroEnabled.12",
            "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"
    };

    public static String[] getAcceptedMimeTypes() {
        List<String> acceptedMimeTypes = new ArrayList<>();
        acceptedMimeTypes.add(TXT);
        acceptedMimeTypes.add(PDF);
//        Collections.addAll(acceptedMimeTypes, WORD);
//        Collections.addAll(acceptedMimeTypes, EXCEL);
        Collections.addAll(acceptedMimeTypes, POWERPOINT);
        return acceptedMimeTypes.toArray(new String[acceptedMimeTypes.size()]);
    }

    public static boolean isPowerpoint(String mimeType) {
        for (int i = 0; i < POWERPOINT.length; i++) {
            if (POWERPOINT[i].equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImage(String mimeType) {
        for (int i = 0; i < IMAGE.length; i++) {
            if (IMAGE[i].equals(mimeType)) {
                return true;
            }
        }
        return false;
    }
}
