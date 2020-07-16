package com.mx.photo;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by 梦雪 on 2020/07/16.
 * 实现一个3D效果的相册程序
 * QQ：2487686673
 * 技术交流群：239721485
 */
public class GalleryFlow extends Gallery {
    //图片的照相机ImageViews用于转换矩阵
    private Camera mCamera=new Camera();
    //ImageView的最大旋转角度
    private int mMaxRotationAngle=60;
    //最大放大效果
    private int mMaxZoom=-120;
    //Coverflow的中心
    private int mCoveflowCenter;

    public GalleryFlow(Context context) {
        super(context);
        this.setStaticTransformationsEnabled(true);
    }

    public GalleryFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setStaticTransformationsEnabled(true);
    }

    public GalleryFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setStaticTransformationsEnabled(true);
    }

    /**
     * 设置每个图像的最大旋转角度
     */
    public void setMaxRotationAngle(int mMaxRotationAngle) {
        this.mMaxRotationAngle = mMaxRotationAngle;
    }

    /**
     * 获取图像的最大旋转角度
     */
    public int getMaxRotationAngle() {
        return mMaxRotationAngle;
    }

    /**
     * 设置中心图像的最大缩放
     */
    public void setMaxZoom(int mMaxZoom) {
        this.mMaxZoom = mMaxZoom;
    }

    /**
     * 获取中心图像的最大缩放
     */
    public int getMaxZoom() {
        return mMaxZoom;
    }

    /**
     * 获取Coverflow的中心
     */
    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    private static int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        //图像的中心点和宽度
        final int childCenter=getCenterOfView(child);
        final int childWidth=child.getWidth();
        int rotationAngle=0;
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        if (childCenter == mCoveflowCenter) {
            //正中间的childView
            transformImageBitmap((ImageView)child, t, 0);
        } else {
            //两侧的childView
            rotationAngle = (int)(((float)(mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
            if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                rotationAngle = (rotationAngle < 0) ?-mMaxRotationAngle: mMaxRotationAngle;
            }
            //根据偏移角度对图片进行处理，看上去有3D的效果
            transformImageBitmap((ImageView)child, t, rotationAngle);
        }
        return true;
    }

    /**
     * 当此视图的大小更改时，在布局过程中调用此方法。如果
     * 您刚刚被添加到视图层次结构中
     * 值为0。
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //重写计算旋转的中心
        mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 通过传递的角度变换图像位图
     */
    private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
        mCamera.save();
        final Matrix imageMatrix=t.getMatrix();
        final int imageHeight=child.getLayoutParams().height;
        final int imageWidth=child.getLayoutParams().width;
        final int rotation=Math.abs(rotationAngle);
        //在Z轴上正向移动Camera的视角，实际效果为放大图片。
        //如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
        mCamera.translate(0.0f, 0.0f, 100.0f);
        ////随着视角的变小，放大
        if (rotation < mMaxRotationAngle) {
            float zoomAmount=(float)(mMaxZoom + (rotation * 1.5));
            mCamera.translate(0.0f, 0.0f, zoomAmount);
        }
        //在Y轴上旋转，对应图片竖向向内旋转
        //如果在X轴上旋转，则对应图片横向向内旋转
        mCamera.rotateY(rotationAngle);
        mCamera.getMatrix(imageMatrix);
        //Preconcats matrix相当于右乘矩阵
        //Postconcats matrix相当于左乘矩阵
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
        mCamera.restore();
    }
}
