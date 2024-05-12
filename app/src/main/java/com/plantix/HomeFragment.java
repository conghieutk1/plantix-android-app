package com.plantix;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters



    ActivityResultLauncher<Intent> activityResultLauncher;
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button btnCapture = (Button) view.findViewById(R.id.btnOpenCamera);
        Button btnSelectImage = (Button) view.findViewById(R.id.btnSelectImage);
        Button buttonPrediction = view.findViewById(R.id.buttonPrediction);

        ImageView imgCapture = (ImageView) view.findViewById(R.id.imageView1);
        // Inflate the layout for this fragment

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
                Toast.makeText(getContext(), "Chụp ảnh thất bại", Toast.LENGTH_SHORT).show();
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
                // Assuming imageView is your ImageView where you set the image
                Bitmap bitmap = ((BitmapDrawable)imgCapture.getDrawable()).getBitmap();
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                byte[] imageBytes = byteArrayOutputStream.toByteArray();
//                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                String base64String = ImageUtil.convert(bitmap);

                assert getActivity() != null;
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = "https://1134-2405-4803-f5a7-3e20-a1b6-3117-1e1c-83cf.ngrok-free.app/api/predict-from-android";
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("base64FromAndroid", base64String);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Handle the response from the server
                                Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(request);
            }
        });


        return view;


    }
}