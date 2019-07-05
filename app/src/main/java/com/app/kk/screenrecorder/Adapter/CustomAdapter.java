package com.app.kk.screenrecorder.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kk.screenrecorder.Model.Item;
import com.app.kk.screenrecorder.R;

import java.io.File;
import java.util.List;

import static com.app.kk.screenrecorder.Activity.MainActivity.listString;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Viewholder> {

    List<Item> arraylist;
    Context context;
    File file;

    public CustomAdapter(Context context, int cusstom_layout, List<Item> arraylist) {
        this.arraylist = arraylist;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_listview, viewGroup, false);
        return new Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder viewholder, final int position) {
        String image = arraylist.get(position).getVidImage();
        String title = arraylist.get(position).getVidTitle();
        String duration = arraylist.get(position).getVidDuration();
        String size = arraylist.get(position).getVidSize();

        viewholder.setData(title, duration, size);

        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));
                intent.setDataAndType(uri, "video/*");
                context.startActivity(intent);
            }
        });

        viewholder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(v.getContext(), viewholder.menuBtn);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_delete:
                                delDialog(position);
                                return true;

                            case R.id.item_share:
                                shareIntent(position);
                                return true;

                            case R.id.item_play:
                                playVid(position);
                                return true;

                            case R.id.item_rename:
                                Rename(position);
                                return true;
                        }
                        popupMenu.dismiss();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void Rename(final int position) {
        final Dialog dialog = new Dialog(context);
        View mylayout = LayoutInflater.from(context).inflate(R.layout.custome_rename_dialg, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));

        final EditText rename = (EditText) dialog.findViewById(R.id.rename);
        rename.setHint(file.getName());

        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        String name = rename.getText().toString();
        final File file2 = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position), name);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean success = file.renameTo(file2);
                if (!success) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                } else {
                    notifyDataSetChanged();
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class Viewholder extends RecyclerView.ViewHolder {

        public ImageView menuBtn;
        private TextView title;
        private TextView duration;
        private TextView size;
        private ImageView vidImage;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            vidImage = itemView.findViewById(R.id.vidImage);
            title = itemView.findViewById(R.id.vidTitle);
            duration = itemView.findViewById(R.id.vidDuration);
            size = itemView.findViewById(R.id.vidSize);
            menuBtn = (ImageView) itemView.findViewById(R.id.itemMenu);

        }

        public void setData(String titletext, String time, String siz) {
            title.setText(titletext);
            duration.setText(time);
            size.setText(siz);

        }
    }

    private void playVid(final int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));
        intent.setDataAndType(uri, "video/*");
        context.startActivity(intent);
    }

    private void shareIntent(final int position) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, listString.get(position));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    public void delDialog(final int position) {
        final Dialog dialog = new Dialog(context);
        View mylayout = LayoutInflater.from(context).inflate(R.layout.custom_delete_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));
                file.delete();
                Toast.makeText(context, "Video deleted successfully",
                        Toast.LENGTH_LONG).show();
                arraylist.remove(position);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();

    }

}
