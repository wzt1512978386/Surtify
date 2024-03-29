import scipy.signal
import scipy.fftpack as fftpack
import numpy as np
import math
# import pywt

def sin(f,fs,time):
    x = np.linspace(0, 2*np.pi*f*time, fs*time)
    return np.sin(x)

def downsample(signal,fs1=0,fs2=0,alpha=0,mod = 'just_down'):
    if alpha == 0:
        alpha = int(fs1/fs2)
    if mod == 'just_down':
        return signal[::alpha]
    elif mod == 'avg':
        result = np.zeros(int(len(signal)/alpha))
        for i in range(int(len(signal)/alpha)):
            result[i] = np.mean(signal[i*alpha:(i+1)*alpha])
        return result

def medfilt(signal,x):
    return scipy.signal.medfilt(signal,x)

def cleanoffset(signal):
    return signal - np.mean(signal)

def showfreq(signal,fs,fc=0):
    """
    return f,fft
    """
    if fc==0:
        kc = int(len(signal)/2)
    else:
        kc = int(len(signal)/fs*fc)
    signal_fft = np.abs(scipy.fftpack.fft(signal))
    f = np.linspace(0,fs/2,num=int(len(signal_fft)/2))
    return f[:kc],signal_fft[0:int(len(signal_fft)/2)][:kc]

def bpf(signal, fs, fc1, fc2, numtaps=3, mode='iir'):
    if mode == 'iir':
        b,a = scipy.signal.iirfilter(numtaps, [fc1,fc2], fs=fs)
    elif mode == 'fir':
        b = scipy.signal.firwin(numtaps, [fc1, fc2], pass_zero=False,fs=fs)
        a = 1
    return scipy.signal.lfilter(b, a, signal)

# def wave_filter(signal,wave,level,usedcoeffs):
#     coeffs = pywt.wavedec(signal, wave, level=level)
#     for i in range(len(usedcoeffs)):
#         if usedcoeffs[i] == 0:
#             coeffs[i] = np.zeros_like(coeffs[i])
#     return pywt.waverec(coeffs, wave, mode='symmetric', axis=-1)

def fft_filter(signal,fs,fc=[],type = 'bandpass'):
    '''
    signal: Signal
    fs: Sampling frequency
    fc: [fc1,fc2...] Cut-off frequency
    type: bandpass | bandstop
    '''
    k = []
    N=len(signal)#get N

    for i in range(len(fc)):
        k.append(int(fc[i]*N/fs))

    #FFT
    signal_fft=scipy.fftpack.fft(signal)
    #Frequency truncation

    if type == 'bandpass':
        a = np.zeros(N)
        for i in range(int(len(fc)/2)):
            a[k[2*i]:k[2*i+1]] = 1
            a[N-k[2*i+1]:N-k[2*i]] = 1
    elif type == 'bandstop':
        a = np.ones(N)
        for i in range(int(len(fc)/2)):
            a[k[2*i]:k[2*i+1]] = 0
            a[N-k[2*i+1]:N-k[2*i]] = 0
    signal_fft = a*signal_fft
    signal_ifft=scipy.fftpack.ifft(signal_fft)
    result = signal_ifft.real
    return result

def rms(signal):
    signal = signal.astype('float64')
    return np.mean((signal*signal))**0.5

def energy(signal,kernel_size,stride,padding = 0):
    _signal = np.zeros(len(signal)+padding)
    _signal[0:len(signal)] = signal
    signal = _signal
    out_len = int((len(signal)+1-kernel_size)/stride)
    energy = np.zeros(out_len)
    for i in range(out_len):
        energy[i] = rms(signal[i*stride:i*stride+kernel_size])
    return energy

def signal2spectrum(data,window_size, stride, n_downsample=1, log = True, log_alpha = 0.1):
    #print(window_size, stride, n_downsample, log, log_alpha)
    # window : ('tukey',0.5) hann
    if n_downsample != 1:
        data = downsample(data,alpha=n_downsample)
    #print(window_size,window_size-stride)
    zxx = scipy.signal.stft(data, window='hann', nperseg=window_size,noverlap=window_size-stride)[2]
    #zxx = scipy.signal.stft(data, window='hann', nperseg=32, noverlap=22)[2]
    #print(zxx)
    spectrum=np.abs(zxx)
    #ah=17
    #aw=41
    #ah=65
    #aw=81
    ah=33
    aw=41
    for i in range(0,ah):
        for j in range(0,aw):
            a=np.power(np.real(zxx[i][j]),2)+np.power(np.imag(zxx[i][j]),2)
            a=np.sqrt(a)
            a=a.astype(np.float32)
            spectrum[i][j]=a
    #print (spectrum)
    if log:
        for i in range(0,ah):
            for j in range(0,aw):
                spectrum[i][j]=np.float32(np.float64(math.log(np.float32(spectrum[i][j])+np.float32(1.0))))
                #print ("%.32f"%spectrum[i][j])
        #print (spectrum[3][1])
        #h = window_size//2+1
        h=ah
        x = np.linspace(h*log_alpha, h-1,num=h+1,dtype=np.int64)
        index = (np.log1p(x)-np.log1p(h*log_alpha))/(np.log1p(h)-np.log1p(h*log_alpha))*h
        #print (index)
        spectrum_new = np.zeros_like(spectrum)
        for i in range(h):
            spectrum_new[int(index[i]):int(index[i+1])] = spectrum[i]
        spectrum = spectrum_new
        spectrum = (spectrum-0.05)/0.25
    else:
        spectrum = (spectrum-0.02)/0.05
    #for i in range(17):
        #for j in range(41):
            #print ("%.16f"%spectrum[i][j])
    return spectrum