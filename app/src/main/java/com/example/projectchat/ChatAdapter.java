package com.example.projectchat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> messageList;
    private String currentUserId; // To check if the current user can delete the message
    private Context context;

    public ChatAdapter(List<ChatMessage> messageList, String currentUserId, Context context) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.senderName.setText(message.getSenderName());
        holder.messageText.setText(message.getMessage());
        holder.timestamp.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(message.getTimestamp())));

        // Handle long-press to delete
        holder.itemView.setOnLongClickListener(v -> {
            if (message.getSenderId().equals(currentUserId)) {
                showDeleteDialog(message, position);
            } else {
                Toast.makeText(context, "You can only delete your own messages", Toast.LENGTH_SHORT).show();
            }
            return true; // Consume the long-press event
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private void showDeleteDialog(ChatMessage message, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMessage(message, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMessage(ChatMessage message, int position) {
        Log.d("ChatAdapter", "Deleting message at position: " + position);
        Log.d("ChatAdapter", "Message ID: " + (message != null ? message.getMessageId() : "null"));

        try {
            if (message == null || message.getMessageId() == null) {
                Toast.makeText(context, "Invalid message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Remove the message from Firebase
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("messages");
            database.child(message.getMessageId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("ChatAdapter", "Message deleted successfully");
                    // Remove the message from the RecyclerView
                    messageList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, messageList.size()); // Update the RecyclerView
                } else {
                    Log.e("ChatAdapter", "Failed to delete message: " + task.getException());
                    Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("ChatAdapter", "Error deleting message: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(context, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}