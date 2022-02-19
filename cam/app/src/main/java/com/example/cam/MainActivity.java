package com.example.cam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button button_capture, button_copy;
    TextView textview_data;
    Bitmap bitmap;
private  static final int request_camera_code=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    button_capture=findViewById(R.id.button_capture);
    button_copy=findViewById(R.id.button3);

    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, request_camera_code);
    }
    button_capture.setOnClickListener(view -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(MainActivity.this));

    button_copy.setOnClickListener(view -> {
        String scanned_text= textview_data.getText().toString();
        copyToClipBoard(scanned_text);
    });



    }

   /* */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resulturi = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resulturi);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

private void getTextFromImage(Bitmap bitmap){
    TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
    if (!recognizer.isOperational()){
        Toast.makeText(MainActivity.this, "Error Hogiya", Toast.LENGTH_SHORT).show();
    }
    else{
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock>  textBlockSparseArray = recognizer.detect(frame);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<textBlockSparseArray.size();i++){
            TextBlock textBlock = textBlockSparseArray.valueAt(i);
            stringBuilder.append(textBlock.getValue());
            stringBuilder.append("\n");
        }
        textview_data.setText(stringBuilder.toString());
        button_copy.setVisibility(View.VISIBLE);

    }
    }
private  void copyToClipBoard(String text){
    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData cliptext= ClipData.newPlainText("Copied data",text);
            clipboard.setPrimaryClip(cliptext);
            Toast.makeText(MainActivity.this,"The Text has been copied!!", Toast.LENGTH_SHORT).show();
}

}

