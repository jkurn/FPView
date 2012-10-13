import java.awt.*;
import java.util.*;
import javax.media.opengl.*;
import com.sun.opengl.util.*;

/** THIS FILE HAS CHANGED **/
public class Avatar {
	public static final double height = 80;
	final double moveStep = 2;
	final double turnStep = 0.02;
	double turnIncrement;
	double moveIncrement;
	int turnframes;
	int moveframes;
	Vector3D pos;
	Vector3D dir;

	float t = 0;

	public Avatar(Vector3D pos, Vector3D dir) {
		this.pos = pos;
		this.dir = dir;
	}

	// move forward or back for given number of frames
	public void move (int noframes) {
		if (noframes > 0) {
			moveIncrement = moveStep;
		} else {
			moveIncrement = -moveStep;
		}
		moveframes = Math.abs(noframes);
	}

	public void rotate (int noframes) {
		if (noframes > 0) {
			// turn left
			turnIncrement = turnStep;
		} else {
			// turn right
			turnIncrement = -turnStep;
		}
		turnframes = Math.abs(noframes);
	}

	// update position, checking for collisions
	public void advancePos(FPShapeList shapeList) {
		Vector3D newpos;

		if (moveframes > 0) {
			newpos = pos.add(dir.scale(moveIncrement));
			moveframes--;
		} else {
			newpos = pos; // doesnt move
		}
		
		if (turnframes > 0) {
			dir = dir.rotatey(turnIncrement);
			turnframes--;
		}
		pos = shapeList.collide(pos, newpos);
	}

	public Vector3D currentPos() {
		return pos;
	}

	public Vector3D currentDir() {
		return dir;
	}

	public void setColor(GL gl, Color c) {
		gl.glColor3ub((byte) c.getRed(), (byte) c.getGreen(),
				(byte) c.getBlue());
	}

	public void render3D(GL gl, GLDrawable glc) {
		setColor(gl, Color.white);
		gl.glDisable(GL.GL_TEXTURE_2D);

		gl.glPushMatrix();
		gl.glTranslated(pos.x, pos.y - height / 2, pos.z);
		gl.glScaled(5, height, 25);
		gl.glRotated((turnframes * turnStep), dir.x, dir.y, dir.z); // !! does not rotate it? !!
		GLUT glut = new GLUT();
		glut.glutSolidCube(1);
		gl.glPopMatrix();
	}

}
