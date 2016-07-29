package com.example.mao.onlineoroffline;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private MyDatabaseHelper sql;
    private RecyclerView recyclerView;
    private Spinner s;
    private int check;
    private Integer[] spinnerArray;
    private SQLiteDatabase DB;
    private Cursor cursor;
    private RecycleAdapter recycleAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sql = new MyDatabaseHelper(this,"users.db",null,3);
        DB = sql.getWritableDatabase();

        updata();

        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.insert_SQL).setOnClickListener(this);
        findViewById(R.id.delete_SQL).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logout:
                sendBroadcast(new Intent("com.example.mao.onlineoroffline.FORCE_OFFLINE"));
                break;
            case R.id.insert_SQL:
                ContentValues values = new ContentValues();
                EditText inputUserName = (EditText) findViewById(R.id.input_user_name);
                EditText inputUserPasswd = (EditText) findViewById(R.id.input_user_passwd);
                values.put("name",inputUserName.getText().toString());
                values.put("password",inputUserPasswd.getText().toString());
                Calendar calendar = Calendar.getInstance();
                values.put("time",new SimpleDateFormat("yyyy-MM-dd H:m:s").format(calendar.getTime()));
                DB.insert("users",null,values);
                updata();
                break;
            case R.id.delete_SQL:
                DB.delete("users","id = ?",new String[]{check+""});
                updata();
                break;
        }
    }
    public void updata(){
        cursor = DB.query("users",new String[]{"id as _id", "name","password","time"},null,null,null,null,null,null);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recycleAdapter = new RecycleAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recycleAdapter.setCursor(cursor);
        recyclerView.setAdapter(recycleAdapter);

        spinnerArray = new Integer[recycleAdapter.getItemCount()];
        for (int i = 0;i < recycleAdapter.getItemCount();i++){
            spinnerArray[i] = i+1;
        }
        s = (Spinner) findViewById(R.id.Spinner);
//        s.setAdapter(new ArrayAdapter<Integer>(this,android.R.layout.simple_list_item_1,spinnerArray));
        s.setAdapter(new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor,new String[]{"name"},new int[] { android.R.id.text1 }));
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.moveToPosition(i);
                int postion = cursor.getInt(cursor.getColumnIndex("_id"));
                Log.i("MainActivity","当前选择的是: " + postion);
                check = postion;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
