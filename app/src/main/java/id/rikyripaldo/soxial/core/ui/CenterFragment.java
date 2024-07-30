package id.rikyripaldo.soxial.core.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.rikyripaldo.soxial.core.VideoDatabase;
import id.rikyripaldo.soxial.databinding.FragmentCenterBinding;
import id.rikyripaldo.soxial.widget.VideoAdapter;

public class CenterFragment extends Fragment {

    private VideoAdapter videoAdapter;
    private DatabaseReference databaseReference;
    private List<VideoDatabase> videoDatabaseList;
    private FragmentCenterBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCenterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoDatabaseList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("rikyripaldo");
        onReadVideo();

    }

    private void onReadVideo() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoDatabaseList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        VideoDatabase videoDatabase = snap.getValue(VideoDatabase.class);
                        videoDatabaseList.add(videoDatabase);
                    }
                    Collections.shuffle(videoDatabaseList);
                    videoAdapter = new VideoAdapter(requireContext(), videoDatabaseList);
                    binding.viewpager.setAdapter(videoAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
