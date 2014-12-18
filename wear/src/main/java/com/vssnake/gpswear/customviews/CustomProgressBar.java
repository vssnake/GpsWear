package com.vssnake.gpswear.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.vssnake.gpswear.R;



public class CustomProgressBar extends View {
    private String mString ="";
    private int mBarColor;
    private float mDimension = 12;
    private Drawable mExampleDrawable;
    private int mPercent;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    Rect r;
    Paint p = new Paint();
    int paddingLeft;
    int paddingTop;
    int paddingRight;
    int paddingBottom;

    int contentWidth;
    int contentHeight;

    public CustomProgressBar(Context context) {
        super(context);
        //this.setWillNotDraw(false);
        init(null, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
            if (attrs != null){


            final TypedArray a = getContext().obtainStyledAttributes(
                    attrs, R.styleable.CustomProgressBar, defStyle, 0);

            mString = a.getString(
                    R.styleable.CustomProgressBar_exampleString);

            mBarColor = a.getColor(R.styleable.CustomProgressBar_bar_color,
                    mBarColor);
            // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
            // values that should fall on pixel boundaries.
                mDimension = a.getDimension(
                    R.styleable.CustomProgressBar_exampleDimension,
                        mDimension);

            mPercent = a.getInteger(R.styleable.CustomProgressBar_bar_percent,mPercent);

            if (a.hasValue(R.styleable.CustomProgressBar_exampleDrawable)) {
                mExampleDrawable = a.getDrawable(
                        R.styleable.CustomProgressBar_exampleDrawable);
                if (mExampleDrawable != null)
                    mExampleDrawable.setCallback(this);
            }

            a.recycle();

            }



        if (isInEditMode() && mString.isEmpty() && mDimension == 0
                && mBarColor == 0){
            mString = "example";
            mDimension = 12;
            mBarColor = Color.RED;
        }




        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mDimension);
        mTextPaint.setColor(Color.WHITE);
        mTextWidth = mTextPaint.measureText(mString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        //int minh = MeasureSpec.getSize(w) - (int)mTextWidth + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w) - (int)mTextWidth, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        float wtf = contentWidth /100f *mPercent;


        r = new Rect(0,0,(int)wtf,contentHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);







        p.setStyle(Paint.Style.FILL);
        p.setColor(mBarColor);
        canvas.drawRect(r,p);

        // Draw the text.
        canvas.drawText(mString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mDimension = exampleDimension;
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    public void setData(String text,int color,int power){
        this.mDimension = 14;
        this.mPercent = power;
        this.mBarColor = color;
        this.mString = text;
        setExampleString(String.valueOf(power) + "SNR");
        invalidateTextPaintAndMeasurements();
        //invalidate();
        requestLayout();


    }
}
