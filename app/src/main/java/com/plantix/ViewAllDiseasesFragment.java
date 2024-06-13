package com.plantix;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewAllDiseasesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewAllDiseasesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewAllDiseasesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewAllDiseasesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static final String TAG = ViewAllDiseasesFragment.class.getName();
    private DiseaseViewModel diseasesViewModel;
    public static final String urlBackend = BuildConfig.URL_SERVER_BACKEND;
    private LinearLayout allDiseasesContainer;
    private ImageButton btnReturnHome;
    private TextView loading;
    public static ViewAllDiseasesFragment newInstance(String param1, String param2) {
        ViewAllDiseasesFragment fragment = new ViewAllDiseasesFragment();
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
        diseasesViewModel = new ViewModelProvider(this).get(DiseaseViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_all_diseases, container, false);

        allDiseasesContainer = view.findViewById(R.id.all_diseases_container);
        loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        btnReturnHome = view.findViewById(R.id.return_button);

        // Sử dụng Handler để trì hoãn việc thay đổi văn bản sau 10 giây
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                loading.setVisibility(View.GONE);
//                noDataText.setVisibility(View.VISIBLE);
                loading.setText("Không có dữ liệu");
            }
        }, 3000); // 10 giây = 10000 milliseconds

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        diseasesViewModel.fetchAllDiseases(urlBackend);

        diseasesViewModel.getAllDiseases().observe(getViewLifecycleOwner(), new Observer<List<JSONObject>>() {
            @Override
            public void onChanged(List<JSONObject> histories) {
                // Clear any existing components first
                allDiseasesContainer.removeAllViews();
                if (histories != null && !histories.isEmpty()) {
                    loading.setVisibility(View.GONE);
                    for (int i = 0; i < histories.size(); i++) {
                        try {
                            JSONObject history = histories.get(i);
                            String enName = history.getString("enName");
                            String viName = history.getString("viName");
                            String linkImage = history.getString("imageData");

                            // Inflate new component layout
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View component = inflater.inflate(R.layout.disease_component, allDiseasesContainer, false);

                            // Find and update views in the new component
                            TextView textNameEng = component.findViewById(R.id.textNameEng);
                            TextView textNameVi = component.findViewById(R.id.textNameVi);
                            ImageView imageViewDisease = component.findViewById(R.id.imageViewDisease);

                            textNameEng.setText(enName);
                            textNameVi.setText(viName);
                            if (!linkImage.isEmpty()) {
                                Picasso.get().load(linkImage).error(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24).into(imageViewDisease);
                            } else {
                                imageViewDisease.setImageResource(R.drawable.photo_24dp_fill0_wght400_grad0_opsz24);
//                                Picasso.get().load("https://endlessicons.com/wp-content/uploads/2012/11/image-holder-icon-614x460.png").into(imageViewDisease);
                            }



                            // Add the new component to the parent layout
                            allDiseasesContainer.addView(component);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                loading.setText("Không có dữ liệu");

            }
        });
        btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        requireActivity().getSupportFragmentManager().popBackStack(ViewPredictFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(HomeFragment.TAG) // Name can be null
                        .commit();
            }
        });
    }
}