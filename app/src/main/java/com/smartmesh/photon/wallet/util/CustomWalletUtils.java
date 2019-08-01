package com.smartmesh.photon.wallet.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.util.ThreadPoolUtils;
import com.smartmesh.photon.wallet.web3j.Keys;
import com.smartmesh.photon.wallet.web3j.SecureRandomUtils;
import com.smartmesh.photon.wallet.web3j.Wallet;
import com.smartmesh.photon.wallet.web3j.WalletFile;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.web3j.crypto.Hash.sha256;
import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;
import static org.web3j.crypto.Keys.PRIVATE_KEY_LENGTH_IN_HEX;

/**
 * Utility functions for working with Wallet files.
 */
public class CustomWalletUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 创建全节点钱包
     * @param password 钱包密码
     * @param destinationDirectory 钱包存储地址
     * */
    public static String generateFullNewWalletFile(String password, File destinationDirectory)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, CipherException, IOException {

        return generateNewWalletFile(password, destinationDirectory, true);
    }

    /**
     * 创建轻节点钱包
     * @param password 钱包密码
     * @param destinationDirectory 钱包存储地址
     * */
    public static String generateLightNewWalletFile(String password, File destinationDirectory)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, CipherException, IOException {

        return generateNewWalletFile(password, destinationDirectory, false);
    }

    /**
     * 创建钱包
     * @param password 钱包密码
     * @param destinationDirectory 钱包存储地址
     * @param useFullScrypt 是否是全节点
     * */
    public static String generateNewWalletFile(
            String password, File destinationDirectory, boolean useFullScrypt)
            throws CipherException, IOException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        return generateWalletFile(password, ecKeyPair, destinationDirectory, useFullScrypt);
    }

    /**
     * 创建钱包
     * @param password 钱包密码
     * @param ecKeyPair secret key pairs
     * @param useFullScrypt 是否是全节点
     * @return the wallet address
     * */
    public static WalletFile generateWalletFile(String password, ECKeyPair ecKeyPair, boolean useFullScrypt)
            throws CipherException, IOException {

        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(password, ecKeyPair);
        } else {
            walletFile = Wallet.createLight(password, ecKeyPair);
        }
        return walletFile;
    }

    /**
     * 创建钱包
     * @param password 钱包密码
     * @param destinationDirectory 钱包存储地址
     * @param useFullScrypt 是否是全节点
     * */
    public static String generateWalletFile(String password, ECKeyPair ecKeyPair, File destinationDirectory, boolean useFullScrypt)
            throws CipherException, IOException {

        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(password, ecKeyPair);
        } else {
            walletFile = Wallet.createLight(password, ecKeyPair);
        }
        String fileName = getWalletFileName(walletFile);
        File destination = new File(destinationDirectory, fileName);
        objectMapper.writeValue(destination, walletFile);
        return fileName;
    }

    /**
     * Generates a BIP-39 compatible Ethereum wallet. The private key for the wallet can
     * be calculated using following algorithm:
     * <pre>
     *     Key = SHA-256(BIP_39_SEED(mnemonic, password))
     * </pre>
     *
     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param destinationDirectory The directory containing the wallet
     * @return A BIP-39 compatible Ethereum wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException if the destination cannot be written to
     */
    public static Bip39Wallet generateBip39Wallet(String password, File destinationDirectory)
            throws CipherException, IOException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));

        String walletFile = generateWalletFile(password, privateKey, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }

    public static Credentials loadCredentials(String password, String source)
            throws IOException, CipherException {
        return loadCredentials(password, new File(source));
    }

    public static Credentials loadCredentials(String password, File source)
            throws IOException, CipherException {
        WalletFile walletFile = objectMapper.readValue(source, WalletFile.class);
        return Credentials.create(Wallet.decrypt(password, walletFile));
    }

    public static Credentials loadBip39Credentials(String password, String mnemonic) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        return Credentials.create(ECKeyPair.create(sha256(seed)));
    }

    /**
     * 获取标准格式的钱包keystore文件名
     * */
    public static String getWalletFileName(WalletFile walletFile) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            return now.format(format) + walletFile.getAddress();
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.ssssssss'Z'");
            return "UTC--" + simpleDateFormat.format(new Date()) + "--" + walletFile.getAddress();
        }
    }

    /**
     * 获取固定前缀的钱包keystore文件名
     * 防止keystore文件重复，导致光子无法启动
     * */
    public static String getDefaultWalletFileName(WalletFile walletFile) {
        return KeyStoreFileUtils.keystorePath + walletFile.getAddress();
    }

    public static String getDefaultKeyDirectory() {
        return getDefaultKeyDirectory(System.getProperty("os.name"));
    }

    static String getDefaultKeyDirectory(String osName1) {
        String osName = osName1.toLowerCase();

        if (osName.startsWith("mac")) {
            return String.format("%s%sLibrary%sEthereum", System.getProperty("user.home"), File.separator, File.separator);
        } else if (osName.startsWith("win")) {
            return String.format("%s%sEthereum", System.getenv("APPDATA"), File.separator);
        } else {
            return String.format("%s%s.ethereum", System.getProperty("user.home"), File.separator);
        }
    }

    public static String getTestnetKeyDirectory() {
        return String.format("%s%stestnet%skeystore", getDefaultKeyDirectory(), File.separator, File.separator);
    }

    public static String getMainnetKeyDirectory() {
        return String.format("%s%skeystore", getDefaultKeyDirectory(), File.separator);
    }

    /**
     * Get keystore destination directory for a Rinkeby network.
     * @return a String containing destination directory
     */
    public static String getRinkebyKeyDirectory() {
        return String.format("%s%srinkeby%skeystore", getDefaultKeyDirectory(), File.separator, File.separator);
    }

    public static boolean isValidPrivateKey(String privateKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey);
        return cleanPrivateKey.length() == PRIVATE_KEY_LENGTH_IN_HEX;
    }

    public static boolean isValidAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);
        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }
        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }

    /**
     * 获取钱包地址
     * */
    public static String getWalletAddress(WalletFile walletFile) {
        if(TextUtils.isEmpty(walletFile.getAddress())){
            return  null;
        }else{
            String address = Keys.toChecksumAddress(walletFile.getAddress());
            if(!address.startsWith("0x")){
                address = "0x" + address;
            }
            return address;
        }
    }

    /**
     * verify wallet password
     * @param walletPwd wallet password
     * */
    public static void verifyWalletPwd(Context context,final String walletPwd){
        if (TextUtils.isEmpty(walletPwd)){
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_VERIFY_PWD_ERROR);
            EventBus.getDefault().post(messageEvent);
        }
        ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
            try {
                String walletAddress = WalletInfoUtils.getInstance().getSelectAddress();
                if (!TextUtils.isEmpty(walletAddress) && !walletAddress.contains("0x")){
                    walletAddress = "0x" + walletAddress;
                }
                WalletStorage.getInstance(PhotonApplication.mContext).getFullWallet(context,walletPwd,walletAddress);
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_VERIFY_SUCCESS);
                messageEvent.setMessage(walletPwd);
                EventBus.getDefault().post(messageEvent);
            } catch (IOException e) {
                e.printStackTrace();
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_VERIFY_OTHER_ERROR);
                EventBus.getDefault().post(messageEvent);
            } catch (JSONException e) {
                e.printStackTrace();
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_VERIFY_OTHER_ERROR);
                EventBus.getDefault().post(messageEvent);
            } catch (CipherException e) {
                e.printStackTrace();
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_VERIFY_PWD_ERROR);
                EventBus.getDefault().post(messageEvent);
            }catch (Exception e) {
                e.printStackTrace();
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_VERIFY_OTHER_ERROR);
                EventBus.getDefault().post(messageEvent);
            }
        });
    }

}
