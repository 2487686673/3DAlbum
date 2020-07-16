package com.mx.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by 梦雪 on 2020/07/16.
 * 实现一个3D效果的相册程序
 * QQ：2487686673
 * 技术交流群：239721485
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    //要显示的图片
    private int[] mImageIds;
    //加载图片的ImageView数组
    private ImageView[] mImageViews;

    public ImageAdapter(Context mContext, int[] mImageIds) {
        this.mContext = mContext;
        this.mImageIds = mImageIds;
        this.mImageViews = new ImageView[mImageIds.length];
        createReflectedForAdapter();
    }

    /**
     * 图片处理过程，主要做的事情就是生成倒影，效果图里面底下是有倒影的。
     */
    private boolean createReflectedForAdapter() {
        //反射图像和原始图像之间的间隙
        final int reflectionGap = 4;
        int index = 0;
        for (int imageId : mImageIds) {
            //获取原始图片
            Bitmap originalImage=BitmapFactory.decodeResource(mContext.getResources(), imageId);
            int width=originalImage.getWidth();
            int height=originalImage.getHeight();
            
            //它不会缩放，但会在Y轴上翻转
            Matrix matrix=new Matrix();
            // 图片矩阵变换（从低部向顶部的倒影）
            matrix.preScale(1, -1);
            
            //创建一个应用翻转矩阵的位图。
            //我们只需要图像的下半部分
            // 截取原图下半部分
            Bitmap reflectionImage=Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);
            
            //创建一个新的位图，宽度相同，但要高一些
            // 创建倒影图片（高度为原图3/2）
            Bitmap bitmapWithReflection=Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
            
            //用足够大的位图创建一个新的画布
            //绘制倒影图（原图 + 间距 + 倒影）
            Canvas canvas=new Canvas(bitmapWithReflection);
            //绘制原图
            canvas.drawBitmap(originalImage, 0, 0, null);
            
            Paint defaultPaint=new Paint();
            // 绘制原图与倒影的间距
            canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
            //绘制倒影图
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
           
            //创建一个线性渐变的着色器
            Paint paint=new Paint();
            LinearGradient shader=new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
            //设置画笔使用这个着色器(线性渐变)
            paint.setShader(shader);
            //倒影遮罩效果
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            //用线性渐变绘制一个矩形
            //绘制倒影的阴影效果
            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
           
            BitmapDrawable bd=new BitmapDrawable(bitmapWithReflection);
            bd.setAntiAlias(true);
            
            ImageView imageView=new ImageView(mContext);
            //设置倒影图片
            imageView.setImageDrawable(bd);
            imageView.setLayoutParams(new GalleryFlow.LayoutParams(500, 800));
            mImageViews[index++] = imageView;
        }
        return true;
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public Object getItem(int p1) {
        return p1;
    }

    @Override
    public long getItemId(int p1) {
        return p1;
    }

    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        if (p2 == null) {
            p2 = mImageViews[p1];
        }
        return p2;
    }

    /**
     * 根据中心的偏移量返回视图的大小(0.0f到1.0f)。
     */
    public float getScale(boolean focused, int offset) {
        return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
    }
}
