package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {
    EditText mresult;
    ImageView mpreview;


    private static  final int CAMERA_REQUEST_CODE=200;
    private static  final int STORAGE_REQUEST_CODE=400;
    private static  final int IMAGE_PICK_GALLERRY_CODE=1000;
    private static  final int IMAGE_PICK_CAMERA_CODE=1001;

    String cameraPermsiion[];
    String storagePermsiion[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setSubtitle("Click +button yo insert  image");


        mresult=findViewById(R.id.resultEt);
        mpreview=findViewById(R.id.imageIv);

        cameraPermsiion=new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermsiion=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    }

    public void ggsearch(View view) {
        Uri uri = Uri.parse("http://www.google.com/search?q=" + mresult.getText().toString());
        Intent ggsearchIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(ggsearchIntent);
    }
@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
    return true;
}
@Override
    public boolean onOptionsItemSelected(MenuItem item)
{
    int id=item.getItemId();
    if(id==R.id.addImage)
    {
showImaageDailog();
    }
    if (id==R.id.settings)
    {
        Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
    }
    return super.onOptionsItemSelected(item);
}

    private void showImaageDailog() {
        //String[] items=(" Camera", " Gallery");
        String[] items={" Camera"," Gallery"};
        AlertDialog.Builder dailog=new AlertDialog.Builder(this);
        dailog.setTitle("Select Image");
        dailog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        PickCamera();
                    }
                }
                if (which==1)
                {
                    if(!checkStoragePermission())
                    {
                        requestStroagePermission();
                    }
                    else
                    {
                        PickGallery();
                    }

                }
            }
        });
        dailog.create().show();
    }

    private void PickGallery() {
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,IMAGE_PICK_GALLERRY_CODE);

    }

    private void PickCamera() {
        ContentValues values=new ContentValues();
       values.put(MediaStore.Images.Media.TITLE,"NewPic");
       values.put(MediaStore.Images.Media.DESCRIPTION,"Image To text");

       image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraintent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraintent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStroagePermission() {
        ActivityCompat.requestPermissions(this,storagePermsiion,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean resukt1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return resukt1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermsiion,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean resukt1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && resukt1;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
           switch (requestCode){
               case CAMERA_REQUEST_CODE:
                   if(grantResults.length>0)
                   {
                       boolean cameraAccep=grantResults[0]==
                               PackageManager.PERMISSION_GRANTED;
                       boolean writeStorage=grantResults[0]==
                               PackageManager.PERMISSION_GRANTED;
                       if(cameraAccep && writeStorage)
                       {
                           PickCamera();
                       }
                       else
                       {
                           Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                       }

                   }
                   break;
               case STORAGE_REQUEST_CODE:
                   if(grantResults.length>0) {

                       boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                       if (writeStorage) {
                           PickGallery();
                       } else {
                           Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                       }
                   }
                   break;

}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERRY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mpreview.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) mpreview.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> item = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < item.size(); i++) {
                        TextBlock myitem = item.valueAt(i);
                        sb.append(myitem.getValue());
                        sb.append("\n");
                    }
                    mresult.setText(sb.toString());
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }

    }
}