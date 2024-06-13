package com.plantix;
import android.app.Application;
import android.util.Log;

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
    private MutableLiveData<String> selectedHistoryID = new MutableLiveData<>();
    private MutableLiveData<List<Prediction>> predictionsSelectedHistory = new MutableLiveData<>();
    private MutableLiveData<DiseaseData> highestProbDiseaseSelectedHistory= new MutableLiveData<>();
    private MutableLiveData<String> time= new MutableLiveData<>();
    private MutableLiveData<String> urlImageSelectedHistory= new MutableLiveData<>();
    private MutableLiveData<List<JSONObject>> histories;
    private MutableLiveData<List<JSONObject>> allHistories;
    private RequestQueue requestQueue;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
    }
    public void setSelectedHistoryID(String id) {
        Log.d("SharedViewModel", "Setting historyId: " + id);
        selectedHistoryID.setValue(id);
    }
    public LiveData<String> getSelectedHistoryID() {
        return selectedHistoryID;
    }
    public void setPredictionsSelectedHistory(List<Prediction> listPrediction) {
        predictionsSelectedHistory.setValue(listPrediction);
    }

    // Phương thức getter để lấy dữ liệu từ ViewModel
    public LiveData<List<Prediction>> getPredictionsSelectedHistory() {
        return predictionsSelectedHistory;
    }

    public void setHighestProbDiseaseSelectedHistory(DiseaseData disease) {
        highestProbDiseaseSelectedHistory.setValue(disease);
    }
    public LiveData<DiseaseData> getHighestProbDiseaseSelectedHistory() {
        return highestProbDiseaseSelectedHistory;
    }

    public void setUrlImageSelectedHistory(String string) {
        urlImageSelectedHistory.setValue(string);
    }
    public LiveData<String> getUrlImageSelectedHistory() {
        return urlImageSelectedHistory;
    }
    public LiveData<String> getTimeSelectedHistory() {
        return time;
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
    public void fetchSelectedHistory(String urlBackend, String id) {
        String apiGetSelectedHistory = urlBackend + "/api/get-detail-history?historyId=" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiGetSelectedHistory, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("listDiseases");
                            List<Prediction> predictions = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject predictionObject = jsonArray.getJSONObject(i);
                                int id = predictionObject.getInt("diseaseId");
                                String name = predictionObject.getString("name");
                                double prob = predictionObject.getDouble("probability");
//                                System.out.println(id + " " + name+ " " + prob);
                                Prediction prediction = new Prediction(id, name, prob);
                                predictions.add(prediction);
                            }
                            // Set danh sách dự đoán vào ViewModel
                            predictionsSelectedHistory.setValue(predictions);

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


                            highestProbDisease.setImageUrls(imageLinks);
                            highestProbDiseaseSelectedHistory.setValue(highestProbDisease);

                            String urlImageSelectedDisease = response.getString("urlImageSelectedDisease");
                            urlImageSelectedHistory.setValue(urlImageSelectedDisease);

                            String timeSelectedDisease = response.getString("time");
                            time.setValue(timeSelectedDisease);
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
    public void fetchHistories(String urlBackend) {
        String apiGetHistory = urlBackend + "/api/get-history-by-userId?userId=4";
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

        String apiGetHistory = urlBackend + "/api/get-all-histories-by-userId?userId=4";
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
