package com.alex.livertmppushsdk;
//javah -classpath E:\AS_code\LiveRtmpPushSDKDemo\liveRtmpPushSDK\build\intermediates\classes\release -d jni com.alex.livertmppushsdk.LibYUVConvert

public class LibYUVConvert {
	public native byte[] LibYUV420Rotate90(byte[] srcYUV, int iSrcWidth, int iSrcHeight);
	public native byte[] LibYUV420Rotate270(byte[] srcYUV, int iSrcWidth, int iSrcHeight);
	public native byte[] LibNV21toYUV420(byte[] srcYUV, int iSrcWidth, int iSrcHeight);
	static {
		System.loadLibrary("AvcEncoder");
	}
}
