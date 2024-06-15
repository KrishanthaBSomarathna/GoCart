package com.example.gocart.UserListView.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.User;
import com.example.gocart.R;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private List<User> adminList;
    private OnItemClickListener clickListener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(int position, User user);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position, String uid);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public AdminAdapter(List<User> adminList) {
        this.adminList = adminList;
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        public TextView adminUnicId;
        public TextView adminName;
        public ImageButton adminEdit;
        public ImageButton adminDelete;

        public AdminViewHolder(View itemView) {
            super(itemView);
            adminUnicId = itemView.findViewById(R.id.user_unicid);
            adminName = itemView.findViewById(R.id.user_name);
            adminEdit = itemView.findViewById(R.id.user_edit);
            adminDelete = itemView.findViewById(R.id.user_del);
        }
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        User currentItem = adminList.get(position);

        holder.adminUnicId.setText(currentItem.getUnicId());
        holder.adminName.setText(currentItem.getName());

        holder.adminEdit.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position, currentItem);
            }
        });

        holder.adminDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(position, currentItem.getUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }
}
