package id.rikyripaldo.soxial.core.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TotpSecret;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import id.rikyripaldo.soxial.core.VideoDatabase;
import id.rikyripaldo.soxial.databinding.FragmentCreateBinding;

public class CreateFragment extends Fragment {

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Uri videoFileUri;
    private FragmentCreateBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("rikyripaldo");
        databaseReference = FirebaseDatabase.getInstance().getReference("rikyripaldo");

        binding.ambilVideo.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        binding.upload.setOnClickListener(v -> {
            if (videoFileUri != null) uploadVideo();
            else Toast.makeText(requireContext(), "Video belum dipilih", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadVideo() {
        final String name = firebaseUser.getDisplayName();
        final String profile = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString();
        final String deskripsi = Objects.requireNonNull(binding.deskripsi.getText()).toString().trim();

        if (deskripsi.isEmpty()) {
            Toast.makeText(requireContext(), "Tolong tambahkan deskripsi", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference filePath = storageReference.child(System.currentTimeMillis() + ".mp4");
        filePath.putFile(videoFileUri).addOnSuccessListener(task -> {
            filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                VideoDatabase videoDatabase = new VideoDatabase(profile, uri.toString(), name, deskripsi);
                String uploadId = databaseReference.push().getKey();
                databaseReference.child(uploadId).setValue(videoDatabase).addOnSuccessListener(upd -> {
                    Toast.makeText(requireContext(), "Video Uploaded", Toast.LENGTH_LONG).show();
                });
            });
        }).addOnFailureListener(error -> {
            Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        assert result.getData() != null;
        binding.video.setVideoURI(result.getData().getData());
        binding.video.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.setLooping(true);
            binding.video.start();
        });
        videoFileUri = result.getData().getData();
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
