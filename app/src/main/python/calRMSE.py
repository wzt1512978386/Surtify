import numpy as np
import wave
import matplotlib.pyplot as plt
import signal as sg


def func_calRMSE(wlen, inc, data, Fs):
    """
    计算短时累计能量
    :param wlen: 帧长
    :param inc: 帧移
    :param data: 待计算RMSE值的数据
    :param Fs:
    :return:
    """

    En = []
    win = np.hanning(wlen)
    N = len(data)
    X = enframe(data, wlen, inc, win).T
    fn = np.size(X,1)

    for il in range(fn):
        u = X[:,il]
        u2 = u * u
        En.append(float(np.sum(u2)))

    frameTime = frame2time(fn,wlen,inc,Fs)

    return En, fn, frameTime


def enframe(signal, wlen, inc, winfunc):
    '''
    将音频信号转化为帧。
    参数含义：
    signal:原始音频型号
    wlen:每一帧的长度(这里指采样点的长度，即采样频率乘以时间间隔)
    inc:相邻帧的间隔（同上定义）
    '''
    signal_length = len(signal)  # 信号总长度
    if signal_length <= wlen:  # 若信号长度小于一个帧的长度，则帧数定义为1
        nf = 1
    else:  # 否则，计算帧的总长度
        nf = int(np.ceil((1.0 * signal_length - wlen + inc) / inc))

    pad_length = int((nf - 1) * inc + wlen)  # 所有帧加起来总的铺平后的长度
    zeros = np.zeros((pad_length - signal_length, 1))  # 不够的长度使用0填补，类似于FFT中的扩充数组操作
    pad_signal = np.concatenate((signal, zeros))  # 填补后的信号记为pad_signal


    indices = np.tile(np.arange(0, wlen), (nf, 1)) + np.tile(np.arange(0, nf * inc, inc),
                                                             (wlen, 1)).T  # 相当于对所有帧的时间点进行抽取，得到nf*nw长度的矩阵
    indices = np.array(indices, dtype=np.int32)  # 将indices转化为矩阵
    frames = pad_signal[indices]  # 得到帧信号
    win = np.tile(winfunc, (nf, 1))  # window窗函数，这里默认取1
    frames = frames.reshape(win.shape)

    return frames * win  # 返回帧信号矩阵

def frame2time(frameNum, framelen, inc, fs):
    """
    计算分帧后每一帧对应的时间
    :param frameNum: 总帧数
    :param framelen: 帧长
    :param inc: 帧移
    :param fs: 采样频率
    :return: 每一帧的时间，即取这一帧数据中间位置的时间
    """
    return ((np.arange(1,frameNum))*inc+framelen/2)/fs