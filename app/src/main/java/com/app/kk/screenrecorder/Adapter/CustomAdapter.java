package com.app.kk.screenrecorder.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kk.screenrecorder.Dialog.DeleteDialog;
import com.app.kk.screenrecorder.Model.Item;
import com.app.kk.screenrecorder.R;

import java.io.File;
import java.util.List;

import static com.app.kk.screenrecorder.Activity.MainActivity.listString;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Viewholder> {

    List<Item> arraylist;
    Context context;
    File file;
    private SparseBooleanArray mSelectedItemsIds;
    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_TWO = 2;

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;

    private GridLayoutManager mLayoutManager;


    public CustomAdapter(Context context, GridLayoutManager layoutManager, int cusstom_layout, List<Item> arraylist) {
        this.arraylist = arraylist;
        this.context = context;
        mLayoutManager = layoutManager;
        mSelectedItemsIds = new SparseBooleanArray();

    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_BIG;
        } else {
            return VIEW_TYPE_SMALL;
        }
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_listview, viewGroup, false);
//        return new Viewholder(view);
        View view;
        if (i == VIEW_TYPE_BIG) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_listview, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_grid, viewGroup, false);
        }
        return new Viewholder(view, i);

    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder viewholder, final int position) {
        String image = arraylist.get(position).getVidImage();
        String title = arraylist.get(position).getVidTitle();
        String duration = arraylist.get(position).getVidDuration();
        String size = arraylist.get(position).getVidSize();

        int color = R.color.select;
        if (mSelectedItemsIds.get(position)) viewholder.lc1
                .setBackground(new ColorDrawable(ContextCompat.getColor(context, color)));
        else viewholder.lc1
                .setBackground(ContextCompat.getDrawable(context, R.drawable.cardbg));

        Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(String.valueOf(uri), MediaStore.Video.Thumbnails.MINI_KIND);

        viewholder.vidImage.setImageBitmap(bitmap);
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
                popupMenu.getMenuInflater().inflate(R.menu.item_popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_delete:
                                DeleteDialog.adapterDelDialog(position, context, arraylist, CustomAdapter.this);
                                //adapterDelDialog(position);
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

    @Override
    public int getItemCount() {
        if (arraylist == null)
            return 0;
        else
            return arraylist.size();
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeItem(int pos) {
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, arraylist.size());
    }

    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    /**
     * Dialogs
     */
    private void Rename(final int position) {
        final Dialog dialog = new Dialog(context);
        View mylayout = LayoutInflater.from(context).inflate(R.layout.custome_rename_dialg, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));

        final EditText rename = (EditText) dialog.findViewById(R.id.rename);
        rename.setText(file.getName());
        rename.setFocusable(true);
        rename.setSelectAllOnFocus(true);
        rename.selectAll();

        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final String name = rename.getText().toString();
        final File file2 = new File(file.getParentFile(), name);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean success = file.renameTo(file2);
                if (!success) {
                    Toast.makeText(context, "Failed to rename file", Toast.LENGTH_LONG).show();
                } else {
                    notifyDataSetChanged();
                    Toast.makeText(context, "File Rename to " + name, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        public ImageView menuBtn;
        private TextView title;
        private TextView duration;
        private TextView size;
        private ImageView vidImage;
        private LinearLayout lc1;

        public Viewholder(@NonNull View itemView, int i) {
            super(itemView);
            lc1 = itemView.findViewById(R.id.lc1);
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

}

