package cn.test.gudong.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jiahaodong on 2017/5/16-18:24.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class BgAnimate extends View {

    private int viewWidth = 0;
    private int viewHeight = 0;

    private int r = 0;

    //圆线和圆偏移18dp
    int h = 18;

    int dymicH=0;

    //左右偏移 dp
    int w = 3;
    // 1/3
    private int curveHeight = 0;

    private Paint mPaint = new Paint();
    private Paint linePaint = new Paint();

    // private double c = 0.551915024494;


    private void initBeforeDraw() {
        // mPaint
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(dp2px(1));

        if (isInEditMode()) {
            return;
        }
        initAnimation();
    }

    public BgAnimate(Context context) {
        super(context);
        initBeforeDraw();
    }

    public BgAnimate(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBeforeDraw();

    }


    public BgAnimate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBeforeDraw();

    }

    private void initAnimation(){
        ValueAnimator animator = ValueAnimator.ofFloat(1, -1f, 1);
        animator.setDuration(3000);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float)animation.getAnimatedValue();


                dymicH= (int) (dp2px(h-9)/2*f);

                //Log.i("Tag", "Value is" + String.valueOf(dymicH));
                invalidate();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas.drawColor(0xffcccccc);
        canvas.translate(viewWidth / 2, viewWidth + dp2px(h));
        //  Path curvePath = new Path();
        //  double bian = 100;
        // curvePath.moveTo(0, 100);
        // curvePath.cubicTo((float) (100 * c), 100, 100, (float) (100 * c), 100, 0);
        // curvePath.lineTo((float) (100*c),100);
        // curvePath.lineTo(100,(float) (100*c));
        //curvePath.lineTo(100,0);
        //canvas.drawPath(curvePath, mPaint);
        canvas.drawCircle(0, 0, r, mPaint);

        canvas.save();
        canvas.translate(-dp2px(w), -dp2px(h / 2)+dymicH);

        canvas.drawCircle(0, 0, r, linePaint);
        canvas.restore();

        canvas.save();
        canvas.translate(dp2px(w), -dp2px(h / 2)-dymicH);

        canvas.drawCircle(0, 0, r, linePaint);
        canvas.restore();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        curveHeight = viewHeight / 3;
        r = viewWidth;

    }

    //dp转像素
    public float dp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}
