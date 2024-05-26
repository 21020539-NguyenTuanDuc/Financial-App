package com.example.financialapp.AddTransaction;

import static com.example.financialapp.Adapter.BudgetAdapter.budgetModelList;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.financialapp.Adapter.CustomSpinnerAdapter;
import com.example.financialapp.MainActivity;
import com.example.financialapp.MainActivityPackage.MainAccountFragment;
import com.example.financialapp.Model.TransactionModel;
import com.example.financialapp.NumberTextWatcherForThousand;
import com.example.financialapp.R;
import com.example.financialapp.databinding.ActivityAddTransactionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddTransactionActivity extends AppCompatActivity {
    public static String[] categories = {"Food", "Shopping", "Housing", "Transportation", "Entertainment", "Investment", "Income", "Others"};
    public static int[] icons = {R.drawable.food_icon, R.drawable.shopping_icon, R.drawable.housing_icon, R.drawable.transportation_icon
            , R.drawable.entertainment_icon, R.drawable.investment_icon, R.drawable.income_icon, R.drawable.others_icon};

    ActivityAddTransactionBinding binding;
    Calendar calendar;
    CustomSpinnerAdapter customSpinnerAdapter;
    SweetAlertDialog sweetAlertDialog;
    NotificationCompat.Builder notificationCompatBuilder;
    private TransactionModel transactionModel;
    private long currentAmount;
    private String currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.add_transactionTT);

        customSpinnerAdapter = new CustomSpinnerAdapter(this, categories, icons);
        binding.categorySpinner.setAdapter(customSpinnerAdapter);

        binding.amountET.addTextChangedListener(new NumberTextWatcherForThousand(binding.amountET));

        String transactionAmount = getIntent().getStringExtra("transactionAmount");
        if (transactionAmount != null && transactionAmount.matches("^[0-9]*$")) {
            binding.amountET.setText(transactionAmount);
        }

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

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void notifyUser(String accountName, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AddTransactionActivity.this, "My Notification");
        builder.setContentTitle(accountName);
        builder.setSmallIcon(R.drawable.wallet_icon);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(AddTransactionActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        managerCompat.notify(1, builder.build());
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
        if (transactionModel != null && android.R.id.home == id) {
            startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTransaction() {
        long currentTimestamp = transactionModel.getTimestamp();
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
                            for (int i = 0; i < budgetModelList.size(); i++) {
                                if (currentTimestamp >= budgetModelList.get(i).getTimeStampStart() && currentTimestamp < budgetModelList.get(i).getTimeStampEnd()) {
                                    budgetModelList.get(i).setSpending(budgetModelList.get(i).getSpending() - currentAmount);
                                }
                            }
                        }
                        MainAccountFragment.currentAccount.setBalance(newBalance);
                        FirebaseFirestore.getInstance().collection("Account")
                                .document(MainAccountFragment.currentAccount.getId()).set(MainAccountFragment.currentAccount)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pushBudgetData();
                                    }
                                });
                    }
                });
    }


    private void updateTransaction() {
        String id = transactionModel.getId();
        String transactionAmount = NumberTextWatcherForThousand.trimCommaOfString(binding.amountET.getText().toString());
        boolean incomeChecked = binding.incomeRadio.isChecked();
        String type;
        String transactionNote = binding.noteET.getText().toString();
        String category = categories[binding.categorySpinner.getSelectedItemPosition()];
        String date = binding.dateET.getText().toString();
        String time = binding.timeET.getText().toString();
        long oldTimestamp = transactionModel.getTimestamp();
        long timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date dateDate = dateFormat.parse(date);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date dateTime = timeFormat.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateDate);
            calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHours());
            calendar.set(Calendar.MINUTE, dateTime.getMinutes());
            timestamp = calendar.getTimeInMillis() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long finalTimestamp = timestamp;

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
                            category, date, time, MainAccountFragment.currentAccId, timestamp);

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
                                for (int i = 0; i < budgetModelList.size(); i++) {
                                    if (oldTimestamp >= budgetModelList.get(i).getTimeStampStart() && oldTimestamp <= budgetModelList.get(i).getTimeStampEnd()) {
                                        budgetModelList.get(i).setSpending(budgetModelList.get(i).getSpending() - currentAmount);
                                    }
                                }
                            }
                            if (type.equals("Income")) {
                                newBalance += Integer.parseInt(transactionAmount);
                            } else {
                                newBalance -= Integer.parseInt(transactionAmount);
                                for (int i = 0; i < budgetModelList.size(); i++) {
                                    if (finalTimestamp >= budgetModelList.get(i).getTimeStampStart() && finalTimestamp <= budgetModelList.get(i).getTimeStampEnd()) {
                                        budgetModelList.get(i).setSpending(budgetModelList.get(i).getSpending() + Long.parseLong(transactionAmount));
                                    }
                                    if (budgetModelList.get(i).isRiskOverspending()
                                            && budgetModelList.get(i).getSpending() - Long.parseLong(transactionAmount) < budgetModelList.get(i).getBudget() * 4 / 5
                                            && budgetModelList.get(i).getSpending() >= budgetModelList.get(i).getBudget() * 4 / 5
                                            && budgetModelList.get(i).getSpending() < budgetModelList.get(i).getBudget()) {
                                        notifyUser(budgetModelList.get(i).getName(), "You might overspent your budget!");
                                    }
                                    if (budgetModelList.get(i).isBudgetOverspent()
                                            && budgetModelList.get(i).getSpending() >= budgetModelList.get(i).getBudget()) {
                                        notifyUser(budgetModelList.get(i).getName(), "You have spent more than your budget!");
                                    }
                                }
                            }
                            MainAccountFragment.currentAccount.setBalance(newBalance);
                            FirebaseFirestore.getInstance().collection("Account")
                                    .document(MainAccountFragment.currentAccount.getId()).set(MainAccountFragment.currentAccount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            pushBudgetData();
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
                            finish();
                        }
                    });
        }
    }

    private void createTransaction() {
        String id = FirebaseFirestore.getInstance().collection("Transaction").document().getId();
        String transactionAmount = NumberTextWatcherForThousand.trimCommaOfString(binding.amountET.getText().toString());
        boolean incomeChecked = binding.incomeRadio.isChecked();
        String type;
        String transactionNote = binding.noteET.getText().toString();
        String category = categories[binding.categorySpinner.getSelectedItemPosition()];
        String date = binding.dateET.getText().toString();
        String time = binding.timeET.getText().toString();
        long timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date dateDate = dateFormat.parse(date);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date dateTime = timeFormat.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateDate);
            calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHours());
            calendar.set(Calendar.MINUTE, dateTime.getMinutes());
            timestamp = calendar.getTimeInMillis() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long finalTimestamp = timestamp;
        if (transactionAmount.length() == 0) {
            binding.amountET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        if (incomeChecked) {
            type = "Income";
        } else {
            type = "Expense";
        }
        if (MainAccountFragment.currentAccId.equals("")) {
            sweetAlertDialog.dismissWithAnimation();
            Toast.makeText(this, "Please choose or create an account for this transaction", Toast.LENGTH_SHORT).show();
        } else {
            TransactionModel transactionModel =
                    new TransactionModel(id, Integer.parseInt(transactionAmount), type, transactionNote,
                            category, date, time, MainAccountFragment.currentAccId, timestamp);

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
                                newBalance += Long.parseLong(transactionAmount);
                            } else {
                                newBalance -= Long.parseLong(transactionAmount);
                            }
                            MainAccountFragment.currentAccount.setBalance(newBalance);
                            FirebaseFirestore.getInstance().collection("Account")
                                    .document(MainAccountFragment.currentAccount.getId()).set(MainAccountFragment.currentAccount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if (type.equals("Expense")) {
                                                System.out.println("Size of budgetModelList: " + budgetModelList.size());
                                                for (int i = 0; i < budgetModelList.size(); i++) {
                                                    if (budgetModelList.get(i).isRiskOverspending()
                                                            && budgetModelList.get(i).getSpending() < budgetModelList.get(i).getBudget() * 4 / 5
                                                            && budgetModelList.get(i).getSpending() + Integer.parseInt(transactionAmount) >= budgetModelList.get(i).getBudget() * 4 / 5
                                                            && budgetModelList.get(i).getSpending() + Integer.parseInt(transactionAmount) < budgetModelList.get(i).getBudget()) {
                                                        notifyUser(budgetModelList.get(i).getName(), "You might overspent your budget!");
                                                    }
                                                    if (budgetModelList.get(i).isBudgetOverspent()
                                                            && budgetModelList.get(i).getSpending() + Integer.parseInt(transactionAmount) >= budgetModelList.get(i).getBudget()) {
                                                        notifyUser(budgetModelList.get(i).getName(), "You have spent more than your budget!");
                                                    }
                                                    if (finalTimestamp >= budgetModelList.get(i).getTimeStampStart() && finalTimestamp <= budgetModelList.get(i).getTimeStampEnd()) {
                                                        budgetModelList.get(i)
                                                                .setSpending(budgetModelList.get(i).getSpending() + Integer.parseInt(transactionAmount));
                                                    }
                                                }
                                                pushBudgetData();
                                            } else {
                                                sweetAlertDialog.dismissWithAnimation();
                                                startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                                                finish();
                                            }
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

    public void pushBudgetData() {
        if (budgetModelList.size() != 0) {
            for (int i = 0; i < budgetModelList.size(); i++) {
                if (i != budgetModelList.size() - 1) {
                    FirebaseFirestore.getInstance().collection("Budget").document(budgetModelList.get(i).getId())
                            .set(budgetModelList.get(i));
                } else {
                    FirebaseFirestore.getInstance().collection("Budget").document(budgetModelList.get(i).getId())
                            .set(budgetModelList.get(i))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddTransactionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    sweetAlertDialog.dismissWithAnimation();
                                    startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                }
            }
        } else {
            sweetAlertDialog.dismissWithAnimation();
            startActivity(new Intent(AddTransactionActivity.this, MainActivity.class));
            finish();
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