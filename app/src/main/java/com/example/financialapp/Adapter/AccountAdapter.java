package com.example.financialapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialapp.Model.AccountModel;
import com.example.financialapp.MainActivityFragments.MainAccountFragment;
import com.example.financialapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private Context context;
    public List<AccountModel> accountModelList;

    public AccountAdapter(Context context) {
        this.context = context;
        accountModelList = new ArrayList<>();
    }

    public List<AccountModel> getAccountModelList() {
        return accountModelList;
    }

    public void setData(List<AccountModel> accountModelList) {
        this.accountModelList = accountModelList;
        notifyDataSetChanged();
    }

    public void addData(AccountModel accountModel) {
        accountModelList.add(accountModel);
        notifyDataSetChanged();
    }

    public void clearData() {
        accountModelList.clear();
        notifyDataSetChanged();
    }

    public boolean IdInAccountModelList(String id) {
        boolean result = false;
        for (int i = 0; i < accountModelList.size(); i++) {
            if (accountModelList.get(i).getId().equals(id)) result = true;
        }
        return result;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_card, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        AccountModel accountModel = accountModelList.get(position);
        if (accountModel == null) return;
        holder.accountName.setText(accountModel.getName());
//        holder.accountBalance.setText(String.valueOf(accountModel.getBalance()));
        NumberFormat nf = NumberFormat.getInstance();
        String accountBalance = nf.format(accountModel.getBalance()) + "đ";
        holder.accountBalance.setText(accountBalance);

        CardView cardView = (CardView) holder.itemView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainAccountFragment.currentAccId = accountModel.getId();
                MainAccountFragment.currentAccount = accountModel;
                Activity activity = (Activity) context;
                activity.recreate();
            }
        });
        if (accountModel.getId().equals(MainAccountFragment.currentAccId)) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.main_green));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));
        }

    }

    @Override
    public int getItemCount() {
        if (accountModelList != null) return accountModelList.size();
        return 0;
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {

        private TextView accountName, accountBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);

            accountName = (TextView) itemView.findViewById(R.id.accountName);
            accountBalance = (TextView) itemView.findViewById(R.id.accountBalance);
        }
    }
}
