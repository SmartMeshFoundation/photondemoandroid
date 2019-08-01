package com.smartmesh.photon.wallet;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.util.Utils;
import com.smartmesh.photon.wallet.contract.QuickMarkShowContract;
import com.smartmesh.photon.wallet.presenter.QuickMarkShowPresenter;
import com.smartmesh.photon.wallet.util.BitmapUtils;
import com.smartmesh.photon.wallet.util.WalletConstants;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.web3j.utils.Numeric;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * show wallet qr code page
 * */
public class WalletQrCodeActivity extends BaseActivity<QuickMarkShowContract.Presenter> implements QuickMarkShowContract.View{

	@BindView(R.id.wallet_quick_mark)
	ImageView mQuickMark;
	@BindView(R.id.wallet_address)
	TextView walletAddress;

	private Bitmap qrBitmap;

	@Override
	protected void initData() {
		setBottomTitle(getString(R.string.qr_code));
		String address = getIntent().getStringExtra(WalletConstants.WALLET_ADDRESS);
		String content = Numeric.prependHexPrefix(address);
		walletAddress.setText(content);
		qrBitmap= CodeUtils.createImage(content,getResources().getDisplayMetrics().widthPixels - Utils.dip2px(this,1),getResources().getDisplayMetrics().widthPixels - Utils.dip2px(this,1),null);
		mQuickMark.setImageBitmap(qrBitmap);
	}

	@OnClick({R.id.wallet_copy_address,R.id.qr_save_picture})
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()){
			case R.id.wallet_copy_address://copy wallet address
				Utils.copyText(WalletQrCodeActivity.this,walletAddress.getText().toString().trim());
				break;
			case R.id.qr_save_picture://save wallet qr code image to photon
				try {
					String qrPath = BitmapUtils.uploadZxing(WalletQrCodeActivity.this,qrBitmap,true,false);
					showToast(qrPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.wallet_qr_layout;
	}

	@Override
	public QuickMarkShowContract.Presenter createPresenter() {
		return new QuickMarkShowPresenter(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(qrBitmap != null  && !qrBitmap.isRecycled()){
			qrBitmap.recycle();
			qrBitmap = null ;
		}
	}


	@Override
	public void onResult(Object result, String message) {

	}

	@Override
	public void onError(Throwable throwable, String message) {

	}
}
