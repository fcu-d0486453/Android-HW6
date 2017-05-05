package shiro.iecs.edu.fcu.notepad;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private ListView listInput;
    private NoteDBHelper helper;
    private Cursor cursor;
    private SimpleCursorAdapter cursorAdapter;
    private List<String> option;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDB();
        initView();
    }

    private void initDB(){
        helper = new NoteDBHelper(getApplicationContext());
        cursor = helper.select();
        listInput = (ListView)findViewById(R.id.listInputText);
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.adapter, cursor,
                new String[]{"item_text"},
                new int[]{R.id.text},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
    }

    private void initView(){
        option = new ArrayList<>();
        option.add(getApplicationContext().getString(R.string.modify));
        option.add(getString(R.string.delete));
        inputText = (EditText)findViewById(R.id.inputText);
        listInput = (ListView)findViewById(R.id.listInputText);
        listInput.setAdapter(cursorAdapter);
        listInput.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
                final int pos = position;
                cursor.moveToPosition(1);
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(option.toArray(new String[option.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0://modify
                                        final View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout, null);
                                        final EditText editText = (EditText) item.findViewById(R.id.edittext);
                                        editText.setText(cursor.getString(1));
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("修改數值")
                                                .setView(item)
                                                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        helper.update(cursor.getInt(0), editText.getText().toString());
                                                        cursor.requery();
                                                        cursorAdapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .show();
                                        break;
                                    case 1://delete
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("刪除列")
                                                .setMessage("你確定要刪除？")
                                                .setPositiveButton("是", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        helper.delete(cursor.getInt(0));
                                                        cursor.requery();
                                                        cursorAdapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .setNegativeButton("否", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
                                        break;
                                }

                            }
                        }).show();
                return false;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "新增");
        menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "離開程式");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST://add new item
                if(!inputText.getText().toString().equals("")){
                    helper.insert(inputText.getText().toString());
                    cursor.requery();
                    cursorAdapter.notifyDataSetChanged();
                    inputText.setText("");
                }
                break;
            case Menu.FIRST + 1://exit app
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("離開此程式")
                        .setMessage("你確定要離開？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
