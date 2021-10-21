import os
import wave
import numpy as np
import calRMSE
import dsp
import pylab as pl
import scipy.signal as signal
import numpy as np
import cv2
import matplotlib.pyplot as plt
import scipy
from scipy.fftpack import fft
from scipy.io import wavfile as wav
from scipy import signal as sig
from scipy.signal import windows

'''
1.录取敲击声音频，音频中包含多个敲击声
2.将数据流中的多个敲击声切割出来，转换为stft矩阵，进行组合为3通道
3.load模型
4.将数据输入进模型进行测试
5.输出识别结果
'''
def read_wav_scipy(wav_path):
    """[使用scipy读取wav]

    Args:
        wav_path ([str]): [路径]

    Returns:
        [np.array, numeric]: [信号及采样率]
    """

    wf = wave.open(wav_path, "rb")  # 打开wav
    params = wf.getparams()  # 参数获取
    nchannels, sampwidth, framerate, nframes = params[:4]   #nframes指bufsize
    #声道数, 量化位数（byte单位）, 采样频率,
    #采样点数, 压缩类型, 压缩类型的描述
    str_data = wf.readframes(nframes)
    wf.close()  # 关闭wave
    #####2.将波形数据转换为数组
    # N-1 一维数组，右声道接着左声道
    wave_data = np.fromstring(str_data, dtype=np.short)
    # 2-N N维数组
    wave_data.shape = -1, 1
    # 将数组转置为 N-2 目标数组
    wave_data = wave_data.T
    #
    #fs, signal = wav.read(wav_path)
    #if(len(signal.shape) > 1):
    #signal = signal[:,0]
    #return_signal = signal
    #
    return_signal=wave_data[0];
    if return_signal.dtype.name == "int16":
        return_signal = return_signal / 32767
    fs=framerate
    return return_signal, fs

def enframe(x, win, inc):
    nx = len(x) # 取数据长度
    nwin = len(win) # 取窗长
    if nwin == 1:   # 判断窗长是否为1，若为1，即表示没有设窗函数
        len_temp = win   # 是，帧长=win
    else:
        len_temp = nwin  # 否，帧长=窗长
    if inc is None: # 如果只有两个参数，设帧inc=帧长
        inc = len_temp
    nf = int(np.fix((nx-len_temp+inc)/inc)) # 计算帧数
    frameout = np.zeros((nf, len_temp)) # 初始化

    indf = np.reshape(inc * np.arange(0, nf), (nf, 1))
    inds = np.reshape(np.arange(0, len_temp), (1, len_temp))

    ## 此处没有matlab的语法糖，无法做到简单复制
    ## Caution: 此处数值已经产生了细微的不同
    # frameout = x[indf[:, np.ones((1, len_temp))] + inds[np.ones((nf, 1)), :]]

    def myfunc(a):
        return x[a]

    # 方式1: 使用vectorize来映射
    # vfunc = np.vectorize(myfunc)
    # frameout = vfunc(np.repeat(indf, len_temp, axis=1) + np.repeat(inds, nf, axis=0))

    # 方式2: 直接遍历来映射
    temp = np.repeat(indf, len_temp, axis=1) + np.repeat(inds, nf, axis=0)
    frameout = np.zeros(np.shape(temp))
    for idx,i in enumerate(temp):
        for idxx,j in enumerate(i):
            frameout[idx, idxx] = myfunc(j)

    if nwin > 1:
        frameout = frameout * win
    return frameout

def vad_specEn(x, wnd, inc, NIS, th1, th2, fs):
    frames = enframe(x, wnd, inc).T  # 分帧
    fn = np.size(frames, axis=1)  # 求帧数
    if len(wnd) == 1:
        wlen = wnd  # 求出帧长
    else:
        wlen = len(wnd)

    df = fs / wlen  # 求出FFT后频率分辨率
    fx1 = int(np.fix(250 / df) + 1)
    fx2 = int(np.fix(3500 / df) + 1)  # 找出250Hz和3500Hz的位置
    km = int(np.floor(wlen / 8))  # 计算出子带个数
    K = 0.5  # 常数K
    Hb = np.zeros((fn))
    for i in range(fn):
        A = np.abs(fft(frames[:, i]))  # 取来一帧数据FFT后取幅值
        E = A[fx1 + 1 : fx2 - 1]  # 只取250～3500Hz之间的分量
        E = E * E  # 计算能量
        P1 = E / np.sum(E)  # 幅值归一化? -> 应该是计算概率
        index = np.argwhere(P1 >= 0.9)  # 寻找是否有分量的概率大于0.9
        if len(index) != 0:
            E[index] = 0  # 若有,该分量置0
        Eb = np.zeros((km))
        for m in range(km):  # 计算子带能量
            Eb[m] = np.sum(E[4 * m : 4 * (m + 1)])
        prob = (Eb + K) / np.sum(Eb + K)  # 计算子带概率
        Hb[i] = -np.sum(prob * np.log(prob + 10 ** -23))

    Enm = sig.medfilt(Hb, 5)  # 1次平滑处理
    for i in range(9):
        Enm = sig.medfilt(Enm, 5)  # 9次平滑处理
    Me = np.min(Enm)  # 设置阈值
    eth = np.mean(Enm[1:NIS])

    Det = eth - Me
    T1 = th1 * Det + Me
    T2 = th2 * Det + Me

    voiceseg, vsl, SF, NF = vad_revr(Enm, T1, T2)

    return voiceseg, vsl, SF, NF, Enm

