package com.example.gocart.Predictor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.VolleyError;
import com.example.gocart.API.ApiService;
import com.example.gocart.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Predictor extends AppCompatActivity {
    private ApiService apiService;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictor);

        apiService = new ApiService(this);
//        Button btnPredict = findViewById(R.id.btnPredict);
//        tvResult = findViewById(R.id.tvResult);

//        btnPredict.setOnClickListener(v -> {
//            String customerId = "JD9ti7rxfeSQYQClJYEcwmt6kwG3"; // Example customer ID
//            predictOrders(customerId);
//        });
    }

    private void predictOrders(String customerId) {
        apiService.predictOrders(customerId, new ApiService.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.toString(4);
                    tvResult.setText(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                tvResult.setText("Error: " + error.getMessage());
            }
        });
    }
}
