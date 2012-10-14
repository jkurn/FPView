/** This class represents a wall */
import java.awt.*;
import java.util.*;
import javax.media.opengl.*;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

/** THIS FILE HAS CHANGED **/
public class Portal extends FPPolygon {

	public final double portalHeight = 100.0;

	public Portal () {
		super();
	}
	
	public String extraName(int i){
		return i==0 ? "Start Height" : "End Height";
	}

	public float lineWidth() {
		return 4.0f;
	}
	
	// edited to return to second portal if it hits the first portal
	@Override
	public Vector3D collide(Vector3D from, Vector3D to) {
		
		// uses this class' contains, to check that it it collides with first 'in' portal
		if (contains(0,(int) Math.round(to.x),(int) Math.round(to.z))) {
			Point2D p2 = pts2d.get(2);
			Point2D p3 = pts2d.get(3);
			double midX = Math.abs((p2.x + p3.x)/2);
			double midY = Math.abs((p2.y + p3.y)/2);
			
			// teleports to second 'out' portal position
			return new Vector3D(midX, extra[1] + portalHeight, midY);
		} else {
			return null;
		}
	}

	/** paint the portal using gl.*/
	public void paint(GL gl, GLDrawable glc){

		if (fill != null){
			setColor(gl,fill);
			gl.glDisable( GL.GL_TEXTURE_2D );
		}
		gl.glLineWidth(lineWidth());
		gl.glBegin( GL.GL_LINES );  //draw each wall
		for(int i = 0; i < pts2d.size(); i++) {
			Point2D p = (Point2D)(pts2d.get(i));
			gl.glVertex2d(p.x, p.y);
		}
		gl.glEnd(); 

		if (pts2d.size() == 4) {
			/* Connect mid point of the two line segments with dotted line*/
			gl.glLineStipple(1, (short) 0x0F0F);
			gl.glEnable(GL.GL_LINE_STIPPLE);
			gl.glLineWidth(1);
			gl.glBegin( GL.GL_LINES ); 
			Point2D mid =  pts2d.get(0).interp(pts2d.get(1),0.5);
			gl.glVertex2d(mid.x, mid.y);
			mid =  pts2d.get(2).interp(pts2d.get(3),0.5);
			gl.glVertex2d(mid.x, mid.y);
			gl.glEnd(); 
			gl.glDisable(GL.GL_LINE_STIPPLE);
		}
	}

	public void render3D(GL gl, GLDrawable glc){
		if (pts2d.size() < 4) return;	// portals need 4 points
		if (fill != null || texture != null){
			textureFilling(gl, glc);

			gl.glPushMatrix();
			
			// normalise for first 'in' portal
			Vector3D v0 = new Vector3D (pts2d.get(0).x, extra[0], pts2d.get(0).y);
			Vector3D v1 = new Vector3D (pts2d.get(1).x, extra[0], pts2d.get(1).y);
			Vector3D v2 = new Vector3D (pts2d.get(0).x, extra[1], pts2d.get(0).y);
			normalisePortal(gl, v0, v1, v2);
			
			// normalise for second 'out' portal
			Vector3D v3 = new Vector3D (pts2d.get(2).x, extra[0], pts2d.get(2).y);
			Vector3D v4 = new Vector3D (pts2d.get(3).x, extra[0], pts2d.get(3).y);
			Vector3D v5 = new Vector3D (pts2d.get(2).x, extra[1], pts2d.get(2).y);
			normalisePortal(gl, v3, v4, v5);
			gl.glBegin( GL.GL_POLYGON );  //draw the portal

			Point2D p0 = pts2d.get(0);
			Point2D p1 = pts2d.get(1);
			Point2D p2 = pts2d.get(2);
			Point2D p3 = pts2d.get(3);
			

			// for the first 'in' portal
			gl.glTexCoord2d(0, 0); 
			gl.glVertex3d(p0.x, extra[0], p0.y);
			gl.glTexCoord2d(0, 1);
			gl.glVertex3d(p0.x, extra[0] + portalHeight, p0.y);	//+100 because it is 100 units high

			gl.glTexCoord2d(1, 1); 
			gl.glVertex3d(p1.x, extra[0] + portalHeight, p1.y);
			gl.glTexCoord2d(1, 0);
			gl.glVertex3d(p1.x, extra[0], p1.y);
			
			gl.glEnd();
			gl.glPopMatrix();
			
			// for the second 'out' portal
			gl.glPushMatrix();
			gl.glBegin( GL.GL_POLYGON );

			gl.glTexCoord2d(0, 0); 
			gl.glVertex3d(p2.x, extra[1], p2.y);
			gl.glTexCoord2d(0, 1);
			gl.glVertex3d(p2.x, extra[1] + portalHeight, p2.y);

			gl.glTexCoord2d(1, 1); 
			gl.glVertex3d(p3.x, extra[1] + portalHeight, p3.y);
			gl.glTexCoord2d(1, 0);
			gl.glVertex3d(p3.x, extra[1], p3.y);

			gl.glEnd();
			gl.glPopMatrix();
		}
	}

	void normalisePortal (GL gl, Vector3D v0, Vector3D v1, Vector3D v2) {
		Vector3D a = v2.subtract(v0);
		Vector3D b = v1.subtract(v0);
		Vector3D n = a.cross(b);
		gl.glNormal3d(n.x, n.y, n.z);
	}

	/** add a control point */
	public void addPoint(Point p) {
		if (pts2d.size() < 4) {
			super.addPoint(p);
		}
	}

	/** remove specified control point - not allowed for a portal*/
	public void removePoint() {
		return;
	}

	//utility for contains takes an offset so we can check the two parts of the portal
	public boolean contains(int offset, int x,int y){
		Edge2D e = new Edge2D(pts2d.get(offset), pts2d.get(offset+1));
		Point2D pe = e.toLineSpace(new Point(x,y));
		return (pe.getX() > 0 && pe.getX() < 1 && Math.abs(pe.getY()) < EPSILON);
	}

	public boolean contains(int x,int y){
		return contains(0,x,y) || contains(2,x,y) ;
	}
}
