package com.jonas.jgraph.graph;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.jonas.jgraph.BuildConfig;
import com.jonas.jgraph.R;
import com.jonas.jgraph.inter.BaseGraph;
import com.jonas.jgraph.models.Jchart;
import com.jonas.jgraph.utils.DrawHelper;
import com.jonas.jgraph.utils.MathHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class JcoolGraph extends BaseGraph {
    private static final String TAG = JcoolGraph.class.getSimpleName();
    private Paint mBarPaint;
    private int mBarStanded = 0;
    private boolean lineStarted;
    private ArrayList<Path> mDashPathList = new ArrayList<>();
    private ArrayList<Path> mLinePathList = new ArrayList<>();
    private Paint mDashLinePaint;
    private Paint mPointPaint;
    private float mLinePointRadio;

    /**
     * 折线
     */
    public final static int LINE_BROKEN = 0;
    /**
     * 曲线
     */
    public final static int LINE_CURVE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LINE_BROKEN, LINE_CURVE})
    public @interface LineStyle {

    }

    /**
     * 线条从无到有 慢慢出现
     */
    public final static int LINESHOW_DRAWING = 0;
    /**
     * 线条 一段一段显示
     */
    public final static int LINESHOW_SECTION = 1;
    /**
     * 线条 一从直线慢慢变成折线/曲线
     */
    public final static int LINESHOW_FROMLINE = 2;

    /**
     * 从左上角 放大
     */
    public final static int LINESHOW_FROMCORNER = 3;
    /**
     * 水波 方式展开
     */
    public final static int LINESHOW_ASWAVE = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LINESHOW_DRAWING, LINESHOW_SECTION, LINESHOW_FROMLINE, LINESHOW_FROMCORNER, LINESHOW_ASWAVE})
    public @interface LineShowStyle {}

    /**
     * 水波 方式展开
     */
    public final static int BARSHOW_ASWAVE = 0;
    /**
     * 线条 一从直线慢慢变成折线/曲线
     */
    public final static int BARSHOW_FROMLINE = 1;
    /**
     * 柱形条 由某个往外扩散
     */
    public final static int BARSHOW_EXPAND = 2;

    /**
     * 线条 一段一段显示
     */
    public final static int BARSHOW_SECTION = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BARSHOW_ASWAVE, BARSHOW_FROMLINE, BARSHOW_EXPAND, BARSHOW_SECTION})
    public @interface BarShowStyle {}

    private int mBarShowStyle = BARSHOW_ASWAVE;

    public final static int SHOWFROMTOP = 0;
    public final static int SHOWFROMBUTTOM = 1;
    public final static int SHOWFROMMIDDLE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOWFROMTOP, SHOWFROMBUTTOM, SHOWFROMMIDDLE})
    public @interface ShowFromMode {}

    /**
     * 连接每一个点
     */
    public final static int LINE_EVERYPOINT = 0;
    /**
     * 跳过0  断开
     */
    public final static int LINE_JUMP0 = 1;

    /**
     * 跳过0 用虚线链接
     */
    public final static int LINE_DASH_0 = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LINE_EVERYPOINT, LINE_JUMP0, LINE_DASH_0})
    public @interface LineMode {

    }

    private int mLineMode = LINE_EVERYPOINT;
    private int mShowFromMode = SHOWFROMMIDDLE;
    private int mLineStyle = LINE_CURVE;
    /**
     * linepath动画存储点
     */
    private float[] mCurPosition = new float[2];
    private PathMeasure mPathMeasure;
    private PointF mPrePoint;
    /**
     * 折线两点的间隔
     */
    private float mBetween2Excel;

    /**
     * 线条 和横轴之间的 渐变区域
     */
    private Paint mShaderAreaPaint;
    private int[] mShaderAreaColors;
    private Path mAniShadeAreaPath = new Path();
    private Path mShadeAreaPath = new Path();

    /**
     * 线条展示的动画风格
     */
    protected int mLineShowStyle = LINESHOW_ASWAVE;


    private Paint mLinePaint;
    private float mLineWidth = -1;

    /**
     * 渐变色
     */
    private int[] mShaderColors;

    /**
     * 动画用的变俩
     */
    private float mAniRatio = 1;
    /**
     * 保存的原始路径数据
     */
    private Path mLinePath = new Path();
    private Path mAniLinePath = new Path();

    /**
     * 图表出现动画旋转角度
     */
    private float mAniRotateRatio = 0;

    public JcoolGraph(Context context){
        super(context);
    }

    public JcoolGraph(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AndroidJgraph);
        mLineStyle = a.getInteger(R.styleable.AndroidJgraph_linestyle, LINE_CURVE);
        mLineMode = a.getInteger(R.styleable.AndroidJgraph_linemode, LINE_EVERYPOINT);
        mLineWidth = a.getDimension(R.styleable.AndroidJgraph_linewidth, MathHelper.dip2px(mContext, 1.2f));
        mLineShowStyle = a.getInt(R.styleable.AndroidJgraph_lineshowstyle, LINESHOW_ASWAVE);
        a.recycle();
        initializeData();
    }

    public JcoolGraph(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context){
        super.init(context);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShaderAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initializeData(){
        mLineWidth = mLineWidth == -1 ? MathHelper.dip2px(mContext, 1.2f) : mLineWidth;
        //画线条 不设置 style 不会画线
        mLinePaint.setStyle(Paint.Style.STROKE);
        mDashLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mNormalColor);
        mDashLinePaint.setColor(mNormalColor);
        mBarPaint.setColor(mNormalColor);
        mLinePaint.setStrokeWidth(mLineWidth);
        mDashLinePaint.setStrokeWidth(mLineWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        paintSetShader(mShaderAreaPaint, mShaderAreaColors);
        paintSetShader(mLinePaint, mShaderColors);
        paintSetShader(mBarPaint, mShaderColors);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.save();
        canvas.rotate(mAniRotateRatio);
        canvas.translate(mSliding, 0);
        super.onDraw(canvas);
        canvas.restore();
        //纵轴信息固定
        if(mNeedY_abscissMasg && mYaxis_msg != null) {
            drawYabscissaMsg(canvas);
        }
    }

    @Override
    protected void drawSugExcel_BAR(Canvas canvas){
        for(Jchart jchart : mJcharts) {
            canvas.drawRect(jchart.getStandedRectF(), mCoordinatePaint);
        }
        if(mState == aniChange && mAniRatio<1) {
            barAniChanging(canvas);
//            for(Jchart jchart : mJcharts) {
//                jchart.draw(canvas, mCoordinatePaint, false);
//            }
        }else {
            mState = -1;
            if(mLastJchart.getAniratio()>=1 && !mValueAnimator.isRunning()) {
                for(Jchart jchart : mJcharts) {
                    jchart.draw(canvas, mBarPaint, false);
                }
            }else if(mBarShowStyle == BARSHOW_ASWAVE) {
                for(Jchart jchart : mJcharts) {
                    jchart.draw(canvas, mBarPaint, false);
                }
            }else if(mBarShowStyle == BARSHOW_FROMLINE) {
                barAniChanging(canvas);
            }else if(mBarShowStyle == BARSHOW_EXPAND) {
                drawBarExpand(canvas);
            }else if(mBarShowStyle == BARSHOW_SECTION) {
                drawBarSection(canvas);
            }
        }
    }

    private void drawBarSection(Canvas canvas){
        for(int i = 0; i<( (int)mAniRatio ); i++) {
            Jchart jchart = mJcharts.get(i);
            jchart.draw(canvas, mBarPaint, false);
        }
    }

    private void drawBarExpand(Canvas canvas){
        for(Jchart jchart : mJcharts) {
            if(mBarStanded>=mJcharts.size()) {
                mBarStanded = mJcharts.size()-1;
            }
            Jchart sjchart = mJcharts.get(mBarStanded);
            //以mBarStanded为准 左右散开
            canvas.drawRect(sjchart.getRectF().left+( jchart.getRectF().left-sjchart.getRectF().left )*mAniRatio,
                    jchart.getRectF().top,
                    sjchart.getRectF().right+( jchart.getRectF().right-sjchart.getRectF().right )*mAniRatio,
                    jchart.getRectF().bottom, mBarPaint);
        }
    }

    private void barAniChanging(Canvas canvas){
        for(int i = 0; i<mJcharts.size(); i++) {
            Jchart jchart = mJcharts.get(i);
            float lasty = mAllLastPoints.get(i).y;
            RectF rectF = jchart.getRectF();
//            if(jchart.mTopRound) {
//                Path rectFPath = jchart.getRectFPath(rectF.bottom, lasty+( rectF.top-lasty )*mAniRatio);
//                canvas.drawPath(rectFPath,mBarPaint);
//            }else {
//                RectF drectf = new RectF(rectF.left, lasty+( rectF.top-lasty )*mAniRatio, rectF.right, rectF.bottom);
//                canvas.drawRect(drectf, mBarPaint);
//            }
            RectF drectf = new RectF(rectF.left, lasty+( rectF.top-lasty )*mAniRatio, rectF.right, rectF.bottom);
            canvas.drawRect(drectf, mBarPaint);
        }
    }

    @Override
    protected void drawSugExcel_LINE(Canvas canvas){
        if(mState == aniChange && mAniRatio<1) {
            drawLineAllFromLine(canvas);
            for(Jchart jchart : mJcharts) {
                jchart.draw(canvas, mLinePaint, true);
            }
            drawLeftShaderArea(canvas);
        }else {
            mState = -1;
            if(mLineMode == LINE_EVERYPOINT) {
                //不跳过为0的点
                lineWithEvetyPoint(canvas);
            }else {
                //跳过为0的点（断开，虚线链接）
                lineSkip0Point(canvas);
            }
            //            }
        }
        //画线上的点
        if(mLinePointRadio>0) {
            for(Jchart jchart : mJcharts) {
                if(jchart.getHeight()>0) {
                    PointF midPointF = jchart.getMidPointF();
                    canvas.drawCircle(midPointF.x, midPointF.y, mLinePointRadio, mPointPaint);
                    //                    mPointPaint.setColor(Color.parseColor("#ffffffff"));
                    mPointPaint.setColor(0Xffffffff);
                    mPointPaint.setStrokeWidth(MathHelper.dip2px(mContext, 2));
                    canvas.drawCircle(midPointF.x, midPointF.y, mLinePointRadio, mPointPaint);
                }
            }
        }
    }

    /**
     * 为画笔 设置 渲染器
     *
     * @param paint
     */
    protected void paintSetShader(Paint paint, int[] shaders){
        paintSetShader(paint, shaders, mChartArea.left, mChartArea.top, mChartArea.left, mChartArea.bottom);
    }

    private void lineWithEvetyPoint(Canvas canvas){

        if(( mLineShowStyle == LINESHOW_FROMCORNER || mLineShowStyle == LINESHOW_DRAWING ||
                mLineShowStyle == LINESHOW_SECTION ) && !mValueAnimator.isRunning()) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "drawSugExcel_LINE animationfinish to the initial state");
            }
            //动画不在执行的时候
            mAniShadeAreaPath.reset();
            mAniLinePath.reset();
            canvas.drawPath(mLinePath, mLinePaint);
            if(mShaderAreaColors != null) {
                canvas.drawPath(mShadeAreaPath, mShaderAreaPaint);
            }
        }else if(mLineShowStyle == LINESHOW_DRAWING) {
            drawLineAllpointDrawing(canvas);
        }else if(mLineShowStyle == LINESHOW_SECTION) {
            drawLineAllpointSectionMode(canvas);
        }else if(mLineShowStyle == LINESHOW_FROMLINE) {
            drawLineAllFromLine(canvas);
            drawLeftShaderArea(canvas);
        }else if(mLineShowStyle == LINESHOW_FROMCORNER) {
            drawLineAllFromCorner(canvas);
            drawLeftShaderArea(canvas);
        }else if(mLineShowStyle == LINESHOW_ASWAVE) {
            drawLineAsWave(canvas);
            drawLeftShaderArea(canvas);
        }
    }

    private void drawLeftShaderArea(Canvas canvas){
        //渐变区域
        if(mShaderAreaColors != null) {
            mAniShadeAreaPath.lineTo(mChartRithtest_x, mChartArea.bottom);
            mAniShadeAreaPath.lineTo(mChartLeftest_x, mChartArea.bottom);
            mAniShadeAreaPath.close();
            canvas.drawPath(mAniShadeAreaPath, mShaderAreaPaint);
        }
    }

    private void drawLineAllFromCorner(Canvas canvas){
        mAniShadeAreaPath.reset();
        mAniLinePath.reset();
        if(mLineStyle == LINE_CURVE) {
            for(int i = 0; i<mAllPoints.size()-1; i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF endPointF = mAllPoints.get(i+1);
                if(mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x*mAniRatio, midPointF.y*mAniRatio);
                    mAniShadeAreaPath.moveTo(midPointF.x*mAniRatio, midPointF.y*mAniRatio);
                }
                float con_x = ( midPointF.x+endPointF.x )/2;
                mAniLinePath.cubicTo(con_x*mAniRatio, midPointF.y*mAniRatio, con_x*mAniRatio, endPointF.y*mAniRatio,
                        endPointF.x*mAniRatio, endPointF.y*mAniRatio);
                mAniShadeAreaPath
                        .cubicTo(con_x*mAniRatio, midPointF.y*mAniRatio, con_x*mAniRatio, endPointF.y*mAniRatio,
                                endPointF.x*mAniRatio, endPointF.y*mAniRatio);
            }
        }else {
            for(PointF midPointF : mAllPoints) {
                if(mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x*mAniRatio, midPointF.y*mAniRatio);
                    mAniShadeAreaPath.moveTo(midPointF.x*mAniRatio, midPointF.y*mAniRatio);
                }else {
                    mAniLinePath.lineTo(midPointF.x*mAniRatio, midPointF.y*mAniRatio);
                    mAniShadeAreaPath.lineTo(midPointF.x*mAniRatio, midPointF.y*mAniRatio);
                }
            }
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void drawLineAllFromLine(Canvas canvas){
        mAniShadeAreaPath.reset();
        mAniLinePath.reset();
        if(BuildConfig.DEBUG) {
            canvas.drawLine(0, mChartArea.bottom, mWidth, mChartArea.bottom, mLinePaint);
            canvas.drawLine(0, mChartArea.top, mWidth, mChartArea.top, mLinePaint);
        }
        if(mAllPoints.size() != mJcharts.size()) {
            throw new RuntimeException("mAllPoints.size() == mJcharts.size()");
        }
        if(mLineStyle == LINE_CURVE) {
            for(int i = 0; i<mAllPoints.size()-1; i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF endPointF = mAllPoints.get(i+1);
                PointF midLastPointF = mAllLastPoints.get(i);
                PointF endLastPointF = mAllLastPoints.get(i+1);
                if(mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x, midLastPointF.y+( midPointF.y-midLastPointF.y )*mAniRatio);
                    mAniShadeAreaPath.moveTo(midPointF.x, midLastPointF.y+( midPointF.y-midLastPointF.y )*mAniRatio);
                }
                DrawHelper.AnipathCubicFromLast(mAniLinePath, midPointF, endPointF, midLastPointF, endLastPointF,
                        mAniRatio);
                DrawHelper.AnipathCubicFromLast(mAniShadeAreaPath, midPointF, endPointF, midLastPointF, endLastPointF,
                        mAniRatio);
            }
        }else {
            for(int i = 0; i<mAllPoints.size(); i++) {
                PointF midPointF = mAllPoints.get(i);
                PointF lastPointF = mAllLastPoints.get(i);
                if(mAniLinePath.isEmpty()) {
                    mAniLinePath.moveTo(midPointF.x, lastPointF.y+( midPointF.y-lastPointF.y )*mAniRatio);
                    mAniShadeAreaPath.moveTo(midPointF.x, lastPointF.y+( midPointF.y-lastPointF.y )*mAniRatio);
                }else {
                    DrawHelper.AnipathLinetoFromLast(mAniLinePath, midPointF, lastPointF, mAniRatio);
                    DrawHelper.AnipathLinetoFromLast(mAniShadeAreaPath, midPointF, lastPointF, mAniRatio);
                }
            }
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void drawLineAsWave(Canvas canvas){
        mAniShadeAreaPath.reset();
        mAniLinePath.reset();
        if(mLineStyle == LINE_CURVE) {
            setUpCurveLinePath();
        }else {
            setUpBrokenLinePath();
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
    }

    private void setUpCurveLinePath(){
        for(int i = 0; i<mJcharts.size()-1; i++) {
            PointF startPoint = mJcharts.get(i).getMidPointF();
            PointF endPoint = mJcharts.get(i+1).getMidPointF();//下一个点
            if(mAniLinePath.isEmpty()) {
                mAniLinePath.moveTo(startPoint.x, startPoint.y);
                mAniShadeAreaPath.moveTo(startPoint.x, startPoint.y);
            }
            DrawHelper.pathCubicTo(mAniLinePath, startPoint, endPoint);
            DrawHelper.pathCubicTo(mAniShadeAreaPath, startPoint, endPoint);
        }
    }

    private void setUpBrokenLinePath(){
        for(Jchart chart : mJcharts) {
            if(mAniLinePath.isEmpty()) {
                mAniLinePath.moveTo(chart.getMidPointF().x, chart.getMidPointF().y);
                mAniShadeAreaPath.moveTo(chart.getMidPointF().x, chart.getMidPointF().y);
            }else {
                mAniLinePath.lineTo(chart.getMidPointF().x, chart.getMidPointF().y);
                mAniShadeAreaPath.lineTo(chart.getMidPointF().x, chart.getMidPointF().y);
            }
        }
    }

    /**
     *   画出完整线条动画
     *
     * @param canvas
     */
    private void drawLineAllpointDrawing(Canvas canvas){
        if(mCurPosition == null) {
            return;
        }
        //动画
        if(mAniLinePath.isEmpty() || mCurPosition[0]<=mChartLeftest_x) {
            mPrePoint = mJcharts.get(0).getMidPointF();
            mAniLinePath.moveTo(mPrePoint.x, mPrePoint.y);
            mAniShadeAreaPath.moveTo(mPrePoint.x, mPrePoint.y);
        }else {
            if(mPrePoint == null) {
                mPrePoint = mJcharts.get(0).getMidPointF();
            }
            if(mLineStyle == LINE_CURVE) {
                float con_X = ( mPrePoint.x+mCurPosition[0] )/2;
                mAniLinePath.cubicTo(con_X, mPrePoint.y, con_X, mCurPosition[1], mCurPosition[0], mCurPosition[1]);
                mAniShadeAreaPath.cubicTo(con_X, mPrePoint.y, con_X, mCurPosition[1], mCurPosition[0], mCurPosition[1]);
            }else {
                mAniLinePath.lineTo(mCurPosition[0], mCurPosition[1]);
                mAniShadeAreaPath.lineTo(mCurPosition[0], mCurPosition[1]);
            }
            mPrePoint.x = mCurPosition[0];
            mPrePoint.y = mCurPosition[1];
        }
        canvas.drawPath(mAniLinePath, mLinePaint);
        Jchart jchart = mJcharts.get((int)( ( mCurPosition[0]-mChartArea.left )/mBetween2Excel ));
        drawAbscissaMsg(canvas, jchart);
    }

    /**
     * 一段一段 画出线条
     *
     * @param canvas
     */
    private void drawLineAllpointSectionMode(Canvas canvas){
        int currPosition = (int)mAniRatio;
        if(currPosition == 0) {
            mAniLinePath.reset();
            mAniShadeAreaPath.reset();
        }
        Jchart jchart = mJcharts.get(currPosition);
        if(mLineStyle == LINE_BROKEN) {
            if(currPosition == 0) {
                mAniLinePath.moveTo(jchart.getMidPointF().x, jchart.getMidPointF().y);
            }else {
                mAniLinePath.lineTo(jchart.getMidPointF().x, jchart.getMidPointF().y);
            }
            canvas.drawPath(mAniLinePath, mLinePaint);
        }else {
            PointF currPointf = jchart.getMidPointF();
            if(mPrePoint == null) {
                mPrePoint = mJcharts.get(0).getMidPointF();
            }
            if(currPosition == 0) {
                mAniLinePath.moveTo(mPrePoint.x, mPrePoint.y);
            }
            DrawHelper.pathCubicTo(mAniLinePath, mPrePoint, currPointf);
            mPrePoint = currPointf;
            canvas.drawPath(mAniLinePath, mLinePaint);
        }
        drawAbscissaMsg(canvas, jchart);
        //渐变区域
        if(mShaderAreaColors != null) {
            mAniShadeAreaPath.reset();
            for(int i = 0; i<currPosition+1; i++) {
                PointF currPoint = mAllPoints.get(i);
                if(i == 0) {
                    mAniShadeAreaPath.moveTo(currPoint.x, currPoint.y);
                }else {
                    if(mLineStyle == LINE_BROKEN) {
                        mAniShadeAreaPath.lineTo(currPoint.x, currPoint.y);
                    }else {
                        DrawHelper.pathCubicTo(mAniShadeAreaPath, mAllPoints.get(i-1), currPoint);
                    }
                }
            }
            mAniShadeAreaPath.lineTo(jchart.getMidX(), mChartArea.bottom);
            mAniShadeAreaPath.lineTo(mChartLeftest_x, mChartArea.bottom);
            mAniShadeAreaPath.close();
            canvas.drawPath(mAniShadeAreaPath, mShaderAreaPaint);
        }
    }

    /**
     * 跳过0 点
     *
     * @param canvas
     */
    private void lineSkip0Point(Canvas canvas){
        arrangeLineDate(canvas);
        if(mLineMode == LINE_DASH_0) {
            //虚线连接
            arrangeDashLineDate(canvas);
            if(mDashPathList.size()>0) {
                for(Path path : mDashPathList) {
                    mDashLinePaint.setPathEffect(pathDashEffect(new float[]{8, 5}));
                    canvas.drawPath(path, mDashLinePaint);
                }
                postInvalidateDelayed(50);
            }
        }
    }

    protected void arrangeDashLineDate(Canvas canvas){
        mDashPathList.clear();
        for(int i = 0; i<mLinePathList.size(); i++) {
            Path path = mLinePathList.get(i);
            PathMeasure pathMeasure = new PathMeasure(path, false);
            float length = pathMeasure.getLength();
            float[] post = new float[2];
            pathMeasure.getPosTan(0, post, null);
            float[] post_end = new float[2];
            pathMeasure.getPosTan(length, post_end, null);
            if(length>0.001f) {
                if(mShaderAreaColors != null && mShaderAreaColors.length>0) {
                    path.lineTo(post_end[0], mChartArea.bottom);
                    path.lineTo(post[0], mChartArea.bottom);//移动到起点
                    path.close();
                    canvas.drawPath(path, mShaderAreaPaint);
                }
            }else {
                post_end[0] = post[0];
                post_end[1] = post[1];
            }
            if(i<mLinePathList.size()-1) {
                path = new Path();
                //当前直线的最后一个点
                path.moveTo(post_end[0], post_end[1]);
                //下一条直线的起点
                PathMeasure pathMeasuredotted = new PathMeasure(mLinePathList.get(i+1), false);
                pathMeasuredotted.getPosTan(0, post, null);

                if(mLineStyle == LINE_BROKEN) {
                    path.lineTo(post[0], post[1]);
                }else {
                    DrawHelper.pathCubicTo(path, new PointF(post_end[0], post_end[1]), new PointF(post[0], post[1]));
                }
                mDashPathList.add(path);
            }
        }
    }

    protected boolean arrangeLineDate(Canvas canvas){
        Path pathline = null;
        mLinePathList.clear();
        for(int i = 0; i<mJcharts.size(); i++) {
            if(!lineStarted) {
                pathline = new Path();
            }
            Jchart excel = null;
            excel = mJcharts.get(i);
            PointF midPointF = excel.getMidPointF();

            if(pathline != null) {
                if(excel.getHeight()>0) {
                    if(!lineStarted) {
                        pathline.moveTo(midPointF.x, midPointF.y);
                        lineStarted = true;
                    }else {
                        if(mLineStyle == LINE_BROKEN) {
                            if(mLineShowStyle == LINESHOW_ASWAVE) {
                                pathline.lineTo(midPointF.x, midPointF.y);
                            }else {
                                PointF lastP = mAllLastPoints.get(i);
                                DrawHelper.AnipathLinetoFromLast(pathline, midPointF, lastP, mAniRatio);

                            }
                        }else {
                            if(mLineShowStyle == LINESHOW_ASWAVE) {
                                DrawHelper.pathCubicTo(pathline, mPrePoint, midPointF);
                            }else {
                                PointF laste = mAllLastPoints.get(i);
                                PointF lastP = mAllLastPoints.get(i-1);
                                DrawHelper
                                        .AnipathCubicFromLast(pathline, mPrePoint, midPointF, lastP, laste, mAniRatio);
                            }
                        }
                    }
                }else {
                    //线段 结束
                    if(!pathline.isEmpty()) {
                        PathMeasure pathMeasure = new PathMeasure(pathline, false);
                        if(i>0 && pathMeasure.getLength()<0.001f) {
                            //线段 只有一个点的情况
                            pathline.lineTo(mPrePoint.x, mPrePoint.y+0.001f);
                        }
                        mLinePathList.add(pathline);
                        canvas.drawPath(pathline, mLinePaint);
                    }
                    lineStarted = false;//线段 结束
                }
                //最后一个点 最后的线段只有最后一个点
                if(i == mJcharts.size()-1 && lineStarted) {
                    PathMeasure pathMeasure = new PathMeasure(pathline, false);
                    if(i>0 && pathMeasure.getLength()<0.001f) {
                        pathline.lineTo(midPointF.x, midPointF.y+0.001f);
                    }
                    mLinePathList.add(pathline);
                    canvas.drawPath(pathline, mLinePaint);
                }
            }
            mPrePoint = midPointF;
        }
        lineStarted = false;
        return false;
    }

    /**
     * 以动画方式 切换数据
     *
     * @param jchartList
     */
    @Override
    public void aniChangeData(List<Jchart> jchartList){
        if(mWidth<=0) {
            throw new RuntimeException("after onsizechange");
        }
        mSliding = 0;//切换数据清除 移动距离
        mState = aniChange;
        if(jchartList != null && jchartList.size() == mAllLastPoints.size()) {
            mAllLastPoints.clear();
            mSelected = -1;
            mJcharts.clear();
            mJcharts.addAll(jchartList);
            for(int i = 0; i<mJcharts.size(); i++) {
                Jchart jchart = mJcharts.get(i);
                jchart.setIndex(i);
                PointF allPoint = mAllPoints.get(i);
                //保存上一次的数据
                mAllLastPoints.add(new PointF(allPoint.x, allPoint.y));
            }
            findTheBestChart();
            refreshChartArea();
            aniShowChar(0, 1, new LinearInterpolator());
        }else {
            throw new RuntimeException("aniChangeData的数据必须和第一次传递cmddata的数据量相同");
        }
    }

    @Override
    protected void refreshOthersWithEveryChart(int i, Jchart jchart){

        if(mGraphStyle == LINE) {
            mAllPoints.add(jchart.getMidPointF());
            if(mAllLastPoints.get(i).y == -1) {
                if(mShowFromMode == SHOWFROMBUTTOM) {
                    mAllLastPoints.get(i).y = mChartArea.bottom;//0转为横轴纵坐标
                }else if(mShowFromMode == SHOWFROMTOP) {
                    mAllLastPoints.get(i).y = mChartArea.top;//0转为横轴纵坐标
                }else if(mShowFromMode == SHOWFROMMIDDLE) {
                    mAllLastPoints.get(i).y = ( mChartArea.bottom+mChartArea.top )/2;//0转为横轴纵坐标
                }
            }
        }else {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "当前图形 是柱状图");
            }
            //continue; //没必要continue
            mAllPoints.add(jchart.getMidPointF());
            if(mAllLastPoints.get(i).y == -1) {
                mAllLastPoints.get(i).y = mChartArea.bottom;//0转为横轴纵坐标
            }
        }
    }

    /**
     * 主要 刷新高度
     */
    protected void refreshExcels(){
        mAllPoints.clear();
        super.refreshExcels();
        refreshLinepath();
        mChartRithtest_x = mJcharts.get(mJcharts.size()-1).getMidPointF().x;
        mChartLeftest_x = mJcharts.get(0).getMidPointF().x;
        if(mJcharts.size()>1) {
            mBetween2Excel = mJcharts.get(1).getMidPointF().x-mJcharts.get(0).getMidPointF().x;
        }
    }

    private void refreshLinepath(){
        //填充 path
        mLinePath.reset();
        mShadeAreaPath.reset();
        //曲线
        if(mLineStyle == LINE_CURVE) {
            for(int i = 0; i<mAllPoints.size()-1; i++) {
                PointF startPoint = mAllPoints.get(i);
                PointF endPoint = mAllPoints.get(i+1);//下一个点
                if(mLinePath.isEmpty()) {
                    mLinePath.moveTo(startPoint.x, startPoint.y);
                    mShadeAreaPath.moveTo(startPoint.x, startPoint.y);
                }
                DrawHelper.pathCubicTo(mLinePath, startPoint, endPoint);
                DrawHelper.pathCubicTo(mShadeAreaPath, startPoint, endPoint);
            }
        }else {
            for(PointF allPoint : mAllPoints) {
                if(mLinePath.isEmpty()) {
                    mLinePath.moveTo(allPoint.x, allPoint.y);
                    mShadeAreaPath.moveTo(allPoint.x, allPoint.y);
                }else {
                    mLinePath.lineTo(allPoint.x, allPoint.y);
                    mShadeAreaPath.lineTo(allPoint.x, allPoint.y);
                }
            }
        }
        mShadeAreaPath.lineTo(mChartRithtest_x, mChartArea.bottom);
        mShadeAreaPath.lineTo(mChartLeftest_x, mChartArea.bottom);
        mShadeAreaPath.close();
    }

    @Override
    public void feedData(@NonNull List<Jchart> jchartList){
        super.feedData(jchartList);
    }

    public void feedData(float... data){
        ArrayList<Jchart> jchartList = new ArrayList<>(data.length);
        for(float v : data) {
            Jchart jchart = new Jchart(v, mNormalColor);
            jchartList.add(jchart);
        }
        super.feedData(jchartList);
    }

    public void aniShow_growing(){
        if(mJcharts.size()<=0) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "数据异常 ");
            }
            return;
        }
        mState = -1;
        if(mGraphStyle == LINE) {
            if(mLineShowStyle == LINESHOW_ASWAVE) {
                aswaveAniTrigger();
            }else if(mLineMode == LINE_DASH_0 || mLineMode == LINE_JUMP0 ||
                    mLineShowStyle == LINESHOW_FROMLINE || mLineShowStyle == LINESHOW_FROMCORNER) {
                if(mLineMode == LINE_DASH_0 || mLineMode == LINE_JUMP0) {
                    //跳过0的线只有两种动画 ASWAVE FROMLINE
                    mLineShowStyle = LINESHOW_FROMLINE;
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "aniShow_growing change showstyle 2 fromline");
                    }
                }
                if(mShowFromMode == SHOWFROMMIDDLE) {
                    aniShowChar(0, 1, new AccelerateInterpolator());
                }else {
                    aniShowChar(0, 1);
                }
            }else if(mLineShowStyle == LINESHOW_SECTION) {
                mAniLinePath.reset();
                mAniShadeAreaPath.reset();
                aniShowChar(0, mJcharts.size()-1, new LinearInterpolator(), 5000, true);
            }else if(mLineShowStyle == LINESHOW_DRAWING) {
                mAniLinePath.reset();
                mAniShadeAreaPath.reset();
                mPathMeasure = new PathMeasure(mLinePath, false);
                mPathMeasure.getPosTan(0, mCurPosition, null);
                aniShowChar(0, mPathMeasure.getLength(), new LinearInterpolator(), 3000);
            }
        }else {
            if(mJcharts.size()>0) {
                if(mBarShowStyle == BARSHOW_ASWAVE) {
                    aswaveAniTrigger();
                }else if(mBarShowStyle == BARSHOW_SECTION) {
                    aniShowChar(0, mJcharts.size()-1, new LinearInterpolator(), ( mJcharts.size()-1 )*300, true);
                }else {
                    aniShowChar(0, 1, new LinearInterpolator());
                }
            }
        }
    }

    private void aswaveAniTrigger(){
        for(Jchart jchart : mJcharts) {
            jchart.setAniratio(0);
        }
        aniShowChar(0, mJcharts.size()-1, new LinearInterpolator(), ( mJcharts.size()-1 )*250, true);
    }

    @Override
    protected void onAnimationUpdating(ValueAnimator animation){
        if(mState == aniChange) {
            floatChangeAni(animation);
        }else if(mGraphStyle == BAR) {
            if(mBarShowStyle == BARSHOW_ASWAVE || mBarShowStyle == BARSHOW_SECTION) {
                if(animation.getAnimatedValue() instanceof Integer) {
                    mAniRatio = (int)animation.getAnimatedValue();
                    mJcharts.get(( (int)mAniRatio )).aniHeight(this, 0, new AccelerateInterpolator());
                }
            }else {
                floatChangeAni(animation);
            }
        }else {
            if(mLineMode == LINE_EVERYPOINT) {
                if(mLineShowStyle == LINESHOW_FROMLINE || mLineShowStyle == LINESHOW_FROMCORNER) {
                    floatChangeAni(animation);
                }else if(mLineShowStyle == LINESHOW_ASWAVE || mLineShowStyle == LINESHOW_SECTION) {
                    intChangeAni(animation);
                }else {
                    if(animation.getAnimatedValue() instanceof Float) {
                        mAniRatio = (float)animation.getAnimatedValue();
                        if(mGraphStyle == LINE) {
                            if(mLineShowStyle == LINESHOW_DRAWING && mState != aniChange) {
                                //mCurPosition必须要初始化mCurPosition = new float[2];
                                mPathMeasure.getPosTan(mAniRatio, mCurPosition, null);
                            }
                        }
                    }
                }
            }else {
                if(mLineShowStyle == LINESHOW_FROMLINE) {
                    floatChangeAni(animation);
                }else {
                    //波浪
                    intChangeAni(animation);
                }
            }
        }

        postInvalidate();
    }

    private void intChangeAni(ValueAnimator animation){
        if(animation.getAnimatedValue() instanceof Integer) {
            int curr = (int)animation.getAnimatedValue();
            mJcharts.get(curr).aniHeight(this);
            mAniRatio = curr;
        }
    }

    private void floatChangeAni(ValueAnimator animation){
        if(animation.getAnimatedValue() instanceof Float) {
            mAniRatio = (float)animation.getAnimatedValue();
        }
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        mAniShadeAreaPath = null;
        mLinePath = null;
        mShaderColors = null;
        mPathMeasure = null;
        mCurPosition = null;
        mAllPoints = null;
        mAllLastPoints = null;
        mAniLinePath = null;
    }

    @Override
    public void setInterval(float interval){
        super.setInterval(interval);
        refreshChartSetData();
    }

    /**
     * 线条 宽度
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth){
        mLineWidth = lineWidth;
    }

    /**
     * 渐变色
     * 从上到下
     *
     * @param colors
     */
    public void setPaintShaderColors(@ColorInt int... colors){
        mShaderColors = colors;
        if(mWidth>0) {
            paintSetShader(mLinePaint, mShaderColors);
            paintSetShader(mBarPaint, mShaderColors);
        }
    }

    public void setShaderAreaColors(@ColorInt int... colors){
        mShaderAreaColors = colors;
        if(mWidth>0) {
            paintSetShader(mShaderAreaPaint, mShaderAreaColors);
            postInvalidate();
        }
    }

    public float getAniRatio(){
        return mAniRatio;
    }

    public void setAniRatio(float aniRatio){
        this.mAniRatio = aniRatio;
    }

    public float getAniRotateRatio(){
        return mAniRotateRatio;
    }

    public void setAniRotateRatio(float aniRotateRatio){
        mAniRotateRatio = aniRotateRatio;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        if(BuildConfig.DEBUG) {
            Log.e(TAG, "onAttachedToWindow ");
        }
    }

    @Override
    public void setSelectedMode(@SelectedMode int selectedMode){
        mSelectedMode = selectedMode;
        if(mWidth>0) {
            refreshChartArea();
        }
    }

    public void setLineStyle(@LineStyle int lineStyle){
        mLineStyle = lineStyle;
        if(mWidth>0) {
            refreshLinepath();
        }
    }

    /**
     * 设置-1 没动画
     *
     * @param lineShowStyle
     *         one of {@link LineShowStyle}
     */
    public void setLineShowStyle(@LineShowStyle int lineShowStyle){
        if(mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            if(mLineShowStyle == LINESHOW_ASWAVE) {
                for(Jchart jchart : mJcharts) {
                    jchart.setAniratio(1);
                }
            }
            mAniRatio = 1;
        }
        mLineShowStyle = lineShowStyle;
    }

    public void setBarShowStyle(@BarShowStyle int barShowStyle){
        if(mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            if(mBarShowStyle == BARSHOW_ASWAVE) {
                for(Jchart jchart : mJcharts) {
                    jchart.setAniratio(1);
                }
            }
        }
        mBarShowStyle = barShowStyle;
    }

    /**
     * 设置fromline动画的最初起点
     *
     * @param showFromMode
     *         one of {@link ShowFromMode}
     */
    public void setShowFromMode(@ShowFromMode int showFromMode){
        mShowFromMode = showFromMode;
    }

    public int getLineMode(){
        return mLineMode;
    }

    /**
     * 当linemode为跳过0时 只有两种动画 aswave和fromline
     *
     * @param lineMode
     */
    public void setLineMode(@LineMode int lineMode){
        mLineMode = lineMode;
        if(mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    public float getLinePointRadio(){
        return mLinePointRadio;
    }

    public void setLinePointRadio(@DimenRes int linePointRadio){
        mLinePointRadio = linePointRadio;
    }

    public float getLineWidth(){
        return mLineWidth;
    }

    @Override
    public void setNormalColor(@ColorInt int normalColor){
        super.setNormalColor(normalColor);
        mLinePaint.setColor(mNormalColor);
    }
}
//可滚动   根据可见个数 计算barwidth
//
//不可滚动
//   1 无视mVisibleNums 所有柱子平均显示
//   2 固定最多个数mVisibleNums 大于等于mExecels的数量
//