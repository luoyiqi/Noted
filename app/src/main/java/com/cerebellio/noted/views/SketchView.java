package com.cerebellio.noted.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.cerebellio.noted.models.listeners.IOnSketchActionListener;

import java.util.Stack;

/**
 * Custom View which allows user to draw on a canvas
 */
public class SketchView extends View {

    private static final int INITIAL_STROKE_SIZE = 30;

    private Path mDrawPath;
    private Paint mDrawPaint;
    private Bitmap mCanvasBitmap;
    private StrokeType mStrokeType = StrokeType.STROKE;
    private Stack<Pair<Path, Paint>> mUndonePaths = new Stack<>();
    private Stack<Pair<Path, Paint>> mPaths = new Stack<>();

    private int mPaintColour = 0xFF000000;
    private float mX, mY;

    private IOnSketchActionListener mIOnSketchActionListener;

    public enum StrokeType {
        STROKE,
        ERASER
    }

    public SketchView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        mDrawPath = new Path();
        mDrawPaint = new Paint();

        mDrawPaint.setColor(mPaintColour);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(INITIAL_STROKE_SIZE);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mCanvasBitmap != null) {
            canvas.drawBitmap(mCanvasBitmap, 0, 0, null);
        }

        for (Pair<Path, Paint> path : mPaths) {
            canvas.drawPath(path.first, path.second);
        }

        mIOnSketchActionListener.onChange();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        if (mStrokeType.equals(StrokeType.ERASER)) {
            mDrawPaint.setColor(Color.WHITE);
        } else {
            mDrawPaint.setColor(mPaintColour);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath.reset();
                Paint paint = new Paint(mDrawPaint);
                mPaths.push(new Pair<>(mDrawPath, paint));
                mDrawPath.reset();
                mDrawPath.moveTo(touchX, touchY);
                mX = touchX;
                mY = touchY;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.quadTo(mX, mY, (touchX + mX) / 2, (touchY + mY) / 2);
                mX = touchX;
                mY = touchY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mDrawPath.lineTo(mX, mY);
                Paint newPaint = new Paint(mDrawPaint);
                mPaths.push(new Pair<>(mDrawPath, newPaint));
                mDrawPath = new Path();
                invalidate();
                break;
            default:
        }
        return true;
    }

    public void undoAction() {
        if (mPaths.size() > 1) {
            //remove move path
            mUndonePaths.push(mPaths.pop());

            //remove lift up path
            mUndonePaths.push(mPaths.pop());

            invalidate();
        }
    }

    public void redoAction() {
        if (mUndonePaths.size() > 0) {
            mPaths.push(mUndonePaths.pop());
            mPaths.push(mUndonePaths.pop());
            invalidate();
        }
    }

    public boolean isUndoAvailable() {
        return mPaths.size() > 1;
    }

    public boolean isRedoAvailable() {
        return mUndonePaths.size() > 0;
    }

    public boolean hasChangeBeenMade() {
        return mPaths.size() > 0;
    }

    public void setCanvasBitmap(Bitmap canvasBitmap) {
        if (!canvasBitmap.isMutable()) {
            canvasBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        mCanvasBitmap = canvasBitmap;
    }

    public void setColour(int colour) {
        invalidate();
        mPaintColour = colour;
        mDrawPaint.setColor(mPaintColour);
    }

    public Bitmap getSketch() {
        setDrawingCacheEnabled(true);
        return getDrawingCache();
    }

    public int getStrokeSize() {
        return Math.round(mDrawPaint.getStrokeWidth());
    }

    public void setStrokeSize(int strokeSize) {
        mDrawPaint.setStrokeWidth(strokeSize);
    }

    public StrokeType getStrokeType() {
        return mStrokeType;
    }

    public void setStrokeType(StrokeType strokeType) {
        mStrokeType = strokeType;
    }

    public void setIOnSketchActionListener(IOnSketchActionListener IOnSketchActionListener) {
        mIOnSketchActionListener = IOnSketchActionListener;
    }


}
