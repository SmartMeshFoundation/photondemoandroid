package com.smartmesh.photon.ui;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.channel.util.PhotonIntentDataUtils;
import com.smartmesh.photon.ui.contract.AlertContract;
import com.smartmesh.photon.ui.presenter.AlertPresenterImpl;


/**
 * qr code page
 * */
public class AlertActivity extends BaseActivity<AlertContract.Presenter> implements AlertContract.View{


	@Override
	public int getLayoutId() {
		return R.layout.alert_null_layout;
	}

	@Override
	public AlertContract.Presenter createPresenter() {
		return new AlertPresenterImpl(this);
	}

	@Override
	protected void initData() {

		int type=getIntent().getIntExtra("type", 0);
		if(type == 9){//光子显示地址二维码
			String address = getIntent().getStringExtra(PhotonIntentDataUtils.WALLET_ADDRESS);
			showPhotonWalletAddressDialog(address);
		}
	}
	/**
	 * 光子显示地址二维码
	 * */
	private void showPhotonWalletAddressDialog(String address) {
		mPresenter.photonWalletAddressDialog(this,address);
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	public void alertFinish() {
		finish();
	}

	@Override
	public void onResult(Object result, String message) {

	}

	@Override
	public void onError(Throwable throwable, String message) {

	}
}
