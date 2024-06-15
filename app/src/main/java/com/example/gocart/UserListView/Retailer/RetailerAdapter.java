package com.example.gocart.UserListView.Retailer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.R;

import java.util.List;

public class RetailerAdapter extends RecyclerView.Adapter<RetailerAdapter.RetailerViewHolder> {
    private List<Retailer> retailerList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public RetailerAdapter(List<Retailer> retailerList, OnItemClickListener listener) {
        this.retailerList = retailerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RetailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new RetailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RetailerViewHolder holder, int position) {
        Retailer retailer = retailerList.get(position);
        holder.unicidTextView.setText(retailer.getUnicId());
        holder.nameTextView.setText(retailer.getName());
    }

    @Override
    public int getItemCount() {
        return retailerList.size();
    }

    public class RetailerViewHolder extends RecyclerView.ViewHolder {
        TextView unicidTextView;
        TextView nameTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        public RetailerViewHolder(@NonNull View itemView) {
            super(itemView);
            unicidTextView = itemView.findViewById(R.id.user_unicid);
            nameTextView = itemView.findViewById(R.id.user_name);
            editButton = itemView.findViewById(R.id.user_edit);
            deleteButton = itemView.findViewById(R.id.user_del);

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(position);
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(position);
                }
            });
        }
    }
}
