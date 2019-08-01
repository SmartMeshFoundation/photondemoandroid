package com.smartmesh.photon.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.util.Utils;
import com.uuzuche.lib_zxing.activity.CodeUtils;


public class SubmitDialog extends Dialog {
 
    public SubmitDialog(Context context, int theme) {
        super(context, theme);
    }
 
    public SubmitDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
 
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private TextView versionNegateButton;
        private OnClickListener positiveButtonClickListener,negativeButtonClickListener;

        private mappingClosedListener mappingClosedListener;
        private photonWalletListener photonWalletListener;
        private SubmitDialog dialog;

        boolean hasSelect = false;

        private DownloadManager dm;
        private long downloadId;

        public Builder(Context context) {
            this.context = context;
        }

        public interface mappingClosedListener{
            void mappingClosed(DialogInterface dialog);
        };

        public interface photonWalletListener{
            void photonClose();
            void photonCopyAddress(String address);
            void photonSavePhoton(Bitmap qrBitMap);
        };

        public void setPhotonWalletListener(photonWalletListener photonWalletListener){
            this.photonWalletListener = photonWalletListener;
        }

        public void setMappingClosed(mappingClosedListener mappingClosedListener){
            this.mappingClosedListener = mappingClosedListener;
        }

        /**
         * Set the Dialog setCancelable
         * @return
         */
        public void setCancelable(boolean cancelable){
        	if(dialog!=null){
        		dialog.setCancelable(cancelable);
        		dialog.setCanceledOnTouchOutside(cancelable);
        	}
        }
        /**
         * Set the Dialog message from String
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }


        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * 显示光子地址弹框
         */
        public void showPhotonWalletAddressDialog(String address) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final SubmitDialog dialog = new SubmitDialog(context, R.style.SubmitDialog);
            this.dialog=dialog;
            View layout = inflater.inflate(R.layout.submit_dialog_photon_wallet_address, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            TextView walletAddress = layout.findViewById(R.id.photon_wallet_address);
            ImageView mQuickMark = layout.findViewById(R.id.qr_code_img);
            ImageView dialogClose = layout.findViewById(R.id.dialog_photon_close);
            TextView copyAddress = layout.findViewById(R.id.copy_address);
            TextView saveQr = layout.findViewById(R.id.qr_save_picture);
            walletAddress.setText(address);
            Bitmap qrBitmap= CodeUtils.createImage(address, PhotonApplication.mContext.getResources().getDisplayMetrics().widthPixels,PhotonApplication.mContext.getResources().getDisplayMetrics().widthPixels,null);
            mQuickMark.setImageBitmap(qrBitmap);
            dialogClose.setOnClickListener((View v) -> {
                dialog.dismiss();
                if (photonWalletListener != null){
                    photonWalletListener.photonClose();
                }
            });

            copyAddress.setOnClickListener(v -> {
                if (photonWalletListener != null){
                    photonWalletListener.photonCopyAddress(address);
                }
            });

            saveQr.setOnClickListener(v -> {
                if (photonWalletListener != null){
                    photonWalletListener.photonSavePhoton(qrBitmap);
                }
            });
            showDialog(dialog,layout);
        }

    }

    private static void  setPositiveListener(TextView positiveButton,String positiveButtonText,final OnClickListener positiveButtonClickListener,final DialogInterface dialog){
        if (positiveButtonText != null) {
            positiveButton.setText(positiveButtonText);
            if (positiveButtonClickListener != null) {
                positiveButton.setOnClickListener(v -> positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE));
            }
        } else {
            positiveButton.setVisibility(View.GONE);
        }
    }

    private static void  setCancelListener(ImageView cancelButton,final OnClickListener positiveButtonClickListener,final DialogInterface dialog){
        if (cancelButton != null) {
            if (positiveButtonClickListener != null) {
                cancelButton.setOnClickListener(v -> positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE));
            }
        }
    }

    private static void setNegativeListener(TextView negativeButton,String negativeButtonText,final OnClickListener negativeButtonClickListener,final DialogInterface dialog){
        if (negativeButtonText != null) {
            negativeButton.setText(negativeButtonText);
            if (negativeButtonClickListener != null) {
                negativeButton.setOnClickListener(v -> negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE));
            }
        } else {
            negativeButton.setVisibility( View.GONE);
        }
    }

    private static void setDialogTitle(TextView titleView,String title){
        if(TextUtils.isEmpty(title)){
            titleView.setVisibility(View.GONE);
        }else{
            titleView.setText(title);
        }
    }

    @SuppressLint("WrongViewCast")
    private static void setMessageView(TextView messageView, String message, Context context, View layout, View contentView){
        if (message != null) {
            messageView.setText(message);
        } else if (contentView != null) {
            ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            lp.setMargins(Utils.dip2px(context, 15), 0, Utils.dip2px(context, 15), 0);
            ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, lp);
        }
    }

    private static void showDialog(SubmitDialog dialog,View layout){
        if (dialog != null){
            dialog.setContentView(layout);
            dialog.show();
            if (dialog.getWindow() != null){
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(params);
            }
        }
    }
}
