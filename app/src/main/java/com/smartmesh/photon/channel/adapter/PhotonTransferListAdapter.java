package com.smartmesh.photon.channel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.channel.entity.PhotonTransferEntity;
import com.smartmesh.photon.channel.util.ChannelNoteUtils;
import com.smartmesh.photon.channel.util.PhotonUtils;
import com.smartmesh.photon.util.Utils;

import org.yczbj.ycrefreshviewlib.adapter.RecyclerArrayAdapter;
import org.yczbj.ycrefreshviewlib.holder.BaseViewHolder;

/**
 * Created on 2018/1/24.
 * photon 内部转账适配器
 * {@link com.smartmesh.photon.channel.fragment.PhotonTransferListFragment}
 */

public class PhotonTransferListAdapter extends RecyclerArrayAdapter<PhotonTransferEntity> {

    private Context context = null;

    private PhotonTransferViewHolder viewHolder;

    public PhotonTransferListAdapter(Context c) {
        super(c);
        this.context = c;
    }

    public PhotonTransferViewHolder getViewHolder() {
        return viewHolder;
    }

    public class PhotonTransferViewHolder extends BaseViewHolder<PhotonTransferEntity> {

        private TextView partner;//partner
        private TextView amount;//amount
        private TextView channelNote;
        private TextView state;
        private TextView time;


        PhotonTransferViewHolder(ViewGroup parent) {
            super(parent, R.layout.photon_transfer_list_item);
            partner = getView(R.id.partner);
            amount = getView(R.id.amount);
            channelNote = getView(R.id.channel_note);
            state = getView(R.id.state);
            time = getView(R.id.time);
        }

        @Override
        public void setData(PhotonTransferEntity vo) {
            super.setData(vo);
            try {
                partner.setText(context.getString(R.string.photon_channel_address,vo.getTargetAddress()));
                time.setText(Utils.formatTransMsgTime(vo.getTime()));
                if (vo.getType() == 0){
                    amount.setText(context.getString(R.string.photon_amount_send,vo.getAmount(), PhotonUtils.getPhotonTokenSymbol(vo.getTokenAddress())));
                }else{
                    amount.setText(context.getString(R.string.photon_amount_received,vo.getAmount(),PhotonUtils.getPhotonTokenSymbol(vo.getTokenAddress())));
                }

                if (vo.getStatus() == 3){
                    state.setText(context.getString(R.string.photon_transfer_success));
                }else if (vo.getStatus() == 5){
                    state.setText(context.getString(R.string.photon_transfer_error));
                }else if (vo.getStatus() == 4){
                    state.setText(context.getString(R.string.photon_transfer_cancel));
                }else{
                    state.setText(context.getString(R.string.photon_transfer_ing));
                }

                String channelNoteString = ChannelNoteUtils.getChannelNote(vo.getTokenAddress(),vo.getTargetAddress());
                if (TextUtils.isEmpty(channelNoteString)){
                    channelNote.setVisibility(View.GONE);
                }else{
                    channelNote.setVisibility(View.VISIBLE);
                    channelNote.setText(context.getString(R.string.photon_channel_note_2,channelNoteString));
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
        viewHolder = new PhotonTransferListAdapter.PhotonTransferViewHolder(parent);
        return viewHolder;
    }

}