def vad_revr(dst1, T1, T2):
    fn = len(dst1)  # 取得帧数
    maxsilence = 8  # 初始化
    minlen = 5
    status = 0
    count = [0]
    silence = [0]

    x1 = [0]
    x2 = [0]

    # 开始端点检测
    xn = 0
    for n in range(1, fn):
        if status == 0 or status == 1:  # 0 = 静音, 1 = 可能开始
            if dst1[n] < T2:  # 确信进入语音段
                x1[xn] = np.max((n - count[xn] - 1, 1))
                status = 2
                silence[xn] = 0
                count[xn] += 1
            elif dst1[n] < T1:  # 可能处于语音段
                status = 1
                count[xn] += 1
            else:  # 静音状态
                status = 0
                count[xn] = 0
                x1[xn] = 0
                x2[xn] = 0

        elif status == 2:  # 2 = 语音段
            if dst1[n] < T1:  # 保持在语音段
                count[xn] += 1
            else:  # 语音将结束
                silence[xn] += 1
                if silence[xn] < maxsilence:  # 静音还不够长，尚未结束
                    count[xn] += 1
                elif count[xn] < minlen:  # 语音长度太短，认为是噪声
                    status = 0
                    silence[xn] = 0
                    count[xn] = 0
                else:
                    status = 3
                    x2[xn] = x1[xn] + count[xn]

        elif status == 3:  # 语音结束，为下一个语音准备
            status = 0
            xn += 1
            count.append(0)
            silence.append(0)
            x1.append(0)
            x2.append(0)

    el = len(x1) - 1
    if x1[el] == 0:
        el = el - 1  # 获得x1的实际长度
    if el == 0:
        return None,None,None,None
    if x2[el] == 1:  # 如果x2最后一个值为0，对它设置为fn
        print("Error: Not find endding point!")
        x2[el] = fn
    SF = np.zeros((1, fn))
    NF = np.ones((1, fn))
    for i in range(el+1): # 按x1和x2，对SF和NF赋值
        SF[0, x1[i] : x2[i]] = 1
        NF[0, x1[i] : x2[i]] = 0
    speechIndex = np.argwhere(SF == 1)
    voiceseg = findSegment(speechIndex) # 计算voiceseg
    vsl = len(voiceseg)

    return voiceseg, vsl, SF, NF

def findSegment(express):
    express_temp = [e[0] if e[0] != 0 else e[1] for e in express]
    express = express_temp
    if express[0] == 0:
        voiceIndex = np.argwhere(express != 0)  # 寻找express中为1的位置
    else:
        voiceIndex = express

    class seg_object(object):
        """
        保存begin/end/duration属性
        """

        def __init__(self) -> None:
            pass

    soundSegment = [seg_object()]
    k = 0
    soundSegment[k].begin = voiceIndex[0]  # 设置第一组有话段的起始位置
    for i in range(len(voiceIndex)-1):
        if voiceIndex[i + 1] - voiceIndex[i] > 1:  # 本组有话段结束
            soundSegment[k].end = voiceIndex[i]  # 设置本组有话段的结束位置
            soundSegment.append(seg_object())
            soundSegment[k + 1].begin = voiceIndex[i + 1]  # 设置下一组有话段的起始位置
            k += 1

    soundSegment[k].end = voiceIndex[-1]  # 最后一组有话段的结束位置
    # 计算每组有话段的长度
    for i in range(k+1):
        soundSegment[i].duration = soundSegment[i].end - soundSegment[i].begin + 1

    return soundSegment

def FrameTimeC(frameNum, framelen, inc, fs):
    frameTime = ((np.arange(1, frameNum + 1) - 1) * inc + framelen / 2) / fs
    return frameTime


def read_data(i):
    global list1;
    x=list1[i];
    return np.array(x,float);

