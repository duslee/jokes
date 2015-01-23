package com.common.as.struct;

public class ScaleSwitchInfo {

	private int mScaleSwitch = 0;
	
	private String url;
	


	public String getUrl() {
		return url;
	}





	public void setUrl(String url) {
		this.url = url;
	}





	public int getmScaleSwitch() {
		return mScaleSwitch;
	}





	public void setmScaleSwitch(int mScaleSwitch) {
		this.mScaleSwitch = mScaleSwitch;
	}





	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "通道开关："+mScaleSwitch;
	}
	
	
	
}
