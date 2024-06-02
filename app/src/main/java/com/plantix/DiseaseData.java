package com.plantix;

import java.util.ArrayList;
import java.util.List;

public class DiseaseData {
    private String keyDiseaseName;
    private String enName;
    private String viName;
    private String symtomMarkdown;

    public String getKeyDiseaseName() {
        return keyDiseaseName;
    }

    public void setKeyDiseaseName(String keyDiseaseName) {
        this.keyDiseaseName = keyDiseaseName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getViName() {
        return viName;
    }

    public void setViName(String viName) {
        this.viName = viName;
    }

    public DiseaseData(String keyDiseaseName, String enName, String viName, String symtomMarkdown, String precautionMarkdown, String reasonMarkdown, String treatmentMarkdown, String descriptionMarkdown) {
        this.keyDiseaseName = keyDiseaseName;
        this.enName = enName;
        this.viName = viName;
        this.symtomMarkdown = symtomMarkdown;
        this.precautionMarkdown = precautionMarkdown;
        this.reasonMarkdown = reasonMarkdown;
        this.treatmentMarkdown = treatmentMarkdown;
        this.descriptionMarkdown = descriptionMarkdown;
    }

    private String precautionMarkdown;
    private String reasonMarkdown;
    private String treatmentMarkdown;
    private String descriptionMarkdown;

    public DiseaseData(String symtomMarkdown, String precautionMarkdown, String reasonMarkdown, String treatmentMarkdown, String descriptionMarkdown, List<String> imageUrls) {
        this.symtomMarkdown = symtomMarkdown;
        this.precautionMarkdown = precautionMarkdown;
        this.reasonMarkdown = reasonMarkdown;
        this.treatmentMarkdown = treatmentMarkdown;
        this.descriptionMarkdown = descriptionMarkdown;
        this.imageUrls = imageUrls;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    List<String> imageUrls = new ArrayList<>();


    public DiseaseData(String symtomMarkdown, String precautionMarkdown, String reasonMarkdown, String treatmentMarkdown, String descriptionMarkdown) {
        this.symtomMarkdown = symtomMarkdown;
        this.precautionMarkdown = precautionMarkdown;
        this.reasonMarkdown = reasonMarkdown;
        this.treatmentMarkdown = treatmentMarkdown;
        this.descriptionMarkdown = descriptionMarkdown;
    }

    public String getSymtomMarkdown() {
        return symtomMarkdown;
    }

    public void setSymtomMarkdown(String symtomMarkdown) {
        this.symtomMarkdown = symtomMarkdown;
    }

    public String getPrecautionMarkdown() {
        return precautionMarkdown;
    }

    public void setPrecautionMarkdown(String precautionMarkdown) {
        this.precautionMarkdown = precautionMarkdown;
    }

    public String getReasonMarkdown() {
        return reasonMarkdown;
    }

    public void setReasonMarkdown(String reasonMarkdown) {
        this.reasonMarkdown = reasonMarkdown;
    }

    public String getTreatmentMarkdown() {
        return treatmentMarkdown;
    }

    public void setTreatmentMarkdown(String treatmentMarkdown) {
        this.treatmentMarkdown = treatmentMarkdown;
    }

    public String getDescriptionMarkdown() {
        return descriptionMarkdown;
    }

    public void setDescriptionMarkdown(String descriptionMarkdown) {
        this.descriptionMarkdown = descriptionMarkdown;
    }
}
