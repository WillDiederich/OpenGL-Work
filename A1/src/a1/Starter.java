package a1;

import java.awt.BorderLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import static com.jogamp.opengl.GL4.*;


import graphicslib3D.GLSLUtils;

public class Starter extends JFrame implements GLEventListener, MouseWheelListener{
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	private GLSLUtils util = new GLSLUtils();
	
	private float x = 0.0f;
	private float inc = 0.01f;
	private float y = 0.0f;
	private float inc2 = 0.01f;
	private float color;
	private boolean gradientOn = false;
	private boolean verticalMovement = false;
	private boolean circularMovement = false;
	private float size = 1.0f;
	private int degrees = 0;
		
	public static void main(String[] args) {
		new Starter();
		System.out.println("Java Version: " + System.getProperty("java.version"));
		System.out.println("JOGL Version: " + Package.getPackage("com.jogamp.opengl").getImplementationVersion());
	}


	public Starter() {
		//Creates a window of size 600x600, a GLCanvas with a GLEventListener is created and placed within the window and set visible
		setSize(600, 600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		setVisible(true);
		
		//Creates a JPanel and puts it in the north section of the BorderLayout
		JPanel topPanel = new JPanel();
		this.add(topPanel, BorderLayout.NORTH);
		
		//Creates instances of each command
		Gradient gradientCmd = new Gradient(this);
		Vertical verticalCmd = new Vertical(this);
		Circle circleCmd = new Circle(this);
		
		//Creates the button for vertical movement, attaches the vertical movement command, and places it in the topPanel
		JButton vertButton = new JButton();
		vertButton.setAction(verticalCmd);
		topPanel.add(vertButton);
		vertButton.setText("Vertical Movement");
		
		//Creates the button for circular movement, attaches the circular movement command, and places it in topPanel
		JButton circButton = new JButton();
		circButton.setAction(circleCmd);
		topPanel.add(circButton);
		circButton.setText("Circular Movement");
		
		//Creates a listener that will listen for Mouse Wheel activity
		this.addMouseWheelListener(this);

		//Creates a key listener for the C key, attaches the color gradient command, and places makes it listen for the key when the window is in focus
		JComponent contentPane = (JComponent) this.getContentPane();
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		KeyStroke cKey = KeyStroke.getKeyStroke('c');
		imap.put(cKey, "color");
		ActionMap amap = contentPane.getActionMap();
		amap.put("color", gradientCmd);
		this.requestFocus();
		
		//Creates an animator for myCanvas and starts it
		FPSAnimator animator = new FPSAnimator(myCanvas, 30);
		animator.start();
		
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int direction = e.getWheelRotation();
		
		//if the mouse is moved up, the size will be increased to a maximum of 1.5f
		if(direction == 1 && size < 1.5f) {
			size += 0.01f;
		}
		//If the mouse is moved down, the size will be decreased to a minimum of 0.25f
		if(direction == -1 && size > 0.25f) {
			size -= 0.01f;
		}
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(rendering_program);

		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
		
		
		if(isVerticalMovement()) {
			y += inc;
			if (y*size > 1.0f)
				inc = -0.01f;
			if (y*size < -1.0f) 
				inc = 0.01f;
		}
		if(isCircularMovement()) {
			if(degrees == 360)
				degrees = 0;
			degrees++;
			x = (float) (0 + Math.cos(Math.toRadians(degrees))*size/2);
			y = (float) (0 + Math.sin(Math.toRadians(degrees))*size/2);
		}
		
		
		if(isGradientOn())
			color = 1.0f;
		else
			color = 0.0f;
		
	
		int offset_loc = gl.glGetUniformLocation(rendering_program, "y");
		gl.glProgramUniform1f(rendering_program, offset_loc, y);
		offset_loc = gl.glGetUniformLocation(rendering_program, "x");
		gl.glProgramUniform1f(rendering_program, offset_loc, x);
		offset_loc = gl.glGetUniformLocation(rendering_program, "color");
		gl.glProgramUniform1f(rendering_program, offset_loc, color);
		offset_loc = gl.glGetUniformLocation(rendering_program, "color");
		gl.glProgramUniform1f(rendering_program, offset_loc, color);
		offset_loc = gl.glGetUniformLocation(rendering_program, "size");
		gl.glProgramUniform1f(rendering_program, offset_loc, size);

		gl.glDrawArrays(GL_TRIANGLES,0,3);
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		System.out.println("OpenGL version " + gl.glGetString(GL_RENDERER));
		rendering_program = createShaderProgram();
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
	}
	
	private int createShaderProgram() {	
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];

		String vshaderSource[] = GLSLUtils.readShaderSource("a1/vert.shader");
		String fshaderSource[] = GLSLUtils.readShaderSource("a1/frag.shader");
		
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		checkOpenGLError();  // can use returned boolean if desired
		gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1)
		{	System.out.println("vertex compilation success");
		} else
		{	System.out.println("vertex compilation failed");
			printShaderLog(vShader);
		}
		
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		checkOpenGLError();  // can use returned boolean if desired
		gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1)
		{	System.out.println("fragment compilation success");
		} else
		{	System.out.println("fragment compilation failed");
			printShaderLog(fShader);
		}

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		checkOpenGLError();
		gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked, 0);
		if (linked[0] == 1)
		{	System.out.println("linking succeeded");
		} else
		{	System.out.println("linking failed");
			printProgramLog(vfprogram);
		}

		return vfprogram;
	}
	
	public boolean isGradientOn() {
		return gradientOn;
	}

	public void setGradientOn() {
		if(gradientOn)
			gradientOn = false;
		else
			gradientOn = true;
	}

	public boolean isVerticalMovement() {
		return verticalMovement;
	}

	public void setVerticalMovement() {
		if(verticalMovement) {
			verticalMovement = false;
		}
		else
			verticalMovement = true;
	}

	public boolean isCircularMovement() {
		return circularMovement;
	}

	public void setCircularMovement() {
		if(circularMovement) {
			circularMovement = false;
		}
		else
			circularMovement = true;
	}
	
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	boolean checkOpenGLError()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR)
		{	System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}
	
	void printProgramLog(int prog)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine length of the program compilation log
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}
	
	private void printShaderLog(int shader)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine the length of the shader compilation log
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}
}
