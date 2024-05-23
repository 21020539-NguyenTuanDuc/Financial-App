package com.example.financialapp.MainActivityFragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialapp.R;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private Context context;
    private List<AccountModel> accountModelList;

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
        holder.accountBalance.setText(String.valueOf(accountModel.getBalance()));

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
