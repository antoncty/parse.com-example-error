package com.example.anton.mytest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        final ParseObject po = new ParseObject("Post");
        po.put("text", "text");
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.shapka);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] buff = stream.toByteArray();
        final ParseFile pf = new ParseFile("file", buff);
        pf.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                po.put("photo", pf);
                po.saveInBackground();
            }
        });
        //po.saveInBackground();
    }
}
