package com.alex.livertmppushsdk;
//javah -classpath bin/classes -d jni com.alex.livertmppushsdk.FdkAacEncode

public class FdkAacEncode {
	public native int FdkAacInit(int iSampleRate, int iChannel);
	public native byte[] FdkAacEncode(int handle, byte[] buffer);
	public native void FdkAacDeInit(int handle);
	static {
		System.loadLibrary("AvcEncoder");
	}
}
