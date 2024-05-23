package com.example.financialapp.AddTransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.financialapp.MainActivity;
import com.example.financialapp.MainActivityFragments.MainAccountFragment;
import com.example.financialapp.MainActivityFragments.TransactionModel;
import com.example.financialapp.R;
import com.example.financialapp.databinding.ActivityAddTransactionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;

import android.os.Handler;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddTransactionActivity extends AppCompatActivity {
    public static String[] categories = {"Food", "Shopping", "Housing", "Transportation", "Entertainment", "Investment", "Income", "Others"};
    public static int icons[] = {R.drawable.food_icon, R.drawable.shopping_icon, R.drawable.housing_icon, R.drawable.transportation_icon
            , R.drawable.entertainment_icon, R.drawable.investment_icon, R.drawable.income_icon, R.drawable.others_icon};

    ActivityAddTransactionBinding binding;
    Calendar calendar;
    CustomSpinnerAdapter customSpinnerAdapter;
    private TransactionModel transactionModel;
    SweetAlertDialog sweetAlertDialog;
    private long currentAmount;
    private String currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        customSpinnerAdapter = new CustomSpinnerAdapter(this, categories, icons);
        binding.categorySpinner.setAdapter(customSpinnerAdapter);

        String transactionAmount = getIntent().getStringExtra("transactionAmount");
        binding.amountET.setText(transactionAmount);

        getTimeAndDateInput();

        transactionModel = (TransactionModel) getIntent().getSerializableExtra("transaction");
        if (transactionModel != null) {
            if (transactionModel.getType().equals("Income")) {
                binding.incomeRadio.setChecked(true);
            } else {
                binding.expenseRadio.setChecked(true);
            }
            binding.amountET.setText(String.valueOf(transactionModel.getAmount()));
            binding.noteET.setText(transactionModel.getNote());
            binding.dateET.setText(transactionModel.getDate());
            binding.timeET.setText(transactionModel.getTime());
            int idx = 0;
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(transactionModel.getCategory())) {
                    idx = i;
                    break;
                }
            }
            binding.categorySpinner.setSelection(idx);
            binding.addTransactionButton.setText("Update Transaction");

            currentAmount = transactionModel.getAmount();
            currentType = transactionModel.getType();
        }

        binding.addTransactionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(AddTransactionActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                if (transactionModel == null) {
                    createTransaction();
                } else {
                    updateTransaction();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (transactionModel != null) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.delete_transaction_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (R.id.deleteTransactionButton == id) {
            sweetAlertDialog = new SweetAlertDialog(AddTransactionActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            deleteTransaction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTransaction() {
        FirebaseFirestore
                .getInstance()
                .collection("Transaction")
                .document(transactionModel.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddTransactionActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                        long newBalance = MainAccountFragment.currentAccount.getBalance();
                        if (currentType.equals("Income")) {
                            newBalance -= currentAmount;
                        } else {
                            newBalance += currentAmount;
                        }
                        MainAccountFragment.currentAccount.setBalance(newBalance);
                        FirebaseFirestore.getInstance().collection("Account")
                                .document(MainAccountFragment.currentAccount.getId()).set(MainAccountFragment.currentAccount)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                                    }
                                });
                    }
                });
    }


    private void updateTransaction() {
        String id = transactionModel.getId();
        String transactionAmount = binding.amountET.getText().toString();
        boolean incomeChecked = binding.incomeRadio.isChecked();
        String type;
        String transactionNote = binding.noteET.getText().toString();
        String category = categories[binding.categorySpinner.getSelectedItemPosition()];
        String date = binding.dateET.getText().toString();
        String time = binding.timeET.getText().toString();
        if (transactionAmount.length() == 0) {
            binding.amountET.setError("Empty");
        }
        if (incomeChecked) {
            type = "Income";
        } else {
            type = "Expense";
        }
        if (MainAccountFragment.currentAccId.equals("")) {
            Toast.makeText(this, "Please choose or create an account for this transaction", Toast.LENGTH_SHORT).show();
        } else {
            TransactionModel transaction =
                    new TransactionModel(id, Integer.parseInt(transactionAmount), type, transactionNote,
                            category, date, time, MainAccountFragment.currentAccId);

            FirebaseFirestore
                    .getInstance()
                    .collection("Transaction")
                    .document(id)
                    .set(transaction)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddTransactionActivity.this, "Transaction update", Toast.LENGTH_SHORT).show();
                            long newBalance = MainAccountFragment.currentAccount.getBalance();
                            if (currentType.equals("Income")) {
                                newBalance -= currentAmount;
                            } else {
                                newBalance += currentAmount;
                            }
                            if (type.equals("Income")) {
                                newBalance += Integer.parseInt(transactionAmount);
                            } else {
                                newBalance -= Integer.parseInt(transactionAmount);
                            }
                            MainAccountFragment.currentAccount.setBalance(newBalance);
                            FirebaseFirestore.getInstance().collection("Account")
                                    .document(MainAccountFragment.currentAccount.getId()).set(MainAccountFragment.currentAccount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddTransactionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismissWithAnimation();
                            startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                        }
                    });
        }
    }

    private void createTransaction() {
        String id = FirebaseFirestore.getInstance().collection("Transaction").document().getId();
        String transactionAmount = binding.amountET.getText().toString();
        boolean incomeChecked = binding.incomeRadio.isChecked();
        String type;
        String transactionNote = binding.noteET.getText().toString();
        String category = categories[binding.categorySpinner.getSelectedItemPosition()];
        String date = binding.dateET.getText().toString();
        String time = binding.timeET.getText().toString();
        // TODO: continue save transaction data
        if (transactionAmount.length() == 0) {
            binding.amountET.setError("Empty");
        }
        if (incomeChecked) {
            type = "Income";
        } else {
            type = "Expense";
        }
        if (MainAccountFragment.currentAccId.equals("")) {
            Toast.makeText(this, "Please choose or create an account for this transaction", Toast.LENGTH_SHORT).show();
        } else {
            TransactionModel transactionModel =
                    new TransactionModel(id, Integer.parseInt(transactionAmount), type, transactionNote, category, date, time, MainAccountFragment.currentAccId);

            FirebaseFirestore
                    .getInstance()
                    .collection("Transaction")
                    .document(id)
                    .set(transactionModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddTransactionActivity.this, "Transaction added", Toast.LENGTH_SHORT).show();
                            long newBalance = MainAccountFragment.currentAccount.getBalance();
                            if (type.equals("Income")) {
                                newBalance += Integer.parseInt(transactionAmount);
                            } else {
                                newBalance -= Integer.parseInt(transactionAmount);
                            }
                            MainAccountFragment.currentAccount.setBalance(newBalance);
                            FirebaseFirestore.getInstance().collection("Account")
                                    .document(MainAccountFragment.currentAccount.getId()).set(MainAccountFragment.currentAccount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddTransactionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismissWithAnimation();
                            startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                        }
                    });
        }
    }

    public void getTimeAndDateInput() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        calendar = Calendar.getInstance();
        binding.dateET.setText(dateFormat.format(calendar.getTime()));
        binding.timeET.setText(timeFormat.format(calendar.getTime()));

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDate();
            }

            private void updateDate() {
                binding.dateET.setText(dateFormat.format((calendar.getTime())));
            }
        };
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                updateClock();
            }

            private void updateClock() {
                binding.timeET.setText(timeFormat.format(calendar.getTime()));
            }
        };
        binding.dateET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddTransactionActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        binding.timeET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new TimePickerDialog(AddTransactionActivity.this, time, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                        true).show();
            }
        });
    }
}