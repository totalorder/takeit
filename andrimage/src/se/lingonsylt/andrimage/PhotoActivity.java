package se.lingonsylt.andrimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private final static int PICTURE_RESULT = 1;
    private static String currentPhotoPath;
    ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mImageView = (ImageView)findViewById(R.id.imageView);
    }

    public void onButtonClick(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
        startActivityForResult(takePictureIntent, PICTURE_RESULT);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "andrimage_" + timeStamp;
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    Environment.getExternalStorageDirectory()
            );
        } catch (IOException e) {
            return null;
        }

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_RESULT){
            if (resultCode == Activity.RESULT_OK) {
                displayImage(currentPhotoPath);
                uploadFileToHTTPServer(currentPhotoPath);
                new File(currentPhotoPath).delete();
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private void displayImage(String path) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        mImageView.setImageBitmap(bitmap);

    }

    private void uploadFileToHTTPServer(String path) {
        HttpFileUpload.sendNow(new File(currentPhotoPath).getName(), "http://192.168.0.170:8000/upload/", "my file title", "my file description", path);
    }
}
