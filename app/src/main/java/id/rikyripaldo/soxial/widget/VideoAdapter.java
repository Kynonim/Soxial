package id.rikyripaldo.soxial.widget;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import id.rikyripaldo.soxial.R;
import id.rikyripaldo.soxial.core.VideoDatabase;
import id.rikyripaldo.soxial.databinding.AdapterVideoBinding;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private final Context context;
    private final List<VideoDatabase> videoDatabaseList;
    private AdapterVideoBinding binding;

    public VideoAdapter(Context context, List<VideoDatabase> videoDatabaseList) {
        this.context = context;
        this.videoDatabaseList = videoDatabaseList;
    }

    static class VideoHolder extends RecyclerView.ViewHolder {

        public VideoHolder(@NonNull AdapterVideoBinding view) {
            super(view.getRoot());
        }
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AdapterVideoBinding.inflate(LayoutInflater.from(context), parent, false);
        return new VideoHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        VideoDatabase videoDatabase = videoDatabaseList.get(position);
        binding.videoview.setVideoURI(Uri.parse(videoDatabase.getVideoUrl()));
        binding.name.setText(videoDatabase.getUserName());
        binding.deskripsi.setText(videoDatabase.getUserDeskripsi());
        Glide.with(context.getApplicationContext())
                .load(videoDatabase.getProfileUrl())
                .placeholder(R.drawable.baseline_account_circle_24)
                .circleCrop()
                .into(binding.profile);
        binding.videoview.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            binding.videoview.start();
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("verify");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Object object = dataSnapshot.child("email").getValue();
                        assert object != null;
                        if (Objects.equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail(), object.toString())) binding.verify.setImageResource(R.drawable.baseline_verified_24);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoDatabaseList.size();
    }
}
