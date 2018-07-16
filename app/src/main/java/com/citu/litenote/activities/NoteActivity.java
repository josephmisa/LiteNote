package com.citu.litenote.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.citu.litenote.R;
import com.citu.litenote.data.models.Item;
import com.citu.litenote.utils.FileUtilities;
import com.citu.litenote.utils.SnackBarUtilities;
import com.citu.litenote.utils.ViewUtilities;

import java.nio.file.Files;
import java.util.HashMap;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "### " + NoteActivity.class.getSimpleName();

    private LinearLayout mLinearLayout;
    private EditText mEditTextTitle;
    private EditText mEditTextNote;

    private Item mItem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        // Get passed Item
        if (getIntent().getExtras() != null) {
            mItem = getIntent().getExtras().getParcelable("item");
            mItem.setFile(FileUtilities.getFile(mItem.getName()));
        }
        prepareToolbar();
        prepareViews();
    }

    private void prepareViews() {
        mLinearLayout = findViewById(R.id.linear_layout_root);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextNote = findViewById(R.id.edit_text_note);
    }

    private void prepareToolbar() {
        getSupportActionBar().setTitle(getString(R.string.navigation_add_note));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_note:
                Log.d(TAG, "Menu Add Note is clicked");
                ViewUtilities.hideKeyboard(this);
                String title = mEditTextTitle.getText().toString().trim();
                String note = mEditTextNote.getText().toString().trim();
                if (title.isEmpty() || note.isEmpty()) {
                    String message = "";
                    if (title.isEmpty()) {
                        message = getString(R.string.label_title_is_empty);
                    } else if (note.isEmpty()) {
                        message = getString(R.string.label_note_is_empty);
                    }
                    SnackBarUtilities.showSnackBarShort(NoteActivity.this,
                            mLinearLayout,
                            false,
                            message
                    );
                } else {
                    HashMap<String, Object> result = FileUtilities.createNote(mItem.getFile(), title, note);
                    String message = (String) result.get("message");
                    SnackBarUtilities.showSnackBarShort(NoteActivity.this,
                            mLinearLayout,
                            (boolean) result.get("success"),
                            message
                    );
                    if (!message.equals("Note Exists")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
