package com.example.gocart.Predictor;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gocart.API.ApiService;
import com.example.gocart.API.PredictionRequest;
import com.example.gocart.API.PredictionResponse;
import com.example.gocart.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Predictor extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictor);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://127.0.0.1:5000") // Replace with your Flask server IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Set up the button and its click listener
        Button btnPredict = findViewById(R.id.btnPredict);
        btnPredict.setOnClickListener(v -> callApi("customer_id_here")); // Replace with actual customer ID
    }

    private void callApi(String customerId) {
        PredictionRequest request = new PredictionRequest(customerId);
        Call<PredictionResponse> call = apiService.predict(request);

        call.enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful()) {
                    PredictionResponse predictionResponse = response.body();
                    if (predictionResponse != null) {
                        // Handle the predicted items and item details
                        List<String> predictedItemIds = predictionResponse.getPredicted_item_ids();
                        // Do something with the predicted item IDs and item details
                    }
                } else {
                    Toast.makeText(Predictor.this, "Failed to predict", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                Toast.makeText(Predictor.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
