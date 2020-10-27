package com.example.whatsapp.message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.room.OnConflictStrategy;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.whatsapp.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class Message {

    public String content ;
    public String  Date ;
    public String MessageWriter;


    public Message(String content, String MessageWriter, String Date) {
        this.content = content;
        this.MessageWriter = MessageWriter;
        this.Date = Date;
    }

    public Message(){}



    public static boolean create_message_table(String conversationNumber) {

        SupportSQLiteDatabase sdb = MainActivity.database.getOpenHelper().getWritableDatabase();
        try {
            sdb.execSQL("CREATE TABLE IF NOT EXISTS " + "T" + conversationNumber  + " (id  INTEGER  PRIMARY KEY AUTOINCREMENT, content   TEXT ,MessageWriter TEXT, Time TEXT)");
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    public static void Insert_message_to_table(String conversationNumber, Message message) {
        SupportSQLiteDatabase sdb = MainActivity.database.getOpenHelper().getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("content",message.content);
        cv.put("MessageWriter",message.MessageWriter);
        cv.put("Time",message.Date);

        try{ sdb.insert("T" + conversationNumber, OnConflictStrategy.IGNORE,cv);}
        catch (Exception e) {}
    }

    public static List<Message> get_all_messages(String conversationNumber) {
        SupportSQLiteDatabase sdb = MainActivity.database.getOpenHelper().getWritableDatabase();
        Cursor cursor = sdb.query("SELECT * FROM " + "T"+conversationNumber,null);
        List<Message> messages = new ArrayList<Message>();
        if (cursor.moveToFirst()){
            do{
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String MessageWriter = cursor.getString(cursor.getColumnIndex("MessageWriter"));
                String Time = cursor.getString(cursor.getColumnIndex("Time"));
                Log.d("content",content);
                messages.add(new Message(content,MessageWriter,Time));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

}
