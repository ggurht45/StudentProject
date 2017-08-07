package controller;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger.Type;
import com.leapmotion.leap.Hand;

public class Comparer {

	static double weight_wrist = 4;
	static double weight_pinky_proximal = 4;
	static double weight_pinky_intermediate = 2;
	static double weight_pinky_distal = 1;
	static double weight_ring_proximal = 4;
	static double weight_ring_intermediate = 2;
	static double weight_ring_distal = 1;
	static double weight_middle_proximal = 4;
	static double weight_middle_intermediate = 2;
	static double weight_middle_distal = 1;
	static double weight_index_proximal = 4;
	static double weight_index_intermediate = 2;
	static double weight_index_distal = 1;
	static double weight_thumb_proximal = 4;
	static double weight_thumb_intermediate = 2;
	static double weight_thumb_distal = 1;

	private double totalWeight(){
		//literally seems to be the addition of all the weights.
		return weight_wrist + weight_pinky() + weight_ring() + weight_middle() + weight_index() + weight_thumb();
	}

	//all the appropriate weights added up.
	private double weight_pinky() {return weight_pinky_proximal + weight_pinky_distal + weight_pinky_intermediate;}
	
	private double weight_ring(){
		return weight_ring_proximal + weight_ring_distal + weight_ring_intermediate;
	}
	
	private double weight_middle(){
		return weight_middle_proximal + weight_middle_distal + weight_middle_intermediate;
	}
	
	private double weight_index(){
		return weight_index_proximal + weight_index_distal + weight_index_intermediate;
	}
	
	private double weight_thumb(){
		return weight_thumb_proximal + weight_thumb_distal + weight_thumb_intermediate;
	}
	
	
	//returns a number between 0,1. cos(ang) ang is less than 90.
	//determines if ANY two angles are close to each other 89,87 would return 1. so would 43, 39. etc.
	private double compareAngles(float angle1, float angle2) {
		double differenceBtwAngles = Math.abs(angle1-angle2);
		double tmp = Math.min(differenceBtwAngles, Math.PI/4); //pi is 180 degrees. so tmp can be at most 45
		return Math.cos(2*tmp); //if tmp is exactly 45, will return 0. cos(90) = 0.
	}

	public double compare(Hand h1, Hand h2) {

		if (h1.isLeft() == h2.isLeft()) {
			double x = 0;
			x += compareAngles(angleWristArm(h1), angleWristArm(h2))* weight_wrist;

			//five fingers "proximal". compare always returns between 0,1. * the weight
			x += compareAngles(anglePinkyProximal(h1), anglePinkyProximal(h2))*weight_pinky_proximal;//0-1*weight. in perfect world match will be 1*weight,
			x += compareAngles(angleRingProximal(h1), angleRingProximal(h2))* weight_ring_proximal;//so if each one matches perfectly... eventually x will
			x += compareAngles(angleMiddleProximal(h1), angleMiddleProximal(h2))* weight_middle_proximal;//equal totalweight. and then at end x/totalweight =1
			x += compareAngles(angleIndexProximal(h1), angleIndexProximal(h2))* weight_index_proximal; //which would be a perfect match
			x += compareAngles(angleThumbProximal(h1), angleThumbProximal(h2))* weight_thumb_proximal;

			//five fingers "intermediate"
			x += compareAngles(anglePinkyIntermediate(h1), anglePinkyIntermediate(h2))* weight_pinky_intermediate;
			x += compareAngles(angleRingIntermediate(h1), angleRingIntermediate(h2))* weight_ring_intermediate;
			x += compareAngles(angleMiddleIntermediate(h1), angleMiddleIntermediate(h2))* weight_middle_intermediate;
			x += compareAngles(angleIndexIntermediate(h1), angleIndexIntermediate(h2))* weight_index_intermediate;
			x += compareAngles(angleThumbIntermediate(h1), angleThumbIntermediate(h2))* weight_thumb_intermediate;

			//five fingers "distal"
			x += compareAngles(anglePinkyDistal(h1), anglePinkyDistal(h2))* weight_pinky_distal;
			x += compareAngles(angleRingDistal(h1), angleRingDistal(h2))* weight_ring_distal;
			x += compareAngles(angleMiddleDistal(h1), angleMiddleDistal(h2))* weight_middle_distal;
			x += compareAngles(angleIndexDistal(h1), angleIndexDistal(h2))* weight_index_distal;
			x += compareAngles(angleThumbDistal(h1), angleThumbDistal(h2))* weight_thumb_distal;

			x /= totalWeight();
			return x;
		} else
			return 0; //not sure why this returns 0. what if both hands are not left?
	}

