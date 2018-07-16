package com.citu.litenote.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.citu.litenote.utils.FileUtilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shemchavez on 3/12/2018.
 */

public class Item implements Parcelable {

    private File file;
    private String name;
    private long created_date;
    private long updated_date;
    private String mimeType;

    public Item(File file) {
        this.file = file;
        this.name = file.getName();
        this.created_date = file.lastModified();
        this.updated_date = file.lastModified();
        this.mimeType = FileUtilities.getMimeType(this.file);
    }

    protected Item(Parcel in) {
        name = in.readString();
        created_date = in.readLong();
        updated_date = in.readLong();
        mimeType = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public long getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(long updated_date) {
        this.updated_date = updated_date;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getParseCreatedDate() {
        return new SimpleDateFormat("MM/dd/yyyy h:mm a").format(new Date(getCreated_date()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(created_date);
        dest.writeLong(updated_date);
        dest.writeString(mimeType);
    }

    @Override
    public String toString() {
        return getName();
    }
}
