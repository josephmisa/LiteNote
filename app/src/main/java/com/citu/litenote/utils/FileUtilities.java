package com.citu.litenote.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.citu.litenote.R;
import com.citu.litenote.data.models.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by shemchavez on 3/13/2018.
 */

public class FileUtilities {

    private static final String TAG = "### " + FileUtilities.class.getSimpleName();

    public static final String HOME_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/LiteNote";

    public static HashMap<String, Object> createDirectory(String name) {
        HashMap<String, Object> hashMap = new HashMap<>();
        File file = new File(createPath(name));
        try {
            // File already exists
            if (file.exists()) {
                Log.d(TAG, "Directory exists => " + file.getPath());
                hashMap.put("success", false);
                hashMap.put("message", "Exists");
            } else {
                if (file.mkdirs()) {
                    Log.d(TAG, "Directory created => " + file.getPath());
                    hashMap.put("success", true);
                    hashMap.put("message", "Created");
                } else {
                    Log.w(TAG, "Directory not created => " + file.getPath());
                    hashMap.put("success", false);
                    hashMap.put("message", "Not Created");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            hashMap.put("success", false);
            hashMap.put("message", e.getLocalizedMessage());
        }
        return hashMap;
    }

    public static File createTempFile(Context context, Uri uri) {
        File tempFile = new File(uri.getPath());
        File file = new File(Environment.getExternalStorageDirectory(), tempFile.getName());
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024];
            int length = 0;
            length = inputStream.read(buffer);
            while (length != -1) {
                fileOutputStream.write(buffer, 0, length);
                length = inputStream.read(buffer);
            }
            fileOutputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<String, Object> createPhoto(File file, Bitmap bitmap) {
        File imageFile = new File(file, UUID.randomUUID().toString() + ".jpg");
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.d(TAG, "Created Photo => " + imageFile.getPath());
            hashMap.put("success", true);
            hashMap.put("message", "Created Item");
        } catch (Exception e) {
            e.printStackTrace();
            hashMap.put("success", false);
            hashMap.put("message", e.getLocalizedMessage());
        }
        return hashMap;
    }

    public static HashMap<String, Object> createNote(File file, String title, String note) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            File noteFile = new File(file, title + ".txt");
            if (noteFile.exists()) {
                hashMap.put("success", false);
                hashMap.put("message", "Note Exists");
            }
            else {
                FileWriter fileWriter = new FileWriter(noteFile);
                fileWriter.append(note);
                fileWriter.flush();
                fileWriter.close();
                Log.d(TAG, "Created Note => " + noteFile.getPath());
                hashMap.put("success", true);
                hashMap.put("message", "Created Note");
            }
        } catch (IOException e) {
            e.printStackTrace();
            hashMap.put("success", false);
            hashMap.put("message", e.getLocalizedMessage());
        }
        return hashMap;
    }

    public static HashMap<String, Object> copyFile(File from, File to, boolean deleteFrom) {
        HashMap<String, Object> hashMap = new HashMap<>();
        InputStream inputStream;
        OutputStream outputStream;
        try {
            inputStream = new FileInputStream(from);
            outputStream = new FileOutputStream(to);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            Log.d(TAG, "Copied File => " + to.getPath());
            // Close
            inputStream.close();
            outputStream.close();
            // Delete
            if (deleteFrom) {
                from.delete();
            }
            hashMap.put("success", true);
            hashMap.put("message", deleteFrom ? "Moved" : "Created");
        } catch (IOException e) {
            e.printStackTrace();
            hashMap.put("success", false);
            hashMap.put("message", e.getLocalizedMessage());
        }
        return hashMap;
    }

    public static HashMap<String, Object> renameFile(File file, String name) {
        HashMap<String, Object> hashMap = new HashMap<>();
        File to = new File(file.getParentFile(), file.isDirectory() ? name : name + "." + getFileExtension(file));
        if (file.exists()) {
            file.renameTo(to);
            Log.d(TAG, "Renamed File => " + to.getPath());
            hashMap.put("success", true);
            hashMap.put("message", "Renamed");
        } else {
            Log.w(TAG, "File not exists => " + file.getPath());
            hashMap.put("success", true);
            hashMap.put("message", "Not exists");
        }
        return hashMap;
    }

    public static HashMap<String, Object> deleteFile(File file) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (file.exists()) {
            // Delete contents of Directory
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    f.delete();
                }
            }
            if (file.delete()) {
                Log.d(TAG, "File deleted => " + file.getPath());
                hashMap.put("success", true);
                hashMap.put("message", "Deleted");
            } else {
                Log.w(TAG, "File not deleted => " + file.getPath());
                hashMap.put("success", false);
                hashMap.put("message", "Not Deleted");
            }
        } else {
            Log.d(TAG, "File not exists => " + file.getPath());
            hashMap.put("success", false);
            hashMap.put("message", "Not Exists");
        }
        return hashMap;
    }

    public static File getFile(String name) {
        File file = new File(createPath(name));
        if (file.exists()) {
            Log.d(TAG, "File exists => " + file.getPath());
        } else {
            Log.w(TAG, "File not exists => " + file.getPath());
        }
        return file;
    }

    public static File[] getFiles(String name) {
        File file = new File(createPath(name));
        if (file.isDirectory()) {
            if (file.exists()) {
                Log.d(TAG, "Directory exists => " + file.getPath());
            } else {
                Log.w(TAG, "Directory not exists => " + file.getPath());
            }
        } else {
            Log.w(TAG, "File is not provider_paths Directory => " + file.getPath());
        }
        return file.listFiles();
    }

    public static List<Item> getItems(File file, File except) {
        List<Item> items = new ArrayList<>();
        for (File f : file.listFiles()) {
            if (!f.getName().equals(except.getName())) {
                items.add(new Item(f));
            }
        }
        return items;
    }

    public static String getMimeType(File file) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file));
    }

    public static String getNameOnly(File file) {
        if (file.isDirectory()) {
            return file.getName();
        } else {
            String path = file.getName();
            return path.substring(0, path.lastIndexOf("."));
        }
    }

    public static String getFileExtension(File file) {
        return MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
    }

    public static String createPath(String name) {
        return HOME_DIRECTORY + (name.isEmpty() ? "" : "/" + name);
    }
}
