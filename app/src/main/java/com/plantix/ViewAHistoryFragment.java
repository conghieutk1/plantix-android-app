package com.plantix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewAHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewAHistoryFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HistoryViewModel historyViewModel;
    public static final  String urlBackend = BuildConfig.URL_SERVER_BACKEND;
    public static final String TAG = ViewAHistoryFragment.class.getName();

    private LinearLayout reasonContainer, treatmentContainer, precautionContainer;
    private Button buttonViewMore, buttonGotoHome;
    private ImageButton buttonFeedback, buttonRemoveHistory;
    private RelativeLayout loadingLayout;
    private Spinner feedbackSpinner;
    private TextView textFeedbackSpinner;
    private String disease = "";
    private String urlImageForFeedback;
    public ViewAHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewAHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewAHistoryFragment newInstance(String param1, String param2) {
        ViewAHistoryFragment fragment = new ViewAHistoryFragment();
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
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_a_history, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String historyId = bundle.getString("historyId");
            if (historyId != null) {
                // Sử dụng historyId ở đây
                historyViewModel.setSelectedHistoryID(historyId);
            }
        }
//        historyViewModel.setSelectedHistoryID("19");
//        System.out.println("history ID = "+ historyViewModel.getSelectedHistoryID());
//        historyViewModel.fetchSelectedHistory(urlBackend, historyViewModel.getSelectedHistoryID());
        buttonViewMore = view.findViewById(R.id.buttonViewTreatment);
        buttonViewMore.setVisibility(View.GONE);
        buttonGotoHome = view.findViewById(R.id.buttonGotoHome);
        buttonGotoHome.setVisibility(View.GONE);
        buttonRemoveHistory = view.findViewById(R.id.buttonRemoveHistory);

        reasonContainer = view.findViewById(R.id.reasonContainer);
        reasonContainer.setVisibility(View.GONE);

        treatmentContainer = view.findViewById(R.id.treatmentContainer);
        treatmentContainer.setVisibility(View.GONE);

        precautionContainer = view.findViewById(R.id.precautionContainer);
        precautionContainer.setVisibility(View.GONE);
        ImageButton btnReturnHome = view.findViewById(R.id.return_button);
        buttonFeedback = view.findViewById(R.id.buttonFeedback);
        loadingLayout = view.findViewById(R.id.loadingLayout);
        btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack(ViewAHistoryFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonFeedback.setVisibility(View.GONE);
        ImageView imageView = view.findViewById(R.id.SelectedImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageFullScreen(v);
            }
        });
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        TableRow headerRow = createHeaderRow();
        setTextViewColor(headerRow, Color.BLACK);
        // Thêm hàng tiêu đề vào bảng
        tableLayout.addView(headerRow);

        TableRow row = new TableRow(requireContext());
        TextView notiTextView = createTextView(String.valueOf("Không có dữ liệu"));
        row.addView(notiTextView);
        tableLayout.addView(row);

        historyViewModel.getSelectedHistoryID().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String id) {
                // Handle the change
                // You can use the 'id' to fetch data or update UI
                historyViewModel.fetchSelectedHistory(urlBackend, id);
            }
        });
        buttonViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonViewMore.setVisibility(View.GONE);
                buttonGotoHome.setVisibility(View.VISIBLE);
                reasonContainer.setVisibility(View.VISIBLE);
                treatmentContainer.setVisibility(View.VISIBLE);
                precautionContainer.setVisibility(View.VISIBLE);
            }
        });
        buttonGotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // Name can be null
                        .commit();
            }
        });
        buttonFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackDialog();
            }
        });
        buttonRemoveHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getArguments();
                if (bundle != null) {
                    String historyId = bundle.getString("historyId");
                    if (historyId != null) {
                        // Sử dụng historyId ở đây
                        showDeleteConfirmationDialog(historyId);
                    }
                }

            }
        });
        historyViewModel.getPredictionsSelectedHistory().observe(getViewLifecycleOwner(), new Observer<List<Prediction>>() {
            @Override
            public void onChanged(List<Prediction> predictions) {
//                Toast.makeText(getActivity(), "predictions", Toast.LENGTH_LONG).show();
                if (predictions != null && !predictions.isEmpty()) { // && !predictions.isEmpty()
                    // Xóa tất cả các hàng cũ trong bảng (nếu có)
                    tableLayout.removeAllViews();

                    TableRow headerRow = createHeaderRow();
                    setTextViewColor(headerRow, Color.BLACK);
                    // Thêm hàng tiêu đề vào bảng
                    tableLayout.addView(headerRow);


                    // Tạo hàng và cột cho mỗi mục dự đoán
                    int i = 1;
                    for (Prediction prediction : predictions) {
                        TableRow row = new TableRow(requireContext());

                        TextView noTextView = createTextView(String.valueOf(i));
                        row.addView(noTextView);

//                        TextView idTextView = createTextView(String.valueOf(prediction.getId()));
//                        row.addView(idTextView);
//                        String newName = transformName(prediction.getName());
                        TextView nameTextView = createTextView(prediction.getName());
                        row.addView(nameTextView);

                        // Làm tròn prop đến 3 chữ số thập phân
                        DecimalFormat df = new DecimalFormat("#.###");
                        String probRounded = df.format(prediction.getProb() * 100);
                        TextView probTextView = createTextView(probRounded + "%");
                        row.addView(probTextView);

                        setTextViewColor(row, Color.BLACK);
                        // Thêm hàng vào bảng
                        tableLayout.addView(row);

                        i++;
                    }
                } else {
                    // Nếu không có dữ liệu dự đoán, có thể hiển thị một thông báo hoặc làm gì đó khác
                }
            }
        });

        historyViewModel.getHighestProbDiseaseSelectedHistory().observe(getViewLifecycleOwner(), new Observer<DiseaseData>() {
            @Override
            public void onChanged(DiseaseData highestProbDisease) {
//                Toast.makeText(getActivity(), "highestProbDisease", Toast.LENGTH_LONG).show();
                // Update UI with highestProbDisease
                if (highestProbDisease != null) {
                    buttonViewMore.setVisibility(View.VISIBLE);
                    // Example: Display the highest probability disease details
                    TextView symtomTextView = view.findViewById(R.id.textViewSymtom);
                    String symtomMarkdown = highestProbDisease.getSymtomMarkdown();
                    Markwon markdown1 = Markwon.create(requireActivity());
                    markdown1.setMarkdown(symtomTextView, symtomMarkdown);

                    TextView descriptionTextView = view.findViewById(R.id.textViewMoreInformation);
                    String descriptionMarkdown = highestProbDisease.getDescriptionMarkdown();
                    Markwon markdown2 = Markwon.create(requireActivity());
                    markdown2.setMarkdown(descriptionTextView, descriptionMarkdown);

                    TextView reasonTextView = view.findViewById(R.id.textViewReason);
                    String reasonMarkdown = highestProbDisease.getReasonMarkdown();
                    Markwon markdown3 = Markwon.create(requireActivity());
                    markdown3.setMarkdown(reasonTextView, reasonMarkdown);

                    TextView treamentTextView = view.findViewById(R.id.textViewTreatment);
                    String treatmentMarkdown = highestProbDisease.getTreatmentMarkdown();
                    Markwon markdown4 = Markwon.create(requireActivity());
                    markdown4.setMarkdown(treamentTextView, treatmentMarkdown);

                    TextView precautionTextView = view.findViewById(R.id.textViewPrecaution);
                    String precautionMarkdown = highestProbDisease.getPrecautionMarkdown();
                    Markwon markdown5 = Markwon.create(requireActivity());
                    markdown5.setMarkdown(precautionTextView, precautionMarkdown);


                    TextView textViewDiseaseName = view.findViewById(R.id.textViewDiseaseName);
                    String diseaseName = highestProbDisease.getViName();
                    textViewDiseaseName.setText(diseaseName);
//                    Markwon markdown2 = Markwon.create(requireActivity());
//                    markdown2.setMarkdown(descriptionTextView, descriptionMarkdown);

                }
                if (highestProbDisease.getImageUrls() != null) {
                    ViewPager2 viewPager = view.findViewById(R.id.viewPager);

                    List<String> imageUrls = highestProbDisease.getImageUrls();

                    SlideshowAdapter adapter = new SlideshowAdapter(getActivity(), imageUrls);
                    viewPager.setAdapter(adapter);
                }
            }
        });

        historyViewModel.getUrlImageSelectedHistory().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && !s.isEmpty()) {
                    urlImageForFeedback = s;
                    buttonFeedback.setVisibility(View.VISIBLE);
//                    imageView = view.findViewById(R.id.SelectedImage);
                    Picasso.get().load(s).into(imageView);
                }


            }
        });

        historyViewModel.getTimeSelectedHistory().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && !s.isEmpty()) {
                    TextView textView = view.findViewById(R.id.textDate);
                    textView.setText("Thời gian: " + s);
                }
            }
        });


    }
    private void showDeleteConfirmationDialog(String historyId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá lịch sử không?")
                .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Xoá lịch sử ở đây
                        deleteHistory(historyId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Hủy bỏ thao tác
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(Color.parseColor("#006769"));
        negativeButton.setTextColor(Color.parseColor("#006769"));
    }

    private void showFeedbackDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_feedback, null);
        feedbackSpinner = dialogView.findViewById(R.id.feedbackSpinner);
        textFeedbackSpinner = dialogView.findViewById(R.id.textFeedbackSpinner);

        historyViewModel.getHighestProbDiseaseSelectedHistory().observe(getViewLifecycleOwner(), new Observer<DiseaseData>() {
            @Override
            public void onChanged(DiseaseData highestProbDisease) {
                disease = highestProbDisease.getViName();
            }
        });
        loadFeedbackOptions();
        assert getActivity() != null;
        Spinner feedbackTrueFalseSpinner = dialogView.findViewById(R.id.feedbackTrueFalseSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.true_false_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackTrueFalseSpinner.setAdapter(adapter);

        // Thiết lập sự kiện OnItemSelectedListener cho Spinner đầu tiên
        feedbackTrueFalseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Sai")) {
                    feedbackSpinner.setVisibility(View.VISIBLE);
                    textFeedbackSpinner.setVisibility(View.VISIBLE);
                } else {
                    feedbackSpinner.setVisibility(View.GONE);
                    textFeedbackSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        ImageView imageViewDisease = dialogView.findViewById(R.id.imageViewDisease);
        Picasso.get().load(urlImageForFeedback).into(imageViewDisease);
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(dialogView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(scrollView)
                .setPositiveButton("Gửi", null) // Chúng ta sẽ thiết lập OnClickListener riêng cho nút Submit
                .setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        // Thiết lập background cho dialog
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_component1);
        dialog.show();

        // Thiết lập OnClickListener riêng cho nút Submit để thực hiện validate
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
            EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
            EditText feedbackEditText = dialogView.findViewById(R.id.feedbackEditText);

            String email = emailEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String selectedOption = feedbackSpinner.getSelectedItem() != null ? feedbackSpinner.getSelectedItem().toString() : "";
            String selectedTrueFalseOption = feedbackTrueFalseSpinner.getSelectedItem() != null ? feedbackTrueFalseSpinner.getSelectedItem().toString() : "";
            String feedback = feedbackEditText.getText().toString().trim();

            // Thực hiện validate các trường bắt buộc
            if (email.isEmpty()) {
                emailEditText.setError("Email là bắt buộc");
                emailEditText.requestFocus();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Email chưa hợp lệ");
                emailEditText.requestFocus();
            }  else if (selectedOption.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn một bệnh", Toast.LENGTH_SHORT).show();
            } else {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("email", email);
                    jsonObject.put("userName", name);

                    jsonObject.put("selectedTrueFalseOption", selectedTrueFalseOption);
                    if (selectedTrueFalseOption.equals("Đúng")) {
                        jsonObject.put("selectedUserDisease", disease);
                        jsonObject.put("diseaseModelPrediction", disease);
                    } else {
                        jsonObject.put("selectedUserDisease", selectedOption);
                        jsonObject.put("diseaseModelPrediction", disease);
                    }
                    jsonObject.put("linkImage", urlImageForFeedback);
                    jsonObject.put("comments", feedback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendFeedback(jsonObject);
                // Xử lý phản hồi (gửi lên server hoặc lưu trữ cục bộ)
//                Toast.makeText(getContext(), "Email: " + urlImageForFeedback + "\nName: " + name + "\nOption: " + selectedOption + "\nFeedback: " + feedback, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // style cho button
        // Thiết lập phong cách cho các nút
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Thiết lập margin cho nút negativeButton
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        params.rightMargin = 32; // Điều chỉnh giá trị margin bên phải theo nhu cầu của bạn
        negativeButton.setLayoutParams(params);

        positiveButton.setBackgroundColor(Color.parseColor("#006769"));
        positiveButton.setTextColor(Color.WHITE);
        positiveButton.setPadding(10, 10, 10, 10);
        positiveButton.setTextSize(14);

        negativeButton.setBackgroundColor(Color.parseColor("#B4B4B8"));
        negativeButton.setTextColor(Color.WHITE);
        negativeButton.setPadding(10, 10, 10, 10);
        negativeButton.setTextSize(14);
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
    private void loadFeedbackOptions() {
        loadingLayout.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = urlBackend + "/api/get-data-feedback";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> feedbackOptions = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                if (response.getString(i).equals(disease)) continue;
                                feedbackOptions.add(response.getString(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, feedbackOptions);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        feedbackSpinner.setAdapter(adapter);
                        loadingLayout.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Lỗi lấy danh sách bệnh", Toast.LENGTH_SHORT).show();
                        loadingLayout.setVisibility(View.GONE);
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
    private void sendFeedback(JSONObject jsonObject) {
        loadingLayout.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = urlBackend + "/api/send-a-feedback";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String errMessage = jsonObject.getString("errMessage");
                            Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
                            loadingLayout.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            loadingLayout.setVisibility(View.GONE);
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Có lỗi khi gửi phản hồi", Toast.LENGTH_SHORT).show();
                        loadingLayout.setVisibility(View.GONE);
                    }
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }
    private void deleteHistory(String id) {
        loadingLayout.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = urlBackend + "/api/delete-history-from-android?historyId=" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String errMessage = jsonObject.getString("message");
                            Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack(ViewAHistoryFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            loadingLayout.setVisibility(View.GONE);
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        loadingLayout.setVisibility(View.GONE);
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
    private TableRow createHeaderRow() {
        TableRow row = new TableRow(requireContext());

        TextView noHeader = createTextViewForHeader("STT");
        row.addView(noHeader);

//        TextView idHeader = createTextView("ID");
//        row.addView(idHeader);

        TextView nameHeader = createTextViewForHeader("Tên bệnh");
        row.addView(nameHeader);

        TextView probHeader = createTextViewForHeader("Xác suất dự đoán");
        row.addView(probHeader);

        return row;
    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
    private TextView createTextViewForHeader(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
    private void setTextViewColor(ViewGroup viewGroup, int color) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else if (view instanceof ViewGroup) {
                setTextViewColor((ViewGroup) view, color);
            }
        }
    }
}