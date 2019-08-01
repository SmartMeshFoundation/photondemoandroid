package com.smartmesh.photon.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.util.WalletStorage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomListDialogAdapter extends BaseAdapter {

    Context context;

    public CustomListDialogAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return WalletStorage.getInstance(context.getApplicationContext()).get().size();
    }

    @Override
    public Object getItem(int position) {
        return WalletStorage.getInstance(context.getApplicationContext()).get().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CustomListDialogAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_list_item, null);
            holder = new CustomListDialogAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CustomListDialogAdapter.ViewHolder) convertView.getTag();
        }
        StorableWallet storableWallet = WalletStorage.getInstance(context.getApplicationContext()).get().get(position);
        holder.title.setText(storableWallet.getWalletName());
        String address = storableWallet.getPublicKey();
        if(!address.startsWith("0x")){
            address = "0x"+address;
        }
        holder.content.setText(address);
        if (position < getCount() - 1){
            holder.customDialogLine.setVisibility(View.VISIBLE);
        }else{
            holder.customDialogLine.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.dialog_list_body)
        LinearLayout dialogListBody;
        @BindView(R.id.custom_dialog_line)
        View customDialogLine;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
