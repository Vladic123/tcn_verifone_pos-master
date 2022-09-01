package com.serenegiant.media;
/*
 * libcommon
 * utility/helper classes for myself
 *
 * Copyright (c) 2014-2018 saki t_saki@serenegiant.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;

public interface IRecorder {

    /**
     * キャプチャしていない
     */
    int STATE_UNINITIALIZED = 0;

    /**
     * キャプチャ初期化済(Muxerセット済)
     */
    int STATE_INITIALIZED = 1;

    /**
     * キャプチャ準備完了(prepare済)
     */
    int STATE_PREPARED = 2;

    /**
     * キャプチャ開始中
     */
    int STATE_STARTING = 3;

    /**
     * キャプチャ中
     */
    int STATE_STARTED = 4;

    /**
     * キャプチャ停止要求中
     */
    int STATE_STOPPING = 5;

    /**
     * Encoderの準備
     * 割り当てられているMediaEncoderの下位クラスのインスタンスの#prepareを呼び出す
     *
     * @throws IOException
     */
    void prepare();

    /**
     * キャプチャ開始要求
     * 割り当てられているEncoderの下位クラスのインスタンスの#startRecordingを呼び出す
     */
    void startRecording() throws IllegalStateException;

    /**
     * キャプチャ終了要求
     * 割り当てられているEncoderの下位クラスの#stopRecordingを呼び出す
     */
    void stopRecording();

    Surface getInputSurface();

    Encoder getVideoEncoder();

    Encoder getAudioEncoder();

    /**
     * Muxerが出力開始しているかどうかを返す
     *
     * @return
     */
    boolean isStarted();

    /**
     * エンコーダーの初期化が終わって書き込み可能になったかどうかを返す
     *
     * @return
     */
    boolean isReady();

    /**
     * 終了処理中かどうかを返す
     *
     * @return
     */
    boolean isStopping();

    /**
     * 終了したかどうかを返す
     *
     * @return
     */
    boolean isStopped();

    int getState();

    IMuxer getMuxer();

    void setMuxer(final IMuxer muxer);

    @Nullable
    String getOutputPath();

    @Nullable
    DocumentFile getOutputFile();

    void frameAvailableSoon();

    /**
     * 関連するリソースを開放する
     */
    void release();

    void addEncoder(final Encoder encoder);

    void removeEncoder(final Encoder encoder);

    boolean start(final Encoder encoder);

    void stop(final Encoder encoder);

    int addTrack(final Encoder encoder, final MediaFormat format);

    void writeSampleData(final int trackIndex,
                         final ByteBuffer byteBuf, final MediaCodec.BufferInfo bufferInfo);

    interface RecorderCallback {
        void onPrepared(IRecorder recorder);

        void onStarted(IRecorder recorder);

        void onStopped(IRecorder recorder);

        void onError(Exception e);
    }

    /**
     * キャプチャ終了
     */
//	public static final int STATE_STOPPED = 6;

    @IntDef({STATE_UNINITIALIZED, STATE_INITIALIZED, STATE_PREPARED,
            STATE_STARTING, STATE_STARTED, STATE_STOPPING})
    @Retention(RetentionPolicy.SOURCE)
    @interface RecorderState {
    }
}