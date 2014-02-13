package org.nypl.libretto.ui;

import java.util.ArrayList;

import org.nypl.libretto.R;
import org.nypl.libretto.adapter.VersionListAdapter;
import org.nypl.libretto.dataholder.VersionBean;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class WebViewFragment extends BaseFragment{
	
	private WebView mPlayDetailView;
	private	VersionBean mVersionBean;
	private String mVersionHtmlId = null;
	private ArrayList<VersionBean> mVersionDetailList;
	
	public interface PlaySelectioListener{
		public void onPlaySelected(int playIndex);
	}
	
	public static WebViewFragment getInstance(Bundle bundle){
		 WebViewFragment fragment = new WebViewFragment();
		 fragment.setArguments(bundle);
		 return fragment;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mVersionBean	=	(VersionBean) bundle.getSerializable("version");
		mVersionDetailList	=	(ArrayList<VersionBean>) bundle.getSerializable("ver_list");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View	view	=	getView();
		mPlayDetailView= (WebView) view.findViewById(R.id.s_plays_detail_webview);
		
		mPlayDetailView.getSettings().setJavaScriptEnabled(true);
		mPlayDetailView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mPlayDetailView.setBackgroundColor(Color.TRANSPARENT);//getResources().getColor(android.R.color.transparent));
		mPlayDetailView.getSettings().getBuiltInZoomControls();
		mPlayDetailView.getSettings().setBuiltInZoomControls(true);

		mPlayDetailView.loadUrl("file:///android_asset/"+mVersionBean.getVersionHTMLFile());
		mPlayDetailView.setWebViewClient(new NyplWebClient(sActivityInstance, mVersionDetailList, mVersionHtmlId));
		
	}
    
	private static class NyplWebClient extends WebViewClient{
		
		private String	htmlVersionID;
		private Dialog	mVersionDialog;
		private	FragmentActivity	mContext;
		private ArrayList<VersionBean> mVersionDetailList;
		
		public NyplWebClient(FragmentActivity  context,ArrayList<VersionBean> versionDetailList, String htmlVersionID) {
			super();
			this.htmlVersionID = htmlVersionID;
			mContext = context;
			mVersionDetailList  = versionDetailList;
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
				if(htmlVersionID!=null )
			{  
				view.loadUrl("javascript:scrollToElement('" + htmlVersionID.trim() + "')");
			}

		}
		@Override
		public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			//progress.setVisibility(View.GONE);
			//view.findViewById(R.id.progress).setVisibility(View.GONE);
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if(url.contains("file:///android_asset/")){

				getVersion(url.replace("file:///android_asset/", ""));
			}

			return true;

		}
		
		private void getVersion(final String mVersion){
			///	versionDetailList = VersionDAO.getVersionOf(PlaysDetailActivity.this, mPlaysId);
			mVersionDialog=new Dialog(mContext,android.R.style.Theme_Translucent);
			mVersionDialog.getWindow();
			mVersionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mVersionDialog.setContentView(R.layout.popup_plays_version);
			ListView mVersionList=(ListView) mVersionDialog.findViewById(R.id.s_play_version_list);
		
			mVersionList.setAdapter(new VersionListAdapter(mVersionDetailList));
			mVersionList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long arg3) {
					mVersionDialog.cancel();
					((PlaySelectioListener)mContext).onPlaySelected(position);
				}
			});
			mVersionDialog.show();

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.e_play_fulltext, null);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