	// wrist in normal position (i.e. not turned)
	private Boolean wristNormal(Hand h) {
		if (h.isLeft())
			//checks if the rightmost finger is a thumb. so we know left hand is facing down.
			// if its not then wristNormal will return false
			return (h.fingers().rightmost().type() == Type.TYPE_THUMB);
		else
			//see comment above
			return (h.fingers().rightmost().type() == Type.TYPE_PINKY);
	}

	private float normalize(float rawAngle, Hand h) {
		if (wristNormal(h))
			return rawAngle; //if hand is facing downwards, return rawAngle
		else
			return (float) (2 * Math.PI - rawAngle); //flips the angle on x-axis, so to speak, subtracts it from 360. 30 -> 330
	}

	private float angleWristArm(Hand h) {
		float rawAngle = h.arm().direction().angleTo(h.direction());
		return normalize(rawAngle, h);
	}

	private float angleThumbDistal(Hand h) {
		float rawAngle = h.fingers().get(0).bone(Bone.Type.TYPE_DISTAL).direction().angleTo(h.fingers().get(0).bone(Bone.Type.TYPE_INTERMEDIATE).direction());
		return normalize(rawAngle, h);
	}

	private float angleIndexDistal(Hand h) {
		float rawAngle = h.fingers().get(1).bone(Bone.Type.TYPE_DISTAL).direction().angleTo(h.fingers().get(1).bone(Bone.Type.TYPE_INTERMEDIATE).direction());
		return normalize(rawAngle, h);
	}

	private float angleMiddleDistal(Hand h) {
		float rawAngle = h.fingers().get(2).bone(Bone.Type.TYPE_DISTAL).direction().angleTo(h.fingers().get(2).bone(Bone.Type.TYPE_INTERMEDIATE).direction());
		return normalize(rawAngle, h);
	}

	private float angleRingDistal(Hand h) {
		float rawAngle = h.fingers().get(3).bone(Bone.Type.TYPE_DISTAL).direction().angleTo(h.fingers().get(3).bone(Bone.Type.TYPE_INTERMEDIATE).direction());
		return normalize(rawAngle, h);
	}

	private float anglePinkyDistal(Hand h) {
		float rawAngle = h.fingers().get(4).bone(Bone.Type.TYPE_DISTAL).direction().angleTo(h.fingers().get(4).bone(Bone.Type.TYPE_INTERMEDIATE).direction());
		return normalize(rawAngle, h);
	}

	private float angleThumbIntermediate(Hand h) {
		float rawAngle = h.fingers().get(0).bone(Bone.Type.TYPE_INTERMEDIATE).direction().angleTo(h.fingers().get(0).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float angleIndexIntermediate(Hand h) {
		float rawAngle = h.fingers().get(1).bone(Bone.Type.TYPE_INTERMEDIATE).direction().angleTo(h.fingers().get(1).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float angleMiddleIntermediate(Hand h) {
		float rawAngle = h.fingers().get(2).bone(Bone.Type.TYPE_INTERMEDIATE).direction().angleTo(h.fingers().get(2).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float angleRingIntermediate(Hand h) {
		float rawAngle = h.fingers().get(3).bone(Bone.Type.TYPE_INTERMEDIATE).direction().angleTo(h.fingers().get(3).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float anglePinkyIntermediate(Hand h) {
		float rawAngle = h.fingers().get(4).bone(Bone.Type.TYPE_INTERMEDIATE).direction().angleTo(h.fingers().get(4).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	// angle of fingers (proximals) to hand complex(i.e. to the palm)

	private float angleThumbProximal(Hand h) {
		float rawAngle = h.direction().angleTo(h.fingers().get(0).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float angleIndexProximal(Hand h) {
		float rawAngle = h.direction().angleTo(h.fingers().get(1).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float angleMiddleProximal(Hand h) {
		float rawAngle = h.direction().angleTo(h.fingers().get(2).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float angleRingProximal(Hand h) {
		float rawAngle = h.direction().angleTo(h.fingers().get(3).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

	private float anglePinkyProximal(Hand h) {
		//returns the angle between two vectors.
		float rawAngle = h.direction().angleTo(h.fingers().get(4).bone(Bone.Type.TYPE_PROXIMAL).direction());
		return normalize(rawAngle, h);
	}

}
