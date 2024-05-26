package com.example.financialapp.MainActivityFragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financialapp.Adapter.AccountDeleteAdapter;
import com.example.financialapp.MainActivity;
import com.example.financialapp.Model.AccountModel;
import com.example.financialapp.R;
import com.example.financialapp.databinding.ActivityDeleteAccountBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeleteAccountActivity extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;
    ActivityDeleteAccountBinding binding;
    private AccountDeleteAdapter accountDeleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.delete_accountTT);

        // Account RecyclerView
        accountDeleteAdapter = new AccountDeleteAdapter(this);
        binding.rcvCardAccount.setAdapter(accountDeleteAdapter);
        binding.rcvCardAccount.setLayoutManager(new LinearLayoutManager(DeleteAccountActivity.this));

    }

    public void onResume() {
        super.onResume();
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        getAccountModelData();
    }

    public void getAccountModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Account")
                .whereEqualTo("userId", MainActivity.currentUser.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        accountDeleteAdapter.clearData();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds : dsList) {
                            AccountModel accountModel = ds.toObject(AccountModel.class);
                            accountDeleteAdapter.addData(accountModel);
                        }
                        Collections.sort(accountDeleteAdapter.accountModelDList, new Comparator<AccountModel>() {

                            @Override
                            public int compare(AccountModel accountModel, AccountModel t1) {
                                return accountModel.getName().compareTo(t1.getName());
                            }
                        });
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
    }

}