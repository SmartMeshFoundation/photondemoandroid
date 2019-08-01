package com.smartmesh.photon.wallet;

import android.content.Context;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.util.SDCardCtrl;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.util.CustomWalletUtils;
import com.smartmesh.photon.wallet.util.WalletInfoUtils;
import com.smartmesh.photon.wallet.util.WalletStorage;
import com.smartmesh.photon.wallet.web3j.Wallet;
import com.smartmesh.photon.wallet.web3j.WalletFile;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.greenrobot.eventbus.EventBus;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * Create or import the wallet
 */
public class WalletThread extends Thread {

    private String walletName;//Name of the wallet
    private String password;//The wallet password
    private String pwdInfo;//Password prompt information
    private String source;//Type 1 is a privatekey 2 is a keyStore
    private int type;//Type 0 create wallets, 1 private key import wallet, 2 keyStore into the purse  3 mnemonic into wallet
    private Context context;
    private boolean isNoPrivateHex;

    private String mnemonic;

    private String createMnemonic;
    
    public WalletThread(Context context, String walletName, String password, String pwdInfo, String source, int type, boolean isNoPrivateHex) {
        this.context = context;
        this.walletName = walletName;
        this.password = password;
        this.pwdInfo = pwdInfo;
        this.source = source;
        this.type = type;
        this.isNoPrivateHex = isNoPrivateHex;
    }

