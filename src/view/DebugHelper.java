package view;

import javafx.scene.Node;


public class DebugHelper {


    public static void printNodeInfo(Node n, String msg){
        System.out.println("------------- " + msg + " Node Info -------------");
        System.out.println("Node: \t" + n);
        System.out.println("layout (x, y): \t(" + n.getLayoutX() + ", " + n.getLayoutY() + ")");
        System.out.println("translate: \t(" + n.getTranslateX() + ", " + n.getTranslateY() + ", " + n.getTranslateZ() + ")");
        System.out.println("getRotate(): " + n.getRotate());
        System.out.println("getRotationAxis(): " + n.getRotationAxis());
        System.out.println("------------- End -------------");
    }

}
