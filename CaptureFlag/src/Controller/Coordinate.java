package Controller;

import lejos.geom.Point;

/**
 * Cartesian Coordinate
 * @author Joanna
 *
 */
public class Coordinate{
	private double x;
	private double y;
	
	//constructor
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
}