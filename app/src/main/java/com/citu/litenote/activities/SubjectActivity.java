package com.citu.litenote.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.citu.litenote.R;
import com.citu.litenote.data.models.Item;
import com.citu.litenote.list.ItemClickSupport;
import com.citu.litenote.list.adapters.ItemAdapter;
import com.citu.litenote.utils.Constants;
import com.citu.litenote.utils.FileUtilities;
import com.citu.litenote.utils.SnackBarUtilities;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SubjectActivity extends AppCompatActivity {

    private static final String TAG = "### " + SubjectActivity.class.getSimpleName();

    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerViewSubjects;
    private TextView mTextViewNoSubjects;
    private FloatingActionButton mFabCreateNewSubject;

    private ItemAdapter mItemAdapter;
    private SearchView mSearchView;
    private List<Item> mItems = new ArrayList<>();
    private List<Item> mItemsFiltered = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "PermissionCode for Write External Storage not granted");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PermissionCode.PERMISSION_WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
        FileUtilities.createDirectory("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        prepareToolbar();
        prepareViews();
        prepareRecyclerViewSubjects();
        mFabCreateNewSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FAB Button Create New Folder is clicked");
                new MaterialDialog.Builder(SubjectActivity.this)
                        .title(R.string.label_enter_subject)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(getString(R.string.label_enter_subject), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                Log.d(TAG, "Inputted Item => " + input);
                                HashMap<String, Object> result = FileUtilities.createDirectory(input.toString());
                                SnackBarUtilities.showSnackBarShort(SubjectActivity.this,
                                        mRelativeLayout,
                                        (boolean) result.get("success"),
                                        result.get("message") + " " + getString(R.string.label_subject)
                                );
                                reloadSubjects();
                            }
                        }).show();
            }
        });
    }

    private void prepareToolbar() {
        getSupportActionBar().setTitle(getString(R.string.navigation_lite_note));
    }

    private void prepareViews() {
        mRelativeLayout = findViewById(R.id.relative_layout_root);
        mRecyclerViewSubjects = findViewById(R.id.recycler_view_subjects);
        mTextViewNoSubjects = findViewById(R.id.text_view_no_subjects);
        mFabCreateNewSubject = findViewById(R.id.fab_create_new_subject);
    }

    private void prepareRecyclerViewSubjects() {
        mItemAdapter = new ItemAdapter(this, mItemsFiltered);
        mRecyclerViewSubjects.setAdapter(mItemAdapter);
        mRecyclerViewSubjects.setLayoutManager(new LinearLayoutManager(this));
        ItemClickSupport.addTo(mRecyclerViewSubjects).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.d(TAG, "Item is clicked position [" + position + "]");
                Item item = mItemsFiltered.get(position);
                Intent intent = new Intent(SubjectActivity.this, ItemActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
        ItemClickSupport.addTo(mRecyclerViewSubjects).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
                Log.d(TAG, "Item is long clicked position [" + position + "]");
                final Item item = mItemsFiltered.get(position);
                // Show choices
                new MaterialDialog.Builder(SubjectActivity.this)
                        .title(getString(R.string.label_select))
                        .items(new String[]{
                                getString(R.string.label_rename),
                                getString(R.string.label_delete)})
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    // Rename
                                    case 0:
                                        Log.d(TAG, "Rename is selected");
                                        showRenameDialog(item);
                                        break;
                                    // Delete
                                    case 1:
                                        Log.d(TAG, "Delete is selected");
                                        showDeleteDialog(item);
                                }
                                reloadSubjects();
                                return true;
                            }
                        })
                        .positiveText(R.string.action_ok)
                        .show();
                return true;
            }
        });
        mTextViewNoSubjects.setVisibility(mItemsFiltered.size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    private void showRenameDialog(final Item item) {
        new MaterialDialog.Builder(this)
                .title(R.string.label_enter_new_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.label_enter_new_name), FileUtilities.getNameOnly(item.getFile()), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Log.d(TAG, "New Item Name => " + input);
                        HashMap<String, Object> result = FileUtilities.renameFile(item.getFile(), input.toString());
                        SnackBarUtilities.showSnackBarShort(SubjectActivity.this,
                                mRelativeLayout,
                                (boolean) result.get("success"),
                                result.get("message") + " " + getString(R.string.label_subject)
                        );
                        reloadSubjects();
                    }
                }).show();
    }

    private void showDeleteDialog(final Item item) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.label_delete))
                .setMessage("Are you sure you want to Delete " + item.getName())
                .setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.action_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        HashMap<String, Object> result = FileUtilities.deleteFile(item.getFile());
                        SnackBarUtilities.showSnackBarShort(SubjectActivity.this,
                                mRelativeLayout,
                                (boolean) result.get("success"),
                                result.get("message") + " " + getString(R.string.label_subject)
                        );
                        reloadSubjects();
                    }
                }).show();
    }

    private void reloadSubjects() {
        mItemsFiltered.clear();
        File[] files = FileUtilities.getFiles("");
        for (File file : files) {
            if (file.isDirectory()) {
                mItemsFiltered.add(new Item(file));
            } else {
                Log.w(TAG, "File is not provider_paths Directory => " + file.getPath());
            }
        }
        Collections.sort(mItemsFiltered, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return String.valueOf(o2.getCreated_date()).compareTo(String.valueOf(o1.getCreated_date()));
            }
        });
        mItemAdapter.swapItems(mItemsFiltered);
        mTextViewNoSubjects.setVisibility(mItemsFiltered.size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mItemAdapter.getFilter().filter(query);
                mItems = mItemAdapter.mItemsFiltered;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mItemAdapter.getFilter().filter(query);
                mItems = mItemAdapter.mItemsFiltered;
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mItemsFiltered = mItems;
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PermissionCode.PERMISSION_WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FileUtilities.createDirectory("");
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadSubjects();
    }
}
