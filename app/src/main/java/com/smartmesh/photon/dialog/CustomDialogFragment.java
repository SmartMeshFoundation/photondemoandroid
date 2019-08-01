package com.smartmesh.photon.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.custom.CustomHeightListView;
import com.smartmesh.photon.custom.CustomListDialogAdapter;
import com.smartmesh.photon.custom.SuccessLoadingView;

@SuppressLint("ValidFragment")
public class CustomDialogFragment extends DialogFragment {

    public CustomDialogFragment(int dialogType) {
        this.mDialogTpe = dialogType;
    }

    private int mDialogTpe = 0;
    private View mDialogView;

    public static final int CUSTOM_DIALOG = 0;
    public static final int CUSTOM_DIALOG_LIST = CUSTOM_DIALOG + 1;
    public static final int CUSTOM_DIALOG_NO_BUTTON = CUSTOM_DIALOG_LIST + 1;
    public static final int CUSTOM_DIALOG_INPUT_PWD = CUSTOM_DIALOG_NO_BUTTON + 1;

    private String title;

    private String content;

    private String content2 = null;
    private String content3 = null;

    private int contentColor = 0;

    private boolean hindCancelButton;

    private String confirm;
    private String cancelText;


    private SetSubmitListener submitListener;
    private SetCancelListener cancelListener;
    private SetOnItemListener onItemListener;
    private SetEditCallbackListener editCallbackListener;


    public interface SetEditCallbackListener {
        void getEditText(String content);
    }

    public interface SetSubmitListener {
        void submit();
    }

    public interface SetOnItemListener {
        void onItemListener(int position);
    }

    public interface SetCancelListener {
        void cancel();
    }

    public void setEditCallbackListener(SetEditCallbackListener lis) {
        this.editCallbackListener = lis;
    }

   public void setSubmitListener(SetSubmitListener lis) {
        this.submitListener = lis;
    }

    public void setCancelListener(SetCancelListener lis) {
        this.cancelListener = lis;
    }

    public void setOnItemListener(SetOnItemListener lis) {
        this.onItemListener = lis;
    }

    public void setConfirmButton(String confirm){
        this.confirm = confirm;
    }

    public void setCancelButton(String cancelText){
        this.cancelText = cancelText;
    }

    private SuccessLoadingView customDialogContentLoading;
    private SuccessLoadingView customDialogContentLoading2;
    private SuccessLoadingView customDialogContentLoading3;

