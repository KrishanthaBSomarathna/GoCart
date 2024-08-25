package com.example.gocart.API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/predict")
    Call<PredictionResponse> predict(@Body PredictionRequest request);
}
