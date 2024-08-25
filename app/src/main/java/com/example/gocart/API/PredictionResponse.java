package com.example.gocart.API;

import java.util.List;
import java.util.Map;

public class PredictionResponse {
    private List<String> predicted_item_ids;
    private Map<String, Map<String, Object>> item_details;

    public List<String> getPredicted_item_ids() {
        return predicted_item_ids;
    }

    public void setPredicted_item_ids(List<String> predicted_item_ids) {
        this.predicted_item_ids = predicted_item_ids;
    }

    public Map<String, Map<String, Object>> getItem_details() {
        return item_details;
    }

    public void setItem_details(Map<String, Map<String, Object>> item_details) {
        this.item_details = item_details;
    }
}
