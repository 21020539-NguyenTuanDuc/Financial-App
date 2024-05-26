package com.example.financialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialapp.Model.TransactionModel;
import com.example.financialapp.NavigationFragments.CurrencyFragment;
import com.example.financialapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    public List<TransactionModel> transactionModelList;
    private Context context;

    public TransactionAdapter(Context context) {
        this.context = context;
        transactionModelList = new ArrayList<TransactionModel>();
    }

    public void addData(TransactionModel transactionModel) {
        transactionModelList.add(transactionModel);
        notifyDataSetChanged();
    }

    public void clearData() {
        transactionModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_card, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionModel transactionModel = transactionModelList.get(position);
        if (transactionModel == null) return;
        String category = transactionModel.getCategory();
        int cnt = 0;
        for (int i = 0; i < AddTransactionActivity.categories.length; i++) {
            if (AddTransactionActivity.categories[i].equals(category)) {
                cnt = i;
                break;
            }
        }
        holder.transaction_category_icon.setImageResource(AddTransactionActivity.icons[cnt]);
        holder.transaction_category.setText(transactionModel.getCategory());
        holder.transaction_type.setText(transactionModel.getType());
        NumberFormat nf = NumberFormat.getInstance();
        String transactionAmount = nf.format(transactionModel.getAmount()) + CurrencyFragment.current_symbol;
        boolean income = false;
        if (transactionModel.getType().equals("Income")) {
            transactionAmount = "+" + transactionAmount;
            income = true;
        } else {
            transactionAmount = "-" + transactionAmount;
        }
        holder.transaction_amount.setText(transactionAmount);
        if (income) holder.transaction_amount.setTextColor(Color.GREEN);
        else holder.transaction_amount.setTextColor(Color.RED);
        holder.transaction_date.setText(transactionModel.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddTransactionActivity.class);
                intent.putExtra("transaction", transactionModel);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (transactionModelList != null) return transactionModelList.size();
        return 0;
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView transaction_category, transaction_type, transaction_amount, transaction_date;
        private ImageView transaction_category_icon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction_category_icon = itemView.findViewById(R.id.transaction_category_icon);
            transaction_amount = itemView.findViewById(R.id.transaction_amount);
            transaction_date = itemView.findViewById(R.id.transaction_date);
            transaction_category = itemView.findViewById(R.id.transaction_category);
            transaction_type = itemView.findViewById(R.id.transaction_type);
        }
    }
}
