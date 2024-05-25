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

import com.example.financialapp.MainActivity;
import com.example.financialapp.Model.GoalModel;
import com.example.financialapp.R;
import com.example.financialapp.databinding.ActivityAddSavingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddSavingActivity extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;
    ActivityAddSavingBinding binding;
    private GoalModel currentGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSavingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentGoal = (GoalModel) getIntent().getSerializableExtra("goal");

        binding.progressCircular.setProgressMax(currentGoal.getTarget());
        if (currentGoal.getSaved() < currentGoal.getTarget()) {
            binding.progressCircular.setProgress(currentGoal.getSaved());
        } else {
            binding.progressCircular.setProgress(currentGoal.getTarget());
        }
        String displayPercentage = String.format("%.2f", 1.00 * currentGoal.getSaved() / currentGoal.getTarget() * 100) + "%";
        NumberFormat nf = NumberFormat.getInstance();
        String displayFraction = nf.format(currentGoal.getSaved()) + "/" + nf.format(currentGoal.getTarget());
        binding.percentageTV.setText(displayPercentage);
        binding.fractionTV.setText(displayFraction);

        binding.addSavedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(AddSavingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                String savedValue = binding.savedET.getText().toString();
                if (savedValue.length() == 0) {
                    binding.savedET.setError("Empty");
                    return;
                }
                currentGoal.setSaved(currentGoal.getSaved() + Long.parseLong(savedValue));
                FirebaseFirestore.getInstance().collection("Goal").document(currentGoal.getId())
                        .set(currentGoal)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddSavingActivity.this, "Update saving!", Toast.LENGTH_SHORT).show();
                                if (currentGoal.getSaved() >= currentGoal.getTarget() && currentGoal.isGoalAchievedNoti()) {
                                    notifyUser(currentGoal.getName(), "You have successfully achieved your goal!");
                                }
                                sweetAlertDialog.dismissWithAnimation();
                                AddSavingActivity.this.recreate();
                            }
                        });
            }
        });

        binding.setGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(AddSavingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                currentGoal.setReached(true);
                FirebaseFirestore.getInstance().collection("Goal").document(currentGoal.getId())
                        .set(currentGoal)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddSavingActivity.this, "Goal reached!", Toast.LENGTH_SHORT).show();
                                sweetAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(AddSavingActivity.this, MainActivity.class);
                                intent.putExtra("fragment_position", 1);
                                startActivity(intent);
                                finish();
                            }
                        });
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

    private void notifyUser(String name, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AddSavingActivity.this, "My Notification");
        builder.setContentTitle(name);
        builder.setSmallIcon(R.drawable.wallet_icon);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(AddSavingActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        managerCompat.notify(1, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment_position", 1);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.editButton) {
            Intent intent = new Intent(this, CreateGoalActivity.class);
            intent.putExtra("goal", currentGoal);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}