package com.example.textdetector;

import static android.Manifest.permission.CAMERA;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class Scanner extends AppCompatActivity {
    ImageView img;
    TextView textview;
    Button snapBtn;
    Button detectBtn;

    // variable for our image bitmap.
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scanner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        img = findViewById(R.id.CaptureView);
        textview = findViewById(R.id.Scannertext);
        snapBtn = findViewById(R.id.snapbutton);
        detectBtn = findViewById(R.id.detectbutton);

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectText();

            }
        });
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                    captureImage();
                }
                    else{
                       requestPermission();
                    }


            }
        });
    }
        private boolean checkPermission(){
            int camerPermission= ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
            return camerPermission== PackageManager.PERMISSION_GRANTED;
        }

        private void requestPermission(){
            int PERMISSION_CODE=200;
            ActivityCompat.requestPermissions(this,new String[]{CAMERA},PERMISSION_CODE);
        }
        private void captureImage(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
        }

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
        boolean cameraPermission=grantResults[0]==PackageManager.PERMISSION_GRANTED;
        if(cameraPermission){
            Toast.makeText(this,"PERMISSION GRANTED..",Toast.LENGTH_SHORT).show();
            captureImage();
        }else{
            Toast.makeText(this,"PERMISSION DENIED..",Toast.LENGTH_SHORT).show();
        }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode ==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
//            Bundle extras = data.getExtras();
//            imageBitmap= (Bitmap)extras.get("data");
//            img.setImageBitmap(imageBitmap);
//        }
//    }
//
//    private void detectText(){
//        InputImage image=InputImage.fromBitmap(imageBitmap,0);
//        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//        Task<Text> result= recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
//            @Override
//            public void onSuccess(Text text) {
//                StringBuilder result = new StringBuilder();
//                for(Text.TextBlock block: text.getTextBlocks()){
//                    String blockText = block.getText();
//                    Point[] blockCornerPoint = block.getCornerPoints();
//                    Rect blockFrame =block.getBoundingBox();
//                    for(Text.Line line: block.getLines()){
//                        String lineTExt = line.getText();
//                        Point[] lineCornerPoint = line.getCornerPoints();
//                        Rect linRect = line.getBoundingBox();
//                        for(Text.Element element : line.getElements()){
//                            String elementText = element.getText();
//                            result.append(elementText);
//                        }
//                        textview.setText(blockText);
//                    }
//                }
//                if (text.getTextBlocks().isEmpty()) {
//                    Toast.makeText(Scanner.this, "No text detected.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(Scanner.this,"FAIL TO DETECT TEXT FROM IMAGE.."+e.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//
//    }
//}

            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    img.setImageBitmap(imageBitmap);
                }
            }

            private void detectText() {
                if (imageBitmap == null) {
                    Toast.makeText(this, "No image to process.", Toast.LENGTH_SHORT).show();
                    return;
                }

                InputImage image = InputImage.fromBitmap(imageBitmap, 0);
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                if (text.getTextBlocks().isEmpty()) {
                                    Toast.makeText(Scanner.this, "No text detected.", Toast.LENGTH_SHORT).show();
                                    textview.setText(""); // Clear previous text
                                    return;
                                }

                                StringBuilder resultText = new StringBuilder();
                                for (Text.TextBlock block : text.getTextBlocks()) {
                                    for (Text.Line line : block.getLines()) {
                                        resultText.append(line.getText()).append("\n");
                                    }
                                }

                                textview.setText(resultText.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Scanner.this, "Failed to detect text: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }                                                                                                                                                                                                                                                                                                                                                       