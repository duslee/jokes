package com.ly.duan.user_inter;

public interface IDownload {

	/** 下载成功 */
	public void onDlSuccess();

	/** 下载失败 */
	public void onDlFailed();

	/** 下载错误 */
	public void onDlError();

}
