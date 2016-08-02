package com.alex.livertmppushsdk;
//javah -classpath bin/classes -d jni com.alex.livertmppushsdk.OpenH264Encoder

public class OpenH264Encoder {
	public static final int YUV12_TYPE   = 24;
	public static final int YUV420_TYPE  = 23;
    public native int InitEncode(int nImgWidth, int nImgHight, int nBitrate, int Frmrate, int iYUVType);
    public native byte[] EncodeH264frame(int iHandle, byte[] inbuf_ptr);
    public native void DeInitEncode(int iHandle);
    static {
        System.loadLibrary("AvcEncoder");
    }
}
