package com.cerebellio.noted.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * Created by Sam on 11/02/2016.
 */
public class SketchView extends View {

    private Path mDrawPath;
    private Paint mDrawPaint, mCanvasPaint;
    private Canvas mDrawCanvas;
    private Bitmap mCanvasBitmap;
    private int mPaintColour = 0xFF660000;

    public SketchView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        mDrawPath = new Path();
        mDrawPaint = new Paint();

        mDrawPaint.setColor(mPaintColour);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(10);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mDrawCanvas = new Canvas(mCanvasBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                mDrawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setCanvasBitmap(Bitmap canvasBitmap) {
        if (!canvasBitmap.isMutable()) {
            canvasBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        mCanvasBitmap = canvasBitmap;
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    public void setColour(int colour) {
        invalidate();
        mPaintColour = colour;
        mDrawPaint.setColor(mPaintColour);
    }

    public byte[] getBitmapAsByteArray() {
        setDrawingCacheEnabled(true);

        Bitmap bitmap = getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

}
