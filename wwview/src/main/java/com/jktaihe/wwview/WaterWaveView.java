package com.jktaihe.wwview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * @Description: 水波效果
 */
public class WaterWaveView extends View {

	private static final int PEROID = 16;// 绘制周期
	private static final int X_STEP = 15;// X轴的量化步长-越小曲线月平滑，但是计算量越大

	private Path firstWavePath;
	private Path secondWavePath;

	private Paint firstPaint;
	private Paint secondPaint;

	private int firstColor = 0x9e00ff00;
	private int secondColor = 0x5e00ff00;

	private int width;// view的高度
	private int height;// view的宽度

	private float moveWave = 0.0f;// 波形移动
	private float omega;// 波形的周期
	private double waveHeight;// 波形的幅度
	private float moveSpeed;// 波形的移动速度
	private static float heightOffset = 10f;

	private static float radius = 0;

	public boolean isOnDraw = true;

	private Thread thread = null;

	private Path mPath;

	public WaterWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public WaterWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void parseStyle(Context context, AttributeSet attrs) {
		if(attrs != null){
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.waterwaveview);
			firstColor = ta.getColor(R.styleable.waterwaveview_firstColor,firstColor);
			secondColor = ta.getColor(R.styleable.waterwaveview_secondColor,secondColor);
			moveSpeed = ta.getFloat(R.styleable.waterwaveview_waveHeight, 0.1f);
			omega = ta.getFloat(R.styleable.waterwaveview_omega,60f);
			waveHeight = ta.getFloat(R.styleable.waterwaveview_waveHeight,60f);
			heightOffset = 100 - ta.getFloat(R.styleable.waterwaveview_heightOffset,10f);
			ta.recycle();

		}
	}

	private void init(Context context, AttributeSet attrs) {
		parseStyle(context, attrs);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		//增加组件绘制之前的监听
		ViewTreeObserver vto = getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				height = getMeasuredHeight();
				width = getMeasuredWidth();
				initWaveParam();
				return true;
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		path(canvas);
		canvas.drawPath(getSecondWavePath(), getSecondLayerPaint());// 绘制第二层
		canvas.drawPath(getFristWavePath(), getFristLayerPaint());// 绘制第一层
	}

	private void path(Canvas canvas) {

		if (mPath == null){
			mPath = new Path();
		}else {
			mPath.reset();
		}

		mPath.addCircle(radius / 2, radius / 2, radius / 2-10, Path.Direction.CCW);
		canvas.clipPath(mPath, Region.Op.REPLACE);
	}

	private Path getSecondWavePath() {
		// 绘制区域2的路径
		if (secondWavePath == null) {
			secondWavePath = new Path();
		}else {
			secondWavePath.reset();
		}

		secondWavePath.moveTo(0, height);// 移动到左下角的点
		for (float x = 0; x <= width; x += X_STEP) {
			float y = (float) (waveHeight
					* Math.cos(omega * x + moveWave * 1.7f) + waveHeight)
					+ heightOffset * height * 0.01f +6;
			secondWavePath.lineTo(x, y);
		}
		secondWavePath.lineTo(width, 0);
		secondWavePath.lineTo(width, height);
		return secondWavePath;
	}

	private Paint getSecondLayerPaint() {
		if (secondPaint == null) {
			secondPaint = new Paint();
		}
		secondPaint.setColor(secondColor);
		secondPaint.setStyle(Paint.Style.FILL);
		secondPaint.setAntiAlias(true);
		return secondPaint;
	}

	private Paint getFristLayerPaint() {
		//  获取第一层的Paint，主要是颜色
		if (firstPaint == null) {
			firstPaint = new Paint();
		}
		firstPaint.setColor(firstColor);
		firstPaint.setStyle(Paint.Style.FILL);
		firstPaint.setAntiAlias(true);
		return firstPaint;
	}

	private Path getFristWavePath() {
		// 绘制区域1的路径
		if (firstWavePath == null) {
			firstWavePath = new Path();
		}else {
			firstWavePath.reset();
		}

		firstWavePath.moveTo(0, height);// 移动到左下角的点
		for (float x = 0; x <= width; x += X_STEP) {
			float y = (float) (waveHeight * Math.sin(omega * x + moveWave) + waveHeight)
					+ heightOffset * height * 0.01f;
			firstWavePath.lineTo(x, y);
		}
		firstWavePath.lineTo(width, 0);
		firstWavePath.lineTo(width, height);
		return firstWavePath;
	}

	private void initWaveParam() {
		this.width = getWidth();
		this.height = getHeight();
		waveHeight = height / 30;
		omega = (float) (3.5f * Math.PI / width);
		moveSpeed = 0.1f;
		radius = Math.max(width,height);
	}

	public void stop() {
		isOnDraw = false;
		thread = null;
	}

	public void start() {
		isOnDraw = true;
		thread = new Thread(new MyRunnable());// 启动绘制线程
		thread.start();
	}

	/**
	 * 设置omega
	 * 
	 * @param omega
	 */
	public void setOmega(float omega) {
		this.omega = omega;
	}

	/**
	 * 设置波形的高度
	 * 
	 * @param waveHeight
	 */
	public void setWaveHeight(double waveHeight) {
		this.waveHeight = waveHeight;
	}

	/**
	 * 设置波形的移动速度
	 * 
	 * @param moveSpeed
	 */
	public void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	/**
	 * 设置第一层颜色
	 * 
	 * @param firstColor
	 */
	public void setFirstColor(int firstColor) {
		this.firstColor = firstColor;
	}

	/**
	 * 设置第二层颜色
	 * 
	 * @param secondColor
	 */
	public void setSecondColor(int secondColor) {
		this.secondColor = secondColor;
	}

	/**
	 * 参考值63
	 * 
	 * @param progress
	 */
	public void setMoveSpeedByProgress(int progress) {
		if (progress >= 0 && progress <= 100) {
			initWaveParam();
			moveSpeed = progress * 0.003f;
		}
	}

	/**
	 * 参考值18
	 * 
	 * @param progress
	 */
	public void setOmegaByProgress(int progress) {
		if (progress >= 0 && progress <= 100) {
			initWaveParam();
			omega = progress * (float) (0.1f * Math.PI / width);
		}
	}

	/**
	 * 参考范围0-10
	 * 
	 * @param progress
	 */
	public void setWaveHeightByProgress(int progress) {
		if (progress >= 0 && progress <= 100) {
			waveHeight = progress * height * 0.005f;
			initWaveParam();
		}
	}

	public void setHeightOffsetByProgress(int progress) {
		if (progress >= 0 && progress <= 100) {
			heightOffset = 100 - progress ;
            initWaveParam();
        }
	}


	public class MyRunnable implements Runnable {
			@Override
			public void run() {
				while (isOnDraw) {
					long startTime = System.currentTimeMillis();
					moveWave += moveSpeed;
					postInvalidate();
					long time = System.currentTimeMillis() - startTime;
					if (time < PEROID) {
						try {
							Thread.sleep(PEROID - time);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
			}
		}
	}
}
