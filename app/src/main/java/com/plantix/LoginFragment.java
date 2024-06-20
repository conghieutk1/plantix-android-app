package com.plantix;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final  String urlBackend = BuildConfig.URL_SERVER_BACKEND;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final String TAG = LoginFragment.class.getName();
    private SharedPreferences sharedPreferences;
    private RelativeLayout loadingLayout;
    public LoginFragment() {
        // Required empty public constructor
    }
    private View view;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        view = inflater.inflate(R.layout.fragment_login, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String savedAccount = sharedPreferences.getString("account", "");
        String savedPassword = sharedPreferences.getString("password", "");
        loadingLayout = view.findViewById(R.id.loadingLayout);

        EditText accountEditText = view.findViewById(R.id.accountEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        CheckBox checkBoxRememberMe = view.findViewById(R.id.checkBoxRememberMe);
        if (!savedAccount.isEmpty() || !savedPassword.isEmpty()) {
            accountEditText.setText(savedAccount);
            passwordEditText.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);
        }

        ImageButton returnAuthentication = view.findViewById(R.id.returnAuthentication);
        returnAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đổi fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, AuthenticationFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(AuthenticationFragment.TAG) // Name can be null
                        .commit();
            }
        });

        TextView buttonGotoSignup = view.findViewById(R.id.buttonGotoSignup);
        buttonGotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đổi fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, SignupFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(SignupFragment.TAG) // Name can be null
                        .commit();
            }
        });
        TextView errLogin = view.findViewById(R.id.errLogin);
        errLogin.setVisibility(View.GONE);

        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLayout.setVisibility(View.VISIBLE);
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                String url = urlBackend + "/api/login";
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String account = accountEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                boolean rememberMe = checkBoxRememberMe.isChecked();


                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("account", account);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    String errCode = jsonObject.getString("errCode");
                                    String errMessage = jsonObject.getString("errMessage");

                                    if (errCode.equals("0")) {
                                        // Đăng nhập thành công
                                        errLogin.setVisibility(View.GONE);
                                        int id = jsonObject.getInt("id");
                                        String fullName = jsonObject.getString("fullName");
                                        String timeExpire = jsonObject.getString("timeExpire");
                                        editor.putInt("userId", id);
                                        editor.putString("fullName", fullName);
                                        editor.putString("timeExpire", timeExpire);
                                        if (rememberMe) {
                                            editor.putString("account", account);
                                            editor.putString("password", password);
                                            editor.apply();
                                        } else {
                                            editor.putString("account", "");
                                            editor.putString("password", "");
                                            editor.apply();
                                        }
                                        Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.fragmentContainerView, HomeFragment.class, null)
                                                .setReorderingAllowed(true)
                                                .addToBackStack(null) // Name can be null
                                                .commit();
                                    } else {
                                        errLogin.setVisibility(View.VISIBLE);
                                        errLogin.setText(errMessage);
                                        loadingLayout.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {

                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                loadingLayout.setVisibility(View.GONE);
                            }
                        }
                );
                // Unable retry api
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}