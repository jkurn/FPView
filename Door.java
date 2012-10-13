/** This class represents a door */
import java.awt.*;
import java.util.*;
import javax.media.opengl.*;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

/** THIS FILE HAS CHANGED **/
public class Door extends Wall {

	public float lineWidth() {
		return 2.0f;
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
		if (fill != null || texture != null){
			if (texture == null) {
				setColor(gl,fill);
				gl.glDisable( GL.GL_TEXTURE_2D );
			} else {
				setColor(gl, Color.white);
				Texture gltexture = texture.getTexture(glc);
				gltexture.enable();
				gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT );
				gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT );
				gl.glTexEnvf( GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
						GL.GL_MODULATE);
				gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER ,
						GL.GL_NEAREST);
				gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER ,
						GL.GL_NEAREST);
				gltexture.bind();
			}
			
			
			gl.glPushMatrix();
			gl.glBegin( GL.GL_POLYGON );

			Point2D p0 =  pts2d.get(0);
			Point2D p1 =  pts2d.get(1);
			
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

}
