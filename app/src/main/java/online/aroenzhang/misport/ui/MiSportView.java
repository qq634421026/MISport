package online.aroenzhang.misport.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import online.aroenzhang.misport.R;

/**
 * @author aroenzhang
 * @date 2017/10/16
 */
public class MiSportView extends View {

  /**
   * 绘制步数的画笔
   */
  private Paint walkNumPaint;
  /**
   * 绘制公里和卡路里的画笔
   */
  private Paint caloriePaint;
  private Bitmap bitmap;

  private Paint lightPaint;
  private Paint smallCirclePaint;
  private float[] pos;
  private float[] tan;
  private Paint dotPaint;
  private ValueAnimator dotAnimator;
  private Paint rangPaint;
  private Shader shader;
  private ValueAnimator rangAnimator;
  private float rangProess;

  public MiSportView(Context context) {
    this(context, null);
  }

  public MiSportView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MiSportView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private int mWidth;
  private int mHeight;

  private boolean isDrawSmallCircle = false;
  private float currentPross;

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
    mHeight = h;
    bigRadiu = mWidth / 2 / 3 * 2;
    smallRadiu = bigRadiu * 0.95f;
    dotAnimator = ValueAnimator.ofFloat(0, 0.8f);
    dotAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        currentPross = (float) valueAnimator.getAnimatedValue();

