package com.app.kk.screenrecorder.Utils;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.kk.screenrecorder.Activity.MainActivity;
import com.app.kk.screenrecorder.Adapter.CustomAdapter;
import com.app.kk.screenrecorder.Model.Item;
import com.app.kk.screenrecorder.R;

import java.util.ArrayList;
import java.util.List;

public class ToolbarActionModeCallback implements ActionMode.Callback {

    private Context context;
    private CustomAdapter recyclerView_adapter;
    private MainActivity mainActivity;
    private List<Item> message_models;


    public ToolbarActionModeCallback(Context context, CustomAdapter recyclerView_adapter, MainActivity infofragment, List<Item> message_models) {
        this.context = context;
        this.recyclerView_adapter = recyclerView_adapter;
        this.mainActivity = infofragment;
        this.message_models = message_models;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_main, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Drawable drawable = item.getIcon();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
                mainActivity.delDialog();
                break;
            case R.id.action_share:
//                mainActivity.shareRows();
                Toast.makeText(context, "Coming Soon", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        recyclerView_adapter.removeSelection();
        mainActivity.setNullToActionMode();
    }
}