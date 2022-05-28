package org.langrid.service.ml;

public class ContinuousSpeechRecognitionConfig {
	private int channels;
	private int sampleSizeInBits;
	private int sampleRate;  // hertz. 8000, 16000, 44100.

	public ContinuousSpeechRecognitionConfig(){
	}

	public ContinuousSpeechRecognitionConfig(int channels, int sampleSizeInBits, int sampleRate) {
		this.channels = channels;
		this.sampleSizeInBits = sampleSizeInBits;
		this.sampleRate = sampleRate;
	}

	public int getChannels() {
		return channels;
	}
	public void setChannels(int channels) {
		this.channels = channels;
	}
	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}
	public void setSampleSizeInBits(int sampleSizeInBits) {
		this.sampleSizeInBits = sampleSizeInBits;
	}
	public int getSampleRate() {
		return sampleRate;
	}
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	
}
