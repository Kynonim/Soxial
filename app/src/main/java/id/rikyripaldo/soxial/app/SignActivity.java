package id.rikyripaldo.soxial.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import id.rikyripaldo.soxial.R;
import id.rikyripaldo.soxial.databinding.ActivitySignBinding;

public class SignActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private SignInClient signInClient;
    private ActivitySignBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(this);

        if (firebaseAuth.getCurrentUser() == null) tapSign();
        binding.login.setOnClickListener(v -> alertSign());
    }

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
        try {
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(result.getData());
            String token = credential.getGoogleIdToken();
            firebaseLogin(token);
        } catch (ApiException apiException) {
            onDisplay("ApiException", apiException.getMessage());
        }
    });

    private void firebaseLogin(String token) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(token, null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Assalamualaikum " + firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(SignActivity.this, RikyRipaldo.class));
                SignActivity.this.finish();
            } else {
                onDisplay("AuthCredential", Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void alertSign() {
        GetSignInIntentRequest sign = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.apikey))
                .build();
        signInClient.getSignInIntent(sign).addOnSuccessListener(intent -> {
            IntentSenderRequest request = new IntentSenderRequest.Builder(intent).build();
            activityResultLauncher.launch(request);
        }).addOnFailureListener(except -> {
            onDisplay("IntentRequest", except.getMessage());
        });
    }

    private void tapSign() {
        BeginSignInRequest tapRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.apikey))
                        .setFilterByAuthorizedAccounts(true)
                        .build()
                ).build();
        signInClient.beginSignIn(tapRequest).addOnSuccessListener(result -> {
            IntentSenderRequest request = new IntentSenderRequest.Builder(result.getPendingIntent()).build();
            activityResultLauncher.launch(request);
        }).addOnFailureListener(excpt -> {
            onDisplay("OnTapSign", excpt.getMessage());
        });
    }

    private void onDisplay(String type, String teks) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle(type);
        materialAlertDialogBuilder.setMessage(teks);
        materialAlertDialogBuilder.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(SignActivity.this, RikyRipaldo.class));
            SignActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}