    public void setHindCancelButton(boolean hindCancelButton){
        this.hindCancelButton = hindCancelButton;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setContent2(String content2){
        this.content2 = content2;
    }

    public void setContent3(String content3){
        this.content3 = content3;
    }

    public void setContentColor(int contentColor){
        this.contentColor = contentColor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        switch (mDialogTpe) {
            case CUSTOM_DIALOG:
                mDialogView = inflater.inflate(R.layout.custom_dialog, container);
                customMethod();
                break;
            case CUSTOM_DIALOG_LIST:
                mDialogView = inflater.inflate(R.layout.custom_dialog_list, container);
                customListMethod();
                break;
            case CUSTOM_DIALOG_NO_BUTTON:
                mDialogView = inflater.inflate(R.layout.custom_dialog_no_button, container);
                customNoButtonMethod();
                break;
            case CUSTOM_DIALOG_INPUT_PWD:
                mDialogView = inflater.inflate(R.layout.custom_dialog_input_pwd, container);
                customInputPwdMethod();
                break;
            default:
                break;
        }
        return mDialogView;
    }

   private void customInputPwdMethod(){
        final EditText editText =  mDialogView.findViewById(R.id.dialogPwd);
        Button okBtn = mDialogView.findViewById(R.id.okBtn);
        Button cancelBtn = mDialogView.findViewById(R.id.cancelBtn);
       cancelBtn.setOnClickListener(v -> dismiss());
        okBtn.setOnClickListener(view -> {
            dismiss();
            if (isAdded() && getActivity() != null && editCallbackListener != null){
                editCallbackListener.getEditText(editText.getText().toString());
            }
        });
   }

    /**
     * 列表弹框
     * */
    private void customListMethod() {
        TextView titleView = mDialogView.findViewById(R.id.custom_dialog_title);
        TextView cancelView = mDialogView.findViewById(R.id.custom_dialog_cancel);

        CustomHeightListView listView = mDialogView.findViewById(R.id.custom_dialog_list_view);
        CustomListDialogAdapter customListDialogAdapter = new CustomListDialogAdapter(PhotonApplication.mContext);
        listView.setAdapter(customListDialogAdapter);

        if (!TextUtils.isEmpty(title)){
            titleView.setText(title);
        }else{
            titleView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(cancelText)){
            cancelView.setText(cancelText);
        }

        cancelView.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.cancel();
            }
            dismiss();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (onItemListener != null) {
                onItemListener.onItemListener(position);
            }
            dismiss();
        });
    }

    /**
     * 默认弹框
     * */
    private void customMethod() {
        TextView titleView = mDialogView.findViewById(R.id.custom_dialog_title);
        TextView contentView = mDialogView.findViewById(R.id.custom_dialog_content);
        TextView contentView2 = mDialogView.findViewById(R.id.custom_dialog_content_2);
        TextView contentView3 = mDialogView.findViewById(R.id.custom_dialog_content_3);
        if (!TextUtils.isEmpty(title)){
            titleView.setText(title);
        }else{
            titleView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content)){
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(content);
        }else{
            contentView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content2)){
            contentView2.setVisibility(View.VISIBLE);
            contentView2.setText(content2);
        }else{
            contentView2.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content3)){
            contentView3.setVisibility(View.VISIBLE);
            contentView3.setText(content3);
        }else{
            contentView3.setVisibility(View.GONE);
        }

        if (contentColor != 0){
            contentView.setTextColor(contentColor);
        }

        Button cancelDialog= mDialogView.findViewById(R.id.cancelBtn);
        Button submitDialog = mDialogView.findViewById(R.id.okBtn);

        if (hindCancelButton){
            cancelDialog.setVisibility(View.GONE);
        }else{
            cancelDialog.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(confirm)){
            submitDialog.setText(confirm);
        }

        if (!TextUtils.isEmpty(cancelText)){
            cancelDialog.setText(cancelText);
        }

        cancelDialog.setOnClickListener(view -> {
            if (cancelListener != null) {
                cancelListener.cancel();
            }
            dismiss();
        });
        submitDialog.setOnClickListener(view -> {
            if (submitListener != null) {
                submitListener.submit();
            }
            dismiss();
        });

    }


    /**
     * 无按钮弹框
     * */
    private void customNoButtonMethod() {
        TextView titleView = mDialogView.findViewById(R.id.custom_dialog_title);
        LinearLayout customDialogContentBody2 = mDialogView.findViewById(R.id.custom_dialog_content_body_2);
        LinearLayout customDialogContentBody3 = mDialogView.findViewById(R.id.custom_dialog_content_body_3);
        TextView contentView = mDialogView.findViewById(R.id.custom_dialog_content);
        TextView contentView2 = mDialogView.findViewById(R.id.custom_dialog_content_2);
        TextView contentView3 = mDialogView.findViewById(R.id.custom_dialog_content_3);
        customDialogContentLoading = mDialogView.findViewById(R.id.custom_dialog_content_loading);
        customDialogContentLoading2 = mDialogView.findViewById(R.id.custom_dialog_content_loading_2);
        customDialogContentLoading3 = mDialogView.findViewById(R.id.custom_dialog_content_loading_3);
        if (!TextUtils.isEmpty(title)){
            titleView.setText(title);
        }else{
            titleView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content)){
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(content);
        }else{
            contentView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content2)){
            customDialogContentBody2.setVisibility(View.VISIBLE);
            contentView2.setText(content2);
        }else{
            customDialogContentBody2.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content3)){
            customDialogContentBody3.setVisibility(View.VISIBLE);
            contentView3.setText(content3);
        }else{
            customDialogContentBody3.setVisibility(View.GONE);
        }

        if (contentColor != 0){
            contentView.setTextColor(contentColor);
        }
    }

    private boolean contentViewHasReset;
    private boolean contentViewHasReset2;

    public void resetContentViewDrawable(){
        if (contentViewHasReset){
            resetContentView2Drawable();
        }else{
            contentViewHasReset = true;
            if (customDialogContentLoading != null){
                customDialogContentLoading.startAnim();
            }
        }
    }

    private void resetContentView2Drawable(){
        if (!contentViewHasReset2){
            if (customDialogContentLoading2 != null){
                customDialogContentLoading2.startAnim();
            }
            new Handler().postDelayed(() -> contentViewHasReset2 = true,100);
        }

    }

    public void resetContentView3Drawable(){
        resetContentView2Drawable();
        if (contentViewHasReset2){
            if (customDialogContentLoading3 != null){
                customDialogContentLoading3.startAnim();
            }
        }else{
            new Handler().postDelayed(() -> {
                if (customDialogContentLoading3 != null){
                    customDialogContentLoading3.startAnim();
                }
            },200);
        }

    }
}
