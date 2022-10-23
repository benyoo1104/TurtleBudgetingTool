package com.example.vandyhackathon;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpendingsAdapter extends RecyclerView.Adapter<SpendingsAdapter.ViewHolder> {
    private Context mContext;
    private List<Data> myDataList;
    private String post_key = "";
    private String item = "";
    private String note;
    private int amount = 0;

    public SpendingsAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout, parent, false);
        return new SpendingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Data data = myDataList.get(position);
        holder.item.setText("Category: " + data.getItem());
        holder.amount.setText("Amount: $" + data.getAmount());
        holder.date.setText("On " + data.getDate());
        holder.note.setText("Note: " + data.getNote());
        Log.w("debug", data.toString());
        switch (data.getItem())
        {
            case "Charity":
                holder.imageView.setImageResource(R.drawable.turtle);
                break;
            case "Education":
                holder.imageView.setImageResource(R.drawable.star);
                break;
            case "Entertainment":
                holder.imageView.setImageResource(R.drawable.fin);
                break;
            case "Food":
                holder.imageView.setImageResource(R.drawable.fish);
                break;
            case "Health":
                holder.imageView.setImageResource(R.drawable.turtle);
                break;
            case "Home":
                holder.imageView.setImageResource(R.drawable.turtle);
                break;
            case "Personal":
                holder.imageView.setImageResource(R.drawable.turtle);
                break;
            case "Transportation":
                holder.imageView.setImageResource(R.drawable.turtle);
                break;
            case "Other":
                holder.imageView.setImageResource(R.drawable.turtle);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView item, amount, date, note;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            note = itemView.findViewById(R.id.note);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}