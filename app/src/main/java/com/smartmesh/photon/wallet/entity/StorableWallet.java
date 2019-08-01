package com.smartmesh.photon.wallet.entity;



import java.io.Serializable;


public class StorableWallet implements Serializable{

    private String localId;

   //The wallet address 、public key
    private String publicKey;

    //Name of the wallet
    private String walletName;
    //Password prompt information
    private String pwdInfo;

    //The currently selected purse
    private boolean isSelect;

    //0 can't export, 1 can be derived
    private int canExportPrivateKey;

    //is backup true or false
    private boolean isBackup;

    //The currently selected purse
    private String walletImageId;


    //The etheric fang balance
    private double ethBalance;

    //SMT balance
    private double fftBalance;

    //SMT balance
    private double meshBalance;

    // 0 the default wallet 1 observe the purse
    private int walletType;

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getWalletImageId() {
        return walletImageId;
    }

    public void setWalletImageId(String walletImageId) {
        this.walletImageId = walletImageId;
    }

    public boolean isBackup() {
        return isBackup;
    }

    public void setBackup(boolean backup) {
        isBackup = backup;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getCanExportPrivateKey() {
        return canExportPrivateKey;
    }

    public void setCanExportPrivateKey(int canExportPrivateKey) {
        this.canExportPrivateKey = canExportPrivateKey;
    }

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }


    public double getEthBalance() {
        return ethBalance;
    }

    public void setEthBalance(double ethBalance) {
        this.ethBalance = ethBalance;
    }

    public double getFftBalance() {
        return fftBalance;
    }

    public void setFftBalance(double fftBalance) {
        this.fftBalance = fftBalance;
    }

    public double getMeshBalance() {
        return meshBalance;
    }

    public void setMeshBalance(double meshBalance) {
        this.meshBalance = meshBalance;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPwdInfo() {
        return pwdInfo;
    }

    public void setPwdInfo(String pwdInfo) {
        this.pwdInfo = pwdInfo;
    }


    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

}
