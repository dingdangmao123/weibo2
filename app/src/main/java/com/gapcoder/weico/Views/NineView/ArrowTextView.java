package com.gapcoder.weico.Views.NineView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gapcoder.weico.R;

/**
 * Created by gapcoder on 2018/6/9.
 */

public class ArrowTextView extends View {


    Paint p;

    private int bgCoclor;
    private int textColor;

    private float arrowLeft;
    private float arrowHeight;
    private float arrowWidth;
    private float textSize;
    private String text;

    public ArrowTextView(Context context) {
        this(context, null);
    }

    public ArrowTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowTextView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowTextView, defStyle, 0);
        arrowHeight = a.getDimension(R.styleable.ArrowTextView_arrowHeight, 30);
        arrowLeft = a.getDimension(R.styleable.ArrowTextView_arrowLeft, 20);
        arrowWidth = a.getDimension(R.styleable.ArrowTextView_arrowWidth, 20);

        textSize = a.getDimension(R.styleable.ArrowTextView_textSize, 5);

        text = a.getString(R.styleable.ArrowTextView_text);
        bgCoclor = a.getColor(R.styleable.ArrowTextView_bgColor, 0);
        textColor = a.getColor(R.styleable.ArrowTextView_textColor, 0);
        a.recycle();

        p = new Paint();
        p.setTextSize(textSize);
        p.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = 0;
        int h = 0;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            w = widthSize;
        } else {

            Rect r = new Rect();
            p.getTextBounds(text, 0, text.length(), r);
            w = r.width() + getPaddingLeft() + getPaddingRight();

        }


        if (heightMode == MeasureSpec.EXACTLY) {
            h = heightSize;
        } else {
            Rect r = new Rect();
            p.getTextBounds(text, 0, text.length(), r);
            h = r.height() + getPaddingTop() + getPaddingBottom() + (int) arrowHeight;
        }
        setMeasuredDimension(w,h);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        p.setColor(bgCoclor);
        canvas.drawRect(0, arrowHeight, getWidth(), getHeight(), p);

        p.setColor(textColor);
        Paint.FontMetrics m=p.getFontMetrics();
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(text, 0, text.length(), getPaddingLeft(), getPaddingTop() + arrowHeight-m.top-m.bottom, p);

        p.setStyle(Paint.Style.FILL);
        p.setColor(bgCoclor);
        Path path=new Path();
        path.moveTo(arrowLeft, arrowHeight);
        path.lineTo(arrowLeft + arrowWidth/2,0);
        path.lineTo(arrowLeft + arrowWidth, arrowHeight);
        canvas.drawPath(path,p);


    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }
}
