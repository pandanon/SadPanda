package com.ecchi.sadpanda.util;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;
 
/**
 * ImageView to display top-crop scale of an image view.
 *
 * @author Chris Arriola
 */
public class CroppedImageView extends ImageView {
 
    public CroppedImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }
    
    public CroppedImageView(Context context, AttributeSet attrs)
    {
    	super(context, attrs);
    	setScaleType(ScaleType.MATRIX);
    }
    
    public CroppedImageView(Context context, AttributeSet attrs, int defStyle)
    {
    	super(context, attrs, defStyle);
    	setScaleType(ScaleType.MATRIX);
    }
 
    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        Matrix matrix = getImageMatrix();
            
        float scale;
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = getDrawable().getIntrinsicWidth();
        int drawableHeight = getDrawable().getIntrinsicHeight();
        
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }
            
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
            
        return super.setFrame(l, t, r, b);
    }        
}
