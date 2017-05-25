package view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class AccuracyBar extends Group {
	private static Color shadowColor = Color.color(0, 0, 0, 0.1);
	private static Color transparent = Color.color(0, 0, 0, 0);
	private Rectangle background;
	private Rectangle shadow;
	private Rectangle shadow2;
	private Rectangle bar;
	private double barLeftMost;
	private double barMaxWidth;

	public AccuracyBar(double x, double y, double width, double height) {
		barLeftMost = x + 6;
		barMaxWidth = width - 12;

		background = new Rectangle(x, y, width, height);
		background.setFill(backgroundGradient());
		background.setStroke(shadowColor);
		background.setStrokeType(StrokeType.INSIDE);
		background.setArcWidth(10);
		background.setArcHeight(10);

		shadow = new Rectangle(x, y, width, height);
		shadow.setFill(transparent);
		shadow.setStroke(shadowGradient());
		shadow.setStrokeType(StrokeType.INSIDE);
		shadow.setStrokeWidth(2.0);
		shadow.setArcWidth(10);
		shadow.setArcHeight(10);

		shadow2 = new Rectangle(x, y, width, height);
		shadow2.setFill(transparent);
		shadow2.setStroke(shadowColor);
		shadow2.setStrokeType(StrokeType.INSIDE);
		shadow2.setStrokeWidth(3.0);
		shadow2.setArcWidth(10);
		shadow2.setArcHeight(10);

		bar = new Rectangle(barLeftMost, y + 6, barMaxWidth, height - 12);
		bar.setFill(barGradient(0));
		bar.setStroke(barStrokeGradient());
		bar.setStrokeType(StrokeType.INSIDE);
		bar.setStrokeWidth(2.0);
		bar.setArcWidth(10);
		bar.setArcHeight(10);
		bar.setVisible(false);

		getChildren().addAll(background, shadow, shadow2, bar);
	}

	public void setAccuracy(double accuracy) {
		if (accuracy == 0)
			bar.setVisible(false);
		else {
			double width = accuracy * barMaxWidth;
			double start = barLeftMost + (barMaxWidth - width) / 2;
			bar.setX(start);
			bar.setWidth(width);
			bar.setFill(barGradient(accuracy));
			bar.setVisible(true);
		}
	}

	private LinearGradient backgroundGradient() {
		Stop[] stops = new Stop[] { new Stop(0, Color.LIGHTGREY), new Stop(1, Color.LIGHTGREY.brighter()) };
		return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
	}

	private LinearGradient shadowGradient() {
		Stop[] stops = new Stop[] { new Stop(0, shadowColor), new Stop(1, transparent) };
		return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
	}
	
	private LinearGradient barGradient(double accuracy) {
		double hue = accuracy * 120;
		Stop[] stops = new Stop[] { new Stop(0, Color.hsb(hue, 0.5, 1)), new Stop(0.4, Color.hsb(hue, 1, 1)), new Stop(1, Color.hsb(hue, 1, 0.8)) };
		return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
	}

	private LinearGradient barStrokeGradient() {
		Stop[] stops = new Stop[] { new Stop(0, Color.color(1, 1, 1, 0.2)), new Stop(1, Color.color(0, 0, 0, 0.2)) };
		return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
	}
}
