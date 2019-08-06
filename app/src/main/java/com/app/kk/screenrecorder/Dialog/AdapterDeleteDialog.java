package com.app.kk.screenrecorder.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kk.screenrecorder.Activity.MainActivity;
import com.app.kk.screenrecorder.Adapter.CustomAdapter;
import com.app.kk.screenrecorder.Model.Item;
import com.app.kk.screenrecorder.R;

import java.io.File;
import java.util.List;

import static com.app.kk.screenrecorder.Activity.MainActivity.listString;

public class AdapterDeleteDialog {

    private MainActivity mainActivity;

    public static void delDialog(final int position, final Context context, final List<Item> arraylist, final CustomAdapter adapter) {
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
                File file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(position));
                file.delete();
                Toast.makeText(context, "Video deleted successfully",
                        Toast.LENGTH_LONG).show();
                arraylist.remove(position);
                adapter.notifyDataSetChanged();

                adapter.removeItem(position);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    public static void MainDelDialog(final Context context, final CustomAdapter adapter1, final List<Item> arraylist, final MainActivity mainActivity) {
        final Dialog dialog = new Dialog(context);
        View mylayout = LayoutInflater.from(context).inflate(R.layout.custom_delete_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView desc = (TextView) dialog.findViewById(R.id.desc);

        final SparseBooleanArray selected = adapter1
                .getSelectedIds();//Get selected ids

        if (selected.size() == adapter1.getItemCount()) {
            title.setText("Confirm delete All");
            desc.setText("Are you sure you want delete all video files!");
        } else {
            title.setText("Confirm delete");
            desc.setText("delete " + selected.size() + " video files");
        }

        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                /**Delete Rows*/
                //Loop all selected ids
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        //If current id is selected remove the item via key
                        File fdelete = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(selected.keyAt(i)));
                        fdelete.delete();
                        arraylist.remove(selected.keyAt(i));
                        adapter1.notifyDataSetChanged();//notify adapter
                        mainActivity.RecyclerAdapter();
                        //Snackbar.make(view, selected.size() + " item deleted.", Snackbar.LENGTH_LONG).show();
                    }
                }
                /** for full Directory delete */
//                File dir = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/");
//                if (dir.isDirectory()) {
//                    String[] children = dir.list();
//                    for (int i = 0; i < children.length; i++) {
//                        new File(dir, children[i]).delete();
//                    }
//                }
                adapter1.notifyDataSetChanged();
                mainActivity.RecyclerAdapter();
                Toast.makeText(context, selected.size() + " files are deleted successfully",
                        Toast.LENGTH_LONG).show();
                mainActivity.mActionMode.finish();
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
}
