package view;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Matrix;
import com.leapmotion.leap.Vector;
import javafx.scene.Node;
import model.SerializedTargetHand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DebugHelper {

    public static ViewMath vm;

    public static void printNodeInfo(Node n, String msg) {
        System.out.println("------------- " + msg + " Node Info -------------");
        System.out.println("Node: \t" + n);
        System.out.println("layout (x, y): \t(" + n.getLayoutX() + ", " + n.getLayoutY() + ")");
        System.out.println("translate: \t(" + n.getTranslateX() + ", " + n.getTranslateY() + ", " + n.getTranslateZ() + ")");
        System.out.println("getRotate(): " + n.getRotate());
        System.out.println("getRotationAxis(): " + n.getRotationAxis());
        System.out.println("------------- End -------------");
    }


    //helper static methods:
    public static void printHandInfo(Hand h, String msg) {
        System.out.println("--------------------------------------------" + msg);
        Vector d = h.direction();
        System.out.println("direction (rounded u.vec): " + roundVector(d));
        System.out.println("d normalized: " + roundVector(d.normalized()));
//        System.out.println("opposite direction: " + roundVector(h.direction().opposite()));
        Matrix basis = h.basis();
        Vector xBasis = roundVector(basis.getXBasis());
        Vector yBasis = roundVector(basis.getYBasis());
        Vector zBasis = roundVector(basis.getZBasis());
        Vector origin = roundVector(basis.getOrigin());
        System.out.println("basis matrix: ");// + basis);
        System.out.println("(x-Basis): " + xBasis);
        System.out.println("(y-Basis): " + yBasis);
        System.out.println("(z-Basis): " + zBasis);
        System.out.println("(origin): " + origin);
        float pitch = d.pitch();
        float yaw = d.yaw();
        float roll1 = d.roll();
        Vector pn = h.palmNormal();
        float roll2 = pn.roll();
        System.out.println("palmNormal: " + roundVector(pn));
        System.out.println("pitch, yaw, roll in radians");
        System.out.println("pitch: " + pitch);
        System.out.println("yaw: " + yaw);
        System.out.println("roll(d): " + roll1);
        System.out.println("roll(pn): " + roll2);

        pitch = roundFloat((float) Math.toDegrees(pitch));//casting to Float different from casting to float. primitive vs object types
        yaw = roundFloat((float) Math.toDegrees(yaw));
        roll1 = roundFloat((float) Math.toDegrees(roll1));
        roll2 = roundFloat((float) Math.toDegrees(roll2));
        System.out.println("pitch, yaw, roll in degrees");
        System.out.println("pitch: " + pitch);
        System.out.println("yaw: " + yaw);
        System.out.println("roll(d): " + roll1);
        System.out.println("roll(pn): " + roll2);

        //weight by magnitude of projections into the respective planes
        float pitchWeighted = getWeightedPYR(d, "pitch", true, true);
        float yawWeighted = getWeightedPYR(d, "yaw", true, true);
        float rollWeighted = getWeightedPYR(d, "roll", true, true);
        float rollWeightedPN = getWeightedPYR(pn, "roll", true, true);//roll for palm normal
        System.out.println("pitchWeighted: " + pitchWeighted);
        System.out.println("yawWeighted: " + yawWeighted);
        System.out.println("rollWeighted (d): " + rollWeighted);
        System.out.println("rollWeighted (pn): " + rollWeightedPN);

        System.out.println("palmPosition: " + roundVector(h.palmPosition()));
        System.out.println("stabilizedPalmPosition: " + roundVector(h.stabilizedPalmPosition()));
        System.out.println("palmWidth: " + roundFloat(h.palmWidth()));
        System.out.println("wristPosition: " + roundVector(h.wristPosition()));
        Vector negZaxis = Vector.zAxis().opposite(); //seems to give -0s shouldnt be a problem. but its kinda weird.
        System.out.println("learning about pitch, roll yaw: vector =" + negZaxis + " \t pitch: " + negZaxis.pitch() + " \t roll: " + negZaxis.roll() + " \t yaw: " + negZaxis.yaw());
        System.out.println("-------------------------------------------- END");
    }


    //given a vector and a possible choice of "pitch", "roll", "yaw" this returns the weighted such angle
    //in degrees and rounded if specified
    public static float getWeightedPYR(Vector d, String type, boolean useDegrees, boolean round) {
        d = d.normalized();

        //anglePYR is angle for pitch yaw or roll. depending on type passed in
//        float anglePYR = d.pitch(); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  WRONG!
        float anglePYR = 0.0f;

        Vector proj = d; //initially set proj to just d passed in

        //really set the projection
        if (type.equalsIgnoreCase("pitch")) {
            anglePYR = d.pitch();
            proj = getProjection(d, "YZ");
        } else if (type.equalsIgnoreCase("yaw")) {
            anglePYR = d.yaw();
            proj = getProjection(d, "XZ");
        } else if (type.equalsIgnoreCase("roll")) {
            anglePYR = d.roll();
            //correct roll angle
            anglePYR = correctRoll_Radians(anglePYR);
            proj = getProjection(d, "XY");
        } else {
            System.out.println("\n \n Wrong angle type passed in !!! \n \n ");
        }

        //weight the angle
        float w = proj.magnitude();
        anglePYR = anglePYR * w;//weight to multiply pitch/yaw/roll by. cuz direction is unit vector

        if (useDegrees) {
            anglePYR = (float) Math.toDegrees(anglePYR);// converted to degrees
        }//else doesnt do anything. pitch is already in radians

        if (round) {
            return roundFloat(anglePYR);
        } else {
            return anglePYR;
        }
    }

    //this method returns the projection vector onto a specified plane.
    //planes for angles pitch -> yz;    yaw -> xz;      roll -> xy
    public static Vector getProjection(Vector d, String plane) {
        Vector v = new Vector(d.getX(), d.getY(), d.getZ());
        if (plane.equalsIgnoreCase("YZ")) {
            v.setX(0);
        } else if (plane.equalsIgnoreCase("XZ")) {
            v.setY(0);
        } else if (plane.equalsIgnoreCase("XY")) {
            v.setZ(0);
        } else {
            System.out.println("\n \n Error! wrong plane specified \n \n");
        }
        //System.out.println("getProjection(pitch:yz; yaw:xz; roll:xy): " + plane + " " + v + " mag (weight): " + v.magnitude());
        return v;
    }

    //returns a vector rounded to 2 decimal points
    public static Vector roundVector(Vector u) {
        float x = roundFloat(u.getX());
        float y = roundFloat(u.getY());
        float z = roundFloat(u.getZ());
        return new Vector(x, y, z);
    }

    //default rounding precision
    public static float roundFloat(float d) {
        return roundFloat(d, 2);
    }

    public static float roundedAngleDegrees(float a) {
        return roundFloat((float) Math.toDegrees(a));
    }

    //more generic float rounding method
    public static float roundFloat(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    public static void printInfoManyHands() {
        String str1 = "targets/2015-05-05 08-17-01.hand"; //normal facing up(1)
        String str2 = "targets/2017-06-12 12-13-58.hand"; //should be very close to the 0th hand. normal facing up(2)
        String str3 = "targets/2017-06-12 12-18-33.hand"; //palm facing downwards
        String str4 = "targets/2017-06-12 12-21-01.hand"; //palm facing downwards and fingers pointing to right
        String str5 = "targets/2017-06-12 12-23-19.hand"; //right hand palm, upwards
        String str6 = "targets/2017-06-12 12-30-56.hand"; //palm facing down again
        List<String> msges = new ArrayList<String>(Arrays.asList("normal facing up(1)", "normal facing up(2)", "facing down", "facing down, fingers to right", "right hand palm upright", "facing down 2"));
        List<String> files = new ArrayList<String>(Arrays.asList(str1, str2, str3, str4, str5, str6));

        for (int i = 0; i < msges.size(); i++) {
            Hand h = null;
            try {
                h = SerializedTargetHand.readFromFile(files.get(i));
                printHandInfo(h, msges.get(i));
                if (i == 3) {
                    System.out.println("** special pitch calculations **");
                    Vector d = h.direction();
                    float p = d.pitch();
                    Vector projectionOntoYZplane = new Vector(0, d.getY(), d.getZ());
                    System.out.println("dir: " + d + " \t prjYZ: " + projectionOntoYZplane + " \n p(radians): " + p + " \t (deg): " + Math.toDegrees(p));
                    Vector negZAxis = Vector.zAxis().opposite();
                    float angBtw = negZAxis.angleTo(projectionOntoYZplane);
                    System.out.println("negZAxis: " + negZAxis + " \t angBtw(-z, proj): " + angBtw + " \t angBtw(deg): " + Math.toDegrees(angBtw));

                    //multiply by percentage of magnitude.
                    float fullMag = d.magnitude();
                    float projYZmag = projectionOntoYZplane.magnitude();
                    System.out.println("dMagnitude: " + fullMag + " \t projMag" + projYZmag);
                    float angBtwWeighted = angBtw * projYZmag;
                    float angBtwDegW = (float) Math.toDegrees(angBtw) * projYZmag;
                    System.out.println("angW (rad): " + angBtwWeighted + " \t angW (deg): " + Math.toDegrees(angBtwWeighted) + " \t convertDeg 1st: " + angBtwDegW);
                    System.out.println("*** End special *** ");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //this method prints info about different vectors and their respective pitch, roll, and yaw angles to learn more about
    //how lm calculates these angles.
    public static void runVectorDebugTests() {

        // tests on z-axis
        printVectorOrientationAngles(new Vector(0, 0, -1.0f), "Negative Z Axis (lmcs)");
        printVectorOrientationAngles(new Vector(0.05f, 0.05f, -0.9f), "Aaaalmost Negative Z Axis (lmcs)");
        printVectorOrientationAngles(new Vector(0.01f, 0.09f, -0.9f), "Aaaalmost Negative Z Axis (more skewed) (lmcs)");
        printVectorOrientationAngles(new Vector(0.01f, -0.09f, -0.9f), "Aaaalmost Negative Z Axis, more skewed, -y  (lmcs)");


//
//        System.out.println("Why is the roll 180?!! \n \n");
//        // z-axis angles (especially roll is weird
//        Vector almostNegZAxis = new Vector(0.05f, 0.05f, -0.9f);
//        almostNegZAxis = almostNegZAxis.normalized();
//        ViewMath.printVectorOrientationAngles(almostNegZAxis, "Aaaalmost Negative Z Axis (lmotion)");
//        System.out.println("Why is the roll 135?!! ");
//
//        Vector projectionXY = ViewMath.getProjection(almostNegZAxis, "XY");
//        float angle = Vector.yAxis().angleTo(projectionXY);
//        System.out.println("y axis in lm??!!: " + Vector.yAxis() + " \n" + "angle btw yaxis and projection: " + projectionXY + " angle(radians): " + angle + " angle(deg): " + Math.toDegrees(angle));
//        System.out.println("so, the Roll angle seems to be between the javafx y-axis (which points down) and projection onto the xy plane. because 135+45 = 180 \n");
//
//        Vector test2 = new Vector(0.01f, 0.09f, -0.9f);
//        test2 = test2.normalized();
//        ViewMath.printVectorOrientationAngles(test2, "Aaaalmost Negative Z Axis, a more skewed projection (lmotion)");
//        Vector ptest2 = ViewMath.getProjection(test2, "XY");
//        float ang2 = Vector.yAxis().angleTo(ptest2);
//        System.out.println("y axis in lm(more like javafx): " + Vector.yAxis() + " \n" + "angle btw yaxis and projection: " + ptest2 + " angle(radians): " + ang2 + " angle(deg): " + Math.toDegrees(ang2));
//
//        Vector test3 = new Vector(0.01f, -0.09f, -0.9f);
//        test3 = test3.normalized();
//        ViewMath.printVectorOrientationAngles(test3, "Aaaalmost Negative Z Axis, -y direction");
//        Vector ptest3 = ViewMath.getProjection(test3, "XY");
//        float ang3 = Vector.yAxis().angleTo(ptest3);
//        System.out.println("y axis in lm(more like javafx): " + Vector.yAxis() + " \n" + "angle btw yaxis and projection: " + ptest3 + " angle(radians): " + ang3 + " angle(deg): " + Math.toDegrees(ang3));
//        System.out.println("\n");
//
//        System.out.println("\n");
//
//
//        // y-axis
//        ViewMath.printVectorOrientationAngles(new Vector(0, 1, -0.0f), "Positive Y Axis (lmotion)");
//
//
//        // x-axis -- -0.0f?!!
//        ViewMath.printVectorOrientationAngles(new Vector(1, 0, 0.0f), "Positive X Axis with POSITIVE 0 for zAxis (lm cs)", true);
//        //what the fudge! there is such thing as a negative zero.
//        ViewMath.printVectorOrientationAngles(new Vector(1, 0, -0.0f), "Positive X Axis with NEGATIVE -0! for zAxis (lm cs (supposedly))", true);
//
//        Vector almostXAxis = new Vector(0.9f, 0.05f, -0.05f); //note the direction change in the z axis.
//        almostXAxis = almostXAxis.normalized();
//        ViewMath.printVectorOrientationAngles(almostXAxis, "Aaaalmost Positive X Axis, with slight z, y coordinates. (lmotion)", true);
//        System.out.println("note the high pitch. this is what makes me think i need to multiply it by the weight of the projection? in addition, note the NEGATIVE z direction \n");
//
//        ViewMath.printVectorOrientationAngles(new Vector(0.5f, 0, -0.5f), "X(-Z) plane 45 degree Axis (lm_cs)", true);
//
//        Vector xz45deg = new Vector(0.5f, 0.03f, -0.5f).normalized();
//        ViewMath.printVectorOrientationAngles(xz45deg, "X(-Z) plane 45 degree Axis with slight y direction. Note the pitch. (lm_cs)", true);
//
//        Vector xyz45deg = new Vector(0.5f, 0.5f, -0.5f).normalized();
//        ViewMath.printVectorOrientationAngles(xyz45deg, "XY(-Z) plane 45 degrees all. Note the pitch. (lm_cs)", true);
//
//        Vector xy45deg = new Vector(0.5f, 0.5f, -0.0f).normalized();
//        ViewMath.printVectorOrientationAngles(xy45deg, "xy 45 degrees (lm cs)", true);
//
//        Vector xy_smallz45deg = new Vector(0.5f, 0.5f, -0.1f).normalized();
//        ViewMath.printVectorOrientationAngles(xy_smallz45deg, "xy smallZ 45 degrees (lm cs)", true);


    }


    public static void printVectorOrientationAngles(Vector v, String vectorName) {
        printVectorOrientationAngles(v, vectorName, true);
    }


    //expects roll being passed in to be in Radians
    public static float correctRoll_Radians(float rollToCorrect) {
        return (float) (Math.PI) - rollToCorrect;
    }

    //expects roll being passed in to be in Radians
    public static float correctRoll_Degrees(float rollToCorrect) {
        return 180.0f - rollToCorrect;
    }

    public static void printVectorOrientationAngles(Vector v, String vectorName, boolean weighProjections) {
        System.out.println("------------- " + vectorName + " Orientation Info -------------");
        Vector vn = v.normalized();
        System.out.println("\t\t\tVector: " + v + "\n\t\t\tVector Normalized: " + vn);

        //pitch info
        float p = roundedAngleDegrees(v.pitch());
        System.out.println("pitch: \t" + p);
        Vector axis = new Vector(0, 0, -1);
        Vector prj = roundVector(getProjection(v, "YZ"));
        float angleBtw = roundedAngleDegrees(axis.angleTo(prj));
        System.out.println("Angle btw neg-ZAxis" + axis + " and projection onto YZ-plane" + prj + ":\n\t" + angleBtw);

        //roll info
        float cr = roundedAngleDegrees(correctRoll_Radians(v.roll()));
        System.out.println("roll (corrected): \t" + cr); //only show corrected roll from now on
        axis = new Vector(0, 1, 0);
        prj = roundVector(getProjection(v, "XY"));
        angleBtw = roundedAngleDegrees(axis.angleTo(prj));
        System.out.println("Angle btw YAxis" + axis + " and projection onto XY-plane" + prj + ":\n\t" + angleBtw);

        //yaw info
        float y = roundedAngleDegrees(v.yaw());
        System.out.println("yaw: \t" + y);
        axis = new Vector(0, 0, -1);
        prj = roundVector(getProjection(v, "XZ"));
        angleBtw = roundedAngleDegrees(axis.angleTo(prj));
        System.out.println("Angle btw neg-ZAxis" + axis + " and projection onto XZ-plane" + prj + ":\n\t" + angleBtw);

        if (weighProjections) {
            float wp = getWeightedPYR(v, "pitch", true, true);
            float wcr = getWeightedPYR(v, "roll", true, true); //only have correct roll method.
            float wy = getWeightedPYR(v, "yaw", true, true);
            System.out.println("\nNon-weighted angles and WEIGHTED by the projection size, after vector has been normalized");
            System.out.println("pitch: \t\t" + p + "\t\tpitch(weighted): \t" + wp);
            System.out.println("roll(cr): \t" + cr + "\t\troll(cr,weighted): \t" + wcr);
            System.out.println("yaw: \t\t" + y + "\t\tyaw(weighted): \t\t" + wy);
        }
        System.out.println("------------- End -------------");
    }


}
