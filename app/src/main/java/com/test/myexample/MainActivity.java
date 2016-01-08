package com.test.myexample;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;

    ParseFile pFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        (findViewById(R.id.browsImg_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

            }
        });

        final Context ctx = this;
                (findViewById(R.id.sendImg_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pFile == null) {
                    Toast.makeText(ctx,
                            "Please, select image.", Toast.LENGTH_SHORT).show();
                } else {
                    final ParseObject po = new ParseObject("Post");
                    po.put("text", "text");
                    Log.d("MY_TAG", "Start upload.");
                    pFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("MY_TAG", "ParseException: " + e.getMessage());
                            } else {
                                Log.d("MY_TAG", "Start save ParseObject");
                                po.put("photo", pFile);
                                saveParseObject(po, ctx);
                            }
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer percentDone) {
                            Log.d("MY_TAG", percentDone + " %");
                        }
                    });
                }
            }
        });

    }

    void saveParseObject(ParseObject parseObject, final Context ctx) {
        Log.d("MY_TAG", "Start saveParseObject()");
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("MY_TAG", e.getMessage());
                    Log.d("MY_TAG", "End. ParseObject saved. " + e.getMessage());
                    Log.i("Write post.", "Can`t write post. Problem with internet connection.");
                }
                else Toast.makeText(ctx,
                        "Message sent", Toast.LENGTH_SHORT).show();
            }
        });

    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                File file = new File(cursor.getString(columnIndex));
                cursor.close();

                BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file));
                int size = (int)file.length();
                byte[] bytes = new byte[size];
                bf.read(bytes, 0, size);
                bf.close();
                pFile = new ParseFile("file", bytes);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, size);
                ((ImageView) findViewById(R.id.setIMG)).setImageBitmap(bmp);


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

}
