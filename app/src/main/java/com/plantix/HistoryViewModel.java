package com.plantix;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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


public class HistoryViewModel extends AndroidViewModel {

    private MutableLiveData<List<JSONObject>> histories;
    private MutableLiveData<List<JSONObject>> allHistories;
    private RequestQueue requestQueue;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
    }

    public LiveData<List<JSONObject>> getHistories() {
        if (histories == null) {
            histories = new MutableLiveData<>();
        }
        return histories;
    }
    public LiveData<List<JSONObject>> getAllHistories() {
        if (allHistories == null) {
            allHistories = new MutableLiveData<>();
        }
        return allHistories;
    }
    public void fetchHistories(String urlBackend) {
        String apiGetHistory = urlBackend + "/api/get-history-by-userId?userId=3";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiGetHistory, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("histories");
                            List<JSONObject> historyList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                historyList.add(jsonArray.getJSONObject(i));
                            }
                            histories.setValue(historyList);
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

        requestQueue.add(request);
    }

    public void fetchAllHistories(String urlBackend) {

        String apiGetHistory = urlBackend + "/api/get-all-histories-by-userId?userId=3";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiGetHistory, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("histories");
                            List<JSONObject> historyList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                historyList.add(jsonArray.getJSONObject(i));
                            }
                            allHistories.setValue(historyList);
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

        requestQueue.add(request);
    }
}
