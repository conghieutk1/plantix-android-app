package com.plantix;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import io.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewADiseaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewADiseaseFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = ViewADiseaseFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final  String urlBackend = BuildConfig.URL_SERVER_BACKEND;
    private RelativeLayout loadingLayout;
    private LinearLayout componentLoading;
    private ProgressBar progressBar;
    private TextView loading;
    private ScrollView scrollView;
    public ViewADiseaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewADiseaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewADiseaseFragment newInstance(String param1, String param2) {
        ViewADiseaseFragment fragment = new ViewADiseaseFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_a_disease, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String diseaseId = bundle.getString("diseaseId");
            if (diseaseId != null) {
                fetchDiseaseData(view, diseaseId);
            }
        }

        ImageButton btnReturnHome = view.findViewById(R.id.return_button);
        loadingLayout = view.findViewById(R.id.loadingLayout);
//        loadingLayout.setVisibility(View.VISIBLE);
        loading = view.findViewById(R.id.loading);
        progressBar = view.findViewById(R.id.progressBar);
//        loading.setVisibility(View.VISIBLE);
        scrollView = view.findViewById(R.id.content);
        componentLoading = view.findViewById(R.id.componentLoading);
        componentLoading.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack(ViewADiseaseFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    void fetchDiseaseData(View view, String diseaseId) {
        String apiGetDiseaseData = urlBackend + "/api/get-detail-disease?diseaseId=" + diseaseId;
        assert getActivity() != null;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiGetDiseaseData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Kiểm tra nếu phản hồi không chứa dữ liệu
                            if (!response.has("enName") || !response.has("viName")) {
                                loading.setText("Không có dữ liệu");
                                return;
                            }

//                            String keyDiseaseName = response.getString("keyDiseaseName");
                            String enName = response.getString("enName");
                            String viName = response.getString("viName");
                            String symtomMarkdown = response.getString("symtomMarkdown");
                            String precautionMarkdown = response.getString("precautionMarkdown");
                            String reasonMarkdown = response.getString("reasonMarkdown");
                            String treatmentMarkdown = response.getString("treatmentMarkdown");
                            String descriptionMarkdown = response.getString("descriptionMarkdown");


                            // Example: Display the highest probability disease details
                            TextView symtomTextView = view.findViewById(R.id.textViewSymtom);
                            Markwon markdown1 = Markwon.create(requireActivity());
                            markdown1.setMarkdown(symtomTextView, symtomMarkdown);

                            TextView descriptionTextView = view.findViewById(R.id.textViewMoreInformation);
                            Markwon markdown2 = Markwon.create(requireActivity());
                            markdown2.setMarkdown(descriptionTextView, descriptionMarkdown);

                            TextView reasonTextView = view.findViewById(R.id.textViewReason);
                            Markwon markdown3 = Markwon.create(requireActivity());
                            markdown3.setMarkdown(reasonTextView, reasonMarkdown);

                            TextView treamentTextView = view.findViewById(R.id.textViewTreatment);
                            Markwon markdown4 = Markwon.create(requireActivity());
                            markdown4.setMarkdown(treamentTextView, treatmentMarkdown);

                            TextView precautionTextView = view.findViewById(R.id.textViewPrecaution);
                            Markwon markdown5 = Markwon.create(requireActivity());
                            markdown5.setMarkdown(precautionTextView, precautionMarkdown);


                            TextView textViewDiseaseName = view.findViewById(R.id.titleDiseaseName);
                            textViewDiseaseName.setText(viName);
                            // Lấy danh sách URL hình ảnh từ phản hồi JSON
                            JSONArray imageDataArray = response.getJSONArray("imageData");
                            List<String> imageUrls = new ArrayList<>();
                            for (int i = 0; i < imageDataArray.length(); i++) {
                                JSONObject imageObject = imageDataArray.getJSONObject(i);
                                String linkImage = imageObject.getString("linkImage");
                                imageUrls.add(linkImage);
                            }

                            // Cấu hình ViewPager2 với SlideshowAdapter
                            ViewPager2 viewPager = view.findViewById(R.id.viewPager);
                            SlideshowAdapter adapter = new SlideshowAdapter(getActivity(), imageUrls);
                            viewPager.setAdapter(adapter);

                            // Ẩn loadingLayout sau khi tất cả các cập nhật giao diện hoàn tất
                            componentLoading.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
//                            componentLoading.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            loading.setText("Có lỗi xảy ra: " + e);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loadingLayout.setVisibility(View.GONE);
//                        componentLoading.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        loading.setText("Có lỗi xảy ra: " + error);
                        error.printStackTrace();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


}