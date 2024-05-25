package com.example.financialapp.MainActivityFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financialapp.Adapter.BudgetAdapter;
import com.example.financialapp.Adapter.GoalAdapter;
import com.example.financialapp.MainActivity;
import com.example.financialapp.Model.BudgetModel;
import com.example.financialapp.Model.GoalModel;
import com.example.financialapp.Model.TransactionModel;
import com.example.financialapp.R;
import com.example.financialapp.databinding.FragmentMainBudgetsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainBudgetsFragment extends Fragment {
    SweetAlertDialog sweetAlertDialog;
    FragmentMainBudgetsBinding binding;
    BudgetAdapter budgetAdapter;
    GoalAdapter goalAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainBudgetsBinding.inflate(getLayoutInflater());

        binding.createBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CreateBudgetActivity.class));
            }
        });

        budgetAdapter = new BudgetAdapter(getContext());
        binding.rcvBudgets.setAdapter(budgetAdapter);
        binding.rcvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.createGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CreateGoalActivity.class));
            }
        });

        goalAdapter = new GoalAdapter(getContext());
        binding.rcvGoals.setAdapter(goalAdapter);
        binding.rcvGoals.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        getBudgetModelData();
    }

    private void getBudgetModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Budget").whereEqualTo("userId", MainActivity.currentUser.getId())
                .whereEqualTo("ongoing", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        budgetAdapter.clearData();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds : dsList) {
                            BudgetModel budgetModel = ds.toObject(BudgetModel.class);
                            assert budgetModel != null;
                            if (budgetModel.getTimeStampEnd() < Calendar.getInstance().getTimeInMillis() / 1000) {
                                budgetModel.setOngoing(false);
                                db.collection("Budget")
                                        .document(budgetModel.getId()).set(budgetModel);
                                continue;
                            }
                            budgetAdapter.addData(budgetModel);
                        }
                        getSavingModelData();
                    }
                });
    }

    private void getSavingModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Goal").whereEqualTo("userId", MainActivity.currentUser.getId())
                .whereEqualTo("reached", false).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        goalAdapter.clearData();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        System.out.println(dsList.size());
                        for (DocumentSnapshot ds : dsList) {
                            GoalModel goalModel = ds.toObject(GoalModel.class);
                            goalAdapter.addData(goalModel);
                        }
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
    }
}