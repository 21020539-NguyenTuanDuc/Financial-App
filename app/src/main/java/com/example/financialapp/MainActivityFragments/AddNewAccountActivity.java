package com.example.financialapp.MainActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.financialapp.Model.AccountModel;
import com.example.financialapp.NumberTextWatcherForThousand;
import com.example.financialapp.databinding.ActivityAddNewAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNewAccountActivity extends AppCompatActivity {

    ActivityAddNewAccountBinding binding;

    AccountModel tempAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.initValueET.addTextChangedListener(new NumberTextWatcherForThousand(binding.initValueET));

        tempAccount = (AccountModel) getIntent().getSerializableExtra("account");
        if (tempAccount != null) {
            binding.accountNameET.setText(tempAccount.getName());
            if (tempAccount.getType().equals("Bank")) {
                binding.bankRadio.setChecked(true);
            } else {
                binding.cashRadio.setChecked(true);
            }
            binding.initValueET.setText(String.valueOf(tempAccount.getInitialBalance()));
            binding.initValueET.setEnabled(false);
            binding.initValueET.setInputType(InputType.TYPE_NULL);

            binding.addAccountButton.setText("Update Account");
        }

        binding.addAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (tempAccount == null) {
                    createAccount();
                } else {
                    updateAccount();
                }
            }
        });
    }

    private void updateAccount() {
        String id = tempAccount.getId();
        String name = binding.accountNameET.getText().toString().trim();
        if (name.length() == 0) {
            binding.accountNameET.setError("Empty");
            return;
        }
        boolean cashChecked = binding.cashRadio.isChecked();
        String type;
        if (binding.initValueET.getText().toString().trim().length() == 0) {
            binding.initValueET.setError("Empty");
            return;
        }
        long initialValue = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.initValueET.getText().toString()));

        if (cashChecked) type = "Cash";
        else type = "Bank";


        AccountModel accountModel = new AccountModel(id, name, type, initialValue);

        FirebaseFirestore
                .getInstance()
                .collection("Account")
                .document(id)
                .set(accountModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddNewAccountActivity.this, "Account updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        startActivity(new Intent(AddNewAccountActivity.this, MainActivity.class));
        finish();
    }

    private void createAccount() {
        String id = FirebaseFirestore.getInstance().collection("Account").document().getId();
        String name = binding.accountNameET.getText().toString().trim();
        if (name.length() == 0) {
            binding.accountNameET.setError("Empty");
            return;
        }
        boolean cashChecked = binding.cashRadio.isChecked();
        String type;
        if (binding.initValueET.getText().toString().trim().length() == 0) {
            binding.initValueET.setError("Empty");
            return;
        }
        long initialValue = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.initValueET.getText().toString().trim()));
        if (cashChecked) type = "Cash";
        else type = "Bank";


        AccountModel accountModel = new AccountModel(id, name, type, initialValue);

        FirebaseFirestore
                .getInstance()
                .collection("Account")
                .document(id)
                .set(accountModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddNewAccountActivity.this, "Account added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        startActivity(new Intent(AddNewAccountActivity.this, MainActivity.class));
        finish();
    }
}