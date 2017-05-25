package model;

import com.leapmotion.leap.Frame;

public class FrameWithScore {

	private Frame frame;
	private double score;

	public FrameWithScore(Frame f, Double s) {
		frame = f;
		score = s;
	}

	public double getScore() {
		return score;
	}

	public Frame getFrame() {
		return frame;
	}
}
