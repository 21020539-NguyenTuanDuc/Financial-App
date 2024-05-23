package com.example.financialapp.MainActivityFragments;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountDeleteAdapter extends RecyclerView.Adapter<AccountDeleteAdapter.AccountDeleteViewHolder> {

    private Context context;
    public List<AccountModel> accountModelDList;

    public AccountDeleteAdapter(Context context) {
        this.context = context;
        accountModelDList = new ArrayList<>();
    }

    public void setData(List<AccountModel> accountModelList) {
        this.accountModelDList = accountModelList;
        notifyDataSetChanged();
    }

    public void addData(AccountModel accountModel) {
        accountModelDList.add(accountModel);
        notifyDataSetChanged();
    }

    public void removeData(String id) {
        for (int i = 0; i < accountModelDList.size(); i++) {
            if (accountModelDList.get(i).getId().equals(id)) {
                accountModelDList.remove(i);
                notifyDataSetChanged();
            }
        }
    }

    public void clearData() {
        accountModelDList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_full_card, parent, false);
        return new AccountDeleteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountDeleteViewHolder holder, int position) {
        AccountModel accountModel = accountModelDList.get(position);
        if (accountModel == null) return;
        String id = accountModel.getId();

        holder.account_name.setText(accountModel.getName());
        holder.account_type.setText(accountModel.getType());
        NumberFormat nf = NumberFormat.getInstance();
        String accountBalance = nf.format(accountModel.getBalance()) + "đ";
        holder.account_balance.setText(accountBalance);
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Account")
                        .document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                db.collection("Transaction")
                                        .whereEqualTo("accountId", id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        db.collection("Transaction").document(documentSnapshot.getId()).delete();
                                                    }
                                                }
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Cannot delete account!", Toast.LENGTH_SHORT).show();
                            }
                        });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = (Activity) context;
                        activity.recreate();
                    }
                }, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (accountModelDList != null) return accountModelDList.size();
        return 0;
    }

    public class AccountDeleteViewHolder extends RecyclerView.ViewHolder {

        private TextView account_name, account_balance, account_type;
        private ImageView delete_button;

        public AccountDeleteViewHolder(@NonNull View itemView) {
            super(itemView);

            account_name = itemView.findViewById(R.id.account_name);
            account_type = itemView.findViewById(R.id.account_type);
            account_balance = itemView.findViewById(R.id.account_balance);
            delete_button = itemView.findViewById(R.id.delete_button);
        }
    }
}
