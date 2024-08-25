package com.example.gocart.Predictor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder> {

    private List<String> predictedItemIds;

    public PredictionAdapter(List<String> predictedItemIds) {
        this.predictedItemIds = predictedItemIds;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new PredictionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        String itemId = predictedItemIds.get(position);
        holder.itemIdTextView.setText(itemId);
    }

    @Override
    public int getItemCount() {
        return predictedItemIds.size();
    }

    static class PredictionViewHolder extends RecyclerView.ViewHolder {
        TextView itemIdTextView;

        public PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIdTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
