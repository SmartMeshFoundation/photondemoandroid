package com.smartmesh.photon.util;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.smartmesh.photon.R;

public class MyToast {
	
	private static Toast toast;

	public static void showToast(Context context, String message) {
		try {
			if(TextUtils.isEmpty(message)){
				return;
			}
			if (context != null) {
				if (toast == null) {
					toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
				} else {
					toast.setText(message);
				}
				toast.setGravity(Gravity.BOTTOM, 0, 300);
				try {
					View view = toast.getView();
					if(view != null){
						TextView messageView= view.findViewById(android.R.id.message);
						view.setBackgroundResource(R.drawable.shape_solid_1b72b_5);
						view.setPadding(Utils.dip2px(context,18),Utils.dip2px(context,8),Utils.dip2px(context,18),Utils.dip2px(context,8));
						messageView.setTextColor(context.getResources().getColor(R.color.color_e2edea));
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				toast.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}catch(Error e){
			e.printStackTrace();
		}
	}

	public static void showCenterToast(Context context, String message) {
		try {
			if(TextUtils.isEmpty(message)){
				return;
			}
			if (context != null) {
				if (toast == null) {
					toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
				} else {
					toast.setText(message);
				}
				toast.setGravity(Gravity.CENTER, 0, 0);
				try {
					View view = toast.getView();
					if(view != null){
						TextView messageView= view.findViewById(android.R.id.message);
						view.setBackgroundResource(R.drawable.shape_solid_1b72b_5);
						view.setPadding(Utils.dip2px(context,18),Utils.dip2px(context,8),Utils.dip2px(context,18),Utils.dip2px(context,8));
						messageView.setTextColor(context.getResources().getColor(R.color.color_e2edea));
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				toast.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}catch(Error e){
			e.printStackTrace();
		}
	}


	public static void showNewToast(Context context, String message) {
		try {
			if(TextUtils.isEmpty(message)){
				return;
			}
			if (context != null) {
				Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, 0, 300);
				try {
					View view = toast.getView();
					if(view != null){
						TextView messageView= view.findViewById(android.R.id.message);
						view.setBackgroundResource(R.drawable.shape_solid_1b72b_5);
						view.setPadding(Utils.dip2px(context,18),Utils.dip2px(context,8),Utils.dip2px(context,18),Utils.dip2px(context,8));
						messageView.setTextColor(context.getResources().getColor(R.color.color_e2edea));
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				toast.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}catch(Error e){
			e.printStackTrace();
		}
	}
}
