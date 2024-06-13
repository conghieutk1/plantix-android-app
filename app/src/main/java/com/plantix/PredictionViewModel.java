package com.plantix;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class PredictionViewModel extends ViewModel {
    private MutableLiveData<List<Prediction>> predictions = new MutableLiveData<>();
    private MutableLiveData<DiseaseData> highestProbDisease = new MutableLiveData<>();
    private MutableLiveData<String> urlImageSelectedDisease= new MutableLiveData<>();
    private MutableLiveData<Bitmap> imageLiveData = new MutableLiveData<>();
    // Phương thức setter để đặt dữ liệu vào ViewModel
    public void setImage(Bitmap bitmap) {
        imageLiveData.setValue(bitmap);
    }

    // Phương thức getter để lấy dữ liệu từ ViewModel
    public LiveData<Bitmap> getImage() {
        return imageLiveData ;
    }
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

    public void setUrlImageSelectedDisease(String string) {
        urlImageSelectedDisease.setValue(string);
    }
    public LiveData<String> getUrlImageSelectedDisease() {
        return urlImageSelectedDisease;
    }
}