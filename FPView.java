/** 
 *  Program to display floorplans designed using FPED
 *
 *      The code is copyright (C) Waleed Kadous 2001 
 *      and copyright (C) Tim Lambert 2010
 *   
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

/** THIS FILE HAS CHANGED **/
public class FPView extends Frame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	FPShapeList shapeList = null; // List of shapes that we have to render

	private int currentViewPoint; // index for views 

	int appHeight = 400; // Height of the browsing window
	int appWidth = 400; // Width of the browsing window
	int prevx = 0; // Previously observed x value of mouse click.
	int prevy = 0; // Previously observed y value of mouse click.
	GLCanvas glc = null;
	Vector3D viewpos = new Vector3D(10, 60, 30); // Default viewing position
	Vector3D viewdir = new Vector3D(1, 0, 1); // Default viewing direction
	// (diagonal across world)
	Avatar avatar; //
	static final int FIRST_PERSON_VIEW = 1;
	static final int THIRD_PERSON_VIEW = 2;
	int viewType = THIRD_PERSON_VIEW;
	static final int DAY_TIME = 0;
	static final int NIGHT_TIME = 1;
	int isDayTime = DAY_TIME;

	public FPView(String filename) {

		super("Floor Plan Viewer: " + filename); // Create window with title
		shapeList = new FPShapeList();
		currentViewPoint = 0;
		try {
			shapeList.read(filename);
		} catch (Exception e) {
			System.err.println("Could not load the floor plan " + filename);
			e.printStackTrace();
			System.exit(1);
		}

		setLayout(new BorderLayout());

		glc = new GLCanvas();

		// quit if the window closes
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// More initialisation.
		glc.addGLEventListener(this);
		glc.addKeyListener(this);
		glc.addMouseListener(this);
		glc.addMouseMotionListener(this);

		add("Center", glc);
		pack();
		setSize(appHeight, appWidth);

		// Run animation in a tight loop.
		Animator animator = new FPSAnimator(glc, 30); // animate at 30 fps

		animator.start();

		setVisible(true);
	}

	/* Main simply created a new window. */
	public static void main(String[] args) {
		FPView dsview = new FPView(args[0]);
	}

	/** Methods that must be implemented for Interface fulfillment */

	/** For GLEventListener */
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		avatar = new Avatar(new Vector3D(0, Avatar.height, 75), new Vector3D(1,
				0, 0));
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL gl = drawable.getGL();
		GLU glu = new GLU();
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60, 1, 1, 10000);

		gl.glEnable(GL.GL_DEPTH_TEST);

		appWidth = width;
		appHeight = height;
	}

	public void display(GLAutoDrawable drawable) {

		GL gl = drawable.getGL();
		GLU glu = new GLU();
		// automatically make normals length 1
		gl.glEnable(GL.GL_NORMALIZE);

		// Use two-sided lighting .
		gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, 1);

		gl.glShadeModel(GL.GL_SMOOTH); // Use Gouraud shading.
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_COLOR_MATERIAL);

		// Clear the matrix stack.
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		// If it's a user view, set it from viewpos and viewdir.
		if (viewType == THIRD_PERSON_VIEW) {
			setView(glu, viewpos, viewdir);
		}
		// ... otherwise use information from avatar to do it.
		else {
			setView(glu, avatar.currentPos(), avatar.currentDir());
		}

		// checks day or night, and adjust background and lighting accodingly
		if (isDayTime == DAY_TIME) {
			daylight(gl);
			gl.glClearColor(0.6f, 0.6f, 0.9f, 1.0f);
		} else {
			nightlight(gl);
			gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		}
		gl.glEnable(GL.GL_LIGHTING);

		gl.glPushMatrix();
		// Render the shapes.
		shapeList.render3D(gl, glc);

		// Render the avatar.
		avatar.advancePos(shapeList);
		if (viewType == THIRD_PERSON_VIEW) {
			avatar.render3D(gl, glc);
		}

		gl.glPopMatrix();
	}

	/** This method handles things if display depth changes */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/** For KeyListener */
	public void keyTyped(KeyEvent evt) {

	}

	public void keyReleased(KeyEvent evt) {

	}

	public void keyPressed(KeyEvent evt) {

		/** changing point of views */
		if (evt.getKeyChar() == 'f') {
			if (viewType == FIRST_PERSON_VIEW) {
				viewType = THIRD_PERSON_VIEW;
			} else {
				viewType = FIRST_PERSON_VIEW;
			}
		}
		if (evt.getKeyChar() == 'v') {
			if (viewType == THIRD_PERSON_VIEW) {
				// cycle through viewpoints, by cycling list of viewPoints 
				ArrayList<ViewPoint> viewList = shapeList.getViews();
				if (viewList.size() > 0) {
					currentViewPoint = ((currentViewPoint + 1) % viewList.size());
					ViewPoint v = viewList.get(currentViewPoint);
					viewpos = v.getViewerPos();
					viewdir = v.getViewerDir();
				}
			}
		}
		if (evt.getKeyChar() == 'p') {
			// adds viewpoint 
			System.out.println(new ViewPoint(viewpos,viewdir));
		}

		/** avatar movement */
		if (evt.getKeyChar() == 'w') {
			// moves forward
			avatar.move(4);
		}
		if (evt.getKeyChar() == 's') {
			// moves backward
			avatar.move(-4);
		}
		if (evt.getKeyChar() == 'a') {
			// looks left
			avatar.rotate(4);
		}
		if (evt.getKeyChar() == 'd') {
			// looks right
			avatar.rotate(-4);
		}

		/** door animation */
		ArrayList<Door> doorList = shapeList.getDoors();
		if (evt.getKeyChar() >= '1' && evt.getKeyChar() <= '9') {
			int elementSelection = evt.getKeyCode() - '1';
			//toggle open door if present
			// and makes sure we aren't selecting outside the array size
			if (doorList.size() > elementSelection) {
				doorList.get(elementSelection).toggleDoor();
			}
		}
		/** light toggle */
		if (evt.getKeyChar() == 'n') {
			//changes from night to day
			isDayTime ^= 1;
		}
		if (evt.getKeyChar() == '!') {
			//toggle open first light

		}
		if (evt.getKeyChar() == '@') {

		}
		if (evt.getKeyChar() == '#') {

		}
		if (evt.getKeyChar() == '$') {

		}

		if (evt.getKeyChar() == 'q')
			System.exit(0);
	}

	// Set view, given eye and view direction
	public void setView(GLU glu, Vector3D pos, Vector3D dir) {
		Vector3D look = pos.add(dir);
		glu.gluLookAt(pos.x, pos.y, pos.z, look.x, look.y, look.z, 0, 1, 0);
	}

	/** For MouseListener and MouseMotionListener */

	public void mouseMoved(MouseEvent evt) {

	}

	public void mouseEntered(MouseEvent evt) {

	}

	public void mouseExited(MouseEvent evt) {

	}

	public void mouseClicked(MouseEvent evt) {

	}

	public void mouseReleased(MouseEvent evt) {

	}

	public void mousePressed(MouseEvent evt) {
		prevx = evt.getX();
		prevy = evt.getY();
	}

	public void mouseDragged(MouseEvent evt) {
		// finds the current x and y
		int currX = evt.getX();
		int currY = evt.getY();

		int diffX = prevx - currX;
		int diffY = prevy - currY;

		double niceScalingRate = 0.02;

		if (evt.isMetaDown()) {
			/** using right button click (i.e. zooming mode) */
			//scale the viewpos to be zooming
			viewpos = viewpos.add(viewdir.scale(diffY));	//scale to viewdirection instead
		} else {
			/** using left button click (i.e. panning mode) */
			// {Both of these works for diagonal too} 
			//looking up and down changes viewdir just by increments
			viewdir.y += (diffY * niceScalingRate);
			//looking left and right rotates viewdir in y axis
			viewdir = viewdir.rotatey(diffX * niceScalingRate);

		}
		// re-update prevx and prevy
		prevx = currX;
		prevy = currY;
	}

	public void daylight(GL gl) {
		// light position is -1, 1, -1, and is directional lighting
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {-1, 1, -1, 0}, 0);
		// light color is white
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[] {0.6f, 0.6f, 0.6f, 0}, 0);
		gl.glEnable(GL.GL_LIGHT0);
	} 

	public void nightlight(GL gl) {
		// disable lighting at night
		gl.glDisable(GL.GL_LIGHT0);
	}
}