package com.smartmesh.photon.custom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.util.ImageUtil;
import com.smartmesh.photon.wallet.util.CustomWalletUtils;
import com.smartmesh.photon.wallet.util.WalletStorage;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class CustomCaptureActivity extends BaseActivity {

    /**
     * 0.默认扫描
     * 1.扫描钱包地址
     * 2.扫描转账联系人地址
     * 3.群组二维码
     * 4.绑定挖矿
     * */
    private  int type;

    @BindView(R.id.app_btn_right)
    TextView titleRightView;
    @BindView(R.id.qr_code_light_tv)
    TextView qrCodeLightTv;

    //是否打开了手电筒
    private boolean onFlashlight;

    @Override
    public int getLayoutId() {
        return R.layout.custom_capture_layout;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        titleRightView.setVisibility(View.VISIBLE);
        titleRightView.setTextColor(getResources().getColor(R.color.color_e2edea));
        titleRightView.setText(getString(R.string.album));
        type = getIntent().getIntExtra("type",0);
        setBottomTitle(getString(R.string.qm_qm));
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.custom_capture_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            if (type != 4){
                parseAnalyze(result);
            }else{
                parseMinerAnalyze(result);
            }
        }

        @Override
        public void onAnalyzeFailed() {
            parseAnalyze("");
        }
    };

    @OnClick({R.id.app_btn_right,R.id.qr_code_light})
    public void onClickView(View view){
        switch (view.getId()){
            case R.id.app_btn_right:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, RequestCodeUtils.PHOTON_CREATE_IMAGE_CODE);
                break;
            case R.id.qr_code_light:
                onFlashlight = !onFlashlight;
                CodeUtils.isLightEnable(onFlashlight);
                if (onFlashlight){
                    qrCodeLightTv.setText(getString(R.string.quick_mark_turn_off));
                }else{
                    qrCodeLightTv.setText(getString(R.string.quick_mark_turn_on));
                }
                break;
        }
    }


    private void parseAnalyze(String message){
        if (TextUtils.isEmpty(message)){
            showToast(getString(R.string.quickmark_parse_error));
            finish();
            return;
        }
        if(message.startsWith("0x")){
            if (WalletStorage.getInstance(PhotonApplication.mContext).get().size() <= 0){
                finish();
                return;
            }
            if(CustomWalletUtils.isValidAddress(message)){
                handleDecode42(message);
            }else if(message.length() > 42){
                handleDecodeMoreThan42(message);
            }else{
                finish();
            }
        }else{
            if (type == 1){
                Intent i = new Intent();
                i.putExtra("sendtype", -1);
                setResult(RESULT_OK,i);
                finish();
            }else{
                finish();
            }
        }
    }

    /**
     * 扫描挖矿签名
     * */
    private void parseMinerAnalyze(String message){
        if (TextUtils.isEmpty(message)){
            showToast(getString(R.string.quickmark_parse_error));
            finish();
            return;
        }
        Intent i = new Intent();
        i.putExtra("result", message);
        i.putExtra("minerScanType", type);
        setResult(RESULT_OK,i);
        finish();
    }

    private void handleDecode42(String msg){
        if(type == 1 || type == 2){
            Intent i = new Intent();
            i.putExtra("address",msg);
            setResult(RESULT_OK,i);
            finish();
        }
    }

    private void handleDecodeMoreThan42(String msg){
        try {
            if (msg.contains("?")){
                String address = msg.substring(0,msg.indexOf("?"));
                Uri parse = Uri.parse(msg);
                float amount = 0f;
                if(!TextUtils.isEmpty(parse.getQueryParameter("amount"))){
                    amount = Float.valueOf(parse.getQueryParameter("amount"));
                }
                if(type == 1 || type == 2){
                    Intent i = new Intent();
                    i.putExtra("address",address);
                    i.putExtra("amount", amount);
                    setResult(RESULT_OK,i);
                    finish();
                }
            }else{
                showToast(getString(R.string.quickmark_parse_error_2));
                finish();
            }
        }catch(Exception e){
            e.printStackTrace();
            showToast(getString(R.string.quickmark_parse_error_2));
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 选择系统图片并解析
         */
        if (requestCode == RequestCodeUtils.PHOTON_CREATE_IMAGE_CODE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            if (type != 4){
                                parseAnalyze(result);
                            }else{
                                parseMinerAnalyze(result);
                            }
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            parseAnalyze("");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }
}
