package com.rubengees.easyheaderfooteradapter.sample;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * A simple item for showing in our list. It contains a text and a color.
 *
 * @author Ruben Gees
 */
public class Item implements Parcelable {

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private String text;
    private int color;

    public Item(@NonNull String text, @ColorInt int color) {
        this.text = text;
        this.color = color;
    }

    protected Item(Parcel in) {
        this.text = in.readString();
        this.color = in.readInt();
    }

    @NonNull
    public String getText() {
        return text;
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (color != item.color) return false;
        return text.equals(item.text);
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + color;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeInt(this.color);
    }
}
