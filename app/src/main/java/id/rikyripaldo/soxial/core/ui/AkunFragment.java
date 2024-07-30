package id.rikyripaldo.soxial.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import id.rikyripaldo.soxial.R;
import id.rikyripaldo.soxial.app.SignActivity;
import id.rikyripaldo.soxial.databinding.FragmentAkunBinding;

public class AkunFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FragmentAkunBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAkunBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Glide.with(requireActivity().getApplicationContext())
                .load(firebaseUser.getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.image);
        binding.name.setText(firebaseUser.getDisplayName());
        binding.email.setText(firebaseUser.getEmail());

        binding.logout.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
            builder.setTitle("Keluar");
            builder.setMessage("Apakah kamu yakin ingin keluar dari akun ini sekarang ?");
            builder.setPositiveButton("Ya", (type, views) -> {
                SignInClient sign = Identity.getSignInClient(requireActivity());
                firebaseAuth.signOut();
                sign.signOut().addOnCompleteListener(requireActivity(), task -> {
                    Toast.makeText(requireActivity(), "Sayounara " + firebaseUser.getDisplayName(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(requireActivity(), SignActivity.class));
                    requireActivity().finish();
                });
            });
            builder.create().show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
