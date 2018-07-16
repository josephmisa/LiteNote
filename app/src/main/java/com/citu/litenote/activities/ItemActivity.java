package com.citu.litenote.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.citu.litenote.R;
import com.citu.litenote.data.models.Item;
import com.citu.litenote.list.ItemClickSupport;
import com.citu.litenote.list.adapters.ItemAdapter;
import com.citu.litenote.ui.icons.Icons;
import com.citu.litenote.utils.Constants;
import com.citu.litenote.utils.FileUtilities;
import com.citu.litenote.utils.MimeTypesUtilities;
import com.citu.litenote.utils.SnackBarUtilities;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ItemActivity extends AppCompatActivity {

    private static final String TAG = "### " + ItemActivity.class.getSimpleName();

    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerViewItems;
    private TextView mTextViewNoItems;
    private FloatingActionMenu mFabMenuImport;
    private FloatingActionButton mFabTakePhoto;
    private FloatingActionButton mFabGetFromLibrary;
    private FloatingActionButton mFabImportFile;
    private FloatingActionButton mFabAddNote;

    private Item mItem;

    private ItemAdapter mItemAdapter;
    private SearchView mSearchView;
    private List<Item> mItems = new ArrayList<>();
    private List<Item> mItemsFiltered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        // Get passed Item
        if (getIntent().getExtras() != null) {
            mItem = getIntent().getExtras().getParcelable("item");
            mItem.setFile(FileUtilities.getFile(mItem.getName()));
        }
        prepareToolbar();
        prepareViews();
        prepareRecyclerViewItems();
        prepareFabButtons();
    }

    private void prepareToolbar() {
        getSupportActionBar().setTitle(mItem.getName());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void prepareViews() {
        mRelativeLayout = findViewById(R.id.relative_layout_root);
        mRecyclerViewItems = findViewById(R.id.recycler_view_items);
        mTextViewNoItems = findViewById(R.id.text_view_no_items);
        mFabMenuImport = findViewById(R.id.fab_menu_import);
        mFabTakePhoto = findViewById(R.id.fab_take_photo);
        mFabGetFromLibrary = findViewById(R.id.fab_get_from_gallery);
        mFabImportFile = findViewById(R.id.fab_import_file);
        mFabAddNote = findViewById(R.id.fab_add_note);
    }

    private void prepareRecyclerViewItems() {
        mItemAdapter = new ItemAdapter(this, mItemsFiltered);
        mRecyclerViewItems.setAdapter(mItemAdapter);
        mRecyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        ItemClickSupport.addTo(mRecyclerViewItems).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.d(TAG, "Item is clicked position [" + position + "]");
                Item item = mItemsFiltered.get(position);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(ItemActivity.this,
                            getApplicationContext().getPackageName() + ".provider",
                            item.getFile());
                    intent.setDataAndType(uri, item.getMimeType());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    SnackBarUtilities.showSnackBarShort(ItemActivity.this,
                            mRelativeLayout,
                            false,
                            getString(R.string.label_cant_open_file)

                    );
                }
            }
        });
        ItemClickSupport.addTo(mRecyclerViewItems).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
                Log.d(TAG, "Item is long clicked position [" + position + "]");
                final Item item = mItemsFiltered.get(position);
                // Show choices
                new MaterialDialog.Builder(ItemActivity.this)
                        .title(getString(R.string.label_select))
                        .items(new String[]{
                                getString(R.string.label_move),
                                getString(R.string.label_rename),
                                getString(R.string.label_delete)})
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    // Move
                                    case 0:
                                        Log.d(TAG, "Move is selected");
                                        showMoveDialog(item);
                                        break;
                                    // Rename
                                    case 1:
                                        Log.d(TAG, "Rename is selected");
                                        showRenameDialog(item);
                                        break;
                                    // Delete
                                    case 2:
                                        Log.d(TAG, "Delete is selected");
                                        showDeleteDialog(item);
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.action_ok)
                        .show();
                return true;
            }
        });
        mTextViewNoItems.setVisibility(mItemsFiltered.size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    private void prepareFabButtons() {
        mFabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "FAB Take Photo is clicked");
                mFabMenuImport.close(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "PermissionCode for Camera not granted");
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.PermissionCode.PERMISSION_CAMERA);
                        return;
                    }
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Constants.RequestCode.REQUEST_CAMERA);
            }
        });
        mFabGetFromLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "FAB Get from Gallery is clicked");
                mFabMenuImport.close(false);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.RequestCode.REQUEST_GALLERY);
            }
        });
        mFabImportFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "FAB Import File is clicked");
                mFabMenuImport.close(false);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, MimeTypesUtilities.getAcceptedMimeTypes());
                startActivityForResult(intent, Constants.RequestCode.REQUEST_FILES);
            }
        });
        mFabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "FAB Add Note is clicked");
                mFabMenuImport.close(false);
                Intent intent = new Intent(ItemActivity.this, NoteActivity.class);
                intent.putExtra("item", mItem);
                startActivity(intent);
            }
        });
    }

    private void showMoveDialog(final Item item) {
        final List<Item> subjects = FileUtilities.getItems(FileUtilities.getFile(""), mItem.getFile());
        if (subjects.size() > 0) {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.label_move_to))
                    .items(subjects)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            File file = subjects.get(which).getFile();
                            HashMap result = FileUtilities.copyFile(item.getFile(),
                                    new File(file.getPath() + "/" + item.getFile().getName()),
                                    true);
                            SnackBarUtilities.showSnackBarShort(ItemActivity.this,
                                    mRelativeLayout,
                                    (boolean) result.get("success"),
                                    getString(R.string.label_item) + " " + result.get("message")
                            );
                            reloadItems();
                            return true;
                        }
                    })
                    .positiveText(R.string.action_ok)
                    .show();
        } else {
            SnackBarUtilities.showSnackBarShort(ItemActivity.this,
                    mRelativeLayout,
                    false,
                    getString(R.string.label_create_another_subject)
            );
        }
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
                        SnackBarUtilities.showSnackBarShort(ItemActivity.this,
                                mRelativeLayout,
                                (boolean) result.get("success"),
                                result.get("message") + " " + getString(R.string.label_item)
                        );
                        reloadItems();
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
                        SnackBarUtilities.showSnackBarShort(ItemActivity.this,
                                mRelativeLayout,
                                (boolean) result.get("success"),
                                result.get("message") + " " + getString(R.string.label_subject)
                        );
                        reloadItems();
                    }
                }).show();
    }

    private void reloadItems() {
        mItemsFiltered.clear();
        File[] files = FileUtilities.getFiles(mItem.getName());
        for (File file : files) {
            if (!file.isDirectory()) {
                mItemsFiltered.add(new Item(file));
            } else {
                Log.w(TAG, "File is provider_paths Directory => " + file.getPath());
            }
        }
        Collections.sort(mItemsFiltered, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return String.valueOf(o2.getCreated_date()).compareTo(String.valueOf(o1.getCreated_date()));
            }
        });
        mItemAdapter.swapItems(mItemsFiltered);
        mTextViewNoItems.setVisibility(mItemsFiltered.size() > 0 ? View.INVISIBLE : View.VISIBLE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            HashMap<String, Object> result = new HashMap<>();
            Bitmap bitmap;
            String message = "";
            switch (requestCode) {
                case Constants.RequestCode.REQUEST_CAMERA:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    result = FileUtilities.createPhoto(mItem.getFile(), bitmap);
                    message = (String) result.get("message");
                    break;
                case Constants.RequestCode.REQUEST_GALLERY:
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        result = FileUtilities.createPhoto(mItem.getFile(), bitmap);
                        message = (String) result.get("message");
                    } catch (IOException e) {
                        e.printStackTrace();
                        message = e.getLocalizedMessage();
                    }
                    break;
                case Constants.RequestCode.REQUEST_FILES:
                    File from = FileUtilities.createTempFile(ItemActivity.this, data.getData());
                    File to = new File(mItem.getFile().getPath() + "/" + from.getName());
                    result = FileUtilities.copyFile(from, to, false);
                    message = (String) result.get("message") + " File";
                    from.delete();
                    break;
            }
            SnackBarUtilities.showSnackBarShort(ItemActivity.this,
                    mRelativeLayout,
                    (boolean) result.get("success"),
                    message
            );
        }
        reloadItems();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PermissionCode.PERMISSION_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constants.RequestCode.REQUEST_CAMERA);
                }
                return;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadItems();
    }
}
