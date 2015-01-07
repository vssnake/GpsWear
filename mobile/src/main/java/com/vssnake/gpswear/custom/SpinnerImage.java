package com.vssnake.gpswear.custom;

/**
 * Created by vssnake on 06/01/2015.
 */
public class SpinnerImage {


    public SpinnerImage(String text, int imageResource, String code){
        this.mImageResource = imageResource;
        this.mText = text;
        this.mCode = code;
    }

    private int mImageResource;
    private String mText;
    private String mCode;

    public int getImageUrl() {
        return mImageResource;
    }

    public void setImageUrl(int mImageUrl) {
        this.mImageResource = mImageUrl;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        this.mCode = code;
    }
}
