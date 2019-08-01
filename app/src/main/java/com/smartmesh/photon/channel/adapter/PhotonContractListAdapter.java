package com.smartmesh.photon.channel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.channel.entity.PhotonContractEntity;
import com.smartmesh.photon.channel.entity.PhotonContractTxEntity;
import com.smartmesh.photon.channel.entity.TxStatus;
import com.smartmesh.photon.channel.entity.TxTypeStr;
import com.smartmesh.photon.channel.util.PhotonUtils;
import com.smartmesh.photon.util.Utils;

import org.yczbj.ycrefreshviewlib.adapter.RecyclerArrayAdapter;
import org.yczbj.ycrefreshviewlib.holder.BaseViewHolder;

/**
 * Created on 2018/1/24.
 * 合约调用列表适配器
 * {@link com.smartmesh.photon.channel.fragment.PhotonContractCallFragment}
 */

public class PhotonContractListAdapter extends RecyclerArrayAdapter<PhotonContractEntity> {

    private Context context = null;
    private PhotonContractViewHolder viewHolder;


    public PhotonContractListAdapter(Context c) {
        super(c);
        this.context = c;
    }

    public PhotonContractViewHolder getViewHolder() {
        return viewHolder;
    }

    public class PhotonContractViewHolder extends BaseViewHolder<PhotonContractEntity> {
        private TextView partner;//partner
        private TextView amount;//amount
        private TextView state;
        private TextView time;
        private TextView contractBalance;
        private LinearLayout partnerBody;
        private LinearLayout balanceBody;

        PhotonContractViewHolder(ViewGroup parent) {
            super(parent, R.layout.photon_contract_list_item);
            partner = getView(R.id.partner);
            amount = getView(R.id.amount);
            state = getView(R.id.state);
            time = getView(R.id.time);
            contractBalance = getView(R.id.contractBalance);
            partnerBody = getView(R.id.partnerBody);
            balanceBody = getView(R.id.balanceBody);
        }

