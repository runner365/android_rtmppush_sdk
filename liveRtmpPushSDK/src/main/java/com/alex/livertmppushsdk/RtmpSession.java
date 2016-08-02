package com.alex.livertmppushsdk;
//javah -classpath E:\AS_code\LiveRtmpPushSDKDemo\liveRtmpPushSDK\build\intermediates\classes\release -d jni com.alex.livertmppushsdk.RtmpSession

/**
 * Created by shiwei on 2016/7/19.
 */
public class RtmpSession {
    public native int RtmpConnect(String rtmpUrl);
    public native int RtmpIsConnect(int handle);
    public native int RtmpSendVideoData(int handle, byte[] videoData, long lLen);
    public native int RtmpSendAudioData(int handle, byte[] audioData, long lLen);
    public native void RtmpDisconnect(int handle);
    static {
        System.loadLibrary("AvcEncoder");
        System.loadLibrary("rtmp");
    }
}
