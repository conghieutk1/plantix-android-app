package com.plantix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.io.InputStream;
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
public class HomeFragment extends BaseFragment {

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
//    public static final String urlBackend = "https://9972-2405-4803-f5a9-1030-d5ee-1c5e-d9de-fe1c.ngrok-free.app";
    public static final  String urlBackend = BuildConfig.URL_SERVER_BACKEND;
//    private FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
    private PredictionViewModel viewModel;
    private HistoryViewModel historyViewModel;
    private LinearLayout mainHistoryLayout, componentHistory1, componentHistory2, componentViewSelectedImage;
    private ProgressBar progressBar, progressBarWeather;
    private RelativeLayout loadingLayout;
    private Button btnCapture, btnSelectImage, buttonPrediction, btnUserPage, buttonViewAllHistories, buttonViewAllDiseases;
    private ImageButton btnSettingPage;
    private ImageView imgCapture, imageViewHistory1, imageViewHistory2;
    private TextView textDateTime1, textDateTime2, loading, textSelectedImage, textDiseaseName1, textDiseaseName2;
    private TextView textToday, textPlace, textDescriptWeather, textTemp;
    private ImageView imageWeather;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
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
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnCapture = (Button) view.findViewById(R.id.btnOpenCamera);
        btnSelectImage = (Button) view.findViewById(R.id.btnSelectImage);
        buttonPrediction = view.findViewById(R.id.buttonPrediction);
        buttonViewAllHistories = view.findViewById(R.id.buttonViewAllHistories);
        componentViewSelectedImage = view.findViewById(R.id.componentViewSelectedImage);
        textSelectedImage = view.findViewById(R.id.textSelectedImage);
        loadingLayout = view.findViewById(R.id.loadingLayout);

        // weather
//        textToday = view.findViewById(R.id.textToday);
//        textPlace = view.findViewById(R.id.textPlace);
//        textDescriptWeather = view.findViewById(R.id.textDescriptWeather);
//        textTemp = view.findViewById(R.id.textTemp);
        imageWeather = view.findViewById(R.id.imageWeather);
        progressBarWeather = view.findViewById(R.id.progressBarWeather);
        imageWeather.setVisibility(View.GONE);
        progressBarWeather.setVisibility(View.VISIBLE);

        buttonViewAllDiseases = view.findViewById(R.id.buttonViewAllDiseases);
        imgCapture = (ImageView) view.findViewById(R.id.imageView1);
        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageFullScreen(v);
            }
        });

        // Inflate the layout for this fragment
        if(imgCapture.getDrawable() == null) {
            // ImageView không có ảnh, vô hiệu hóa button
            buttonPrediction.setEnabled(false);
        } else {
            // ImageView đã có ảnh, kích hoạt button
            buttonPrediction.setEnabled(true);
        }
        // Button open user page
        btnUserPage = view.findViewById(R.id.user_button);

        buttonViewAllHistories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ViewAllHistoriesFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(ViewAllHistoriesFragment.TAG) // Name can be null
                        .commit();
            }
        });
        buttonViewAllDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ViewAllDiseasesFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(ViewAllDiseasesFragment.TAG) // Name can be null
                        .commit();
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

        loading = view.findViewById(R.id.loading);
        progressBar = view.findViewById(R.id.progressBar);
        loading.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                loading.setVisibility(View.GONE);
