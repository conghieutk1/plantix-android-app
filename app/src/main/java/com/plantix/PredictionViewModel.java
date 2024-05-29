package com.plantix;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class PredictionViewModel extends ViewModel {
    private MutableLiveData<List<Prediction>> predictions = new MutableLiveData<>();
    private MutableLiveData<DiseaseData> highestProbDisease = new MutableLiveData<>();
    // Phương thức setter để đặt dữ liệu vào ViewModel
    public void setPredictions(List<Prediction> listDiseases) {
        predictions.setValue(listDiseases);
    }

    // Phương thức getter để lấy dữ liệu từ ViewModel
    public LiveData<List<Prediction>> getPredictions() {
        return predictions;
    }

    public void setHighestProbDisease(DiseaseData disease) {
        highestProbDisease.setValue(disease);
    }
    public LiveData<DiseaseData> getHighestProbDisease() {
        return highestProbDisease;
    }
}