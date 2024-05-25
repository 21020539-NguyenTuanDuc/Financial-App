package com.example.financialapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financialapp.Adapter.BudgetAdapter;
import com.example.financialapp.Adapter.MainViewPagerAdapter;
import com.example.financialapp.Model.BudgetModel;
import com.example.financialapp.Model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.mozilla.javascript.tools.jsc.Main;

import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;
    public static UserModel currentUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BudgetAdapter budgetAdapter;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(androidx.appcompat.R.id.action_bar);


        if (user != null) {
            String Uid = user.getUid();
            String name = user.getDisplayName();
            String number = user.getPhoneNumber();
            String email = user.getEmail();
            currentUser = new UserModel(Uid, name, number, email);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager2) findViewById(R.id.viewPager);

        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Accounts");
                            break;
                        case 1:
                            tab.setText("Budgets & Goals");
                            break;
                        default:
                            tab.setText("Tab " + (position + 1));
                            break;
                    }
                }
        ).attach();
        int position = getIntent().getIntExtra("fragment_position", 0);
        viewPager.setCurrentItem(position);

        budgetAdapter = new BudgetAdapter(this);
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
}