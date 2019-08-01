package com.smartmesh.photon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySharedPrefs {

    /*List stored purse*/
    public static final String FILE_WALLET = "walletlist";

    /*Global information is stored*/
    public static final String FILE_APPLICATION = "application";

    /**
     * Language is the key value
     * */
    public static final String KEY_SAVE_LANGUAGE = "key_save_language";


    /*The wallet key values*/
    public static final String KEY_WALLET = "key_wallet";


    /*Storing user information*/
    public static final String FILE_USER = "userinfo";

    /*photon channel note list*/
    public static final String KEY_PHOTON_CHANNEL_NOTE_LIST = "photon_channel_note_list";


    /**
     * 通道链上余额
     * */
    public static final String KEY_PHOTON_ON_CHAIN_BALANCE = "photon_on_chain_balance";

    /**
     * 通道内总余额
     * */
    public static final String KEY_PHOTON_IN_PHOTON_BALANCE = "photon_in_photon_balance";


    /**
     * read the wallet list
     * @ param context context
     * */
    public static String readWalletList(Context context){
        return readString(context,FILE_WALLET,KEY_WALLET);
    }

    /**
     * read the String
     * @ param fileName file name
     * @ param key key values
     * */
    public static String readString(Context context, String fileName, String key) {
        if (context == null) return "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    public static void write(Context context, String fileName, String key, String value) {
        if (context == null)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void writeLong(Context context, String fileName, final String key, final long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * write a Boolean switch value
     * @ param fileName file name
     * @ param key key values
     * @ param value content
     */
    public static void writeBoolean(Context context, String fileName, String key, boolean value) {
        if (context == null)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * write int value
     * @ param fileName file name
     * @ param key key values
     * @ param value content
     */
    public static void writeInt(Context context, String fileName, final String key, final int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    /**
     * read the String
     * @ param fileName file name
     * @ param key key values
     * */
    public static long readLong(Context context, String fileName, String key) {
        if (context == null) return 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        long value = sharedPreferences.getLong(key,0);
        return value;
    }

    /**
     * read an int value 1 by default
     * @ param fileName file name
     * @ param key key values
     */
    public static int readInt1(Context context, String fileName, String key) {
        if (context == null) return -1;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }

    /**
     * read an int value zero by default
     * @ param fileName file name
     * @ param key key values
     */
    public static int readInt(Context context, String fileName, String key) {
        if (context == null) return 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * read an int value zero by default
     * @ param fileName file name
     * @ param key key values
     */
    public static int readIntDefaultUsd(Context context, String fileName, String key) {
        if (context == null) return 1;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 1);
    }
    /**
     * read the Boolean value of true by default
     * @ param fileName file name
     * @ param key key values
     */
    @Deprecated
    public static boolean readBoolean(Context context, String fileName, String key) {
        if (context == null) return true;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    /**
     * read Boolean false by default
     * @ param fileName file name
     * @ param key key values
     */
    public static boolean readBooleanNormal(Context context, String fileName, String key) {
        if (context == null) return false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * delete key value
     */
    public static void removeCache(Context c, String fileName, String key) {
        if (c == null) return;
        SharedPreferences sharedPreferences = c.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * Delete the key value
     */
    public static void remove(Context c, String key) {
        if (c == null) return;
        SharedPreferences sharedPreferences = c.getSharedPreferences(MySharedPrefs.FILE_USER, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

}
