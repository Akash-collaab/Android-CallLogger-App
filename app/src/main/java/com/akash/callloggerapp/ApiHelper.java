package com.akash.callloggerapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ApiHelper {

    public static void sendIncomingCall(Context context, CallLogModel model) {
        String apiUrl = "https://YOUR_API_URL/api/SaveIncomingCall";  // Use your actual endpoint

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Call sent to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to send call", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", model.getNumber());
                params.put("name", model.getContactName());
                params.put("type", model.getType());        // Incoming or Outgoing
                params.put("status", model.getCallStatus()); // Answered / Missed / Declined
                params.put("duration", model.getDuration()); // In seconds (optional)
                params.put("time", model.getDate());         // e.g., 2025-06-25 12:45 PM
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
