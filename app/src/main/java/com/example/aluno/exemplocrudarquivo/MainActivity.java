package com.example.aluno.exemplocrudarquivo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    static final int READ_REQ = 24;
    static final int WRITE_REQ = 25;
    static final int EDIT_REQ = 26;
    static final int DELETE_REQ = 27;

    ViewGroup cont;
    ListView contactLst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void readFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");

        startActivityForResult(intent, READ_REQ);
    }

    public void createFile(View view) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "zoftino.txt");
        startActivityForResult(intent, WRITE_REQ);
    }

    public void deleteFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/plain");

        startActivityForResult(intent, DELETE_REQ);
    }

    public void editDocument(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, EDIT_REQ);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
            }

            if (requestCode == READ_REQ) {
                readTextFile(uri);
            } else if (requestCode == EDIT_REQ) {
                editDocument(uri);
            } else if (requestCode == WRITE_REQ) {
                editDocument(uri);
            } else if (requestCode == DELETE_REQ) {
                deleteFile(uri);
            }
        }
    }

    private void editDocument(Uri uri) {
        try {
            ParcelFileDescriptor fileDescriptor = this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor.getFileDescriptor());
            fileOutputStream.write(("android latest updates \n").getBytes());
            fileOutputStream.write(("android latest features \n").getBytes());
            fileOutputStream.close();
            fileDescriptor.close();
        } catch (Exception e) {

        }
    }

    private void readTextFile(Uri uri) {
        Log.i("Read", "open text file - content" + "\n");
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            Log.i("Read", "open text file - content" + "\n");
            while ((line = reader.readLine()) != null) {
                Log.i("Read", line + "\n");
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteFile(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String flags = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_FLAGS));
                String[] columns = cursor.getColumnNames();
                for (String col : columns) {
                    Log.i("Delete", "Column Flags  " + col);
                }
                Log.i("Delete", "Delete Flags  " + flags);
                if (flags.contains("" + DocumentsContract.Document.FLAG_SUPPORTS_DELETE)) {
                    DocumentsContract.deleteDocument(getContentResolver(), uri);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }
}
