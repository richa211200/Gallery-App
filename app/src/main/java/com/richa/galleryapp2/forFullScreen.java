package com.richa.galleryapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.widget.Button;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

//import java.io.File;
//import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class forFullScreen extends AppCompatActivity {

    ImageView imageView;
    Button download,share;

    public Intent intent;

    //-----------file--------------
    private FileOutputStream fileOutputStream;

    //-------------file-------------

    private final static int CODE = 100;

    //--------------------------zoom------------------------
    int SELECT_IMAGE_CODE = 1;
    //using scale gesture detector for zoom
    ScaleGestureDetector scaleGestureDetector;
    float scaleFactor = 1.0f;
    //------------------------- -zoom------------------------

    //    private FileOutputStream fileOutputStream;
//    private BitmapDrawable drawable;
//    private Bitmap bitmap;
//    private File file,dir;
//
//    private final static int CODE = 100;

    //Button download,share;
    public ArrayList<Model_images> al_menu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_full_screen);

        imageView = findViewById(R.id.image);
        String imagepath = getIntent().getExtras().getString("imagepath");

        Log.e("forFullScreen", "onCreate: "+imagepath );

        Glide.with(this).load(imagepath).into(imageView);
//        int location = getIntent().getExtras().getInt("value");
//        GridViewAdapter adapter = new GridViewAdapter(this, al_menu,location);
//        Glide.with(this).load(adapter.al_menu.get(adapter.int_position))
//                .into(imageView);


        //----------------------------zoom--------------------------
        scaleGestureDetector = new ScaleGestureDetector(this,new Scalelistner());
        //----------------------------zoom--------------------------


        //-----------------------------downlaod and share button----------
        download = findViewById(R.id.download);
        share = findViewById(R.id.share);


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(forFullScreen.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    download();
                } else {
                    askPermission();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void download() {

        File dir = new File(Environment.getExternalStorageDirectory(),"DownloadImage");
        if(!dir.exists()){
            dir.mkdir();
        }

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File file  = new File(dir,System.currentTimeMillis()+".jpg");

        try{
            fileOutputStream = new FileOutputStream(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        Toast.makeText(forFullScreen.this,"Download Completed",Toast.LENGTH_SHORT).show();


        try{
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        //intent to save image to gallery
        intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        this.intent.setData(Uri.fromFile(file));
        sendBroadcast(this.intent);
    }

    //share image to other app
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void share() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File file = new File(getExternalCacheDir()+"/"+getResources().getString(R.string.app_name)+".jpg");

        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");

            intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivity(Intent.createChooser(intent,"Share Image"));
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(forFullScreen.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (CODE == requestCode){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                download();
                }
            }else{
            Toast.makeText(forFullScreen.this,"Please provide permisssion",Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //----------------------------------------zoom----------------------------

    //detect touch on imageview
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);
    }

    //setup scale factor and applying it to image
    private class Scalelistner extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            Uri uri = data.getData();
            imageView.setImageURI(uri);
        }
    }
    //---------------------------------------zoom-----------------------------




    //oncreate
}