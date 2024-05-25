package com.example.financialapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.financialapp.Adapter.BudgetAdapter;
import com.example.financialapp.Adapter.MainViewPagerAdapter;
import com.example.financialapp.Model.BudgetModel;
import com.example.financialapp.Model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;
    public static UserModel currentUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BudgetAdapter budgetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (user != null) {
            String Uid = user.getUid();
            String name = user.getDisplayName();
            String number = user.getPhoneNumber();
            String email = user.getEmail();
            currentUser = new UserModel(Uid, name, number, email);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        int position = getIntent().getIntExtra("fragment_position", 0);
        viewPager.setCurrentItem(position);

        budgetAdapter = new BudgetAdapter(this);
        getBudgetModelData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        sweetAlertDialog.dismissWithAnimation();
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
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
    }
}