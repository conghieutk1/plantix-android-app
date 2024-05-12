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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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


    public static final String TAG = HomeFragment.class.getName();
    ActivityResultLauncher<Intent> activityResultLauncher;
    private String mData;

    private static final int MAX_REQUESTS_PER_SECOND = 1; // Số yêu cầu tối đa trong 1 giây
    private static final long RATE_LIMIT_INTERVAL_MS = 5000; // Thời gian giới hạn tần suất yêu cầu (5 giây)
    private long lastRequestTimeMs = 0;

    private PredictionViewModel viewModel;

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
        if (savedInstanceState != null) {
            // Khôi phục dữ liệu từ savedInstanceState
            mData = savedInstanceState.getString("data_key");
        }
        viewModel = new ViewModelProvider(requireActivity()).get(PredictionViewModel.class);
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
        // Button open user page
        Button btnUserPage = view.findViewById(R.id.user_button);
        btnUserPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, UserFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(UserFragment.TAG) // Name can be null
                        .commit();
            }
        });

        // Button open setting page
        ImageButton btnSettingPage = view.findViewById(R.id.settings_button);
        btnSettingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Cài đặt", Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, SettingFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(SettingFragment.TAG) // Name can be null
                        .commit();
            }
        });

        // Button open camera
        btnCapture.setOnClickListener(v -> {
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(cInt);
        });

        // Button open gallery
        btnSelectImage.setOnClickListener(v -> {
            Intent cInt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(cInt);
        });

        // Button confirm predict
        buttonPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming imageView is your ImageView where you set the image
                Bitmap bitmap = ((BitmapDrawable)imgCapture.getDrawable()).getBitmap();
                // Convert bitmap to base64
                String base64String = ImageUtil.convert(bitmap);

                assert getActivity() != null;
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = "https://26cb-2001-ee0-1a40-e635-c91e-d618-c4f6-6572.ngrok-free.app/api/predict-from-android";
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

                                // Xử lý response từ API
                                try {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    List<Prediction> predictions = new ArrayList<>();

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject predictionObject = dataArray.getJSONObject(i);
                                        int id = predictionObject.getInt("id");
                                        String name = predictionObject.getString("name");
                                        double prob = predictionObject.getDouble("prob");

                                        Prediction prediction = new Prediction(id, name, prob);
                                        predictions.add(prediction);
                                    }

                                    // Set danh sách dự đoán vào ViewModel
                                    viewModel.setPredictions(predictions);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragmentContainerView, ViewPredictFragment.class, null)
                                        .setReorderingAllowed(true)
                                        .addToBackStack(ViewPredictFragment.TAG) // Name can be null
                                        .commit();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

                long currentTimeMs = System.currentTimeMillis();

                // Kiểm tra xem có thể thêm yêu cầu vào hàng đợi không
                if (currentTimeMs - lastRequestTimeMs < RATE_LIMIT_INTERVAL_MS) {
                    // Nếu vượt quá giới hạn, từ chối yêu cầu
                    request.addMarker("rate-limited");
                    request.deliverError(new VolleyError("Rate limit exceeded"));
                    Toast.makeText(getActivity(), "Cham cham thoi", Toast.LENGTH_LONG).show();
                } else {
                    // Nếu không, thêm yêu cầu vào hàng đợi và gửi đi
                    queue.add(request);
                    lastRequestTimeMs = currentTimeMs;
                }
            }
        });


        return view;
    }

}