        invalidate();
      }
    });
    dotAnimator.setDuration(2000);

    rangAnimator = ValueAnimator.ofFloat(0, 1f);
    rangAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        rangProess = (float) valueAnimator.getAnimatedValue();

        invalidate();
      }
    });
    rangAnimator.setDuration(20000);
    rangAnimator.setRepeatMode(ValueAnimator.RESTART);
    rangAnimator.setRepeatCount(ValueAnimator.INFINITE);

    ValueAnimator valueAnimator = ValueAnimator.ofFloat(mWidth / 2 / 3 * 2, mWidth / 2 / 12 * 9);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        bigRadiu = (float) valueAnimator.getAnimatedValue();
        invalidate();
      }
    });
    valueAnimator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        isDrawSmallCircle = true;
        dotAnimator.start();
        rangAnimator.start();
      }
    });
    valueAnimator.setInterpolator(new OvershootInterpolator(3));
    valueAnimator.setDuration(1000);
    valueAnimator.start();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);

    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);

    int resultW = widthSize;
    int resultH = heightSize;

    int contentW;
    int contentH;
    if (widthMode == MeasureSpec.AT_MOST) {
      contentW = 120;
      resultW = Math.min(resultW, contentW);
    }
    if (heightMode == MeasureSpec.AT_MOST) {
      contentH = 120;
      resultH = Math.min(resultH, contentH);
    }
    setMeasuredDimension(resultW, resultH);
  }

  private Path samllCirclePath;
  private PathMeasure pathMeasure;

  private void init() {
    walkNumPaint = new Paint();
    walkNumPaint.setColor(Color.WHITE);
    walkNumPaint.setTextSize(dip2px(80));
    walkNumPaint.setStyle(Paint.Style.STROKE);
    walkNumPaint.setAntiAlias(true);
    caloriePaint = new Paint();
    caloriePaint.setColor(Color.parseColor("#96d0f8"));
    caloriePaint.setAntiAlias(true);
    caloriePaint.setTextSize(dip2px(18));
    caloriePaint.setStrokeWidth(2);
    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.watch);
    lightPaint = new Paint();
    shader = new SweepGradient(mWidth / 2, getTop() + mHeight / 2, Color.WHITE,
        Color.parseColor("#2a8ee8"));
    lightPaint.setShader(shader);
    lightPaint.setStyle(Paint.Style.STROKE);
    lightPaint.setAntiAlias(true);
    lightPaint.setStrokeWidth(40);

    smallCirclePaint = new Paint();
    smallCirclePaint.setStyle(Paint.Style.STROKE);
    smallCirclePaint.setAntiAlias(true);
    smallCirclePaint.setStrokeWidth(4);
    smallCirclePaint.setColor(Color.WHITE);
    samllCirclePath = new Path();
    pathMeasure = new PathMeasure();

    dotPaint = new Paint();
    dotPaint.setAntiAlias(true);
    dotPaint.setColor(Color.WHITE);

    rangPaint = new Paint();
    rangPaint.setStrokeWidth(40);
    rangPaint.setStyle(Paint.Style.STROKE);
    rangPaint.setAntiAlias(true);
    rangPaint.setAlpha(200);
    //new LinearGradient(0, 100, 100, 100, Color.parseColor("#ffffff"), Color.parseColor("#30ffffff"),
    //    Shader.TileMode.CLAMP);
    //rangPaint.setShader(shader);

    rangPaint.setColor(Color.WHITE);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    drawWalkNum(canvas);
    drawCircle(canvas);
  }

  private float bigRadiu;
  private float smallRadiu;

  private void drawCircle(Canvas canvas) {
    pos = new float[2];
    tan = new float[2];
    Path rang = new Path();

    canvas.drawCircle(mWidth / 2, mHeight / 2, bigRadiu, lightPaint);

    if (isDrawSmallCircle) {

      rangPaint.setShadowLayer(15, 0, 0, Color.parseColor("#ffffff"));
      rang.addCircle(mWidth / 2, mHeight / 2, bigRadiu, Path.Direction.CW);

      PathMeasure pathMeasure1 = new PathMeasure();
      pathMeasure1.setPath(rang, false);
      Path dst1 = new Path();
      pathMeasure1.getSegment(rangProess * pathMeasure1.getLength(),
          rangProess * pathMeasure1.getLength() + 300, dst1, true);
      canvas.drawPath(dst1, rangPaint);

      samllCirclePath.addCircle(mWidth / 2, mHeight / 2, smallRadiu, Path.Direction.CW);
      this.pathMeasure.setPath(samllCirclePath, false);
      Path dst = new Path();

      this.pathMeasure.getSegment(0, (float) (this.pathMeasure.getLength() * currentPross), dst,
          true);
      canvas.drawPath(dst, smallCirclePaint);

      this.pathMeasure.getPosTan((this.pathMeasure.getLength() * currentPross), pos, tan);
      canvas.drawCircle(pos[0], pos[1], 10, dotPaint);
      if (currentPross > 0.79f) {
        Path aa = new Path();
        Log.i("tag", "=========+++");
        this.pathMeasure.getSegment((float) (0.8 * (this.pathMeasure.getLength())),
            (this.pathMeasure.getLength()), aa, true);
        PathEffect pathEffect = new DashPathEffect(new float[] { 3, 5 }, 10);
        smallCirclePaint.setPathEffect(pathEffect);
        canvas.drawPath(aa, smallCirclePaint);
        smallCirclePaint.setPathEffect(null);
      }
    }
  }

  private float km = 1.5f;
  private int calorie = 34;

  private int walkNum = 2472;

  private void drawWalkNum(Canvas canvas) {
    Rect walkNumRect = new Rect();
    walkNumPaint.getTextBounds("" + walkNum, 0, ("" + walkNum).length(), walkNumRect);
    canvas.drawText(walkNum + "", mWidth / 2 - walkNumRect.width() / 2,
        mHeight / 2 + walkNumRect.height() / 2, walkNumPaint);

    canvas.drawLine(mWidth / 2, mHeight / 2 + walkNumRect.height() / 2 + 55, mWidth / 2,
        mHeight / 2 + walkNumRect.height() / 2 + 95, caloriePaint);

    Rect calorieRect = new Rect();
    caloriePaint.getTextBounds(km + "公里", 0, (km + "公里").length(), calorieRect);
    canvas.drawText(km + "公里", mWidth / 2 - calorieRect.width() - 30,
        mHeight / 2 + walkNumRect.height() / 2 + 100 - caloriePaint.descent(), caloriePaint);

    caloriePaint.getTextBounds(calorie + "千卡", 0, (calorie + "千卡").length(), calorieRect);
    canvas.drawText(calorie + "千卡", mWidth / 2 + 30,
        mHeight / 2 + walkNumRect.height() / 2 + 100 - caloriePaint.descent(), caloriePaint);

    canvas.drawBitmap(bitmap, mWidth / 2 - bitmap.getWidth() / 2, mHeight / 4 * 3, caloriePaint);
  }

  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  public int dip2px(float dpValue) {
    final float scale = getContext().getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public int px2dip(float pxValue) {
    final float scale = getContext().getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }
}
