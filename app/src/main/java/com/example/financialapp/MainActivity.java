package com.example.financialapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.financialapp.Adapter.BudgetAdapter;
import com.example.financialapp.Adapter.MainViewPagerAdapter;
import com.example.financialapp.Model.UserModel;
import com.example.financialapp.NavigationFragments.AboutFragment;
import com.example.financialapp.NavigationFragments.CurrencyFragment;
import com.example.financialapp.NavigationFragments.FollowFragment;
import com.example.financialapp.NavigationFragments.HelpFragment;
import com.example.financialapp.NavigationFragments.LanguagesFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_CURRENCY = 2;
    private static final int FRAGMENT_LANGUAGES = 3;
    private static final int FRAGMENT_FOLLOW = 4;
    private static final int FRAGMENT_HELP = 5;
    private static final int FRAGMENT_ABOUT = 6;
    public static Uri profilePicture;
    public static UserModel currentUser;
    SweetAlertDialog sweetAlertDialog;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ShapeableImageView profilePictureIV;
    TextView profileNameTV;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    View headerView;
    FrameLayout frameLayout;
    private int CURRENT_FRAGMENT = FRAGMENT_HOME;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BudgetAdapter budgetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(androidx.appcompat.R.id.action_bar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        frameLayout = findViewById(R.id.content_frame);
        headerView = navigationView.getHeaderView(0);

        profileNameTV = (TextView) headerView.findViewById(R.id.username);
        profilePictureIV = (ShapeableImageView) headerView.findViewById(R.id.imageProfile);
        profilePictureIV.setImageResource(R.drawable.default_profile_picture);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


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
                    }
                }
        ).attach();
        int position = getIntent().getIntExtra("fragment_position", 0);
        viewPager.setCurrentItem(position);
        frameLayout.setVisibility(View.GONE);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        budgetAdapter = new BudgetAdapter(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
//        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        sweetAlertDialog.setCancelable(false);
//        sweetAlertDialog.show();
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
        if (user != null) {
            String Uid = user.getUid();
            currentUser = new UserModel(Uid, null, null, null);
            FirebaseFirestore.getInstance().collection("User").document(Uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            currentUser = documentSnapshot.toObject(UserModel.class);
                            assert currentUser != null;
                            currentUser.setId(Uid);
                            getProfilePicture();
                            if(currentUser.getCurrency_symbol() != null) CurrencyFragment.current_symbol = currentUser.getCurrency_symbol();
                            if(currentUser.getLanguage() != null) LanguagesFragment.current_language = currentUser.getLanguage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "User Failed!", Toast.LENGTH_SHORT).show();
//                            sweetAlertDialog.dismissWithAnimation();
                            if(currentUser.getCurrency_symbol() != null) CurrencyFragment.current_symbol = currentUser.getCurrency_symbol();
                            if(currentUser.getLanguage() != null) LanguagesFragment.current_language = currentUser.getLanguage();
                        }
                    });
        }
    }

    public void getProfilePicture() {
        FirebaseStorage.getInstance().getReference().child("images/" + currentUser.getId()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profilePicture = uri;
                        profileNameTV.setText(currentUser.getName());
                        if (profilePicture != null) {
                            Glide.with(MainActivity.this)
                                    .load(profilePicture)
                                    .into(profilePictureIV);
                        }
//                        sweetAlertDialog.dismissWithAnimation();
                        Log.d("FinancialApp", "Get profile picture");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileNameTV.setText(currentUser.getName());
//                        sweetAlertDialog.dismissWithAnimation();
                        Log.d("FinancialApp", e.getMessage());
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_home) {
            if (CURRENT_FRAGMENT != FRAGMENT_HOME) {
                CURRENT_FRAGMENT = FRAGMENT_HOME;
                viewPager.setCurrentItem(CURRENT_FRAGMENT);

                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
            }
        } else if (id == R.id.nav_currency) {
            if (CURRENT_FRAGMENT != FRAGMENT_CURRENCY) {
                CURRENT_FRAGMENT = FRAGMENT_CURRENCY;

                frameLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);

                replaceFragment(new CurrencyFragment());
            }
        } else if (id == R.id.nav_languages) {
            if (CURRENT_FRAGMENT != FRAGMENT_LANGUAGES) {
                CURRENT_FRAGMENT = FRAGMENT_LANGUAGES;

                frameLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);

                replaceFragment(new LanguagesFragment());
            }
        } else if (id == R.id.nav_follow_us) {
            if (CURRENT_FRAGMENT != FRAGMENT_FOLLOW) {
                CURRENT_FRAGMENT = FRAGMENT_FOLLOW;

                frameLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);

                replaceFragment(new FollowFragment());
            }
        } else if (id == R.id.nav_help) {
            if (CURRENT_FRAGMENT != FRAGMENT_HELP) {
                CURRENT_FRAGMENT = FRAGMENT_HELP;

                frameLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);

                replaceFragment(new HelpFragment());
            }
        } else if (id == R.id.nav_about) {
            if (CURRENT_FRAGMENT != FRAGMENT_ABOUT) {
                CURRENT_FRAGMENT = FRAGMENT_ABOUT;

                frameLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);

                replaceFragment(new AboutFragment());
            }
        }
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_currency).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_languages).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_follow_us).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_help).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_about).setChecked(false);
        navigationView.getMenu().findItem(id).setChecked(true);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
}