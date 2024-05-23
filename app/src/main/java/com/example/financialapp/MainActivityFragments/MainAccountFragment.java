package com.example.financialapp.MainActivityFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.financialapp.AddTransaction.TransactionCalculatorActivity;
import com.example.financialapp.MainActivity;
import com.example.financialapp.databinding.FragmentMainAccountBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainAccountFragment extends Fragment {
    public static List<AccountModel> accountList;
    SweetAlertDialog sweetAlertDialog;

    FragmentMainAccountBinding binding;
    private AccountAdapter accountAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accountList = new ArrayList<>();
        // Inflate the layout for this fragment
        binding = FragmentMainAccountBinding.inflate(getLayoutInflater());

        binding.addTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TransactionCalculatorActivity.class));
            }
        });

        // Account GridManager at top
        accountAdapter = new AccountAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rcvCardAccount.setAdapter(accountAdapter);
        binding.rcvCardAccount.setLayoutManager(gridLayoutManager);

        binding.newAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddNewAccountActivity.class));
            }
        });

        binding.deleteAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DeleteAccountActivity.class));
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        getAccountModelData();
    }

    public void getAccountModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("account")
                .whereEqualTo("userId", MainActivity.currentUser.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        accountAdapter.clearData();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds : dsList) {
                            AccountModel accountModel = ds.toObject(AccountModel.class);
                            accountAdapter.addData(accountModel);
                        }
                        sweetAlertDialog.dismissWithAnimation();
//                        System.out.print(accountList);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}