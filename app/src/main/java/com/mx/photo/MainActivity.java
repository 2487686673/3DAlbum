package com.mx.photo;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by 梦雪 on 2020/07/16.
 * 实现一个3D效果的相册程序
 * QQ：2487686673
 * 技术交流群：239721485
 */
public class MainActivity extends Activity { 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int[] images={
            R.drawable.image1,R.drawable.image2,
            R.drawable.image3,R.drawable.image4,
            R.drawable.image5
        };
        ImageAdapter adapter=new ImageAdapter(this, images);
        GalleryFlow galleryFlow=findViewById(R.id.gallery_flow);
        galleryFlow.setAdapter(adapter);
    }

} 
