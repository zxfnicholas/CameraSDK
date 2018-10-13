/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.muzhi.camerasdk.library.database;





public class StickerDb {  
	
	//HttpUtils httpUtils;
	private OnGetStickerListener mListener;
	
	public interface OnGetStickerListener {
	        void onResult(String json);
	}
	
	public void getStickers(OnGetStickerListener listener){
		mListener=listener;
		
		
		/*httpUtils=new HttpUtils();
		String api="http://d.weibo.com/102803?topnav=1&mod=logo&wvr=6";
		httpUtils.send(HttpMethod.POST,api,null,new RequestCallBack<String>() {

		        @Override
		        public void onStart() {}
	
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {	}
		        
		        @Override
		        public void onSuccess(ResponseInfo<String> res) {
		        	mListener.onResult(res.result);
		        }
	
		        @Override
		        public void onFailure(HttpException error, String msg) {}
		});	*/
		
		
		
	}
	
	
}
