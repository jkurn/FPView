/** This class represents a window */
import java.awt.*;
import java.util.*;
import javax.media.opengl.*;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

/** THIS FILE HAS CHANGED **/
public class Window extends Wall {

	public float lineWidth() {
		return 2.0f;
	}

	public void render3D(GL gl, GLDrawable glc) {
		if (pts2d.size() < 2) return;	//windows need 2 points
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
			
			/** creating base of window */
			gl.glPushMatrix();
			gl.glBegin( GL.GL_POLYGON );
			
			Point2D p0 =  pts2d.get(0);
			Point2D p1 =  pts2d.get(1);
			
			normaliseObject(gl);
			
			// starts from base to 1/3 of height
			gl.glTexCoord2d(0, 0); 
			gl.glVertex3d(p0.x, extra[0], p0.y);
			gl.glTexCoord2d(0, 1);
			gl.glVertex3d(p0.x, extra[0] + extra[1]/3, p0.y);
			
			gl.glTexCoord2d(1, 1); 
			gl.glVertex3d(p1.x, extra[0] + extra[1]/3, p1.y);
			gl.glTexCoord2d(1, 0);
			gl.glVertex3d(p1.x, extra[0], p1.y);
			
			gl.glEnd();
			gl.glPopMatrix();
			
			/** creating top of window */
			gl.glPushMatrix();
			gl.glBegin( GL.GL_POLYGON );
			
			// starts from 2/3 height to top of height
			gl.glTexCoord2d(0, 0); 
			gl.glVertex3d(p0.x, (2 * extra[1]/3), p0.y);
			gl.glTexCoord2d(0, 1);
			gl.glVertex3d(p0.x, extra[1], p0.y);
			
			gl.glTexCoord2d(1, 1); 
			gl.glVertex3d(p1.x, extra[1], p1.y);
			gl.glTexCoord2d(1, 0);
			gl.glVertex3d(p1.x, (2 * extra[1]/3), p1.y);
			
			gl.glEnd();
			gl.glPopMatrix();
		}
	}
}
