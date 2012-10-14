/** This class represents a light */
import java.awt.*;
import java.util.*;
import javax.media.opengl.*;

/** THIS FILE HAS CHANGED **/
public class Light extends FPPolygon {

	private double radius = 6; // radius of circle
	/**
	 * name of the extra parameter
	 * 
	 * @returns name of extra parameter (eg "width")
	 */

	boolean isOn = true;

	public boolean isOn() {
		return isOn;
	}

	public void toggle() {
		isOn = !isOn;
	}

	public float[] getPosition() {
		float[] pos = new float[4];
		Point2D p0 = pts2d.get(0);
		pos[0] = (float) p0.x;
		pos[2] = (float) p0.y;
		pos[1] = (float) extra[0];
		pos[3] = 1;
		return pos;
	}

	public float lineWidth() {
		return 2.0f;
	}

	/** add a control point */
	public void addPoint(Point p) {
		if (pts2d.size() < 1) {
			super.addPoint(p);
		}
	}

	/** paint this light into g. */
	public void paint(GL gl, GLDrawable glc) {
		if (fill != null) {
			setColor(gl, fill);
			gl.glDisable(GL.GL_TEXTURE_2D);
		}
		gl.glLineWidth(lineWidth());
		gl.glBegin(GL.GL_POLYGON); // draw the light

		Point2D centre = (Point2D) (pts2d.get(0));

		for (int i = 0; i < 40; i++) {
			double angle = (i / 40.0) * 2 * Math.PI;
			gl.glVertex2d(centre.x + radius * Math.cos(angle), centre.y
					+ radius * Math.sin(angle));
		}
		gl.glEnd();
	}

	public boolean contains(int x, int y) {
		Point2D centre = (Point2D) (pts2d.get(0));
		double dx = x - centre.x;
		double dy = y - centre.y;
		return (Math.sqrt(dx * dx + dy * dy) < radius);
	}

	// removes collide to light object
	@Override
	public Vector3D collide(Vector3D from, Vector3D to){
		return null;
	}
	
	@Override
	public void render3D(GL gl, GLDrawable glc) {
		// should do nothing in this function
	}
	
	// shines light if light is on, else disables it
	public void shineLight(GL gl, int index) {
		if (isOn()) {
			gl.glLightfv(GL.GL_LIGHT1 + index, GL.GL_POSITION, getPosition(), 0);
			gl.glLightfv(GL.GL_LIGHT1 + index, GL.GL_DIFFUSE, getFillColor().getColorComponents(new float[4]), 0); // gets color from fill, one of the parent class
			gl.glEnable(GL.GL_LIGHT1 + index);
		} else {
			// turns off current light
			gl.glDisable(GL.GL_LIGHT1 + index);
		}
	}
}
