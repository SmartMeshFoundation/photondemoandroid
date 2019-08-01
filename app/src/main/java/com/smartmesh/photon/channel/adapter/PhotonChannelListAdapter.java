package com.smartmesh.photon.channel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.smartmesh.photon.R;
import com.smartmesh.photon.channel.ChangeChannelStateListener;
import com.smartmesh.photon.channel.entity.PhotonChannelVo;
import com.smartmesh.photon.channel.util.ChannelNoteUtils;
import com.smartmesh.photon.channel.util.PhotonConnectStatus;
import com.smartmesh.photon.channel.util.PhotonUtils;

import org.yczbj.ycrefreshviewlib.adapter.RecyclerArrayAdapter;
import org.yczbj.ycrefreshviewlib.holder.BaseViewHolder;

/**
 * Created on 2018/1/24.
 * 通道列表适配器
 * {@link com.smartmesh.photon.channel.PhotonChannelList}
 */

public class PhotonChannelListAdapter extends RecyclerArrayAdapter<PhotonChannelVo> {
    
    private Context context;

    private ChangeChannelStateListener channelStateListener;

    private PhotonChannelViewHolder viewHolder;
    
    public PhotonChannelListAdapter(Context c,ChangeChannelStateListener channelStateListener) {
        super(c);
        this.context = c;
        this.channelStateListener = channelStateListener;
    }

    public PhotonChannelViewHolder getViewHolder() {
        return viewHolder;
    }

    public class PhotonChannelViewHolder extends BaseViewHolder<PhotonChannelVo> {

        private TextView partner;//partner
        private TextView balance;//deposit
        private TextView channelNote;
        private TextView state;
        private TextView stateContent;
        private LinearLayout channelNoteBody;

        /**
         * 提现
         * */
        private TextView photonWithdraw;

        /**
         * 存款
         * */
        private TextView photonAdd;

        /**
         * 关闭通道
         * */
        private TextView photonClose;

        /**
         * 锁定金额
         * */
        private TextView lockedAmountTv;
        private TextView partnerLockedAmount;

        /**
         * 转账委托提交第三方
         * */
        private ImageView photonChannelPms;

//        ImageView photonListHidden;

        private TextView photonTokenSymbol;

        private LinearLayout channelBody;
        private LinearLayout photonHiddenBottom;


        PhotonChannelViewHolder(ViewGroup parent) {
            super(parent, R.layout.photon_channel_list_item);
            partner = getView(R.id.partner);
            balance = getView(R.id.balance);
            channelNote = getView(R.id.channelNote);
            channelNoteBody = getView(R.id.channelNoteBody);
            photonWithdraw = getView(R.id.photonWithdraw);
            photonAdd = getView(R.id.photonAdd);
            photonClose = getView(R.id.photonClose);
            channelBody = getView(R.id.channelBody);
            partnerLockedAmount = getView(R.id.partnerLockedAmount);
            lockedAmountTv = getView(R.id.lockedAmount);
            state = getView(R.id.state);
            stateContent = getView(R.id.stateContent);
            photonTokenSymbol = getView(R.id.photon_token_symbol);
//            holder.photonListHidden = getView(R.id.photon_list_hidden);
            photonHiddenBottom = getView(R.id.photon_hidden_bottom);
            photonChannelPms = getView(R.id.photon_channel_pms);
        }

