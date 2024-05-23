package com.example.financialapp.AddTransaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financialapp.R;
import com.example.financialapp.databinding.ActivityAddTransactionBinding;

import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    String[] categories = {"Food", "Shopping", "Housing", "Transportation", "Entertainment", "Investment", "Income", "Others"};
    int icons[] = {R.drawable.food_icon, R.drawable.shopping_icon, R.drawable.housing_icon, R.drawable.transportation_icon
            , R.drawable.entertainment_icon, R.drawable.investment_icon, R.drawable.income_icon, R.drawable.others_icon};

    ActivityAddTransactionBinding binding;
    Calendar calendar;
    CustomSpinnerAdapter customSpinnerAdapter;

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

        binding.addTransactionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createExpense();
            }
        });
    }

    private void createExpense() {
        String transactionAmount = binding.amountET.getText().toString();
        boolean incomeChecked = binding.incomeRadio.isChecked();
        String transactionNote = binding.noteET.getText().toString();
        String category = categories[binding.categorySpinner.getSelectedItemPosition()];
        // TODO: continue save transaction data
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