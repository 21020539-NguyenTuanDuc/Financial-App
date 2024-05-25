package com.example.financialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialapp.MainActivityFragments.AddSavingActivity;
import com.example.financialapp.Model.GoalModel;
import com.example.financialapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    public static List<GoalModel> goalModelList;
    private Context context;

    public GoalAdapter(Context context) {
        this.context = context;
        goalModelList = new ArrayList<>();
    }

    public void addData(GoalModel goalModel) {
        goalModelList.add(goalModel);
        notifyDataSetChanged();
    }

    public void clearData() {
        goalModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalAdapter.GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_card, parent, false);
        return new GoalAdapter.GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.GoalViewHolder holder, int position) {
        GoalModel goalModel = goalModelList.get(position);
        if (goalModel == null) return;
        String name = goalModel.getName();
        Long target = goalModel.getTarget();
        Long saved = goalModel.getSaved();

        holder.goalName.setText(name);
        NumberFormat nf = NumberFormat.getInstance();
        String targetAmount = nf.format(target);
        String savedAmount = nf.format(saved);
        holder.target.setText(targetAmount);
        holder.saved.setText(savedAmount);

        holder.progressBar.setMax(target.intValue());
        if (saved < target) {
            holder.progressBar.setProgress(saved.intValue());
        } else {
            holder.progressBar.setProgress(target.intValue());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddSavingActivity.class);
                intent.putExtra("goal", goalModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goalModelList.size();
    }

    public class GoalViewHolder extends RecyclerView.ViewHolder {
        private TextView goalName, target, saved;
        private ProgressBar progressBar;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            goalName = itemView.findViewById(R.id.goal_name);
            target = itemView.findViewById(R.id.target);
            saved = itemView.findViewById(R.id.saved);
            progressBar = itemView.findViewById(R.id.goal_progressBar);
        }
    }
}
