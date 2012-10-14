/** This class represents a wall */
import java.awt.*;
import java.util.*;
import javax.media.opengl.*;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

/** THIS FILE HAS CHANGED **/
public class Wall extends FPPolygon {
	
	public Wall() {
		super();
	}
	
	public String extraName(int i) {
		return i == 0 ? "Base" : "Top";
	}

	public float lineWidth() {
		return 4.0f;
	}
	
	@Override
	public Vector3D collide(Vector3D from, Vector3D to) {
		if (contains((int) Math.round(to.x),(int) Math.round(to.z))) {
			return from;
		} else {
			return null;
		}
	}

	/** paint the wall using gl. */
	public void paint(GL gl, GLDrawable glc) {

		if (fill != null) {
			setColor(gl, fill);
			gl.glDisable(GL.GL_TEXTURE_2D);
		}
		gl.glLineWidth(lineWidth());
		gl.glBegin(GL.GL_LINES); // draw the wall
		for (int i = 0; i < 2; i++) {
			Point2D p = (Point2D) (pts2d.get(i));
			gl.glVertex2d(p.x, p.y);
		}
		gl.glEnd();
	}

	public void render3D(GL gl, GLDrawable glc) {
		if (pts2d.size() < 2) return;	//wall needs 2 points
		if (fill != null || texture != null){
			textureFilling(gl, glc);
			gl.glPushMatrix();
			gl.glBegin( GL.GL_POLYGON );  //draw the wall (?)

			Point2D p0 =  pts2d.get(0);
			Point2D p1 =  pts2d.get(1);

			normaliseObject(gl);
			
			
			gl.glTexCoord2d(0, 0);
			gl.glVertex3d(p0.x, extra[0], p0.y);
			gl.glTexCoord2d(0, scale * extra[1]/100);
			gl.glVertex3d(p0.x, extra[1], p0.y);

			gl.glTexCoord2d(scale*p0.x/100, scale * extra[1]/100); 
			gl.glVertex3d(p1.x, extra[1], p1.y);
			gl.glTexCoord2d(scale*p0.x/100, 0);
			gl.glVertex3d(p1.x, extra[0], p1.y);

			gl.glEnd();
			gl.glPopMatrix();
		}
	}
	
	void normaliseObject (GL gl) {
		Vector3D v0 = new Vector3D (pts2d.get(0).x, extra[0], pts2d.get(0).y);
		Vector3D v1 = new Vector3D (pts2d.get(1).x, extra[0], pts2d.get(1).y);
		Vector3D v2 = new Vector3D (pts2d.get(0).x, extra[1], pts2d.get(0).y);
		
		Vector3D a = v2.subtract(v0);
		Vector3D b = v1.subtract(v0);
		Vector3D n = a.cross(b);
		gl.glNormal3d(n.x, n.y, n.z);
	}

	/** add a control point */
	public void addPoint(Point p) {
		if (pts2d.size() < 2) {
			super.addPoint(p);
		}
	}

	/** remove specified control point */
	public void removePoint() {
		return;
	}

	public boolean contains(int x, int y) {
		Edge2D e = new Edge2D((Point2D) pts2d.get(0), (Point2D) pts2d.get(1));
		Point2D pe = e.toLineSpace(new Point(x, y));
		return (pe.getX() > 0 && pe.getX() < 1 && Math.abs(pe.getY()) < EPSILON);
	}

}
