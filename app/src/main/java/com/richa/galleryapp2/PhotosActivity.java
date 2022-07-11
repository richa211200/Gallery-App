package com.richa.galleryapp2;


import static com.richa.galleryapp2.MainActivity.al_images;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.GridView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView)findViewById(R.id.gv_folder);
       int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this,MainActivity.al_images,int_position);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),forFullScreen.class);
                intent.putExtra("imagepath", adapter.al_menu.get(int_position).al_imagepath.get(position));
                Log.e("PhotosActivity", "onCreate: "+adapter.al_menu.get(position).al_imagepath );
                Log.e("PhotosActivity", "onCreate: "+position );
                startActivity(intent);
            }
        });
    }
}

