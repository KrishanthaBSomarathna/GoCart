package com.example.gocart.API;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiService {
    private static final String BASE_URL = "http://192.168.8.115:8501";
    private RequestQueue requestQueue;

    public ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void predictOrders(String customerId, final ApiResponseListener listener) {
        String url = BASE_URL + "/predict";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("customer_id", customerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    public interface ApiResponseListener {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
