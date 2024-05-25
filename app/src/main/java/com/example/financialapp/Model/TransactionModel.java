package com.example.financialapp.Model;

import java.io.Serializable;

public class TransactionModel implements Serializable {
    private String id;
    private int amount;
    private String type;
    private String note;
    private String category;
    private String date;
    private String time;
    private String accountId;
    private long timestamp;

    public TransactionModel() {

    }

    public TransactionModel(String id, int amount, String type, String note, String category, String date, String time, String accountId, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.category = category;
        this.date = date;
        this.time = time;
        this.accountId = accountId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
//    public String getAccountName(String accountId) {
//        String accountName;
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Account")
//                .document(accountId)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        AccountModel accountModel = documentSnapshot.toObject(AccountModel.class);
//                    }
//                });
//        return "0";
//    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
