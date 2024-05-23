package com.example.financialapp.MainActivityFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financialapp.AddTransaction.TransactionCalculatorActivity;
import com.example.financialapp.MainActivity;
import com.example.financialapp.R;
import com.example.financialapp.databinding.FragmentMainAccountBinding;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainAccountFragment extends Fragment {
    private long income = 0, expense = 0;
    public static String currentAccId = "";
    public static AccountModel currentAccount;
    SweetAlertDialog sweetAlertDialog;

    FragmentMainAccountBinding binding;
    private AccountAdapter accountAdapter;
    private TransactionAdapter transactionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // Transaction Adapter
        transactionAdapter = new TransactionAdapter(getContext());
        binding.transactionRcv.setAdapter(transactionAdapter);
        binding.transactionRcv.setLayoutManager(new LinearLayoutManager(getContext()));

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
        income = 0;
        expense = 0;
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        getAccountModelData();
        getTransactionModelData();
    }


    public void getAccountModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Account")
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
                        Collections.sort(accountAdapter.accountModelList, new Comparator<AccountModel>() {

                            @Override
                            public int compare(AccountModel accountModel, AccountModel t1) {
                                return accountModel.getName().compareTo(t1.getName());
                            }
                        });
                        boolean found = accountAdapter.IdInAccountModelList(currentAccId);
                        if (accountAdapter.accountModelList.size() != 0) {
                            AccountModel tempAccountModel = accountAdapter.accountModelList.get(0);
                            if (tempAccountModel != null && !found) {
                                currentAccId = tempAccountModel.getId();
                                currentAccount = tempAccountModel;
                                getTransactionModelData();
                            }
                        } else {
                            currentAccId = "";
                        }
                    }
                });
    }

    public void getTransactionModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Transaction")
                .whereEqualTo("accountId", currentAccId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        transactionAdapter.clearData();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds : dsList) {
                            TransactionModel transactionModel = ds.toObject(TransactionModel.class);
                            if (transactionModel.getType().equals("Income")) {
                                income += transactionModel.getAmount();
                            } else {
                                expense += transactionModel.getAmount();
                            }
                            transactionAdapter.addData(transactionModel);
                        }
                        Collections.sort(transactionAdapter.transactionModelList, new Comparator<TransactionModel>() {

                            @Override
                            public int compare(TransactionModel transactionModel, TransactionModel t1) {
                                int dateCompare = t1.getDate().compareTo(transactionModel.getDate());
                                if (dateCompare != 0) {
                                    return dateCompare;
                                } else {
                                    return t1.getTime().compareTo(transactionModel.getTime());
                                }
                            }
                        });
                        setUpGraph();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
    }

    private void setUpGraph() {
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();
        if (income != 0) {
            pieEntryList.add(new PieEntry(income, "Income"));
            colorList.add(getResources().getColor(R.color.lighter_green));
        }
        if (expense != 0) {
            pieEntryList.add(new PieEntry(expense, "Expense"));
            colorList.add(getResources().getColor(R.color.red));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, String.valueOf(income = expense));
        pieDataSet.setColors(colorList);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        PieData pieData = new PieData(pieDataSet);

        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();
        Description description = new Description();
        description.setText("Income/Expense");
        description.setTextSize(12f);
        binding.pieChart.setDescription(description);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}