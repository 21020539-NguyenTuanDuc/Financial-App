package com.example.financialapp.MainActivityFragments;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.financialapp.Model.GoalModel;
import com.example.financialapp.NumberTextWatcherForThousand;
import com.example.financialapp.R;
import com.example.financialapp.databinding.ActivityCreateGoalBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreateGoalActivity extends AppCompatActivity {
    ActivityCreateGoalBinding binding;
    SweetAlertDialog sweetAlertDialog;
    private GoalModel currentGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.targetET.addTextChangedListener(new NumberTextWatcherForThousand(binding.targetET));
        binding.savedET.addTextChangedListener(new NumberTextWatcherForThousand(binding.savedET));

        currentGoal = (GoalModel) getIntent().getSerializableExtra("goal");
        if (currentGoal != null) {
            binding.nameET.setText(currentGoal.getName());
            binding.targetET.setText(String.valueOf(currentGoal.getTarget()));
            binding.savedET.setText(String.valueOf(currentGoal.getSaved()));
            binding.goalAchievedNotify.setChecked(currentGoal.isGoalAchievedNoti());

            binding.addGoalButton.setText("Update Goal");
        }

        binding.addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(CreateGoalActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                createAndUpdateGoal();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentGoal != null) {
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
            finish();
            return true;
        }
        int id = item.getItemId();
        if (R.id.deleteTransactionButton == id) {
            sweetAlertDialog = new SweetAlertDialog(CreateGoalActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            deleteGoal();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void notifyUser(String name, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(CreateGoalActivity.this, "My Notification");
        builder.setContentTitle(name);
        builder.setSmallIcon(R.drawable.wallet_icon);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(CreateGoalActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        managerCompat.notify(1, builder.build());
    }

    private void deleteGoal() {
        Intent intent = new Intent(CreateGoalActivity.this, MainActivity.class);
        intent.putExtra("fragment_position", 1);

        FirebaseFirestore.getInstance().collection("Goal")
                .document(currentGoal.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateGoalActivity.this, "Delete Goal!", Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateGoalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void createAndUpdateGoal() {
        String id;
        if (currentGoal != null) {
            id = currentGoal.getId();
        } else {
            id = FirebaseFirestore.getInstance().collection("Goal").document().getId();
        }
        String name = binding.nameET.getText().toString();
        String target = NumberTextWatcherForThousand.trimCommaOfString(binding.targetET.getText().toString());
        String saved = NumberTextWatcherForThousand.trimCommaOfString(binding.savedET.getText().toString());
        boolean goalAchievedNoti = binding.goalAchievedNotify.isChecked();
        boolean reached = false;
        String userId = MainActivity.currentUser.getId();

        if (name.length() == 0) {
            binding.nameET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        if (target.length() == 0) {
            binding.targetET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        if (saved.length() == 0) {
            binding.savedET.setError("Empty");
            sweetAlertDialog.dismissWithAnimation();
            return;
        }
        GoalModel goalModel = new GoalModel(id, name, Long.parseLong(target), Long.parseLong(saved), goalAchievedNoti, reached, userId);
        Intent intent = new Intent(CreateGoalActivity.this, MainActivity.class);
        intent.putExtra("fragment_position", 1);

        FirebaseFirestore.getInstance().collection("Goal")
                .document(id).set(goalModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateGoalActivity.this, "Create Goal!", Toast.LENGTH_SHORT).show();
                        if (goalModel.getSaved() >= goalModel.getTarget() && goalModel.isGoalAchievedNoti()) {
                            notifyUser(currentGoal.getName(), "You have successfully achieved your goal!");
                        }
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateGoalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismissWithAnimation();
                        startActivity(intent);
                        finish();
                    }
                });
    }
}