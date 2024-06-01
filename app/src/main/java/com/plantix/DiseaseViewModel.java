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

public class DiseaseViewModel extends AndroidViewModel {

    private MutableLiveData<List<JSONObject>> allDiseases;
    private RequestQueue requestQueue;
    public DiseaseViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
    }
    public LiveData<List<JSONObject>> getAllDiseases() {
        if (allDiseases == null) {
            allDiseases = new MutableLiveData<>();
        }
        return allDiseases;
    }
    public void fetchAllDiseases(String urlBackend) {

        String apiGetHistory = urlBackend + "/api/get-all-diseases-by-userId?userId=3";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiGetHistory, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("diseases");
                            List<JSONObject> diseaseList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                diseaseList.add(jsonArray.getJSONObject(i));
                            }
                            allDiseases.setValue(diseaseList);
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
