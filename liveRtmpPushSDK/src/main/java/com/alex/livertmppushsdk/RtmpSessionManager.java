package com.alex.livertmppushsdk;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/7/20.
 */
public class RtmpSessionManager {
    public RtmpSessionManager(){

    }

    private final String TAG = "RTMP_SESSION_TMP";
    private  Queue<byte[]> _videoDataQueue = new LinkedList<byte[]>();
    private Lock _videoDataQueueLock = new ReentrantLock();

    private  Queue<byte[]> _audioDataQueue = new LinkedList<byte[]>();
    private Lock _audioDataQueueLock = new ReentrantLock();

    private RtmpSession _rtmpSession = null;
    private int _rtmpHandle = 0;
    private String _rtmpUrl = null;

    private Boolean _bStartFlag = false;

    private Thread _h264EncoderThread = new Thread(new Runnable() {

        private Boolean WaitforReConnect(){
            for(int i=0; i < 500; i++){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(_h264EncoderThread.interrupted() || (!_bStartFlag)){
                    return false;
                }
            }
            return true;
        }
        @Override
        public void run() {
            while (!_h264EncoderThread.interrupted() && (_bStartFlag)) {
                if(_rtmpHandle == 0) {
                    _rtmpHandle = _rtmpSession.RtmpConnect(_rtmpUrl);
                    if(_rtmpHandle == 0){
                        if(!WaitforReConnect()){
                            break;
                        }
                        continue;
                    }
                }else{
                    if(_rtmpSession.RtmpIsConnect(_rtmpHandle) == 0){
                        _rtmpHandle = _rtmpSession.RtmpConnect(_rtmpUrl);
                        if(_rtmpHandle == 0){
                            if(!WaitforReConnect()){
                                break;
                            }
                            continue;
                        }
                    }
                }

                if((_videoDataQueue.size() == 0) && (_audioDataQueue.size()==0)){
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                //Log.i(TAG, "VideoQueue length="+_videoDataQueue.size()+", AudioQueue length="+_audioDataQueue.size());
                for(int i = 0; i < 100; i++){
                    byte[] audioData = GetAndReleaseAudioQueue();
                    if(audioData == null){
                        break;
                    }
                    //Log.i(TAG, "###RtmpSendAudioData:"+audioData.length);
                    _rtmpSession.RtmpSendAudioData(_rtmpHandle, audioData, audioData.length);
                }

                byte[] videoData = GetAndReleaseVideoQueue();
                if(videoData != null){
                    //Log.i(TAG, "$$$RtmpSendVideoData:"+videoData.length);
                    _rtmpSession.RtmpSendVideoData(_rtmpHandle, videoData, videoData.length);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            _videoDataQueueLock.lock();
            _videoDataQueue.clear();
            _videoDataQueueLock.unlock();
            _audioDataQueueLock.lock();
            _audioDataQueue.clear();
            _audioDataQueueLock.unlock();

            if((_rtmpHandle != 0) && (_rtmpSession != null)){
                _rtmpSession.RtmpDisconnect(_rtmpHandle);
            }
            _rtmpHandle  = 0;
            _rtmpSession = null;
        }
    });

    public int Start(String rtmpUrl){
        int iRet = 0;

        _rtmpUrl = rtmpUrl;
        _rtmpSession = new RtmpSession();

        _bStartFlag = true;
        _h264EncoderThread.setPriority(Thread.MAX_PRIORITY);
        _h264EncoderThread.start();

        return iRet;
    }

    public void Stop(){
        _bStartFlag = false;
        _h264EncoderThread.interrupt();
    }

    public void InsertVideoData(byte[] videoData){
        if(!_bStartFlag){
            return;
        }
        _videoDataQueueLock.lock();
        if(_videoDataQueue.size() > 50){
            _videoDataQueue.clear();
        }
        _videoDataQueue.offer(videoData);
        _videoDataQueueLock.unlock();
    }

    public void InsertAudioData(byte[] videoData){
        if(!_bStartFlag){
            return;
        }
        _audioDataQueueLock.lock();
        if(_audioDataQueue.size() > 50){
            _audioDataQueue.clear();
        }
        _audioDataQueue.offer(videoData);
        _audioDataQueueLock.unlock();
    }

    public byte[] GetAndReleaseVideoQueue(){
        _videoDataQueueLock.lock();
        byte[] videoData = _videoDataQueue.poll();
        _videoDataQueueLock.unlock();

        return videoData;
    }

    public byte[] GetAndReleaseAudioQueue(){
        _audioDataQueueLock.lock();
        byte[] audioData = _audioDataQueue.poll();
        _audioDataQueueLock.unlock();

        return audioData;
    }
}
