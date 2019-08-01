package com.smartmesh.photon.channel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.channel.entity.PhotonChannelVo;
import com.smartmesh.photon.channel.util.ChannelNoteUtils;

import java.util.ArrayList;

/**
 * 转账页面 通道列表适配器
 * {@link com.smartmesh.photon.channel.PhotonTransferUI}
 * */
public class ChannelListAdapter extends BaseAdapter {
    public Context mContext;
    private ArrayList<PhotonChannelVo> data = new ArrayList<>();

    public ChannelListAdapter(Context c, ArrayList<PhotonChannelVo> d) {
        this.mContext = c;
        this.data = d;
    }

    public void resetSource(ArrayList<PhotonChannelVo> d){
        this.data = d;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data != null && data.size() > 0 ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_raiden_transfer_address, null);
            holder.content = convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            PhotonChannelVo channelVo = data.get(position);
            String channelNote = ChannelNoteUtils.getChannelNote(channelVo.getTokenAddress(),channelVo.getPartnerAddress());
            if (TextUtils.isEmpty(channelNote)){
                holder.content.setText(channelVo.getPartnerAddress());
            }else{
                holder.content.setText(channelNote);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView content;
    }
}
