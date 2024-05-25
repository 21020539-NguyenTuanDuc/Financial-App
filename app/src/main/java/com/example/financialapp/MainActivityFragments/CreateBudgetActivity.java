package com.example.financialapp.MainActivityFragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.financialapp.Adapter.PeriodSpinnerAdapter;
import com.example.financialapp.AddTransaction.AddTransactionActivity;
import com.example.financialapp.MainActivity;
import com.example.financialapp.Model.BudgetModel;
import com.example.financialapp.R;
import com.example.financialapp.databinding.ActivityCreateBudgetBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreateBudgetActivity extends AppCompatActivity {
    public String[] periods = {"7 days", "30 days", "180 days", "365 days"};
    public long[] periodDay = {7, 30, 180, 365};
    ActivityCreateBudgetBinding binding;
    private BudgetModel currentBudget;
    Calendar calendar;
    SweetAlertDialog sweetAlertDialog;
    PeriodSpinnerAdapter periodSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        periodSpinnerAdapter = new PeriodSpinnerAdapter(this, periods);
        binding.periodSpinner.setAdapter(periodSpinnerAdapter);

        currentBudget = (BudgetModel) getIntent().getSerializableExtra("budget");
        if (currentBudget != null) {
            binding.nameET.setText(currentBudget.getName());
            binding.budgetET.setText(String.valueOf(currentBudget.getBudget()));
            int idx = 0;
            for (int i = 0; i < periodDay.length; i++) {
                if (periodDay[i] == currentBudget.getPeriod()) {
                    idx = i;
                    break;
                }
            }
            binding.periodSpinner.setSelection(idx);
            binding.budgetOverspentNotify.setChecked(currentBudget.isBudgetOverspent());
            binding.riskOverspendingNotify.setChecked(currentBudget.isRiskOverspending());

            binding.addBudgetButton.setText("Update budget");
        }
        binding.addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(CreateBudgetActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                if (currentBudget == null) {
                    createBudget();
                } else {
                    updateBudget();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentBudget != null) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.delete_transaction_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment_position", 1);
            startActivity(intent);
            return true;
        }
        int id = item.getItemId();
        if (R.id.deleteTransactionButton == id) {
            sweetAlertDialog = new SweetAlertDialog(CreateBudgetActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            deleteBudget();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteBudget() {
        FirebaseFirestore.getInstance().collection("Budget").document(currentBudget.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateBudgetActivity.this, "Delete budget!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateBudgetActivity.this, MainActivity.class);
                        intent.putExtra("fragment_position", 1);
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                    }
                });
    }

    private void updateBudget() {
        String id = currentBudget.getId();
        String name = binding.nameET.getText().toString();
        String budget = binding.budgetET.getText().toString();
        long timestampStart = currentBudget.getTimeStampStart();
        long budgetPeriod = periodDay[binding.periodSpinner.getSelectedItemPosition()];
        long spending = currentBudget.getSpending();
        boolean ongoing = true;
        boolean budgetOverSpent = binding.budgetOverspentNotify.isChecked();
        boolean riskOverspending = binding.riskOverspendingNotify.isChecked();
        String userId = MainActivity.currentUser.getId();

        if (name.length() == 0) {
            binding.nameET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        if (budget.length() == 0) {
            binding.budgetET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        BudgetModel budgetModel = new BudgetModel(id, name, timestampStart, budgetPeriod, Long.parseLong(budget), spending,
                ongoing, budgetOverSpent, riskOverspending, userId);
        Intent intent = new Intent(CreateBudgetActivity.this, MainActivity.class);
        intent.putExtra("fragment_position", 1);

        FirebaseFirestore.getInstance().collection("Budget")
                .document(id).set(budgetModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateBudgetActivity.this, "Update Budget!", Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateBudgetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                    }
                });
    }

    private void createBudget() {
        String id = FirebaseFirestore.getInstance().collection("Budget").document().getId();
        String name = binding.nameET.getText().toString();
        String budget = binding.budgetET.getText().toString();
        long timestampStart = Calendar.getInstance().getTimeInMillis() / 1000;
        long budgetPeriod = periodDay[binding.periodSpinner.getSelectedItemPosition()];
        long spending = 0;
        boolean ongoing = true;
        boolean budgetOverSpent = binding.budgetOverspentNotify.isChecked();
        boolean riskOverspending = binding.riskOverspendingNotify.isChecked();
        String userId = MainActivity.currentUser.getId();

        if (name.length() == 0) {
            binding.nameET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        if (budget.length() == 0) {
            binding.budgetET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        BudgetModel budgetModel = new BudgetModel(id, name, timestampStart, budgetPeriod, Long.parseLong(budget), spending,
                ongoing, budgetOverSpent, riskOverspending, userId);
        Intent intent = new Intent(CreateBudgetActivity.this, MainActivity.class);
        intent.putExtra("fragment_position", 1);

        FirebaseFirestore.getInstance().collection("Budget")
                .document(id).set(budgetModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateBudgetActivity.this, "Create Budget!", Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateBudgetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                    }
                });
    }
}