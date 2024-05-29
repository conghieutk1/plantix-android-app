package com.plantix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


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
    private boolean isApiCalled = false;
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
    private static final int MAX_REQUESTS_PER_SECOND = 1; // Số yêu cầu tối đa trong 1 giây
    private static final long RATE_LIMIT_INTERVAL_MS = 5000; // Thời gian giới hạn tần suất yêu cầu (5 giây)
    private long lastRequestTimeMs = 0;
    public static final String urlBackend = " https://ba12-2405-4803-f7f0-cbb0-a826-6cc7-b9e-b3c1.ngrok-free.app";
//    private FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
    private PredictionViewModel viewModel;
    private HistoryViewModel historyViewModel;
    private LinearLayout mainHistoryLayout, componentHistory1, componentHistory2, componentViewSelectedImage;
    private Button buttonPrediction;
    private ImageView imageViewHistory1, imageViewHistory2;
    private TextView textDateTime1, textDateTime2, messageWhileDontHaveData;
    private TextView textDiseaseName1, textDiseaseName2;
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
        viewModel = new ViewModelProvider(requireActivity()).get(PredictionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button btnCapture = (Button) view.findViewById(R.id.btnOpenCamera);
        Button btnSelectImage = (Button) view.findViewById(R.id.btnSelectImage);
        buttonPrediction = view.findViewById(R.id.buttonPrediction);
        componentViewSelectedImage = view.findViewById(R.id.componentViewSelectedImage);

        ImageView imgCapture = (ImageView) view.findViewById(R.id.imageView1);

        // Inflate the layout for this fragment
        if(imgCapture.getDrawable() == null) {
            // ImageView không có ảnh, vô hiệu hóa button
            buttonPrediction.setEnabled(false);
        } else {
            // ImageView đã có ảnh, kích hoạt button
            buttonPrediction.setEnabled(true);
        }


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    // Kiểm tra xem kết quả từ ACTION_IMAGE_CAPTURE hay ACTION_PICK
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        // Xử lý khi chọn ảnh từ thư viện
                        imgCapture.setImageURI(selectedImageUri);
                        buttonPrediction.setEnabled(true);
                        componentViewSelectedImage.setVisibility(View.VISIBLE);
                        buttonPrediction.setVisibility(View.VISIBLE);
                    } else {
                        // Xử lý khi chụp ảnh từ camera
                        Bundle extras = data.getExtras();
                        if (extras != null && extras.containsKey("data")) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            // Xử lý ảnh đã chụp từ camera
                            imgCapture.setImageBitmap(imageBitmap);
                            buttonPrediction.setEnabled(true);
                            componentViewSelectedImage.setVisibility(View.VISIBLE);
                            buttonPrediction.setVisibility(View.VISIBLE);
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
                // Vô hiệu hóa nút để tránh nhấp nhiều lần
                buttonPrediction.setEnabled(false);

                // Assuming imageView is your ImageView where you set the image
                Bitmap bitmap = ((BitmapDrawable) imgCapture.getDrawable()).getBitmap();
                // Convert bitmap to base64
                String base64String = ImageUtil.convert(bitmap);

                assert getActivity() != null;
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                String apiGetPredict = urlBackend + "/api/predict-from-android";
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("base64FromAndroid", base64String);
                    jsonObject.put("userId", "3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiGetPredict, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Handle the response from the server
//                                Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();

                                // Xử lý response từ API
                                try {
                                    JSONArray dataArray = response.getJSONArray("listDiseases");
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

                                    // Lấy và set highestProbDisease
                                    JSONObject highestProbDiseaseObject = response.getJSONObject("highestProbDisease");

                                    // Lấy thông tin về bệnh
                                    DiseaseData highestProbDisease = new DiseaseData(
                                            highestProbDiseaseObject.getString("symtomMarkdown"),
                                            highestProbDiseaseObject.getString("precautionMarkdown"),
                                            highestProbDiseaseObject.getString("reasonMarkdown"),
                                            highestProbDiseaseObject.getString("treatmentMarkdown"),
                                            highestProbDiseaseObject.getString("descriptionMarkdown")
                                    );

                                    // Lấy thông tin về hình ảnh
                                    JSONArray imageDataArray = highestProbDiseaseObject.getJSONArray("imageData");
                                    List<String> imageLinks = new ArrayList<>();
                                    for (int i = 0; i < imageDataArray.length(); i++) {
                                        JSONObject imageObject = imageDataArray.getJSONObject(i);
                                        String linkImage = imageObject.getString("linkImage");
                                        imageLinks.add(linkImage);
                                    }

                                    // Thêm thông tin hình ảnh vào đối tượng bệnh
                                    highestProbDisease.setImageUrls(imageLinks);
                                    viewModel.setHighestProbDisease(highestProbDisease);

                                    //Upload image to AWS S3
                                    // Xử lý response từ API và lấy presignedUrl từ response
                                    String presignedUrl = response.getString("presignedUrl");

//                                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                    File imageFile = bitmapToFile(bitmap);
                                    if (imageFile != null) {
                                        uploadImageToS3(presignedUrl, imageFile);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // Chuyển đổi fragment
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragmentContainerView, ViewPredictFragment.class, null)
                                        .setReorderingAllowed(true)
                                        .addToBackStack(ViewPredictFragment.TAG) // Name can be null
                                        .commit();

                                // Kích hoạt lại nút
                                buttonPrediction.setEnabled(true);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        // Kích hoạt lại nút ngay cả khi có lỗi
                        buttonPrediction.setEnabled(true);
                    }
                });

                long currentTimeMs = System.currentTimeMillis();

                // Kiểm tra xem có thể thêm yêu cầu vào hàng đợi không
                if (currentTimeMs - lastRequestTimeMs < RATE_LIMIT_INTERVAL_MS) {
                    // Nếu vượt quá giới hạn, từ chối yêu cầu
                    request.addMarker("rate-limited");
                    request.deliverError(new VolleyError("Rate limit exceeded"));
                    Toast.makeText(getActivity(), "Hệ thống đã ghi nhận hành động của bạn, vui lòng không thực hiện thêm", Toast.LENGTH_LONG).show();
                    // Kích hoạt lại nút
                    buttonPrediction.setEnabled(true);
                } else {
                    // Nếu không, thêm yêu cầu vào hàng đợi và gửi đi
                    queue.add(request);
                    lastRequestTimeMs = currentTimeMs;
                }
            }
        });


        Button btnOpenPTest = view.findViewById(R.id.buttonOpenViewP);
        btnOpenPTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ViewPredictFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(ViewPredictFragment.TAG) // Name can be null
                        .commit();
            }
        });

        // Tìm các view trong layout
        mainHistoryLayout = view.findViewById(R.id.mainHistoryLayout);
        componentHistory1 = view.findViewById(R.id.componentHistory1);
        imageViewHistory1 = view.findViewById(R.id.imageViewHistory1);
        textDateTime1 = view.findViewById(R.id.textDateTime1);
        textDiseaseName1 = view.findViewById(R.id.textDiseaseName1);

        componentHistory2 = view.findViewById(R.id.componentHistory2);
        imageViewHistory2 = view.findViewById(R.id.imageViewHistory2);
        textDateTime2 = view.findViewById(R.id.textDateTime2);
        textDiseaseName2 = view.findViewById(R.id.textDiseaseName2);

        messageWhileDontHaveData = view.findViewById(R.id.messageWhileDontHaveData);
        // Khởi tạo ViewModel
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // Gọi API để cập nhật dữ liệu lịch sử trong ViewModel
        historyViewModel.fetchHistories(urlBackend);

        // Quan sát dữ liệu lịch sử
        historyViewModel.getHistories().observe(getViewLifecycleOwner(), new Observer<List<JSONObject>>() {
            @Override
            public void onChanged(List<JSONObject> histories) {
                if (histories != null && !histories.isEmpty()) {
                    messageWhileDontHaveData.setVisibility(View.GONE);
                    if (histories.size() == 1) {
                        // Lấy lịch sử đầu tiên
                        JSONObject history = histories.get(0);
                        try {
                            String dateTime = history.getString("DateTime");
                            String linkImage = history.getString("linkImage");
                            String diseaseName = history.getString("diseaseName");

                            // Cập nhật TextView với dữ liệu mới
                            textDateTime1.setText(dateTime);
                            textDiseaseName1.setText(diseaseName);

                            // Sử dụng Picasso để tải hình ảnh vào ImageView
                            Picasso.get().load(linkImage).into(imageViewHistory1);

                            componentHistory1.setVisibility(View.VISIBLE);
                            componentHistory2.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (histories.size() == 2) {
                        // Lấy lịch sử đầu tiên
                        JSONObject history1 = histories.get(0);
                        JSONObject history2 = histories.get(1);
                        try {
                            String dateTime = history1.getString("DateTime");
                            String linkImage = history1.getString("linkImage");
                            String diseaseName = history1.getString("diseaseName");

                            // Cập nhật TextView với dữ liệu mới
                            textDateTime1.setText(dateTime);
                            textDiseaseName1.setText(diseaseName);

                            // Sử dụng Picasso để tải hình ảnh vào ImageView
                            Picasso.get().load(linkImage).into(imageViewHistory1);

                            dateTime = history2.getString("DateTime");
                            linkImage = history2.getString("linkImage");
                            diseaseName = history2.getString("diseaseName");

                            // Cập nhật TextView với dữ liệu mới
                            textDateTime2.setText(dateTime);
                            textDiseaseName2.setText(diseaseName);

                            // Sử dụng Picasso để tải hình ảnh vào ImageView
                            Picasso.get().load(linkImage).into(imageViewHistory2);

                            componentHistory1.setVisibility(View.VISIBLE);
                            componentHistory2.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else {
                    messageWhileDontHaveData.setVisibility(View.VISIBLE);
                    componentHistory1.setVisibility(View.GONE);
                    componentHistory2.setVisibility(View.GONE);
                }
            }
        });

        // Gọi API nếu dữ liệu lịch sử chưa được tải
        if (historyViewModel.getHistories().getValue() == null || historyViewModel.getHistories().getValue().isEmpty()) {
            historyViewModel.fetchHistories(urlBackend);
        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Call fetchHistories() again to update data
        historyViewModel.fetchHistories(urlBackend);
        componentViewSelectedImage.setVisibility(View.GONE);
        buttonPrediction.setVisibility(View.GONE);
    }






    private static File bitmapToFile(Bitmap bitmap) {
        try {
            // Tạo một file tạm thời
            File file = File.createTempFile("image", ".jpg");

            // Chuyển đổi bitmap thành file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImageToS3(String presignedUrl, File file) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

//        File file = new File(imagePath);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayRequest request = new ByteArrayRequest(Request.Method.PUT, presignedUrl, fileData, "image/jpeg",
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // Upload successful
                        Toast.makeText(getActivity(), "Lịch sử chuẩn đoán của bạn đã được lưu lại", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getActivity(), "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);
    }

}