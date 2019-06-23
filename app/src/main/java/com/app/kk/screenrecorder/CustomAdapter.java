package com.app.kk.screenrecorder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.kk.screenrecorder.Activity.MainActivity;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Viewholder> {

    private List<Item> itemList;

    public CustomAdapter(MainActivity mainActivity, int custom_listview, List<Item> itemList) {
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_listview, viewGroup, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder viewholder, int position) {
        String image = itemList.get(position).getVidImage();
        String title = itemList.get(position).getVidTitle();
        String duration = itemList.get(position).getVidDuration();
        String size = itemList.get(position).getVidSize();

        viewholder.setData(image, title, duration, size);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        private TextView vidImage;
        private TextView title;
        private TextView duration;
        private TextView size;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            vidImage = itemView.findViewById(R.id.vidImage);
            title = itemView.findViewById(R.id.vidTitle);
            duration = itemView.findViewById(R.id.vidDuration);
            size = itemView.findViewById(R.id.vidSize);
        }

        private void setData(String image, String titletext, String time, String siz) {
            vidImage.setText(image);
            title.setText(titletext);
            duration.setText(time);
            size.setText(siz);

        }
    }
}
