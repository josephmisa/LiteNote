package com.citu.litenote.ui.icons;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.citu.litenote.R;
import com.citu.litenote.utils.FileUtilities;
import com.citu.litenote.utils.MimeTypesUtilities;

import java.io.File;
import java.util.HashMap;

/**
 * Created by shemchavez on 3/13/2018.
 */

public class Icons {


    public static HashMap<String, Object> getIcon(Context context, File file) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (file.isDirectory()) {
            hashMap.put("color", R.color.materialIndigo);
            hashMap.put("drawable", ContextCompat.getDrawable(context, R.drawable.ic_folder_white));
        } else {
            String mimeType = FileUtilities.getMimeType(file);
            if (MimeTypesUtilities.isImage(mimeType)) {
                hashMap.put("color", R.color.materialBlue);
                hashMap.put("drawable", ContextCompat.getDrawable(context, R.drawable.ic_photo_white));
            } else if (MimeTypesUtilities.isPowerpoint(mimeType)) {
                hashMap.put("color", R.color.materialOrange);
                hashMap.put("drawable", ContextCompat.getDrawable(context, R.drawable.ic_powerpoint_white));
            } else if (mimeType.equals(MimeTypesUtilities.TXT)) {
                hashMap.put("color", R.color.materialGreen);
                hashMap.put("drawable", ContextCompat.getDrawable(context, R.drawable.ic_txt_white));
            } else if (mimeType.equals(MimeTypesUtilities.PDF)) {
                hashMap.put("color", R.color.materialRed);
                hashMap.put("drawable", ContextCompat.getDrawable(context, R.drawable.ic_pdf_white));
            }
        }
        return hashMap;
    }
}
