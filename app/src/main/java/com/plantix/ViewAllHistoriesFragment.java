package com.plantix;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Use the {@link ViewAllHistoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewAllHistoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HistoryViewModel historyViewModel;
    public static final String urlBackend = BuildConfig.URL_SERVER_BACKEND;
    private LinearLayout allHistoriesContainer;
    private ImageButton btnReturnHome;
    private TextView messageWhileDontHaveData;
    public ViewAllHistoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewAllHistoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static final String TAG = ViewPredictFragment.class.getName();
    public static ViewAllHistoriesFragment newInstance(String param1, String param2) {
        ViewAllHistoriesFragment fragment = new ViewAllHistoriesFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_all_histories, container, false);
        allHistoriesContainer = view.findViewById(R.id.all_histories_container);
        messageWhileDontHaveData = view.findViewById(R.id.messageWhileDontHaveData);
        messageWhileDontHaveData.setVisibility(View.VISIBLE);

        btnReturnHome = view.findViewById(R.id.return_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyViewModel.fetchAllHistories(urlBackend);

        historyViewModel.getAllHistories().observe(getViewLifecycleOwner(), new Observer<List<JSONObject>>() {
            @Override
            public void onChanged(List<JSONObject> histories) {
                // Clear any existing components first
                allHistoriesContainer.removeAllViews();
                if (histories != null && !histories.isEmpty()) {
                    messageWhileDontHaveData.setVisibility(View.GONE);
                    for (int i = 0; i < histories.size(); i++) {
                        try {
                            JSONObject history = histories.get(i);
                            String dateTime = history.getString("DateTime");
                            String linkImage = history.getString("linkImage");
                            String diseaseName = history.getString("diseaseName");
                            // Inflate new component layout
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View component = inflater.inflate(R.layout.history_component, allHistoriesContainer, false);

                            // Find and update views in the new component
                            TextView textDateTime = component.findViewById(R.id.textDateTime);
                            TextView textDiseaseName = component.findViewById(R.id.textDiseaseName);
                            ImageView imageViewHistory = component.findViewById(R.id.imageViewHistory);

                            textDateTime.setText(dateTime);
                            textDiseaseName.setText(diseaseName);
                            Picasso.get().load(linkImage).into(imageViewHistory);

                            // Add the new component to the parent layout
                            allHistoriesContainer.addView(component);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


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