package com.sundyn.bluesky.hik.util;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Handler;

/**
 * ץ��ʱ��������
 * @author huangweifeng
 * @Data 2013-10-23
 */
public class UtilAudioPlay {

    private static SoundPool     mSoundPool     = null;
    private static UtilAudioPlay mPlayAudioTask = new UtilAudioPlay();
    private static int           mSoundId       = -1;

    private UtilAudioPlay() {
    };

    /**
     * ����Է���������
     * 
     * @param context ������
     * @param rawFile ��Ƶ�ļ�
     * @since V1.0
     */
    public synchronized static void playAudioFile(Context context, int rawFile) {

        if (null == mSoundPool) {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
            mSoundId = mSoundPool.load(context, rawFile, 1);

            new Handler().postDelayed(mPlayAudioTask.new PlayAudioTask1(), 100);
        } else {
            if (-1 != mSoundId)
                mPlayAudioTask.new PlayAudioTask().execute(mSoundId);
        }
    }

    private class PlayAudioTask1 implements Runnable {
        @Override
        public void run() {
            mPlayAudioTask.new PlayAudioTask().execute(mSoundId);
        }
    }

    private class PlayAudioTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... soundId) {
            mSoundPool.play(soundId[0], 1, 1, 1, 0, 1);
            return null;
        }
    }
}
