/** This class represents a door */
import java.awt.*;
import java.util.*;
import javax.media.opengl.*;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

/** THIS FILE HAS CHANGED **/
public class Door extends Wall {

	private double angle;
	private boolean doorIsOpen;

	public Door () {
		super();
	}
	
	public float lineWidth() {
		return 2.0f;
	}

	@Override
	public Vector3D collide(Vector3D from, Vector3D to) {
		if (doorIsOpen) {
			return null;	//lets avatar move through door
		} else if (contains((int) Math.round(to.x),(int) Math.round(to.z))) {
			return from;
		} else {
			return null;
		}
	}
	
	/** paint this door into g. */
	public void paint(GL gl, GLDrawable glc) {

		if (fill != null) {
			setColor(gl, fill);
			gl.glDisable(GL.GL_TEXTURE_2D);
		}
		gl.glLineWidth(lineWidth());
		gl.glBegin(GL.GL_LINE_STRIP); // draw the wall

		Point2D centre = (Point2D) (pts2d.get(0));
		Point2D rad = (Point2D) (pts2d.get(1));
		double dx = rad.x - centre.x;
		double dy = rad.y - centre.y;
		double radius = Math.sqrt(dx * dx + dy * dy);
		double theta = Math.atan2(dy, dx);

		gl.glVertex2d(centre.x, centre.y);
		for (int i = 0; i < 10; i++) {
			double angle = theta + (i / 10.0) * Math.PI / 2.0;
			gl.glVertex2d(centre.x + radius * Math.cos(angle), centre.y
					+ radius * Math.sin(angle));
		}
		gl.glEnd();
	}

	public void render3D(GL gl, GLDrawable glc) {
		if (pts2d.size() < 2) return;	//doors need 2 points
		if (fill != null || texture != null){
			textureFilling(gl, glc);

			// check for opening or closing animation
			if (angle <= 90 && doorIsOpen) {
				// turn the door by a little bit
				angle++;
			} else if (!doorIsOpen && angle >= 0){
				// turn door the other way
				angle--;
			}

			gl.glPushMatrix();

			Point2D p0 =  pts2d.get(0);
			Point2D p1 =  pts2d.get(1);

			normaliseObject(gl);

			// translation and rotation
			// (since opengl does matrix calc backwards, 
			// do in order of translation from origin, rotation, and translate to origin
			gl.glTranslated(p0.x, 0, p0.y);
			gl.glRotated(-angle, 0, 1, 0);	// to accomodate correct rotation
			gl.glTranslated(-p0.x, 0, -p0.y);
			gl.glBegin( GL.GL_POLYGON );

			gl.glTexCoord2d(0, 0);
			gl.glVertex3d(p0.x, extra[0], p0.y);
			gl.glTexCoord2d(0, 1);
			gl.glVertex3d(p0.x, extra[1], p0.y);

			gl.glTexCoord2d(1, 1); 
			gl.glVertex3d(p1.x, extra[1], p1.y);
			gl.glTexCoord2d(1, 0);
			gl.glVertex3d(p1.x, extra[0], p1.y);

			gl.glEnd();
			gl.glPopMatrix();
		}
	}

	public void toggleDoor() {
		doorIsOpen ^= true;
	}
}