        @Override
        public void setData(final PhotonChannelVo vo){
            try {
                partner.setText(vo.getPartnerAddress());
                balance.setText(vo.getBalance());
                photonTokenSymbol.setText(PhotonUtils.getPhotonTokenSymbol(vo.getTokenAddress()));
                String channelNoteString = ChannelNoteUtils.getChannelNote(vo.getTokenAddress(),vo.getPartnerAddress());
                if (TextUtils.isEmpty(channelNoteString)){
                    channelNoteBody.setVisibility(View.GONE);
                }else{
                    channelNoteBody.setVisibility(View.VISIBLE);
                    channelNote.setText(channelNoteString);
                }

                if (TextUtils.isEmpty(vo.getLockedAmount())){
                    lockedAmountTv.setVisibility(View.GONE);
                }else{
                    float lockedAmount = Float.parseFloat(vo.getLockedAmount());
                    if (lockedAmount > 0){
                        lockedAmountTv.setVisibility(View.VISIBLE);
                        lockedAmountTv.setText(vo.getLockedAmount());
                    }else{
                        lockedAmountTv.setVisibility(View.GONE);
                    }
                }

                if (TextUtils.isEmpty(vo.getPartnerLockedAmount())){
                    partnerLockedAmount.setVisibility(View.GONE);
                }else{
                    float lockedAmount = Float.parseFloat(vo.getPartnerLockedAmount());
                    if (lockedAmount > 0){
                        partnerLockedAmount.setVisibility(View.VISIBLE);
                        partnerLockedAmount.setText(vo.getPartnerLockedAmount());
                    }else{
                        partnerLockedAmount.setVisibility(View.GONE);
                    }
                }

                channelBody.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                stateContent.setVisibility(View.GONE);
                photonChannelPms.setVisibility(View.GONE);

                if (PhotonConnectStatus.StateOpened == vo.getState()) {
                    channelBody.setVisibility(View.VISIBLE);
                }else if (PhotonConnectStatus.StateClosing == vo.getState()) {
                    stateContent.setVisibility(View.VISIBLE);
                    stateContent.setText(context.getString(R.string.photon_channel_content));
                }else if (PhotonConnectStatus.StateSettling == vo.getState()) {
                    stateContent.setVisibility(View.VISIBLE);
                    stateContent.setText(context.getString(R.string.photon_channel_list_type_7));
                }else if (PhotonConnectStatus.StatePartnerWithdrawing == vo.getState()) {
                    stateContent.setVisibility(View.VISIBLE);
                    stateContent.setText(context.getString(R.string.photon_channel_content_5));
                    state.setVisibility(View.VISIBLE);
                    stateContent.setVisibility(View.VISIBLE);
                    state.setText(context.getString(R.string.photon_channel_list_type_3));
                }else if (PhotonConnectStatus.StatePartnerCooperativeSettling == vo.getState()) {
                    stateContent.setVisibility(View.VISIBLE);
                    stateContent.setText(context.getString(R.string.photon_channel_content_6));
                    state.setVisibility(View.VISIBLE);
                    stateContent.setVisibility(View.VISIBLE);
                    state.setText(context.getString(R.string.photon_channel_list_type_3));
                } else if (PhotonConnectStatus.StateWithdraw == vo.getState()){
                    state.setVisibility(View.VISIBLE);
                    stateContent.setVisibility(View.VISIBLE);
                    stateContent.setText(context.getString(R.string.photon_channel_content_4));
                    state.setText(context.getString(R.string.photon_channel_list_type_3));
                } else if (PhotonConnectStatus.StateCooperativeSettle == vo.getState()){
                    state.setVisibility(View.VISIBLE);
                    stateContent.setVisibility(View.VISIBLE);
                    stateContent.setText(context.getString(R.string.photon_channel_content_3));
                    state.setText(context.getString(R.string.photon_channel_list_type_3));
                }else if (vo.getState() == PhotonConnectStatus.StateClosed){
                    if (vo.getSettledBlock() != -1 && vo.getCurrentBlock() != -1){
                        if (vo.getCurrentBlock() - vo.getSettledBlock() >= 0){
                            state.setVisibility(View.VISIBLE);
                            stateContent.setVisibility(View.VISIBLE);
                            stateContent.setText(context.getString(R.string.photon_channel_content_2));
                            state.setText(context.getString(R.string.photon_channel_list_type_6));
                        }else{
                            stateContent.setVisibility(View.VISIBLE);
                            stateContent.setText(context.getString(R.string.photon_channel_list_type_5,vo.getCurrentBlock(),vo.getSettledBlock()));
                        }
                    }
                }

    //            if (vo.isHidden()){
    //                photonListHidden.setImageDrawable(context.getResources().getDrawable(R.drawable.custom_spinner_arrow));
    //                photonHiddenBottom.setVisibility(View.GONE);
    //            }else{
    //                photonListHidden.setImageDrawable(context.getResources().getDrawable(R.drawable.custom_spinner_arrow_up));
    //                photonHiddenBottom.setVisibility(View.VISIBLE);
    //            }

                if (vo.getDelegateState() == 1){
                    photonChannelPms.setVisibility(View.VISIBLE);
                    photonChannelPms.setImageResource(R.mipmap.photon_pms_waite);
                }else if (vo.getDelegateState() == 3 || vo.getDelegateState() == 4){
                    photonChannelPms.setVisibility(View.VISIBLE);
                    photonChannelPms.setImageResource(R.mipmap.photon_pms_error_spectrum);
                }else{
                    photonChannelPms.setVisibility(View.GONE);
                }

                photonWithdraw.setOnClickListener(v -> {
                    if (channelStateListener != null) {
                        channelStateListener.withdrawChannel(getDataPosition());
                    }
                });

                photonAdd.setOnClickListener(v -> {
                    if (channelStateListener != null) {
                        channelStateListener.depositChannel(getDataPosition());
                    }
                });

                photonClose.setOnClickListener(v -> {
                    if (channelStateListener != null) {
                        channelStateListener.changeChannel(getDataPosition(), false);
                    }
                });

                state.setOnClickListener(v -> {
                    if (PhotonConnectStatus.StateWithdraw == vo.getState()
                            || PhotonConnectStatus.StateCooperativeSettle == vo.getState()
                            || PhotonConnectStatus.StatePartnerWithdrawing == vo.getState()
                            || PhotonConnectStatus.StatePartnerCooperativeSettling == vo.getState()){
                        if (channelStateListener != null) {
                            channelStateListener.changeChannel(getDataPosition(), true);
                        }
                    }else if (PhotonConnectStatus.StateClosed == vo.getState()){
                        if (channelStateListener != null) {
                            channelStateListener.settleChannel(getDataPosition());
                        }
                    }
                });

                photonChannelPms.setOnClickListener(v -> {
                    if (channelStateListener != null){
                        channelStateListener.pmsChannel(vo.getDelegateState());
                    }
                });

    //            photonListHidden.setOnClickListener(v -> {
    //                if (channelStateListener != null) {
    //                    channelStateListener.hiddenBottom(position);
    //                }
    //            });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        viewHolder = new PhotonChannelViewHolder(parent);
        return viewHolder;
    }

}
