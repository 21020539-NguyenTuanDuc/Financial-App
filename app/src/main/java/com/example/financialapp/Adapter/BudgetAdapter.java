package com.example.financialapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialapp.Model.BudgetModel;
import com.example.financialapp.Model.TransactionModel;
import com.example.financialapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private Context context;
    public static List<BudgetModel> budgetModelList;

    public BudgetAdapter(Context context) {
        this.context = context;
        budgetModelList = new ArrayList<>();
    }

    public void addData(BudgetModel budgetModel) {
        budgetModelList.add(budgetModel);
        notifyDataSetChanged();
    }

    public void clearData() {
        budgetModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_card, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetModel budgetModel = budgetModelList.get(position);
        if (budgetModel == null) return;
        String name = budgetModel.getName();
        Long spending = budgetModel.getSpending();
        Long budget = budgetModel.getBudget();

        holder.budgetName.setText(name);
        NumberFormat nf = NumberFormat.getInstance();
        String spendingAmount = nf.format(spending);
        String budgetAmount = nf.format(budget);
        holder.spendingAmount.setText(spendingAmount);
        holder.budgetAmount.setText(budgetAmount);

        holder.progressBar.setMax(budget.intValue());
        if (spending < budget) {
            holder.progressBar.setProgress(spending.intValue());
        } else {
            holder.progressBar.setProgress(budget.intValue());
        }
    }

    @Override
    public int getItemCount() {
        return budgetModelList.size();
    }


    public class BudgetViewHolder extends RecyclerView.ViewHolder {
        private TextView budgetName, spendingAmount, budgetAmount;
        private ProgressBar progressBar;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetName = itemView.findViewById(R.id.budget_name);
            spendingAmount = itemView.findViewById(R.id.spending_amount);
            budgetAmount = itemView.findViewById(R.id.budget_amount);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
