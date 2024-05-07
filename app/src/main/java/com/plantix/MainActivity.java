package com.plantix;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        Button btnCapture = (Button) findViewById(R.id.btnOpenCamera);
        Button btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        Button buttonPrediction = findViewById(R.id.buttonPrediction);
        imgCapture = (ImageView) findViewById(R.id.imageView1);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    // Kiểm tra xem kết quả từ ACTION_IMAGE_CAPTURE hay ACTION_PICK
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        // Xử lý khi chọn ảnh từ thư viện
                        imgCapture.setImageURI(selectedImageUri);
                    } else {
                        // Xử lý khi chụp ảnh từ camera
                        Bundle extras = data.getExtras();
                        if (extras != null && extras.containsKey("data")) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            // Xử lý ảnh đã chụp từ camera
                            imgCapture.setImageBitmap(imageBitmap);
                        }
                    }
                }
            } else {
                // Xử lý trường hợp Intent không thành công hoặc bị hủy bỏ
                Toast.makeText(this, "Chụp ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCapture.setOnClickListener(v -> {
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(cInt);
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent cInt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(cInt);
        });
        
        buttonPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy string


                // Instantiate the RequestQueue.
//                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//                String url = "https://dummy.restapiexample.com/api/v1/employees";
//
//                // Request a string response from the provided URL.
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // Display the first 500 characters of the response string.
//                                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();
//                            }
//                        });
//
//                // Add the request to the RequestQueue.
//                queue.add(stringRequest);

                // Lấy json
//                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "https://dummy.restapiexample.com/api/v1/employees";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray employees = response.getJSONArray("data");
                                    // Loop through each employee object in the array
                                    for (int i = 0; i < employees.length(); i++) {
                                        JSONObject employee = employees.getJSONObject(i);
                                        int id = employee.getInt("id");
                                        String employeeName = employee.getString("employee_name");
                                        int employeeSalary = employee.getInt("employee_salary");
                                        int employeeAge = employee.getInt("employee_age");
                                        // Process or display the employee data as needed
                                        String employeeDetails = "ID: " + id + "\nName: " + employeeName + "\nSalary: " + employeeSalary + "\nAge: " + employeeAge;
                                        Toast.makeText(MainActivity.this, employeeDetails, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    // Handle JSON parsing error
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                            }
                        });

                        // Access the RequestQueue through your singleton class.
//                        queue.add(jsonObjectRequest);

                MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgCapture.setImageBitmap(bp);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
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