def seg_data(wavfile,th1,th2,tap_num = 3,threshold = 30):
    global list1;
    list1=[];
    # 遍历文件开始处理
    signal, fs = read_wav_scipy(wavfile)

    # step2.0: 变量使用及预处理
    IS = 0.7  # 设置前导无话段长度
    wlen = 200  # 设置帧长为25ms
    inc = 80  # 求帧移

    signal = signal[int(np.round(0.25 * fs))-1:]  # 去除开头的硬件噪声?
    signal_normalized = signal / np.max(np.abs(signal))  # 幅值非严格归一 -> [-1, 1]
    N = len(signal_normalized)  # 信号长度
    time = np.arange(0, N) / fs
    wnd = sig.windows.hamming(wlen, sym=False) # 此处matlab的显示精度需要设置，数值应该是一样的
    overlap = wlen - inc
    NIS = int(np.fix((IS * fs - wlen) / inc + 1))
    #th1=0.7;
    #th2=0.8;
    '''
    # step2.1: 阈值选取
    material_list = ["6061", "yCu", "bxg304"]
    # assert material_type in material_list, "材料类型不在列表中，请更换材料类型参数；Material_type not in the list... Please change the \"material_type\" parameter."
    if material_type in material_list:
        th1 = 0.35
        th2 = 0.3
        # th1 = 0.25
        # th2 = 0.2
    else:
        th1 = 0.7
        th2 = 0.8
    '''

    # step3.0: 谱熵法计算
    voiceseg, vsl, SF, NF, Enm = vad_specEn(
        signal_normalized, wnd, inc, NIS, th1, th2, fs
    )  # 谱熵法
    if voiceseg==None and vsl==None:
        return np.array([[[1]]], float)

    fn = np.max(np.size(SF))
    frameTime = FrameTimeC(fn, wlen, inc, fs)  # 计算各帧对应的时间

    # step3.1: 切分开始
    up_cut_time_point = []
    down_cut_time_point = []
    for k in range(vsl):
        nx1 = voiceseg[k].begin
        nx2 = voiceseg[k].end
        up_cut_time_point.append(frameTime[nx1])
        down_cut_time_point.append(frameTime[nx2])
    multi_data=[];
    for i in range(vsl):
        '''
        if loc.find("10cm") + 1:
            t1 = up_cut_time_point[i] - 0.0025
        else: 
            t1 = up_cut_time_point[i] - 0.001
        '''
        t1 = up_cut_time_point[i] - 0.001
        t2 = t1 + 0.009
        touchsound = signal[int(np.round(t1 * fs)) : int(np.round(t2 * fs))]
        # 观察结果

        N_final = len(touchsound)
        time_final = N_final / fs
        Xtime_final = np.linspace(0, time_final, N_final, endpoint=True)

        """
        plt.close()
        plt.figure()
        touchsound_final=touchsound
        plt.plot(Xtime_final, touchsound_final)
        touchsound_final2=touchsound_final.reshape(1,-1);
        list1.append(Xtime_final);
        list1.append(touchsound_final2[0]);
        plt.title("touchsound_final")
        plt.show()
        """

        # 求切割完成的信号的STFT得到时频分析的矩阵，进行多通道的组合
        # window_size = 32
        #window_size = 32
        #stft_stride = 10
        window_size = 64
        stft_stride = 10
        #window_size = 128
        #stft_stride = 5
        stft_n_downsample = 1
        stft_no_log = False
        log_alpha = 0.1
        #print(touchsound)
        # [f, t, Zxx] = signal.spectrogram(touchsound_final, 44100, window="hamm", nperseg=window_size, noverlap=stft_stride, detrend=False)
        spectrum = dsp.signal2spectrum(touchsound, window_size,stft_stride, stft_n_downsample, not stft_no_log)

        #plt.close()#############
        #plt.figure()############
        # y_max = np.max(spectrum)
        #plt.xlim([2, spectrum.shape[1]])########
        #plt.ylim([0, spectrum.shape[0]])#######
        # plt.axis("off")
        #plt.imshow(spectrum)#########
        # plt.savefig();
        #print(spectrum)
        multi_data.append(spectrum)
    multi_data1 = np.array(multi_data, dtype = np.float32)
    ch = 3
    print("vsl : "+str(vsl))
    if vsl<ch :
        return np.array([[[0]]], float)
    #for i in range(ch):
        #print (multi_data1[i]);
        #multi_data1[i] = normliaze(multi_data[i], mode = 'z-score', truncated=5)
        #print (multi_data1[i])
    return np.array(multi_data1,float)


def normliaze(data, mode = 'z-score', sigma = 0, dtype=np.float32, truncated = 2):
    '''
    mode: norm | std | maxmin | 5_95
    dtype : np.float64,np.float16...
    '''
    data = data.astype(dtype)
    data_calculate = data.copy()
    if mode == 'norm':
        result = (data-np.mean(data_calculate))/sigma
    elif mode == 'z-score':
        mu = np.mean(data_calculate)
        sigma = np.std(data_calculate)
        print(np.mean(data_calculate))
        print(np.std(data_calculate))
        result = (data - mu) / sigma

    return result.astype(dtype)
