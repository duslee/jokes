package com.common.as.struct;

public class PaySwitchInfo {

	private int mPaySwitch = 0;
	
	
	
	public int getmPaySwitch() {
		return mPaySwitch;
	}



	public void setmPaySwitch(int mPaySwitch) {
		this.mPaySwitch = mPaySwitch;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "支付开关："+mPaySwitch;
	}
	
	
	
}