    public void setMnemonic(String tempMnemonic){
        this.mnemonic = tempMnemonic;
    }

    
    @Override
    public void run() {
        try {
            String walletAddress;
            if (type == 0) { // Generate a new address 0 x...
                walletAddress = createWallet();
            } else if (type == 1) { // By private key into the new address
                walletAddress = importPrivateKey();
            } else if (type == 2){//Through the keyStore import new address
                walletAddress = importKeyStore();
            }else{
                if (TextUtils.isEmpty(mnemonic)){
                    return;
                }
                long creationTimeSeconds = System.currentTimeMillis() / 1000;
                DeterministicSeed seed = new DeterministicSeed(Arrays.asList(mnemonic.split(" ")), null, "", creationTimeSeconds);
                DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
                List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
                DeterministicKey key = chain.getKeyByPath(keyPath, true);
                BigInteger tempPrivateKey = key.getPrivKey();
                source = Numeric.toHexStringNoPrefixZeroPadded(tempPrivateKey,64);
                walletAddress = importPrivateKey();
            }
            if (TextUtils.isEmpty(walletAddress)){
                return;
            }
            saveWallet(walletAddress);
            return;
        } catch (CipherException e) {
            e.printStackTrace();
            //password error
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_CREATE_PWD_ERROR);
            EventBus.getDefault().post(messageEvent);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_CREATE_OTHER_ERROR);
            EventBus.getDefault().post(messageEvent);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_CREATE_OTHER_ERROR);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * create wallet    create mnemonic
     * */
    private String createWallet() throws Exception{
        SecureRandom secureRandom = new SecureRandom();
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed deterministicSeed = new DeterministicSeed(secureRandom, 128, "", creationTimeSeconds);
        List<String> mnemonicCode = deterministicSeed.getMnemonicCode();
        if (mnemonicCode != null) {
            createMnemonic = convertMnemonicList(mnemonicCode);
        }
        DeterministicSeed seed = new DeterministicSeed(Arrays.asList(createMnemonic.split(" ")), null, "", creationTimeSeconds);
        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
        List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
        DeterministicKey key = chain.getKeyByPath(keyPath, true);
        BigInteger tempPrivateKey = key.getPrivKey();
        ECKeyPair keys = ECKeyPair.create(tempPrivateKey);
        WalletFile walletFile = CustomWalletUtils.generateWalletFile(password, keys, false);
        String walletAddress = writeValue(walletFile,false);
        if (TextUtils.isEmpty(walletAddress)){
            return null;
        }else{
            return walletAddress;
        }
    }

    /**
     * import private key
     * */
    private String importPrivateKey() throws Exception{
        ECKeyPair keys;
        if (isNoPrivateHex) {
            keys = ECKeyPair.create(new BigInteger(source));
        } else {
            keys = ECKeyPair.create(new BigInteger(source, 16));
        }
        WalletFile walletFile = CustomWalletUtils.generateWalletFile(password, keys, false);
        String walletAddress = writeValue(walletFile,true);
        if (TextUtils.isEmpty(walletAddress)){
            return null;
        }else{
            return walletAddress;
        }
    }

    /**
     * import key store
     * */
    private String importKeyStore() throws Exception{
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        WalletFile walletFile = objectMapper.readValue(source, WalletFile.class);
        Credentials credentials = Credentials.create(Wallet.decrypt(password, walletFile));
        credentials.getEcKeyPair().getPublicKey();
        walletFile = CustomWalletUtils.generateWalletFile(password, credentials.getEcKeyPair(), false);
        String walletAddress = writeValue(walletFile,true);
        if (TextUtils.isEmpty(walletAddress)){
            return null;
        }else{
            return walletAddress;
        }
    }

    /**
     * check wallet is exist
     * return wallet address
     * */
    private String writeValue(WalletFile walletFile, boolean needCheckExists) throws Exception{
        String walletAddress = CustomWalletUtils.getWalletAddress(walletFile);
        if (TextUtils.isEmpty(walletAddress)){
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_CREATE_OTHER_ERROR);
            EventBus.getDefault().post(messageEvent);
            return null;
        }
        if (needCheckExists){
            boolean exists = WalletStorage.getInstance(context).checkExists(walletAddress);
            if (exists) {
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_CREATE_REPEAT_ERROR);
                EventBus.getDefault().post(messageEvent);
                return null;
            }
        }
        File destination = new File(new File(context.getFilesDir(), SDCardCtrl.WALLET_PATH),CustomWalletUtils.getDefaultWalletFileName(walletFile));
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        objectMapper.writeValue(destination, walletFile);
        return walletAddress;
    }

    /**
     * save wallet info
     * @param walletAddress   wallet address
     * */
    private void saveWallet(String walletAddress){
        if (TextUtils.isEmpty(walletName)) {//When import operation
            walletName = WalletInfoUtils.getWalletName(context);
        }
        StorableWallet storableWallet = new StorableWallet();
        storableWallet.setPublicKey(walletAddress);
        storableWallet.setWalletName(walletName);
        storableWallet.setPwdInfo(pwdInfo);
        if (type == 0) {
            storableWallet.setCanExportPrivateKey(1);
        } else {
            storableWallet.setBackup(true);
        }
        if (WalletStorage.getInstance(context).get().size() <= 0) {
            storableWallet.setSelect(true);
        }
        WalletStorage.getInstance(context).add(storableWallet, context);
        if (WalletStorage.getInstance(context).get().size() > 0) {
            WalletStorage.getInstance(PhotonApplication.mContext).updateMapDb(storableWallet.getPublicKey());
            WalletStorage.getInstance(PhotonApplication.mContext).updateWalletToList(PhotonApplication.mContext, storableWallet.getPublicKey(), false);
        }


        if (!TextUtils.isEmpty(createMnemonic)){
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_CREATE_SUCCESS);
            messageEvent.setMessage(walletAddress);
            messageEvent.setMessage2(createMnemonic);
            EventBus.getDefault().post(messageEvent);
            createMnemonic = "";
        }else{
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_CREATE_SUCCESS);
            EventBus.getDefault().post(messageEvent);
        }
    }

    /**
     * covert mnemonic string to List<String>
     */
    private static String convertMnemonicList(List<String> mnemonics) {
        StringBuilder sb = new StringBuilder();
        for (String mnemonic : mnemonics) {
            sb.append(mnemonic);
            sb.append(" ");
        }
        return sb.toString();
    }
}
