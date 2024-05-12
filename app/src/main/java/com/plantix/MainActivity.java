package com.plantix;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout);
        applyWindowInsets(findViewById(R.id.main));

//        Button btnCapture = (Button) findViewById(R.id.btnOpenCamera);
//        Button btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
//        Button buttonPrediction = findViewById(R.id.buttonPrediction);
        Button btnHomePage = findViewById(R.id.home_button);
        Button btnUserPage = findViewById(R.id.user_button);
//        imgCapture = (ImageView) findViewById(R.id.imageView1);

//        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Intent data = result.getData();
//                if (data != null) {
//                    // Kiểm tra xem kết quả từ ACTION_IMAGE_CAPTURE hay ACTION_PICK
//                    Uri selectedImageUri = data.getData();
//                    if (selectedImageUri != null) {
//                        // Xử lý khi chọn ảnh từ thư viện
//                        imgCapture.setImageURI(selectedImageUri);
//                    } else {
//                        // Xử lý khi chụp ảnh từ camera
//                        Bundle extras = data.getExtras();
//                        if (extras != null && extras.containsKey("data")) {
//                            Bitmap imageBitmap = (Bitmap) extras.get("data");
//                            // Xử lý ảnh đã chụp từ camera
//                            imgCapture.setImageBitmap(imageBitmap);
//                        }
//                    }
//                }
//            } else {
//                // Xử lý trường hợp Intent không thành công hoặc bị hủy bỏ
//                Toast.makeText(this, "Chụp ảnh thất bại", Toast.LENGTH_SHORT).show();
//            }
//        });

//        btnCapture.setOnClickListener(v -> {
//            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            activityResultLauncher.launch(cInt);
//        });
//
//        btnSelectImage.setOnClickListener(v -> {
//            Intent cInt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            activityResultLauncher.launch(cInt);
//        });
//
//        buttonPrediction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Assuming imageView is your ImageView where you set the image
//                Bitmap bitmap = ((BitmapDrawable)imgCapture.getDrawable()).getBitmap();
////                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
////                byte[] imageBytes = byteArrayOutputStream.toByteArray();
////                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//                String base64String = ImageUtil.convert(bitmap);
//
//                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//                String url = "https://be6f-1-52-8-187.ngrok-free.app/api/predict-from-android";
//                JSONObject jsonObject = new JSONObject();
//
//                try {
//                    jsonObject.put("base64FromAndroid", base64String);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                // Handle the response from the server
//                                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Handle errors
//                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
//                    }
//                });
//                queue.add(request);
//            }
//        });

        btnHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // Name can be null
                        .commit();
            }
        });

        btnUserPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, UserFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // Name can be null
                        .commit();
            }
        });
    }

    private void applyWindowInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void settingButton(View view) {
        Toast.makeText(this, "Open Settings", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.setting_layout);
        applyWindowInsets(findViewById(R.id.settings));
    }

    public void returnHomeButton(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout);
        applyWindowInsets(findViewById(R.id.main));
    }
    public void personInforButton(View view) {
        setContentView(R.layout.person_infomation);
        applyWindowInsets(findViewById(R.id.personInformation));
    }
}