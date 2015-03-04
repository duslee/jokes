package com.common.as.struct;

public class SwitchInfo {

	private int mPopSwitch = 0;
	private int mShortCutSwitch = 0;
	private int mBgSwitch = 0;
	private int mTopWndSwitch = 0;
	private int mListSwitch =  0;
	public int getmPopSwitch() {
		return mPopSwitch;
	}
	public void setmPopSwitch(int mPopSwitch) {
		this.mPopSwitch = mPopSwitch;
	}
	public int getmShortCutSwitch() {
		return mShortCutSwitch;
	}
	public void setmShortCutSwitch(int mShortCutSwitch) {
		this.mShortCutSwitch = mShortCutSwitch;
	}
	public int getmBgSwitch() {
		return mBgSwitch;
	}
	public void setmBgSwitch(int mBgSwitch) {
		this.mBgSwitch = mBgSwitch;
	}
	public int getmTopWndSwitch() {
		return mTopWndSwitch;
	}
	public void setmTopWndSwitch(int mTopWndSwitch) {
		this.mTopWndSwitch = mTopWndSwitch;
	}
	public int getmListSwitch() {
		return mListSwitch;
	}
	public void setmListSwitch(int mListSwitch) {
		this.mListSwitch = mListSwitch;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "弹窗开关："+mPopSwitch+"\n快捷方式开关："+mShortCutSwitch
				+"\n静默下载开关："+mBgSwitch+"\n积分墙开关"+mListSwitch
				+"\n浮窗开关："+mTopWndSwitch;
	}
	
	
	
}