        @Override
        public void setData(PhotonContractEntity contractEntity) {
            super.setData(contractEntity);
            try {
                PhotonContractTxEntity contractTxEntity = contractEntity.getContractTxEntity();
                time.setText(Utils.formatTxTime(contractEntity.getCallTime()));
                String type = contractEntity.getType();
                String txStatus = contractEntity.getTxStatus();
                if (TextUtils.equals(TxTypeStr.ChannelDeposit.name(),type) || TextUtils.equals(TxTypeStr.ApproveDeposit.name(),type)){
                    amount.setText(context.getString(R.string.token_trans,contractTxEntity.getAmount(), PhotonUtils.getPhotonTokenSymbol(contractEntity.getTokenAddress())));
                    partner.setText(contractTxEntity.getPartner_address());
                    String settleTimeOut = contractTxEntity.getSettle_timeout();
                    state.setTextColor(context.getResources().getColor(R.color.color_e2edea));
                    balanceBody.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(settleTimeOut) || TextUtils.equals(settleTimeOut,"0")){//存款
                        contractBalance.setText(context.getString(R.string.photon_channel_list_deposit));
                        partnerBody.setVisibility(View.GONE);
                        if (TextUtils.equals(TxStatus.pending.name(),txStatus)){
                            state.setText(context.getString(R.string.photon_tx_deposit_3));
                        }else if (TextUtils.equals(TxStatus.failed.name(),txStatus)){
                            state.setText(context.getString(R.string.photon_tx_deposit_2));
                            state.setTextColor(context.getResources().getColor(R.color.colorRed));
                        }else{
                            state.setText(context.getString(R.string.photon_tx_deposit_1));
                        }
                    }else{//创建
                        contractBalance.setText(context.getString(R.string.photon_channel_list_deposit));
                        partnerBody.setVisibility(View.VISIBLE);
                        if (TextUtils.equals(TxStatus.pending.name(),txStatus)){
                            state.setText(context.getString(R.string.photon_tx_create_3));
                        }else if (TextUtils.equals(TxStatus.failed.name(),txStatus)){
                            state.setText(context.getString(R.string.photon_tx_create_2));
                            state.setTextColor(context.getResources().getColor(R.color.colorRed));
                        }else{
                            state.setText(context.getString(R.string.photon_tx_create_1));
                        }
                    }
                }else if (TextUtils.equals(TxTypeStr.Withdraw.name(),type)){
                    balanceBody.setVisibility(View.VISIBLE);
                    contractBalance.setText(context.getString(R.string.photon_withdraw));
                    partnerBody.setVisibility(View.GONE);
                    amount.setText(context.getString(R.string.token_trans,contractTxEntity.getP1_balance(), PhotonUtils.getPhotonTokenSymbol(contractEntity.getTokenAddress())));
                    state.setTextColor(context.getResources().getColor(R.color.color_e2edea));
                    if (TextUtils.equals(TxStatus.pending.name(),txStatus)){
                       state.setText(context.getString(R.string.photon_tx_withdraw_3));
                    }else if (TextUtils.equals(TxStatus.failed.name(),txStatus)){
                        state.setText(context.getString(R.string.photon_tx_withdraw_2));
                    }else{
                        state.setText(context.getString(R.string.photon_tx_withdraw_1));
                    }
                }else if (TextUtils.equals(TxTypeStr.ChannelClose.name(),type)){
                    balanceBody.setVisibility(View.GONE);
                    partnerBody.setVisibility(View.VISIBLE);
                    partner.setText(contractTxEntity.getPartner_address());
                    state.setTextColor(context.getResources().getColor(R.color.color_e2edea));
                    if (TextUtils.equals(TxStatus.pending.name(),txStatus)){
                        state.setText(context.getString(R.string.photon_tx_forced_close_3));
                        state.setTextColor(context.getResources().getColor(R.color.color_3c7266));
                    }else if (TextUtils.equals(TxStatus.failed.name(),txStatus)){
                        state.setText(context.getString(R.string.photon_tx_forced_close_2));
                        state.setTextColor(context.getResources().getColor(R.color.colorRed));
                    }else{
                        state.setText(context.getString(R.string.photon_tx_forced_close_1));
                    }
                }else if (TextUtils.equals(TxTypeStr.ChannelSettle.name(),type)){
                    balanceBody.setVisibility(View.VISIBLE);
                    contractBalance.setText(context.getString(R.string.photon_amount));
                    partnerBody.setVisibility(View.VISIBLE);
                    partner.setText(contractTxEntity.getP2_address());
                    amount.setText(context.getString(R.string.token_trans,contractTxEntity.getP1_balance(), PhotonUtils.getPhotonTokenSymbol(contractEntity.getTokenAddress())));
                    state.setTextColor(context.getResources().getColor(R.color.color_e2edea));
                    if (TextUtils.equals(TxStatus.pending.name(),txStatus)){
                        state.setText(context.getString(R.string.photon_tx_settle_3));
                        state.setTextColor(context.getResources().getColor(R.color.color_3c7266));
                    }else if (TextUtils.equals(TxStatus.failed.name(),txStatus)){
                        state.setText(context.getString(R.string.photon_tx_settle_2));
                        state.setTextColor(context.getResources().getColor(R.color.colorRed));
                    }else{
                        state.setText(context.getString(R.string.photon_tx_settle_1));
                    }
                }else if (TextUtils.equals(TxTypeStr.CooperateSettle.name(),type)){
                    balanceBody.setVisibility(View.VISIBLE);
                    contractBalance.setText(context.getString(R.string.photon_balance_1));
                    partnerBody.setVisibility(View.VISIBLE);
                    partner.setText(contractTxEntity.getP2_address());
                    amount.setText(context.getString(R.string.token_trans,contractTxEntity.getP1_balance(), PhotonUtils.getPhotonTokenSymbol(contractEntity.getTokenAddress())));
                    state.setTextColor(context.getResources().getColor(R.color.color_e2edea));
                    if (TextUtils.equals(TxStatus.pending.name(),txStatus)){
                        state.setText(context.getString(R.string.photon_tx_close_3));
                    }else if (TextUtils.equals(TxStatus.failed.name(),txStatus)){
                        state.setText(context.getString(R.string.photon_tx_close_2));
                        state.setTextColor(context.getResources().getColor(R.color.colorRed));
                    }else{
                        state.setText(context.getString(R.string.photon_tx_close_1));
                    }
                }
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
        viewHolder = new PhotonContractViewHolder(parent);
        return viewHolder;
    }

}
