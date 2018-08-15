package example.test.phong.plaidstudy.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import example.test.phong.plaidstudy.R;

public class ForegroundLinearLayout extends LinearLayout {
    @Nullable
    private Drawable mForeground;

    public ForegroundLinearLayout(Context context) {
        super(context);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundView, defStyleAttr, 0);
        final Drawable d = a.getDrawable(R.styleable.ForegroundView_android_foreground);
        if (d != null) {
            setForeground(d);
        }
        a.recycle();
    }

    public void setForeground(Drawable drawable) {
        if (mForeground != drawable) {
            if (mForeground != null) {
                mForeground.setCallback(null);
                unscheduleDrawable(mForeground);
            }

            mForeground = drawable;

            if (drawable != null) {
                setWillNotDraw(false);
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (who == mForeground);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (mForeground != null) mForeground.jumpToCurrentState();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mForeground != null && mForeground.isStateful()) {
            mForeground.setState(getDrawableState());
        }
    }

    @Override
    @Nullable
    public Drawable getForeground() {
        return mForeground;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mForeground != null) {
            mForeground.setBounds(0, 0, w, h);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mForeground != null) {
            mForeground.draw(canvas);
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (mForeground != null) {
            mForeground.setHotspot(x, y);
        }
    }
}
