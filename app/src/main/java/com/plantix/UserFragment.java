package com.plantix;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences sharedPreferences;
    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static final String TAG = UserFragment.class.getName();
    private PredictionViewModel viewModel;
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button btnLogout = view.findViewById(R.id.buttonLogout);
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        TextView userName = view.findViewById(R.id.userName);
        TextView textDescription = view.findViewById(R.id.textDescription);

        String timeExpire = sharedPreferences.getString("timeExpire", "0");
        try {
            long expireTimeMillis = Long.parseLong(timeExpire);
            if (System.currentTimeMillis() > expireTimeMillis) {
                btnLogout.setVisibility(View.GONE);
                System.out.println("Hết hạn " + timeExpire + " " + System.currentTimeMillis());
                editor.putInt("userId", 1);
                editor.putString("fullName", "");
                editor.apply();
                buttonLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đến trang login
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, AuthenticationFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(AuthenticationFragment.TAG) // Name can be null
                                .commit();
                    }
                });
            } else {
                btnLogout.setVisibility(View.VISIBLE);
                String fullName = sharedPreferences.getString("fullName", "");
                if (fullName.isEmpty()) userName.setText("Người dùng");
                else userName.setText(fullName);


                textDescription.setText("Giới thiệu bản thân");
                buttonLogin.setText("Chỉnh sửa");

                buttonLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đến trang chỉnh sửa thông tin người dùng
                        Toast.makeText(getContext(), "Chỉnh sửa thông tin người dùng", Toast.LENGTH_SHORT).show();
//                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.fragmentContainerView, AuthenticationFragment.class, null)
//                                .setReorderingAllowed(true)
//                                .addToBackStack(AuthenticationFragment.TAG) // Name can be null
//                                .commit();
                    }
                });
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle the exception, maybe log the error or set a default value
        }

        // Inflate the layout for this fragment

        Button btnHomePage = view.findViewById(R.id.home_button);
        btnHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(HomeFragment.TAG) // Name can be null
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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_LONG).show();
                editor.putInt("userId", 1);
                editor.putString("fullName", "");
                editor.putString("timeExpire", "0");
                editor.apply();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, AuthenticationFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // Name can be null
                        .commit();
            }
        });


        return view;
    }
}