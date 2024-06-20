package com.plantix;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = SignupFragment.class.getName();
    public static final  String urlBackend = BuildConfig.URL_SERVER_BACKEND;
    private RelativeLayout loadingLayout;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ImageButton returnAuthentication = view.findViewById(R.id.returnAuthentication);
        EditText accountEditText = view.findViewById(R.id.accountEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        EditText cfPasswordEditText = view.findViewById(R.id.cfPasswordEditText);
        loadingLayout = view.findViewById(R.id.loadingLayout);
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

        TextView buttonGotoLogin = view.findViewById(R.id.buttonGotoLogin);
        buttonGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đổi fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, LoginFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(LoginFragment.TAG) // Name can be null
                        .commit();
            }
        });
        TextView errLogin = view.findViewById(R.id.errLogin);
        errLogin.setVisibility(View.GONE);

        Button buttonSignup = view.findViewById(R.id.buttonSignup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLayout.setVisibility(View.VISIBLE);
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                String url = urlBackend + "/api/signup";

                String account = accountEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String cfPassword = cfPasswordEditText.getText().toString().trim();

                // validate
                if (account.isEmpty()) {
                    errLogin.setVisibility(View.VISIBLE);
                    errLogin.setText("Vui lòng nhập tài khoản.");
                    loadingLayout.setVisibility(View.GONE);
                    return;
                }

                if (password.isEmpty()) {
                    errLogin.setVisibility(View.VISIBLE);
                    errLogin.setText("Vui lòng nhập mật khẩu.");
                    loadingLayout.setVisibility(View.GONE);
                    return;
                }

                if (password.length() < 6) {
                    errLogin.setVisibility(View.VISIBLE);
                    errLogin.setText("Mật khẩu phải có ít nhất 6 ký tự.");
                    loadingLayout.setVisibility(View.GONE);
                    return;
                }

                if (!password.equals(cfPassword)) {
                    errLogin.setVisibility(View.VISIBLE);
                    errLogin.setText("Mật khẩu và xác nhận mật khẩu không khớp.");
                    loadingLayout.setVisibility(View.GONE);
                    return;
                }

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
                            public void onResponse(JSONObject response) {
                                try {
                                    String errCode = response.getString("errCode");
                                    String errMessage = response.getString("errMessage");

                                    if (errCode.equals("0")) {
                                        // Đăng ký thành công
                                        errLogin.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.fragmentContainerView, AuthenticationFragment.class, null)
                                                .setReorderingAllowed(true)
                                                .addToBackStack(null) // Name can be null
                                                .commit();
                                    } else {
                                        errLogin.setVisibility(View.VISIBLE);
                                        errLogin.setText(errMessage);
                                        loadingLayout.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Lỗi trong quá trình xử lý phản hồi.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String errorMessage;
                                if (error instanceof TimeoutError) {
                                    errorMessage = "Request timeout. Please try again.";
                                } else if (error instanceof NoConnectionError) {
                                    errorMessage = "No connection. Please check your internet connection.";
                                } else if (error instanceof AuthFailureError) {
                                    errorMessage = "Authentication error. Please check your credentials.";
                                } else if (error instanceof ServerError) {
                                    errorMessage = "Server error. Please try again later.";
                                } else if (error instanceof NetworkError) {
                                    errorMessage = "Network error. Please try again.";
                                } else if (error instanceof ParseError) {
                                    errorMessage = "Parse error. Please try again.";
                                } else {
                                    errorMessage = "An unknown error occurred.";
                                }
                                errLogin.setVisibility(View.VISIBLE);
                                errLogin.setText(errorMessage);
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