package com.example.logsite.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.logsite.R;
import com.example.logsite.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ImageView imClock, imVase;
    private int vaseState = 0;

    final Handler delayHandler = new Handler();
    Runnable delayedResetClock = new Runnable() {
        @Override
        public void run() {
            imClock.setImageResource(R.drawable.pic_clock);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_home, container,false);

        //小鸟钟，嘿嘿
        imClock = root.findViewById(R.id.home_clock);
        imClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imClock.setImageResource(R.drawable.pic_clock_bird);
                delayHandler.postDelayed(delayedResetClock, 1000);
            }
        });

        imVase = root.findViewById(R.id.home_vase);
        imVase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaseState = (vaseState + 1) % 5;
                switch (vaseState){
                    case 0: imVase.setImageResource(R.drawable.pic_vase_0); break;
                    case 1: imVase.setImageResource(R.drawable.pic_vase_1); break;
                    case 2: imVase.setImageResource(R.drawable.pic_vase_2); break;
                    case 3: imVase.setImageResource(R.drawable.pic_vase_3); break;
                    case 4: imVase.setImageResource(R.drawable.pic_vase_4); break;
                    default: imVase.setImageResource(R.drawable.pic_vase_0); break;
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}