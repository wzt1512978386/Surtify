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

'''
1.录取敲击声音频，音频中包含多个敲击声
2.将数据流中的多个敲击声切割出来，转换为stft矩阵，进行组合为3通道
3.load模型
4.将数据输入进模型进行测试
5.输出识别结果
'''

def get_wavedata(wavfile):
    '''
        输入文件路径，获取处理好的 N-2 左右声部数组
    '''
    #####1.读入wave文件
    wf = wave.open(wavfile, "rb")  # 打开wav
    params = wf.getparams()  # 参数获取
    nchannels, sampwidth, framerate, nframes = params[:4]
    str_data = wf.readframes(nframes)
    wf.close()  # 关闭wave

    #####2.将波形数据转换为数组
    # N-1 一维数组，右声道接着左声道
    wave_data = np.fromstring(str_data, dtype=np.short)
    # 2-N N维数组
    wave_data.shape = -1, 2
    # 将数组转置为 N-2 目标数组
    wave_data = wave_data.T
    return wave_data

def contains(list, str):
    result = False
    for i in list:
        if i in str:
            result = True
            break
        else:
            pass
    return result


def get_framerate(wavfile):
    '''
        输入文件路径，获取帧率
    '''
    wf = wave.open(wavfile, "rb")  # 打开wav
    params = wf.getparams()  # 参数获取
    nchannels, sampwidth, framerate, nframes = params[:4]
    return framerate


def get_nframes(wavfile):
    '''
        输入文件路径，获取帧数
    '''
    wf = wave.open(wavfile, "rb")  # 打开wav
    params = wf.getparams()  # 参数获取
    nchannels, sampwidth, framerate, nframes = params[:4]
    return nframes


def get_ncha_samp_frr_nfrs(wavfile):
    wf = wave.open(wavfile, "rb")  # 打开wav
    params = wf.getparams()  # 参数获取
    nchannels, sampwidth, framerate, nframes = params[:4]
    wf.close()
    return nchannels, sampwidth, framerate, nframes


def ToTensor(data,target=None,gpu_id=0):
    if target is not None:
        data = torch.from_numpy(data).float()
        target = torch.from_numpy(target).long()
        if gpu_id != -1:
            data = data.cuda()
            target = target.cuda()
        return data,target
    else:
        data = torch.from_numpy(data).float()
        if gpu_id != -1:
            device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
            data = data.to(device)
        return data

def read_data(i):
    global list1;
    x=list1[i];
    return np.array(x,float);