////                noDataText.setVisibility(View.VISIBLE);
//                loading.setText("Không có dữ liệu");
//            }
//        }, 10000); // 10 giây = 10000 milliseconds
        componentHistory1.setVisibility(View.GONE);
        componentHistory2.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Call fetchHistories() again to update data
        historyViewModel.fetchHistories(urlBackend);
        componentViewSelectedImage.setVisibility(View.GONE);
        textSelectedImage.setVisibility(View.GONE);
        buttonPrediction.setVisibility(View.GONE);

        // fetch weather
        fetchDataWeather(view);
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
        btnSettingPage = view.findViewById(R.id.settings_button);
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
                        textSelectedImage.setVisibility(View.VISIBLE);
                        buttonPrediction.setVisibility(View.VISIBLE);
                        Bitmap imageBitmap = getBitmapFromUri(selectedImageUri);
                        viewModel.setImage(imageBitmap);
                    } else {
                        // Xử lý khi chụp ảnh từ camera
                        Bundle extras = data.getExtras();
                        if (extras != null && extras.containsKey("data")) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            // Xử lý ảnh đã chụp từ camera
                            imgCapture.setImageBitmap(imageBitmap);
                            buttonPrediction.setEnabled(true);
                            componentViewSelectedImage.setVisibility(View.VISIBLE);
                            textSelectedImage.setVisibility(View.VISIBLE);
                            buttonPrediction.setVisibility(View.VISIBLE);
                            viewModel.setImage(imageBitmap);
                        }
                    }
                }
            } else {
                // Xử lý trường hợp Intent không thành công hoặc bị hủy bỏ
                Toast.makeText(getContext(), "Chụp ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        buttonPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLayout.setVisibility(View.VISIBLE);
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
                    int userId = sharedPreferences.getInt("userId", 1);
                    jsonObject.put("userId", userId);
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
                                            highestProbDiseaseObject.getString("keyDiseaseName"),
                                            highestProbDiseaseObject.getString("enName"),
                                            highestProbDiseaseObject.getString("viName"),
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

                                    String urlImageSelectedDisease = response.getString("urlImageSelectedDisease");
                                    viewModel.setUrlImageSelectedDisease(urlImageSelectedDisease);

                                    //Upload image to AWS S3
                                    // Xử lý response từ API và lấy presignedUrl từ response
                                    String presignedUrl = response.getString("presignedUrl");
//                                    System.out.println("presignedUrl = "+ presignedUrl);
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
                                loadingLayout.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getActivity(), "Có lỗi xảy ra, vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                        // Kích hoạt lại nút ngay cả khi có lỗi
                        buttonPrediction.setEnabled(true);
                        loadingLayout.setVisibility(View.GONE);
                    }
                });
                // Unable retry api
                request.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                long currentTimeMs = System.currentTimeMillis();

                // Kiểm tra xem có thể thêm yêu cầu vào hàng đợi không
                if (currentTimeMs - lastRequestTimeMs < RATE_LIMIT_INTERVAL_MS) {
                    // Nếu vượt quá giới hạn, từ chối yêu cầu
                    request.addMarker("rate-limited");
                    request.deliverError(new VolleyError("Rate limit exceeded"));
                    Toast.makeText(getActivity(), "Hệ thống đã ghi nhận hành động của bạn, vui lòng không thực hiện thêm", Toast.LENGTH_LONG).show();

                } else {
                    // Nếu không, thêm yêu cầu vào hàng đợi và gửi đi
                    queue.add(request);
                    lastRequestTimeMs = currentTimeMs;
                    // Kích hoạt lại nút
                    buttonPrediction.setEnabled(true);
                    loadingLayout.setVisibility(View.GONE);
                }
            }
        });

        historyViewModel.getHistories().observe(getViewLifecycleOwner(), new Observer<List<JSONObject>>() {
            @Override
            public void onChanged(List<JSONObject> histories) {
                progressBar.setVisibility(View.GONE);
                if (histories != null && !histories.isEmpty()) {
                    loading.setVisibility(View.GONE);
                    String historyId1 = "";
                    String historyId2 = "";
                    if (histories.size() == 1) {
                        // Lấy lịch sử đầu tiên
                        JSONObject history = histories.get(0);
                        try {
                            historyId1 = history.getString("id");
                            String dateTime = history.getString("DateTime");
                            String linkImage = history.getString("linkImage");
                            String diseaseName = history.getString("diseaseName");

                            // Cập nhật TextView với dữ liệu mới
                            textDateTime1.setText(dateTime);
                            textDiseaseName1.setText(diseaseName);

                            // Sử dụng Picasso để tải hình ảnh vào ImageView
//                            Picasso.get().load(linkImage).into(imageViewHistory1);
                            if (!linkImage.isEmpty()) {
                                Picasso.get().load(linkImage).error(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24).into(imageViewHistory1);
                            } else {
                                imageViewHistory1.setImageResource(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24);
                            }
                            componentHistory1.setVisibility(View.VISIBLE);
                            componentHistory2.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Lấy lịch sử đầu tiên
                        JSONObject history1 = histories.get(0);
                        JSONObject history2 = histories.get(1);
                        try {
                            historyId1 = history1.getString("id");
                            String dateTime = history1.getString("DateTime");
                            String linkImage = history1.getString("linkImage");
                            String diseaseName = history1.getString("diseaseName");
                            // Cập nhật TextView với dữ liệu mới
                            textDateTime1.setText(dateTime);
                            textDiseaseName1.setText(diseaseName);

                            // Sử dụng Picasso để tải hình ảnh vào ImageView
                            if (!linkImage.isEmpty()) {
                                Picasso.get().load(linkImage).error(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24).into(imageViewHistory1);
                            } else {
                                imageViewHistory1.setImageResource(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24);
                            }

                            historyId2 = history2.getString("id");
                            dateTime = history2.getString("DateTime");
                            linkImage = history2.getString("linkImage");
                            diseaseName = history2.getString("diseaseName");

                            // Cập nhật TextView với dữ liệu mới
                            textDateTime2.setText(dateTime);
                            textDiseaseName2.setText(diseaseName);

                            // Sử dụng Picasso để tải hình ảnh vào ImageView
                            if (!linkImage.isEmpty()) {
                                Picasso.get().load(linkImage).error(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24).into(imageViewHistory2);
                            } else {
                                imageViewHistory2.setImageResource(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24);
                            }
                            componentHistory1.setVisibility(View.VISIBLE);
                            componentHistory2.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String finalHistoryId1 = historyId1;
                    String finalHistoryId2= historyId2;
                    componentHistory1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("historyId", finalHistoryId1);
                            ViewAHistoryFragment fragment = new ViewAHistoryFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView, fragment)
                                    .setReorderingAllowed(true)
                                    .addToBackStack(ViewAHistoryFragment.TAG)
                                    .commit();
                        }
                    });
                    componentHistory2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("historyId", finalHistoryId2);
                            ViewAHistoryFragment fragment = new ViewAHistoryFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView, fragment)
                                    .setReorderingAllowed(true)
                                    .addToBackStack(ViewAHistoryFragment.TAG)
                                    .commit();
                        }
                    });
                }

                loading.setText("Không có dữ liệu");
            }
        });
    }
    public void showImageFullScreen(View view) {
        assert getActivity() != null;
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        ImageView fullScreenImageView = dialog.findViewById(R.id.fullScreenImageView);
        ImageView originalImageView = (ImageView) view;

        // Lấy drawable từ ImageView ban đầu và gán vào ImageView trong dialog
        fullScreenImageView.setImageDrawable(originalImageView.getDrawable());

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Đóng Dialog khi nhấn vào nút Cancel
            }
        });
        dialog.show();
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
//        Log.d("UploadS3", "Presigned URL: " + presignedUrl);
//        Log.d("UploadS3", "File size: " + fileData.length);
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
                        System.out.println("err: "+ error);
                        // Handle error
                        Toast.makeText(getActivity(), "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);
    }
     Bitmap getBitmapFromUri(Uri uri) {
        try {
            // Sử dụng Context của Fragment để truy cập ContentResolver
            Context context = requireContext(); // hoặc getContext() tùy theo trường hợp
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void fetchDataWeather(View view) {
        String apiGetWeather = urlBackend + "/api/infomation-weather-today";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiGetWeather, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String dateTime = response.getString("dateTime");
                            String tempC = response.getString("curTempC");
                            String city = response.getString("city");
                            String description = response.getString("description").trim();
                            int code = response.getInt("codeWeather");

                            textToday = view.findViewById(R.id.textToday);
                            textToday.setText(dateTime);

                            textPlace = view.findViewById(R.id.textPlace);
                            textPlace.setText(city);

                            textDescriptWeather = view.findViewById(R.id.textDescriptWeather);
                            textDescriptWeather.setText(description);

                            textTemp = view.findViewById(R.id.textTemp);
                            textTemp.setText(tempC + "°C");

                            String iconFileName = WeatherIconMapper.getDescription(code);
                            imageWeather = view.findViewById(R.id.imageWeather);
                            loadImageFromAssets(imageWeather, "weather/" + iconFileName);
//                            Picasso.get().load(icon).error(R.drawable.sunny_24dp_fill0_wght400_grad0_opsz24).into(imageWeather);
                            imageWeather.setVisibility(View.VISIBLE);

                            progressBarWeather = view.findViewById(R.id.progressBarWeather);
                            progressBarWeather.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
    private void loadImageFromAssets(ImageView imageView, String filePath) {
        View view = getView();
        if (view != null) {
            try {
                InputStream inputStream = view.getContext().getAssets().open(filePath);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageView.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}