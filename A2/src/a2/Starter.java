package a2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import commands.*;
import graphicslib3D.*;
import graphicslib3D.GLSLUtils.*;

import java.io.File;
import java.nio.*;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;
import graphicslib3D.shape.Sphere;
import javafx.scene.control.Toggle;
import objects.Camera;

public class Starter extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private int rendering_program, render_axisX, render_axisY, render_axisZ;
	private int vao[] = new int[1];
	private int vbo[] = new int[7];
	private int coolTexture, earthTexture;
	private Texture joglCoolTexture, joglEarthTexture;
	private GLSLUtils util = new GLSLUtils();

	Camera cam;

	private	MatrixStack mvStack = new MatrixStack(20);
	private Sun sun = new Sun();
	private PlanetOne p1 = new PlanetOne();
	private PlanetTwoMoon m2 = new PlanetTwoMoon();
	private Sphere sphere = new Sphere();

	private boolean axis = false;

	public Starter()
	{
		setSize(800, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		this.setVisible(true);
		FPSAnimator animator = new FPSAnimator(myCanvas, 50);
		animator.start();

		//Create a camera object
		cam = new Camera();

		//Create the movement commands
		MoveBackwards moveBackwards = new MoveBackwards(cam);
		MoveDown moveDown = new MoveDown(cam);
		MoveForward moveForward = new MoveForward(cam);
		MoveLeft moveLeft = new MoveLeft(cam);
		MoveRight moveRight = new MoveRight(cam);
		MoveUp moveUp = new MoveUp(cam);
		PanDown panDown = new PanDown(cam);
		PanLeft panLeft = new PanLeft(cam);
		PanRight panRight = new PanRight(cam);
		PanUp panUp = new PanUp(cam);
		ToggleAxis toggleAxis = new ToggleAxis(this);

		//Create a pane for the commands, and bind the commands
		JComponent contentPane = (JComponent) this.getContentPane();
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		
		KeyStroke wKey = KeyStroke.getKeyStroke('w');
		imap.put(wKey, "moveForward");
		ActionMap amap = contentPane.getActionMap();
		amap.put("moveForward", moveForward);
		
		KeyStroke aKey = KeyStroke.getKeyStroke('a');
		imap.put(aKey, "moveLeft");
		amap = contentPane.getActionMap();
		amap.put("moveLeft", moveLeft);
		
		KeyStroke sKey = KeyStroke.getKeyStroke('s');
		imap.put(sKey, "moveBackwards");
		amap = contentPane.getActionMap();
		amap.put("moveBackwards", moveBackwards);
		
		KeyStroke dKey = KeyStroke.getKeyStroke('d');
		imap.put(dKey, "moveRight");
		amap = contentPane.getActionMap();
		amap.put("moveRight", moveRight);

		KeyStroke qKey = KeyStroke.getKeyStroke('q');
		imap.put(qKey, "moveUp");
		amap = contentPane.getActionMap();
		amap.put("moveUp", moveUp);

		KeyStroke eKey = KeyStroke.getKeyStroke('e');
		imap.put(eKey, "moveDown");
		amap = contentPane.getActionMap();
		amap.put("moveDown", moveDown);

		KeyStroke leftKey = KeyStroke.getKeyStroke("LEFT");
		imap.put(leftKey, "panLeft");
		amap = contentPane.getActionMap();
		amap.put("panLeft", panLeft);

		KeyStroke rightKey = KeyStroke.getKeyStroke("RIGHT");
		imap.put(rightKey, "panRight");
		amap = contentPane.getActionMap();
		amap.put("panRight", panRight);

		KeyStroke upKey = KeyStroke.getKeyStroke("UP");
		imap.put(upKey, "panUp");
		amap = contentPane.getActionMap();
		amap.put("panUp", panUp);

		KeyStroke downKey = KeyStroke.getKeyStroke("DOWN");
		imap.put(downKey, "panDown");
		amap = contentPane.getActionMap();
		amap.put("panDown", panDown);

		KeyStroke spaceKey = KeyStroke.getKeyStroke("SPACE");
		imap.put(spaceKey, "toggleAxis");
		amap = contentPane.getActionMap();
		amap.put("toggleAxis", toggleAxis);

		this.requestFocus();
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(rendering_program);

		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);

		// push view matrix onto the stack
        mvStack.pushMatrix();
		mvStack.multMatrix(cam.computeView());
		double amt = (double)(System.currentTimeMillis())/1000.0;
		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);

		// Sun - openGL Sphere
        mvStack.pushMatrix();
		gl.glUseProgram(rendering_program);
        mvStack.translate(0, 0, 0);
        mvStack.pushMatrix();
        mvStack.rotate((System.currentTimeMillis())/10.0,1.0,0.0,0.0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, coolTexture);
        int numVerts = sphere.getIndices().length;
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
        mvStack.popMatrix();

        //Planet 1 - Earth with Cool Texture
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt)*4.0f, 0.0f, Math.cos(amt)*4.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
		mvStack.scale(0.5, 0.5, 0.5);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, earthTexture);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		mvStack.popMatrix();

        //Moon 1 - Cube with earth texture
        mvStack.pushMatrix();
        mvStack.translate(0.0f, Math.sin(amt)*2.0f, Math.cos(amt)*2.0f);
        mvStack.rotate((System.currentTimeMillis())/10.0,0.0,0.0,1.0);
        mvStack.scale(0.25, 0.25, 0.25);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();

        //Planet 2 - Mars with Cool Texture
        mvStack.pushMatrix();
        mvStack.translate(Math.sin(amt)*4.0f, 0.0f, Math.cos(amt)*4.0f);
        mvStack.pushMatrix();
        mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
        mvStack.scale(0.5, 0.5, 0.5);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, earthTexture);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();

        //Moon 2 - Diamond
        mvStack.pushMatrix();
        mvStack.translate(0.0f, Math.sin(amt*4)*2.0f, Math.cos(amt*4)*2.0f);
        mvStack.pushMatrix();
        mvStack.rotate((System.currentTimeMillis())/10.0,1.0,1.0,0.0);
        mvStack.scale(0.25, 0.25, 0.25);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, coolTexture);
        gl.glDrawArrays(GL_TRIANGLES, 0, 24);
        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();

		if(axis) {
			gl.glUseProgram(render_axisX);
			gl.glDrawArrays(GL_LINES, 0, 2);
			gl.glUseProgram(render_axisY);
			gl.glDrawArrays(GL_LINES, 0, 2);
			gl.glUseProgram(render_axisZ);
			gl.glDrawArrays(GL_LINES, 0, 2);
		}
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram("vert.shader", "frag.shader");
		render_axisX = createShaderProgram("vert2.shader", "frag2.shader");
		render_axisY = createShaderProgram("vert3.shader", "frag3.shader");
		render_axisZ = createShaderProgram("vert4.shader", "frag4.shader");
		setupVertices();
		joglCoolTexture = loadTexture("CoolTexture.jpg");
		coolTexture = joglCoolTexture.getTextureObject();
		joglEarthTexture = loadTexture("earth.jpg");
		earthTexture = joglEarthTexture.getTextureObject();
	}

	private void setupVertices()
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		//Get and setup the sun vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(sun.getVertices());
		gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);

		//Get and setup the planet one vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(p1.getVertices());
		gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);

		//Get the sphere vertices
		Vertex3D[] vertices = sphere.getVertices();
		int[] indices = sphere.getIndices();
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];

		//Setup the sphere vertixes and textures
		for (int i=0; i<indices.length; i++)
		{	pvalues[i*3] = (float) (vertices[indices[i]]).getX();
			pvalues[i*3+1] = (float) (vertices[indices[i]]).getY();
			pvalues[i*3+2] = (float) (vertices[indices[i]]).getZ();
			tvalues[i*2] = (float) (vertices[indices[i]]).getS();
			tvalues[i*2+1] = (float) (vertices[indices[i]]).getT();
		}

		//Get and setup the sphere vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer sphBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sphBuf.limit()*4, sphBuf, GL_STATIC_DRAW);

		//Get and setup the sphere textures
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

		//Get and setup the moon two vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer thnBuf = Buffers.newDirectFloatBuffer(m2.getVertices());
		gl.glBufferData(GL_ARRAY_BUFFER, thnBuf.limit()*4, thnBuf, GL_STATIC_DRAW);

		//Get and setup the planet one textures
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer texBuf2 = Buffers.newDirectFloatBuffer(p1.getTextures());
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf2.limit()*4, texBuf2, GL_STATIC_DRAW);

		//Get and setup the moon two textures
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer texBuf3 = Buffers.newDirectFloatBuffer(m2.getTextures());
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf3.limit()*4, texBuf3, GL_STATIC_DRAW);

	}

	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		return r;
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	private int createShaderProgram(String x, String y)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource(x);
		String fshaderSource[] = util.readShaderSource(y);

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}
	public Texture loadTexture(String textureFileName)
	{	Texture tex = null;
		try { tex = TextureIO.newTexture(new File(textureFileName), false);
		}
		catch (Exception e) { e.printStackTrace(); }
		return tex;
	}

	public boolean isAxis() {
		return axis;
	}

	public void setAxis(boolean axis) {
		this.axis = axis;
	}
}