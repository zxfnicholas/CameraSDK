package com.muzhi.camerasdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.muzhi.camerasdk.adapter.StickerAdapter;
import com.muzhi.camerasdk.library.database.StickerDb;
import com.muzhi.camerasdk.library.database.StickerDb.OnGetStickerListener;
import com.muzhi.camerasdk.library.utils.MResource;
import com.muzhi.camerasdk.library.utils.T;
import com.muzhi.camerasdk.model.Constants;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;


public class StickerHotFragment extends Fragment {
	
	private View mView;
	private Context mContext;
	
	private StickerAdapter adapter;
	private List<Filter_Sticker_Info> list;
	//private int gv_list_id;
	private GridView gv_list;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {			
		// TODO Auto-generated method stub		
		
		int resId = MResource.getIdByName(getActivity(),MResource.layout, "camerasdk_fragment_sticker"); 
		mView=inflater.inflate(resId, container, false);
		
		resId=MResource.getIdRes(getActivity(),"gv_list");
        gv_list=(GridView)mView.findViewById(resId);
				
		return mView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		
		adapter=new StickerAdapter(mContext);
        list=new ArrayList<Filter_Sticker_Info>();
        gv_list.setAdapter(adapter);
		
		initEvent();
		initData();
		
		StickerDb sdb=new StickerDb();
		sdb.getStickers(new MyListeners());
		
	}
	
	public class MyListeners implements OnGetStickerListener {

		@Override
		public void onResult(String json) {
			// TODO Auto-generated method stub
			T.showShort(mContext, json);
		}
		
	}
	
	
	private void initEvent(){
		
		gv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Filter_Sticker_Info info= adapter.getItem(position);
				Intent intent=new Intent();
				intent.putExtra("info",info);
				getActivity().setResult(Constants.RequestCode_Sticker, intent); 
				getActivity().finish();
			}
		});

	}
	
	private void initData(){		
		list=new ArrayList<Filter_Sticker_Info>();
		for(int i=0;i<30;i++){
			Filter_Sticker_Info info=new Filter_Sticker_Info();
			info.setImage("http://avatar.csdn.net/B/C/5/1_jjwwmlp456.jpg");
			list.add(info);
		}
		adapter.setList(list);
		
	}

	
	

}
