package com.example.financialapp.Model;

import com.example.financialapp.MainActivity;

import java.io.Serializable;

public class AccountModel implements Serializable {
    private String id;
    private String name;
    private long balance;
    private String type;
    private String userId;
    private long initialBalance;

    public AccountModel() {

    }

    public AccountModel(String id, String name, String type, long initialBalance) {
        this.id = id;
        this.name = name;
        this.initialBalance = initialBalance;
        this.type = type;
        this.balance = initialBalance;
        this.userId = MainActivity.currentUser.getId();
    }

    public AccountModel(String id, String name, String type, long initialBalance, long balance) {
        this.id = id;
        this.name = name;
        this.initialBalance = initialBalance;
        this.type = type;
        this.balance = balance;
        this.userId = MainActivity.currentUser.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(long initialBalance) {
        this.initialBalance = initialBalance;
    }
}