def seg_data(wavfile,tap_num = 3,threshold = 30):
    # 遍历文件开始处理
    distance = 5
    global list1;
    list1=[];
    data = get_wavedata(wavfile).T
    Fs = get_framerate(wavfile)
    data = data[:, 0:1]
    data = data[round(0.25 * Fs):-1]

    # 给出帧长和帧移
    wlen = 10
    inc = 5
    # 信号长度
    N = len(data)
    time = N / Fs
    Xtime1 = np.linspace(0, time, N, endpoint=True)

    # 求出RMSE值
    En, fn, frameTime = calRMSE.func_calRMSE(wlen, inc, data, Fs)
    Xtime2 = np.linspace(0, time, fn, endpoint=True)
    Yavr = np.ones(fn) * np.max(En) * 0.05

    # 画图观察，在app端显示的话则不需要把图plot出来，可以直接存入本地，然后选择文件夹读入
    #fig = pl.figure()
    #pl.title("Time-frequency Figure")
    #p1 = fig.add_subplot(211)
    #p2 = fig.add_subplot(212)

    #p1.plot(Xtime1, data)
    #p2.plot(Xtime2, En)
    #pl.show()
    #Time-frequency 两个图的x和y
    data2=data.reshape(1,-1);
    #list1.append(Xtime1);
    #list1.append(data2[0]);
    #list1.append(Xtime2);
    #list1.append(En);
    # 根据En进行粗切
    entotime = []

    En = np.array(En)
    pks, locs = En[signal.argrelextrema(En, np.greater)], signal.argrelextrema(En, np.greater)
    # pks对应峰值，locs对应峰值位数

    locs = np.array(locs)
    locs = locs[0]

    sorted_indices = np.argsort(pks)
    sort_pks = pks[sorted_indices][::-1]  # 从大到小
    sort_loc = locs[sorted_indices][::-1]

    for j in range(len(sort_pks)):
        t = ((sort_loc[j] - 1) * inc + wlen / 2) / Fs  # 将尖峰对应的帧数转换为对应的时间
        entotime.append(t)

    entotime2 = np.sort(entotime[0:tap_num + threshold])  # 对选出的最大的前前tap_num + threshold个候选点进行排序
    entotime3 = []  # 存储最终的tap_num个尖峰

    gap = 0.1;

    for j in range(tap_num + threshold - 1):
        # 若前后相邻两个尖峰出现的时间点大于0.005s则认为他们不来自同一敲击声
        if abs(entotime2[j + 1] - entotime2[j]) > gap:
            entotime3.append(entotime2[j])  # 存储进entotime3



    if len(entotime3) < tap_num:
        return np.array([[[0]]], float)
    #    entotime3.append(entotime2[tap_num + threshold - 1])
    #if len(entotime3) < tap_num:
     #   return np.array([[[0]]], float)

    try:
        count = 0
        for j in range(tap_num + threshold - 1):
            if count == 0:
                if entotime3[count] >= entotime2[j] and entotime3[count] - entotime2[j] < 0.006:
                    entotime3[count] = entotime2[j]
                    count = count + 1
            else:
                if entotime3[count] >= entotime2[j] and entotime3[count] - entotime2[j] < 0.006 and abs(
                        entotime3[count - 1] - entotime2[j]) > 0.006:
                    entotime3[count] = entotime2[j]
                    count = count + 1
            if count == tap_num:
                break
    except IndexError:
        # 两种可能，一种是敲击力度较小，一种是用户未敲击够数目
        print('===============================================================')
        print('Please tap three times on this surface.')
        print('===============================================================\n')

    t1 = 0
    t2 = 0
    multi_data = []

    for j in range(tap_num):
        if j == 0:
            t1 = 0.02
        else:
            t1 = t2
        if (j == tap_num - 1):
            t2 = Xtime1[-1]
        else:
            t2 = (entotime3[j] + entotime3[j+1]) / 2


        # t1 = entotime3[j] - 0.15
        # t2 = entotime3[j] + 0.25
        touchsound = data[int(round(t1 * Fs)):int(round(t2 * Fs)) + 1]

        # 观察第一次粗切的结果
        N_cut = len(touchsound)
        time_cut = N_cut / Fs
        Xtime_cut = np.linspace(0, time_cut, N_cut, endpoint=True)

        #plt.close()
        #plt.figure()

        #plt.plot(Xtime_cut, touchsound)
        #plt.title("touchsound")
        #plt.show()
        touchsound2=touchsound.reshape(1,-1);
        #list1.append(Xtime_cut);
        #list1.append(touchsound2[0]);
        # 对粗切过后的结果寻找能量值最大的尖峰，然后按照固定窗口前后进行切割



        # 信号长度
        # N_1 = len(touchsound)
        # time_1 = N_1 / Fs
        # Xtime1 = np.linspace(0, time_1, N_1, endpoint=True)

        # 求出RMSE值
        En_1, fn_1, frameTime_1 = calRMSE.func_calRMSE(wlen, inc, touchsound, Fs)


        En_1 = np.array(En_1)
        max_locs = np.argmax(En_1)
        t_max = ((max_locs - 1) * inc + wlen / 2) / Fs

        up_limit = 0.0025
        down_limit = 0.0065
        t1_2 = t_max - up_limit
        t2_2 = t_max + down_limit
        touchsound_final = touchsound[int(round(t1_2 * Fs)):int(round(t2_2 * Fs)) + 1].reshape(-1)

        # 观察结果
        N_final = len(touchsound_final)
        time_final = N_final / Fs
        Xtime_final = np.linspace(0, time_final, N_final, endpoint=True)

        #plt.close()
        #plt.figure()
        #plt.plot(Xtime_final, touchsound_final)
        #plt.title("touchsound_final")
        #plt.show()
        touchsound_final2=touchsound_final.reshape(1,-1);
        list1.append(Xtime_final);
        list1.append(touchsound_final2[0]);


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
        # [f, t, Zxx] = signal.spectrogram(touchsound_final, 44100, window="hamm", nperseg=window_size, noverlap=stft_stride, detrend=False)
        spectrum = dsp.signal2spectrum(touchsound_final, window_size,stft_stride, stft_n_downsample, not stft_no_log)

        """
        plt.close()
        plt.figure()
        # y_max = np.max(spectrum)
        plt.xlim([2, spectrum.shape[1]])
        plt.ylim([0, spectrum.shape[0]])
        # plt.axis("off")
        plt.imshow(spectrum)
        # plt.savefig();
        #print(spectrum)
        """
        multi_data.append(spectrum)
    multi_data1 = np.array(multi_data, dtype = np.float32)
    ch = 3
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
