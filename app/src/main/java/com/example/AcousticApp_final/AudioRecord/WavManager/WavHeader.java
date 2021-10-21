package com.example.AcousticApp_final.AudioRecord.WavManager;


import com.example.AcousticApp_final.Utils.UtilByte;

public class WavHeader {
    /**
     * RIFF数据块
     */
    final String riffChunkId = "RIFF";
    int riffChunkSize;
    final String riffType = "WAVE";

    /**
     * FORMAT 数据块
     */
    final String formatChunkId = "fmt ";
    final int formatChunkSize = 16;
    final short audioFormat = 1;
    short channels;
    int sampleRate;
    int byteRate;
    short blockAlign;
    short sampleBits;

    /**
     * FORMAT 数据块
     */
    final String dataChunkId = "data";
    int dataChunkSize;

    public WavHeader(int totalAudioLen, int sampleRate, short channels, short sampleBits) {
        this.riffChunkSize = totalAudioLen;
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.byteRate = sampleRate * sampleBits / 8 * channels;
        this.blockAlign = (short) (channels * sampleBits / 8);
        this.sampleBits = sampleBits;
        this.dataChunkSize = totalAudioLen - 44;
    }

    public byte[] getHeader() {
        byte[] result;
        result = UtilByte.merger(UtilByte.toBytes(riffChunkId), UtilByte.toBytes(riffChunkSize));
        result = UtilByte.merger(result, UtilByte.toBytes(riffType));
        result = UtilByte.merger(result, UtilByte.toBytes(formatChunkId));
        result = UtilByte.merger(result, UtilByte.toBytes(formatChunkSize));
        result = UtilByte.merger(result, UtilByte.toBytes(audioFormat));
        result = UtilByte.merger(result, UtilByte.toBytes(channels));
        result = UtilByte.merger(result, UtilByte.toBytes(sampleRate));
        result = UtilByte.merger(result, UtilByte.toBytes(byteRate));
        result = UtilByte.merger(result, UtilByte.toBytes(blockAlign));
        result = UtilByte.merger(result, UtilByte.toBytes(sampleBits));
        result = UtilByte.merger(result, UtilByte.toBytes(dataChunkId));
        result = UtilByte.merger(result, UtilByte.toBytes(dataChunkSize));
        return result;
    }
}