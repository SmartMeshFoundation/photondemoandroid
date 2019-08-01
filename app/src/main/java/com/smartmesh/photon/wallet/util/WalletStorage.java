package com.smartmesh.photon.wallet.util;

import android.content.Context;
import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.util.MySharedPrefs;
import com.smartmesh.photon.util.SDCardCtrl;
import com.smartmesh.photon.wallet.entity.StorableWallet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WalletStorage {
    
    private ArrayList<StorableWallet> mapdb;
    private static WalletStorage instance;
    private String walletToExport; // Used as temp if users wants to export but still needs to grant write permission


    public static WalletStorage getInstance(Context context) {
        if (instance == null) {
            synchronized (WalletStorage.class) {
                if (instance == null) {
                    instance = new WalletStorage(context);
                }
            }
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }
    
    private WalletStorage(Context context) {
        try {
            mapdb = new ArrayList<>();
            load(context);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
    
    public synchronized boolean add(StorableWallet storableWallet, Context context) {
        for (int i = 0; i < mapdb.size(); i++) {

            String address = mapdb.get(i).getPublicKey();
            String tempAddress = storableWallet.getPublicKey();

            if (address.startsWith("0x")){
                address = "0x" + address;
            }

            if (tempAddress.startsWith("0x")){
                tempAddress = "0x" + tempAddress;
            }

            if (TextUtils.equals(address,tempAddress)) {
                return true;
            }
        }
        mapdb.add(storableWallet);
        addWalletToList(context, storableWallet);
        return true;
    }
    
    /**
     * update wallet list
     *
     * @param context        context
     * @param storableWallet wallet
     */
    public synchronized boolean addWalletList(StorableWallet storableWallet, Context context) {
        if (mapdb != null) {
            mapdb.clear();
        } else {
            mapdb = new ArrayList<>();
        }
        try {
            load(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        boolean hasWallet = false;
        for (int i = 0; i < mapdb.size(); i++) {

            String address = mapdb.get(i).getPublicKey();
            String tempAddress = storableWallet.getPublicKey();

            if (address.startsWith("0x")){
                address = "0x" + address;
            }

            if (tempAddress.startsWith("0x")){
                tempAddress = "0x" + tempAddress;
            }


            if (TextUtils.equals(address,tempAddress)) {
                mapdb.get(i).setSelect(true);
                hasWallet = true;
            } else {
                mapdb.get(i).setSelect(false);
            }
        }
        if (hasWallet) {
            return true;
        }
        storableWallet.setSelect(true);
        mapdb.add(storableWallet);
        addWalletToList(context, storableWallet);
        return true;
    }
    
    /**
     * update wallet list
     *
     * @param publicKey wallet
     */
    public synchronized void updateMapDb(String publicKey) {
        if (mapdb == null) {
            return;
        }
        for (int i = 0; i < mapdb.size(); i++) {
            if (!publicKey.startsWith("0x")){
                publicKey = "0x" + publicKey;
            }

            String tempAddress = mapdb.get(i).getPublicKey();
            if (!tempAddress.startsWith("0x")){
                tempAddress = "0x" + tempAddress;
            }
            if (TextUtils.equals(tempAddress, publicKey)) {
                mapdb.get(i).setSelect(true);
            } else {
                mapdb.get(i).setSelect(false);
            }
        }
    }
    
    /**
     * check wallet exists
     *
     * @param address address
     */
    public synchronized boolean checkExists(String address) {
        for (int i = 0; i < mapdb.size(); i++) {

            String tempAddress = mapdb.get(i).getPublicKey();
            if (!tempAddress.startsWith("0x")){
                tempAddress = "0x" + tempAddress;
            }

            if (!address.startsWith("0x")){
                address = "0x" + address;
            }

            if (tempAddress.equalsIgnoreCase(address)) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized ArrayList<StorableWallet> get() {
        return mapdb;
    }
    
    
    /**
     * increase the purse lists
     *
     * @ param storableWallet wallet
     */
    public void addWalletToList(Context context, StorableWallet storableWallet) {
        String walletList = MySharedPrefs.readWalletList(context);
        JSONObject json = updateJsonArray(walletList,storableWallet,false);
        if (json == null){
            return;
        }
        MySharedPrefs.write(context, MySharedPrefs.FILE_WALLET, MySharedPrefs.KEY_WALLET, json.toString());
        addWalletAllToList(context, storableWallet);
    }
    
    /**
     * increase the purse lists
     *
     * @ param storableWallet wallet
     */
    public void addWalletAllToList(Context context, StorableWallet storableWallet) {
        String walletList = MySharedPrefs.readWalletList(context);
        JSONObject json = updateJsonArray(walletList,storableWallet,true);
        if (json == null){
            return;
        }
        MySharedPrefs.write(context, MySharedPrefs.FILE_WALLET, MySharedPrefs.KEY_WALLET, json.toString());
    }
    

    /**
     * update purse list whether can export the private key
     * update purse list whether backup
     *
     * @param isCopy true update back  false uodate select
     * @ param address wallet address
     */
    public synchronized void updateWalletToList(Context context, String address, boolean isCopy) {
        String walletList = MySharedPrefs.readString(context, MySharedPrefs.FILE_WALLET, MySharedPrefs.KEY_WALLET);
        JSONObject json = copyJsonArray(walletList,address,isCopy,true);
        MySharedPrefs.write(context, MySharedPrefs.FILE_WALLET, MySharedPrefs.KEY_WALLET, json.toString());
    }
    
    
    /**
     * to delete the wallet
     *
     * @ param address wallet address
     * @ param type 0 ordinary purse 1 to delete the wallet
     */
    public void removeWallet(String address, int type, Context context) {
        int position = -1;
        for (int i = 0; i < mapdb.size(); i++) {

            String tempAddress = mapdb.get(i).getPublicKey();
            if (!tempAddress.startsWith("0x")){
                tempAddress = "0x" + tempAddress;
            }

            if (!address.startsWith("0x")){
                address = "0x" + address;
            }

            if (tempAddress.equalsIgnoreCase(address)) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            if (type == 0) {
                ArrayList<String> keyStorePaths = KeyStoreFileUtils.getKeyStoreFileNameArray(address);
                if (keyStorePaths != null && keyStorePaths.size() > 0){
                    for (int i = 0 ; i < keyStorePaths.size() ; i++){
                        boolean deleteResult = new File(context.getFilesDir(), SDCardCtrl.WALLET_PATH + "/" + keyStorePaths.get(i)).delete();
                        if (!deleteResult){
                            new File(context.getFilesDir(), SDCardCtrl.WALLET_PATH + "/" + address.substring(2)).delete();
                        }
                    }
                }
            }
            delWalletList(context, address);
            mapdb.remove(position);
        }
    }
    
    /**
     * delete wallet
     *
     * @param context       context
     * @param walletAddress wallet address
     */
    public void delWalletList(Context context, String walletAddress) {
        String walletList = MySharedPrefs.readString(context, MySharedPrefs.FILE_WALLET, MySharedPrefs.KEY_WALLET);
        JSONObject json = deleteJsonArray(walletList,walletAddress);
        MySharedPrefs.write(context, MySharedPrefs.FILE_WALLET, MySharedPrefs.KEY_WALLET, json.toString());
    }
    
    public static String stripWalletName(String s) {
        if (s.lastIndexOf("--") > 0) s = s.substring(s.lastIndexOf("--") + 2);
        if (s.endsWith(".json")) s = s.substring(0, s.indexOf(".json"));
        return s;
    }
    
    
    /**
     * access to the private key
     *
     * @ param password purse password
     * @ param walletAddress wallet address public key
     */
    public Credentials getFullWallet(Context context, String password, String walletAddress) throws IOException, JSONException, CipherException {
        if (walletAddress.startsWith("0x")){
            walletAddress = walletAddress.substring(2);
        }
        return CustomWalletUtils.loadCredentials(password, new File(context.getFilesDir(), SDCardCtrl.WALLET_PATH + "/" +  KeyStoreFileUtils.getKeyStoreFileName(walletAddress)));
    }
    
    /**
     * get the KeyStore
     *
     * @ param password   purse password
     * @ param walletAddress     wallet address public key
     */
    public String getWalletKeyStore(Context context, String password, String walletAddress) throws IOException, JSONException, CipherException {
        if (walletAddress.startsWith("0x")) {
            walletAddress = walletAddress.substring(2);
        };
        CustomWalletUtils.loadCredentials(password, new File(context.getFilesDir(), SDCardCtrl.WALLET_PATH + "/" + KeyStoreFileUtils.getKeyStoreFileName(walletAddress)));
        File file = new File(context.getFilesDir(), SDCardCtrl.WALLET_PATH + "/" + KeyStoreFileUtils.getKeyStoreFileName(walletAddress));
        InputStream in = new FileInputStream(file);
        int flen = (int) file.length();
        byte[] strBuffer = new byte[flen];
        in.read(strBuffer, 0, flen);
        return new String(strBuffer);
    }
    
    /**
     * Read in json
     */
    @SuppressWarnings("unchecked")
    public synchronized void load(Context context) throws IOException, ClassNotFoundException {
        String walletList = MySharedPrefs.readWalletList(context);
        if (TextUtils.isEmpty(walletList)) {
            return;
        }
        try {
            JSONObject object = new JSONObject(walletList);
            JSONArray walletArray = object.optJSONArray("data");
            int hasSelect = 0;
            int index = 0;
            for (int i = 0; i < walletArray.length(); i++) {
                JSONObject walletObj = walletArray.optJSONObject(i);
                if (walletObj.optBoolean(WalletConstants.WALLET_SELECT)) {
                    hasSelect++;
                    if (index == 0) {
                        index = i;
                    }
                }
            }
            
            for (int i = 0; i < walletArray.length(); i++) {
                JSONObject walletObj = walletArray.optJSONObject(i);
                StorableWallet storableWallet = loadWallet(walletObj,i,hasSelect,index,context);
                mapdb.add(storableWallet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read in json
     *
     * @param context context
     */
    @SuppressWarnings("unchecked")
    public synchronized void reLoad(Context context) throws IOException, ClassNotFoundException {
        String walletList = MySharedPrefs.readWalletList(context);
        if (TextUtils.isEmpty(walletList)) {
            return;
        }
        if (mapdb != null) {
            mapdb.clear();
        } else {
            mapdb = new ArrayList<>();
        }
        try {
            JSONObject object = new JSONObject(walletList);
            JSONArray walletArray = object.optJSONArray("data");
            addWalletArray(context, walletArray, false, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Read in json
     *
     * @param context context
     */
    @SuppressWarnings("unchecked")
    public synchronized void reLoadUserWallet(Context context) throws IOException, ClassNotFoundException {
        String walletList = MySharedPrefs.readWalletList(context);
        if (mapdb != null) {
            mapdb.clear();
        } else {
            mapdb = new ArrayList<>();
        }
        if (TextUtils.isEmpty(walletList)) {
            return;
        }
        try {
            JSONObject object = new JSONObject(walletList);
            JSONArray walletArray = object.optJSONArray("data");
            addWalletArray(context, walletArray, false, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * load wallet list
     *
     * @param context     context
     * @param walletArray wallet json array
     */
    private void addWalletArray(Context context, JSONArray walletArray, boolean needAdd, boolean reSetSelect) {
        if (walletArray == null) {
            return;
        }
        int hasSelect = 0;
        int index = 0;
        for (int i = 0; i < walletArray.length(); i++) {
            JSONObject walletObj = walletArray.optJSONObject(i);
            if (walletObj.optBoolean(WalletConstants.WALLET_SELECT)) {
                hasSelect++;
                if (index == 0) {
                    index = i;
                }
            }
        }
        
        for (int i = 0; i < walletArray.length(); i++) {
            JSONObject walletObj = walletArray.optJSONObject(i);
            StorableWallet storableWallet = loadWallet(walletObj,i,hasSelect,index,context);
            if (needAdd) {
                addWalletToList(PhotonApplication.mContext, storableWallet);
            }
            mapdb.add(storableWallet);
        }
    }

    /**
     * update wallet json
     * @param walletList json string
     * @param checkEquals check is equals
     * @param storableWallet wallet bean
     * */
    private JSONObject updateJsonArray(String walletList,StorableWallet storableWallet,boolean checkEquals){
        JSONObject json = null;
        try {
            if (TextUtils.isEmpty(walletList)) {
                json = new JSONObject();
            } else {
                json = new JSONObject(walletList);
            }

            JSONArray array = json.optJSONArray("data");
            if (array == null) {
                array = new JSONArray();
            }
            if (checkEquals){
                for (int i = 0; i < array.length(); i++) {
                    String address = array.optJSONObject(i).optString(WalletConstants.WALLET_ADDRESS);
                    if (!address.startsWith("0x")){
                        address = "0x" + address;
                    }

                    String tempAddress = storableWallet.getPublicKey();
                    if (!tempAddress.startsWith("0x")){
                        tempAddress = "0x" + tempAddress;
                    }
                    if (TextUtils.equals(address, tempAddress)) {
                        return null;
                    }
                }
            }
            JSONObject newWallet = new JSONObject();
            newWallet.put(WalletConstants.WALLET_NAME, storableWallet.getWalletName());
            newWallet.put(WalletConstants.WALLET_ADDRESS, storableWallet.getPublicKey());
            newWallet.put(WalletConstants.WALLET_EXTRA, storableWallet.getCanExportPrivateKey());
            newWallet.put(WalletConstants.WALLET_BACKUP, storableWallet.isBackup());
            newWallet.put(WalletConstants.WALLET_SELECT, storableWallet.isSelect());
            newWallet.put(WalletConstants.WALLET_PWD_INFO, storableWallet.getPwdInfo());
            array.put(newWallet);
            json.put("data", array);
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    /**
     * update wallet json
     * @param walletList json string
     * @param address wallet address
     * @param isCopy is copy
     * @param updateSelect is update select
     * */
    private JSONObject copyJsonArray(String walletList,String address,boolean isCopy,boolean updateSelect){
        JSONObject json = null;
        try {
            if (TextUtils.isEmpty(walletList)) {
                json = new JSONObject();
            } else {
                json = new JSONObject(walletList);
            }
            JSONArray array = json.optJSONArray("data");
            if (array == null) {
                array = new JSONArray();
            }
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject newWallet = new JSONObject();
                newWallet.put(WalletConstants.WALLET_ADDRESS, array.optJSONObject(i).optString(WalletConstants.WALLET_ADDRESS));
                newWallet.put(WalletConstants.WALLET_NAME, array.optJSONObject(i).optString(WalletConstants.WALLET_NAME));
                newWallet.put(WalletConstants.WALLET_SELECT, array.optJSONObject(i).optBoolean(WalletConstants.WALLET_SELECT));
                newWallet.put(WalletConstants.WALLET_EXTRA, array.optJSONObject(i).optString(WalletConstants.WALLET_EXTRA));
                newWallet.put(WalletConstants.WALLET_BACKUP, array.optJSONObject(i).optBoolean(WalletConstants.WALLET_BACKUP));
                newWallet.put(WalletConstants.WALLET_PWD_INFO, array.optJSONObject(i).optString(WalletConstants.WALLET_PWD_INFO));
                if (updateSelect){

                    if (!address.startsWith("0x")){
                        address = "0x" + address;
                    }

                    String tempAddress = array.optJSONObject(i).optString(WalletConstants.WALLET_ADDRESS);
                    if (!tempAddress.startsWith("0x")){
                        tempAddress = "0x" + tempAddress;
                    }

                    if (isCopy) {
                        if (TextUtils.equals(address,tempAddress)) {
                            newWallet.put(WalletConstants.WALLET_EXTRA, 0);
                            newWallet.put(WalletConstants.WALLET_BACKUP, true);
                        }
                    } else {
                        if (TextUtils.equals(address,tempAddress)) {
                            newWallet.put(WalletConstants.WALLET_SELECT, true);
                        } else {
                            newWallet.put(WalletConstants.WALLET_SELECT, false);
                        }
                    }
                }
                newArray.put(newWallet);
            }
            json.put("data", newArray);
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    /**
     * update wallet json
     * @param walletList json string
     * @param walletAddress wallet address
     * */
    private JSONObject deleteJsonArray(String walletList,String walletAddress){
        JSONObject json = null;
        try {
            if (TextUtils.isEmpty(walletList)) {
                json = new JSONObject();
            } else {
                json = new JSONObject(walletList);
            }
            JSONArray array = json.optJSONArray("data");
            if (array == null) {
                array = new JSONArray();
            }
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < array.length(); i++) {

                if (!walletAddress.startsWith("0x")){
                    walletAddress = "0x" + walletAddress;
                }

                String tempAddress = array.optJSONObject(i).optString(WalletConstants.WALLET_ADDRESS);
                if (!tempAddress.startsWith("0x")){
                    tempAddress = "0x" + tempAddress;
                }

                if (!TextUtils.equals(walletAddress,tempAddress)) {
                    JSONObject newWallet = new JSONObject();
                    newWallet.put(WalletConstants.WALLET_ADDRESS, array.optJSONObject(i).optString(WalletConstants.WALLET_ADDRESS));
                    newWallet.put(WalletConstants.WALLET_NAME, array.optJSONObject(i).optString(WalletConstants.WALLET_NAME));
                    newWallet.put(WalletConstants.WALLET_SELECT, array.optJSONObject(i).optBoolean(WalletConstants.WALLET_SELECT));
                    newWallet.put(WalletConstants.WALLET_EXTRA, array.optJSONObject(i).optString(WalletConstants.WALLET_EXTRA));
                    newWallet.put(WalletConstants.WALLET_BACKUP, array.optJSONObject(i).optBoolean(WalletConstants.WALLET_BACKUP));
                    newWallet.put(WalletConstants.WALLET_PWD_INFO, array.optJSONObject(i).optString(WalletConstants.WALLET_PWD_INFO));
                    newArray.put(newWallet);
                }
            }
            json.put("data", newArray);
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }


    /**
     * load wallet bean
     * @param walletObj json
     * @param i wallet int array position
     * @param hasSelect is select
     * @param index current wallet select
     * */
    private StorableWallet loadWallet(JSONObject walletObj,int i,int hasSelect,int index,Context context){
        StorableWallet storableWallet = new StorableWallet();
        storableWallet.setPublicKey(walletObj.optString(WalletConstants.WALLET_ADDRESS));
        storableWallet.setWalletName(walletObj.optString(WalletConstants.WALLET_NAME));
        storableWallet.setCanExportPrivateKey(walletObj.optInt(WalletConstants.WALLET_EXTRA));
        storableWallet.setBackup(walletObj.optBoolean(WalletConstants.WALLET_BACKUP));
        storableWallet.setPwdInfo(walletObj.optString(WalletConstants.WALLET_PWD_INFO));
        if (hasSelect == 1) {
            storableWallet.setSelect(walletObj.optBoolean(WalletConstants.WALLET_SELECT));
        } else {
            if (i == index) {
                storableWallet.setSelect(true);
            } else {
                storableWallet.setSelect(false);
            }
        }
        File destination = new File(new File(context.getFilesDir(), SDCardCtrl.WALLET_PATH), KeyStoreFileUtils.getKeyStoreFileName(storableWallet.getPublicKey()));
        if (!destination.exists()) {
            storableWallet.setWalletType(1);
        }
        return storableWallet;
    }
}
