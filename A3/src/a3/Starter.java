package a3;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import commands.*;
import graphicslib3D.*;
import graphicslib3D.light.*;
import graphicslib3D.shape.*;

import java.io.File;
import java.nio.*;
import javax.swing.*;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import objects.Camera;

public class Starter extends JFrame implements GLEventListener
{
    private GLCanvas myCanvas;
    private Material thisMaterial;
    private String[] vBlinn1ShaderSource, vBlinn2ShaderSource, fBlinn2ShaderSource;
    private int rendering_program1, rendering_program2;
    private int vao[] = new int[1];
    private int vbo[] = new int[18];
    private int mv_location, proj_location, vertexLoc, n_location;
    private float aspect;
    private GLSLUtils util = new GLSLUtils();

    // location of torus and camera
    private Point3D torusLoc = new Point3D(1.6, 0.0, -0.3);
    private Point3D pyrLoc = new Point3D(-1.0, 0.1, 0.3);
    private Point3D sphLoc = new Point3D(-2.0, 0.6, 0.3);
    private Point3D lightLoc = new Point3D(-3.8f, 2.45f, -1.15f);

    private Matrix3D m_matrix = new Matrix3D();
    private Matrix3D v_matrix = new Matrix3D();
    private Matrix3D mv_matrix = new Matrix3D();
    private Matrix3D proj_matrix = new Matrix3D();

    // light stuff
    private float [] globalAmbient = new float[] { 0.7f, 0.7f, 0.7f, 1.0f };
    private PositionalLight fixedLight = new PositionalLight();
    private PositionalLight movableLight = new PositionalLight();

    // shadow stuff
    private int scSizeX, scSizeY;
    private int [] shadow_tex = new int[1];
    private int [] shadow_buffer = new int[1];
    private Matrix3D lightV_matrix = new Matrix3D();
    private Matrix3D lightP_matrix = new Matrix3D();
    private Matrix3D shadowMVP1 = new Matrix3D();
    private Matrix3D shadowMVP2 = new Matrix3D();
    private Matrix3D b = new Matrix3D();

    // model stuff
    private ImportedModel pyramid = new ImportedModel("pyr.obj");
    private ImportedModel grid = new ImportedModel("grid.obj");
    private Torus torus = new Torus(0.6f, 0.4f, 48);
    private int numPyramidVertices, numTorusVertices, numSphereVertices, numGridVertices;

    private Texture moonTex;
    private int textureID1, textureID2, textureID3, textureID4, red, green, blue;

    Camera cam;

    double amt = 0.0f;

    private Sphere sphere = new Sphere(24);

    private boolean lightOn = true;

    private boolean axesOn = false;

    Material jd1 = new Material();
    float [ ] amb = new float[ ] { .135f, .2225f, .1575f, .95f };
    float [ ] dif = new float[ ] { .54f, .89f, .63f, .95f };
    float [ ] spec = new float[ ] { .3162f, .3162f, .3162f, .95f };

    public Starter()
    {
        setSize(800, 800);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        getContentPane().add(myCanvas);
        setVisible(true);
        FPSAnimator animator = new FPSAnimator(myCanvas, 30);
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
        LightMoveBackwards lightMoveBackwards = new LightMoveBackwards(this);
        LightMoveDown lightMoveDown = new LightMoveDown(this);
        LightMoveForward lightMoveForward = new LightMoveForward(this);
        LightMoveLeft lightMoveLeft = new LightMoveLeft(this);
        LightMoveRight lightMoveRight = new LightMoveRight(this);
        LightMoveUp lightMoveUp = new LightMoveUp(this);
        ToggleLight toggleLight = new ToggleLight(this);
        ToggleAxes toggleAxes = new ToggleAxes(this);

        //Create a pane for the commands, and bind the commands
        JComponent contentPane = (JComponent) this.getContentPane();
        int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap imap = contentPane.getInputMap(mapName);

        KeyStroke tKey = KeyStroke.getKeyStroke('t');
        imap.put(tKey, "toggleLight");
        ActionMap amap = contentPane.getActionMap();
        amap.put("toggleLight", toggleLight);

        KeyStroke oKey = KeyStroke.getKeyStroke('o');
        imap.put(oKey, "lightMoveForward");
        amap = contentPane.getActionMap();
        amap.put("lightMoveForward", lightMoveForward);

        KeyStroke kKey = KeyStroke.getKeyStroke('k');
        imap.put(kKey, "lightMoveBackwards");
        amap = contentPane.getActionMap();
        amap.put("lightMoveBackwards", lightMoveBackwards);

        KeyStroke jKey = KeyStroke.getKeyStroke('j');
        imap.put(jKey, "lightMoveLeft");
        amap = contentPane.getActionMap();
        amap.put("lightMoveLeft", lightMoveLeft);

        KeyStroke lKey = KeyStroke.getKeyStroke('l');
        imap.put(lKey, "lightMoveRight");
        amap = contentPane.getActionMap();
        amap.put("lightMoveRight", lightMoveRight);

        KeyStroke pKey = KeyStroke.getKeyStroke('p');
        imap.put(pKey, "lightMoveDown");
        amap = contentPane.getActionMap();
        amap.put("lightMoveDown", lightMoveDown);

        KeyStroke iKey = KeyStroke.getKeyStroke('i');
        imap.put(iKey, "lightMoveUp");
        amap = contentPane.getActionMap();
        amap.put("lightMoveUp", lightMoveUp);

        KeyStroke spaceKey = KeyStroke.getKeyStroke(' ');
        imap.put(spaceKey, "toggleAxes");
        amap = contentPane.getActionMap();
        amap.put("toggleAxes", toggleAxes);


        KeyStroke wKey = KeyStroke.getKeyStroke('w');
        imap.put(wKey, "moveForward");
        amap = contentPane.getActionMap();
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

        this.requestFocus();

        jd1.setAmbient(amb);
        jd1.setDiffuse(dif);
        jd1.setSpecular(spec);
        jd1.setShininess(12.8f);
    }

    public void display(GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        //System.out.println(lightLoc.getX() + " " + lightLoc.getY() + " " + lightLoc.getZ());

        fixedLight.setPosition(lightLoc);
        aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        proj_matrix = perspective(50.0f, aspect, 0.1f, 1000.0f);

        float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadow_buffer[0]);
        gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadow_tex[0], 0);

        gl.glDrawBuffer(GL_NONE);
        gl.glEnable(GL_DEPTH_TEST);

        gl.glEnable(GL_POLYGON_OFFSET_FILL);	// for reducing
        gl.glPolygonOffset(2.0f, 4.0f);			//  shadow artifacts

        passOne();

        gl.glDisable(GL_POLYGON_OFFSET_FILL);	// artifact reduction, continued

        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        gl.glActiveTexture(GL_TEXTURE2);
        gl.glBindTexture(GL_TEXTURE_2D, shadow_tex[0]);

        gl.glDrawBuffer(GL_FRONT);

        passTwo();
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void passOne()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(rendering_program1);

        Point3D origin = new Point3D(0.0, 0.0, 0.0);
        Vector3D up = new Vector3D(0.0, 1.0, 0.0);
        lightV_matrix.setToIdentity();
        lightP_matrix.setToIdentity();

        lightV_matrix = lookAt(fixedLight.getPosition(), origin, up);	// vector from light to origin
        lightP_matrix = perspective(50.0f, aspect, 0.1f, 1000.0f);

        ////------------------START Draw the Torus START------------------\\\\
        //---------Setup the torus position and rotation on the screen
        m_matrix.setToIdentity();
        m_matrix.translate(torusLoc.getX(),torusLoc.getY(),torusLoc.getZ());
        m_matrix.rotateX(25.0);

        //---------Setup the shadow mappings
        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightP_matrix);
        shadowMVP1.concatenate(lightV_matrix);
        shadowMVP1.concatenate(m_matrix);
        int shadow_location = gl.glGetUniformLocation(rendering_program1, "shadowMVP");
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);

        //---------Set up torus vertices buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, numGridVertices);
        ////------------------END Draw the Torus END------------------\\\\

        ////------------------START Draw the Pyramid START------------------\\\\
        //---------Setup the torus position and rotation on the screen
        m_matrix.setToIdentity();
        m_matrix.translate(pyrLoc.getX(),pyrLoc.getY(),pyrLoc.getZ());
        m_matrix.rotateX(30.0);
        m_matrix.rotateY(40.0);

        //---------Setup the shadow mappings
        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightP_matrix);
        shadowMVP1.concatenate(lightV_matrix);
        shadowMVP1.concatenate(m_matrix);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);

        //---------Set up torus vertices buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, pyramid.getNumVertices());
        ////------------------END Draw the Pyramid END------------------\\\\



        ////------------------START Draw the Sphere START------------------\\\\
        //---------Setup the torus position and rotation on the screen
        m_matrix.setToIdentity();
        m_matrix.translate(sphLoc.getX(),sphLoc.getY(),sphLoc.getZ());
        m_matrix.rotateX(25.0);
        m_matrix.scale(0.25,0.25,0.25);

        //---------Setup the shadow mappings
        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightP_matrix);
        shadowMVP1.concatenate(lightV_matrix);
        shadowMVP1.concatenate(m_matrix);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);

        //---------Set up sphere vertices buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVertices);
        ////------------------END Draw the Sphere END------------------\\\\
}

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void passTwo()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(rendering_program2);

        //---------Setup these integer location things
        mv_location = gl.glGetUniformLocation(rendering_program2, "mv_matrix");
        proj_location = gl.glGetUniformLocation(rendering_program2, "proj_matrix");
        n_location = gl.glGetUniformLocation(rendering_program2, "normalMat");
        int shadow_location = gl.glGetUniformLocation(rendering_program2,  "shadowMVP");

        ////------------------START Draw the Torus START------------------\\\\
        //---------Setup the material for the torus
        thisMaterial = jd1;

        //---------Build the Model Matrix
        m_matrix.setToIdentity();
        m_matrix.translate(torusLoc.getX(),torusLoc.getY(),torusLoc.getZ());
        m_matrix.rotateX(25.0);

        //---------Build the View Matrix and install lights
        v_matrix.setToIdentity();
        v_matrix.translate(-cam.getX(), -cam.getY(), -cam.getZ());
        installLights(rendering_program2, v_matrix);

        //---------Build the Model-View Matrix
        mv_matrix.setToIdentity();
        mv_matrix.concatenate(v_matrix);
        mv_matrix.concatenate(m_matrix);

        //---------Setup the shadow mappings
        shadowMVP2.setToIdentity();
        shadowMVP2.concatenate(b);
        shadowMVP2.concatenate(lightP_matrix);
        shadowMVP2.concatenate(lightV_matrix);
        shadowMVP2.concatenate(m_matrix);

        //---------put the Model-View and Projection matrices into the corresponding uniforms
        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

        //---------Setup the Torus Vertex Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        //---------Setup the Torus Normal Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        //---------Setup the Torus Texture Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(2);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, textureID1);

        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, textureID2);

        //---------Do all of this cool stuff such as culling non visible faces
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        //---------Draw the Torus
        gl.glDrawArrays(GL_TRIANGLES, 0, numGridVertices);
        ////------------------END Draw the Torus END------------------\\\\



        ////------------------START Draw the Pyramid START------------------\\\\
        //---------Setup the material for the torus
        thisMaterial = graphicslib3D.Material.GOLD;

        //---------Build the Model Matrix
        m_matrix.setToIdentity();
        m_matrix.translate(pyrLoc.getX(), pyrLoc.getY(), pyrLoc.getZ());
        m_matrix.rotateX(30.0);
        m_matrix.rotateY(40.0);

        //---------Build the View Matrix and install lights
        v_matrix.setToIdentity();
        v_matrix.translate(-cam.getX(), -cam.getY(), -cam.getZ());
        installLights(rendering_program2, v_matrix);

        //---------Build the Model-View Matrix
        mv_matrix.setToIdentity();
        mv_matrix.concatenate(v_matrix);
        mv_matrix.concatenate(m_matrix);

        //---------Setup the shadow mappings
        shadowMVP2.setToIdentity();
        shadowMVP2.concatenate(b);
        shadowMVP2.concatenate(lightP_matrix);
        shadowMVP2.concatenate(lightV_matrix);
        shadowMVP2.concatenate(m_matrix);

        //---------put the Model-View and Projection matrices into the corresponding uniforms
        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

        //---------Setup the Torus Vertex Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        //---------Setup the Torus Normal Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        //---------Setup the Torus Texture Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(2);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, textureID3);

        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, textureID3);

        //---------Do all of this cool stuff such as culling non visible faces
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        //---------Draw the Torus
        gl.glDrawArrays(GL_TRIANGLES, 0, pyramid.getNumVertices());
        ////------------------END Draw the Torus END------------------\\\\



        ////------------------START Draw the Sphere START------------------\\\\
        //---------Setup the material for the torus
        thisMaterial = graphicslib3D.Material.SILVER;

        //---------Build the Model Matrix
        m_matrix.setToIdentity();
        m_matrix.translate(sphLoc.getX() ,sphLoc.getY(), sphLoc.getZ());
        m_matrix.rotateX(25.0);
        m_matrix.scale(0.45,0.45,0.45);

        //---------Build the View Matrix and install lights
        v_matrix.setToIdentity();
        v_matrix.translate(-cam.getX(), -cam.getY(), -cam.getZ());
        installLights(rendering_program2, v_matrix);

        //---------Build the Model-View Matrix
        mv_matrix.setToIdentity();
        mv_matrix.concatenate(v_matrix);
        mv_matrix.concatenate(m_matrix);

        //---------Setup the shadow mappings
        shadowMVP2.setToIdentity();
        shadowMVP2.concatenate(b);
        shadowMVP2.concatenate(lightP_matrix);
        shadowMVP2.concatenate(lightV_matrix);
        shadowMVP2.concatenate(m_matrix);

        //---------put the Model-View and Projection matrices into the corresponding uniforms
        gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

        //---------Setup the Torus Vertex Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        //---------Setup the Torus Normal Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        //---------Setup the Torus Texture Buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(2);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, textureID1);

        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, textureID2);

        //---------Do all of this cool stuff such as culling non visible faces
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);


        //---------Draw the Torus
        gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVertices);
        ////------------------END Draw the Sphere END------------------\\\\

        ////------------------START Draw the Sphere START------------------\\\\
        //---------Setup the material for the torus
        if(isLightOn()) {
            //---------Build the Model Matrix
            m_matrix.setToIdentity();
            m_matrix.translate(lightLoc.getX(), lightLoc.getY(), lightLoc.getZ());
            m_matrix.rotateX(25.0);
            m_matrix.scale(0.25, 0.25, 0.25);

            //---------Build the View Matrix and install lights
            v_matrix.setToIdentity();
            v_matrix.translate(-cam.getX(), -cam.getY(), -cam.getZ());
            installLights(rendering_program2, v_matrix);

            //---------Build the Model-View Matrix
            mv_matrix.setToIdentity();
            mv_matrix.concatenate(v_matrix);
            mv_matrix.concatenate(m_matrix);

            //---------Setup the shadow mappings
            shadowMVP2.setToIdentity();
            shadowMVP2.concatenate(b);
            shadowMVP2.concatenate(lightP_matrix);
            shadowMVP2.concatenate(lightV_matrix);
            shadowMVP2.concatenate(m_matrix);

            //---------put the Model-View and Projection matrices into the corresponding uniforms
            gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
            gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

            //---------Setup the Torus Vertex Buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            //---------Setup the Torus Normal Buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
            gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(1);

            //---------Setup the Torus Texture Buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
            gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(2);

            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, textureID1);

            gl.glActiveTexture(GL_TEXTURE1);
            gl.glBindTexture(GL_TEXTURE_2D, textureID2);

            //---------Do all of this cool stuff such as culling non visible faces
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            gl.glEnable(GL_CULL_FACE);
            gl.glFrontFace(GL_CCW);
            gl.glEnable(GL_DEPTH_TEST);
            gl.glDepthFunc(GL_LEQUAL);


            //---------Draw the Torus
            gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVertices);
        }
        ////------------------END Draw the Sphere END------------------\\\\

        ////////////
        if(axesOn) {
            m_matrix.setToIdentity();
            m_matrix.translate(0, 0, 0);

            v_matrix.setToIdentity();
            v_matrix.translate(-cam.getX(), -cam.getY(), -cam.getZ());

            mv_matrix.setToIdentity();
            mv_matrix.concatenate(v_matrix);
            mv_matrix.concatenate(m_matrix);


            gl.glDisable(GL_CULL_FACE);

            //Red x-axis
            gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
            gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(1);

            gl.glActiveTexture(GL_TEXTURE1);
            gl.glBindTexture(GL_TEXTURE_2D, red);

            gl.glDrawArrays(GL_TRIANGLES, 0, 9);

            //Green y-axis
            gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
            gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(1);

            gl.glActiveTexture(GL_TEXTURE1);
            gl.glBindTexture(GL_TEXTURE_2D, green);

            gl.glDrawArrays(GL_TRIANGLES, 0, 9);

            //Blue z-axis
            gl.glUniformMatrix4fv(mv_location, 1, false, mv_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(proj_location, 1, false, proj_matrix.getFloatValues(), 0);
            gl.glUniformMatrix4fv(n_location, 1, false, (mv_matrix.inverse()).transpose().getFloatValues(), 0);
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
            gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(1);

            gl.glActiveTexture(GL_TEXTURE1);
            gl.glBindTexture(GL_TEXTURE_2D, blue);

            gl.glDrawArrays(GL_TRIANGLES, 0, 9);
        }

    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void init(GLAutoDrawable drawable)
    {
        //---------Initialize all of the usual cool stuff
        GL4 gl = (GL4) GLContext.getCurrentGL();
        createShaderPrograms();
        setupVertices();
        setupShadowBuffers();
        //---------This was included with the sample program and I'm honestly not sure what it does but I'm too afraid to remove it
        b.setElementAt(0,0,0.5);
        b.setElementAt(0,1,0.0);
        b.setElementAt(0,2,0.0);
        b.setElementAt(0,3,0.5f);
        b.setElementAt(1,0,0.0);
        b.setElementAt(1,1,0.5);
        b.setElementAt(1,2,0.0);
        b.setElementAt(1,3,0.5f);
        b.setElementAt(2,0,0.0);
        b.setElementAt(2,1,0.0);
        b.setElementAt(2,2,0.5);
        b.setElementAt(2,3,0.5f);
        b.setElementAt(3,0,0.0);
        b.setElementAt(3,1,0.0);
        b.setElementAt(3,2,0.0);
        b.setElementAt(3,3,1.0f);

        //---------Initialize all of the textures and normals
        moonTex = loadTexture("moon.jpg");  // moon surface texture
        textureID2 = moonTex.getTextureObject();

        // apply mipmapping and anisotropic filtering to moon surface texture
        gl.glBindTexture(GL_TEXTURE_2D, textureID2);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        gl.glGenerateMipmap(GL_TEXTURE_2D);
        if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))
        {	float aniso[] = new float[1];
            gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso, 0);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);
        }

        moonTex = loadTexture("moonNORMAL.jpg");  // moon normal map
        textureID1 = moonTex.getTextureObject();

        // apply mipmapping and anisotropic filtering to moon normal texture
        gl.glBindTexture(GL_TEXTURE_2D, textureID1);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        gl.glGenerateMipmap(GL_TEXTURE_2D);
        if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))
        {	float aniso[] = new float[1];
            gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso, 0);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);
        }

        ////////////////////////////////////////
        moonTex = loadTexture("earth.jpg");  // moon surface texture
        textureID3 = moonTex.getTextureObject();

        // apply mipmapping and anisotropic filtering to moon surface texture
        gl.glBindTexture(GL_TEXTURE_2D, textureID3);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        gl.glGenerateMipmap(GL_TEXTURE_2D);
        if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))
        {	float aniso[] = new float[1];
            gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso, 0);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);
        }

        //---------This may reduce shadow border artifacts, but probably not.
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        moonTex = loadTexture("Red.jpg");
        red = moonTex.getTextureObject();

        moonTex = loadTexture("Green.jpg");
        green = moonTex.getTextureObject();

        moonTex = loadTexture("Blue.jpg");
        blue = moonTex.getTextureObject();
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void setupShadowBuffers()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        scSizeX = myCanvas.getWidth();
        scSizeY = myCanvas.getHeight();

        gl.glGenFramebuffers(1, shadow_buffer, 0);

        gl.glGenTextures(1, shadow_tex, 0);
        gl.glBindTexture(GL_TEXTURE_2D, shadow_tex[0]);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32,
                scSizeX, scSizeY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        setupShadowBuffers();
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void setupVertices()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        //---------Default setup for the Vertex Arrays and Buffers
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(18, vbo, 0);

        //---------Setup the Grid Vertices, Textures, and Normals.
        Vertex3D[] grid_vertices = grid.getVertices();
        numGridVertices = grid.getNumVertices();
        float[] grid_fValues = new float[numGridVertices*3];
        float[] grid_tValues = new float[numGridVertices*2];
        float[] grid_nValues = new float[numGridVertices*3];

        for (int i=0; i<numGridVertices; i++)
        {	grid_fValues[i*3]   = (float) (grid_vertices[i]).getX();
            grid_fValues[i*3+1] = (float) (grid_vertices[i]).getY();
            grid_fValues[i*3+2] = (float) (grid_vertices[i]).getZ();

            grid_tValues[i*2] = (float) (grid_vertices[i]).getS();
            grid_tValues[i*2+1] = (float) (grid_vertices[i]).getT();

            grid_nValues[i*3]   = (float) (grid_vertices[i]).getNormalX();
            grid_nValues[i*3+1] = (float) (grid_vertices[i]).getNormalY();
            grid_nValues[i*3+2] = (float) (grid_vertices[i]).getNormalZ();
        }
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer grdVertBuf = Buffers.newDirectFloatBuffer(grid_fValues);
        gl.glBufferData(GL_ARRAY_BUFFER, grdVertBuf.limit()*4, grdVertBuf, GL_STATIC_DRAW);

        //---------Grid Normals into the second buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer grdNormBuf = Buffers.newDirectFloatBuffer(grid_nValues);
        gl.glBufferData(GL_ARRAY_BUFFER, grdNormBuf.limit()*4, grdNormBuf, GL_STATIC_DRAW);

        //---------Grid Textures into the second buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer grdTextBuf = Buffers.newDirectFloatBuffer(grid_tValues);
        gl.glBufferData(GL_ARRAY_BUFFER, grdTextBuf.limit()*4, grdTextBuf, GL_STATIC_DRAW);

        //---------Setup the Pyramid Vertices, Textures, and Normals.
        Vertex3D[] pyramid_vertices = pyramid.getVertices();
        numPyramidVertices = pyramid.getNumVertices();
        float[] pyramid_fValues = new float[numPyramidVertices*3];
        float[] pyramid_tValues = new float[numPyramidVertices*2];
        float[] pyramid_nValues = new float[numPyramidVertices*3];

        for (int i=0; i<numPyramidVertices; i++)
        {	pyramid_fValues[i*3]   = (float) (pyramid_vertices[i]).getX();
            pyramid_fValues[i*3+1] = (float) (pyramid_vertices[i]).getY();
            pyramid_fValues[i*3+2] = (float) (pyramid_vertices[i]).getZ();

            pyramid_tValues[i*2] = (float) (pyramid_vertices[i]).getS();
            pyramid_tValues[i*2+1] = (float) (pyramid_vertices[i]).getT();

            pyramid_nValues[i*3]   = (float) (pyramid_vertices[i]).getNormalX();
            pyramid_nValues[i*3+1] = (float) (pyramid_vertices[i]).getNormalY();
            pyramid_nValues[i*3+2] = (float) (pyramid_vertices[i]).getNormalZ();
        }
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        FloatBuffer pyrVertBuf = Buffers.newDirectFloatBuffer(pyramid_fValues);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrVertBuf.limit()*4, pyrVertBuf, GL_STATIC_DRAW);

        //---------Torus Normals into the second buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        FloatBuffer pyrNormBuf = Buffers.newDirectFloatBuffer(pyramid_nValues);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrNormBuf.limit()*4, pyrNormBuf, GL_STATIC_DRAW);

        //---------Torus Textures into the second buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        FloatBuffer pyrTextBuf = Buffers.newDirectFloatBuffer(pyramid_tValues);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrTextBuf.limit()*4, pyrTextBuf, GL_STATIC_DRAW);


        //---------Setup the Sphere Vertices, Textures, and Normals.
        Vertex3D[] sphere_vertices = sphere.getVertices();
        int[] sphere_indices = sphere.getIndices();
        float[] sphere_fValues = new float[sphere_indices.length*3];
        float[] sphere_nValues = new float[sphere_indices.length*3];
        float[] sphere_tValues = new float[sphere_indices.length*2];
        numSphereVertices = sphere_indices.length;

        for (int i=0; i<sphere_indices.length; i++)
        {	sphere_fValues[i*3]   = (float) (sphere_vertices[sphere_indices[i]]).getX();
            sphere_fValues[i*3+1] = (float) (sphere_vertices[sphere_indices[i]]).getY();
            sphere_fValues[i*3+2] = (float) (sphere_vertices[sphere_indices[i]]).getZ();

            sphere_tValues[i*2]   = (float) (sphere_vertices[sphere_indices[i]]).getS();
            sphere_tValues[i*2+1] = (float) (sphere_vertices[sphere_indices[i]]).getT();

            sphere_nValues[i*3]   = (float) (sphere_vertices[sphere_indices[i]]).getNormalX();
            sphere_nValues[i*3+1] = (float) (sphere_vertices[sphere_indices[i]]).getNormalY();
            sphere_nValues[i*3+2] = (float) (sphere_vertices[sphere_indices[i]]).getNormalZ();
        }

        //---------sphere Vertices into the first buffer
        //  put the Sphere vertices into the first buffer,
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        FloatBuffer sphVertBuf = Buffers.newDirectFloatBuffer(sphere_fValues);
        gl.glBufferData(GL_ARRAY_BUFFER, sphVertBuf.limit()*4, sphVertBuf, GL_STATIC_DRAW);

        // load the Sphere normal coordinates into the fourth buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        FloatBuffer sphNorBuf = Buffers.newDirectFloatBuffer(sphere_nValues);
        gl.glBufferData(GL_ARRAY_BUFFER, sphNorBuf.limit()*4, sphNorBuf, GL_STATIC_DRAW);

        // load the Sphere normal coordinates into the fourth buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        FloatBuffer sphTexBuf = Buffers.newDirectFloatBuffer(sphere_tValues);
        gl.glBufferData(GL_ARRAY_BUFFER, sphTexBuf.limit()*4, sphTexBuf, GL_STATIC_DRAW);

        //---------Setup the Torus Vertices, Textures, and Normals.
        Vertex3D[] torus_vertices = torus.getVertices();
        int[] torus_indices = torus.getIndices();
        float[] torus_fValues = new float[torus_indices.length*3];
        float[] torus_nValues = new float[torus_indices.length*3];
        float[] torus_tValues = new float[torus_indices.length*2];
        numTorusVertices = torus_indices.length;

        for (int i=0; i<torus_indices.length; i++)
        {	torus_fValues[i*3]   = (float) (torus_vertices[torus_indices[i]]).getX();
            torus_fValues[i*3+1] = (float) (torus_vertices[torus_indices[i]]).getY();
            torus_fValues[i*3+2] = (float) (torus_vertices[torus_indices[i]]).getZ();

            torus_tValues[i*2]   = (float) (torus_vertices[torus_indices[i]]).getS();
            torus_tValues[i*2+1] = (float) (torus_vertices[torus_indices[i]]).getT();

            torus_nValues[i*3]   = (float) (torus_vertices[torus_indices[i]]).getNormalX();
            torus_nValues[i*3+1] = (float) (torus_vertices[torus_indices[i]]).getNormalY();
            torus_nValues[i*3+2] = (float) (torus_vertices[torus_indices[i]]).getNormalZ();
        }

        //---------torus Vertices into the first buffer
        //  put the Torus vertices into the first buffer,
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
        FloatBuffer torVertBuf = Buffers.newDirectFloatBuffer(torus_fValues);
        gl.glBufferData(GL_ARRAY_BUFFER, torVertBuf.limit()*4, torVertBuf, GL_STATIC_DRAW);

        // load the Torus normal coordinates into the fourth buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
        FloatBuffer torNorBuf = Buffers.newDirectFloatBuffer(torus_nValues);
        gl.glBufferData(GL_ARRAY_BUFFER, torNorBuf.limit()*4, torNorBuf, GL_STATIC_DRAW);

        // load the Torus normal coordinates into the fourth buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
        FloatBuffer torTexBuf = Buffers.newDirectFloatBuffer(torus_tValues);
        gl.glBufferData(GL_ARRAY_BUFFER, torTexBuf.limit()*4, torTexBuf, GL_STATIC_DRAW);

        float[] xLine_positions = { 0.0f, 0.0f, 0.0f, 100.0f, 0.0f, 0.0f, 0.0f, 0.05f, 0.0f };
        float[] red_texCoords = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f };
        float[] yLine_positions = { 0.0f, 0.0f, 0.0f, 0.05f, 0.0f, 0.0f, 0.0f, 100.0f, 0.0f };
        float[] green_texCoords = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f };
        float[] zLine_positions = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 100.0f, 0.0f, -0.05f, 0.0f };
        float[] blue_texCoords = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f };

        //VBO's for x,y,z colroed axes (positions + texture coordinates)
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
        FloatBuffer xLine_pBuffer = Buffers.newDirectFloatBuffer(xLine_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, xLine_pBuffer.limit()*4, xLine_pBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
        FloatBuffer red_texBuffer = Buffers.newDirectFloatBuffer(red_texCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, red_texBuffer.limit()*4, red_texBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
        FloatBuffer yLine_pBuffer = Buffers.newDirectFloatBuffer(yLine_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, yLine_pBuffer.limit()*4, yLine_pBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
        FloatBuffer green_texBuffer = Buffers.newDirectFloatBuffer(green_texCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, green_texBuffer.limit()*4, green_texBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
        FloatBuffer zLine_pBuffer = Buffers.newDirectFloatBuffer(zLine_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, zLine_pBuffer.limit()*4, zLine_pBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
        FloatBuffer blue_texBuffer = Buffers.newDirectFloatBuffer(blue_texCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, blue_texBuffer.limit()*4, blue_texBuffer, GL_STATIC_DRAW);

    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void installLights(int rendering_program, Matrix3D v_matrix)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        //---------Setup the material for use
        Material currentMaterial = new Material();
        currentMaterial = thisMaterial;

        //---------Setup the light points
        Point3D lightP = fixedLight.getPosition();
        Point3D lightPv = lightP.mult(v_matrix);

        //---------Setup the current light position using the previous points
        float [] currLightPos = new float[] { (float) lightPv.getX(),
                (float) lightPv.getY(),
                (float) lightPv.getZ() };

        //---------Get the location of the global ambient light field in the shader
        int globalAmbLoc = gl.glGetUniformLocation(rendering_program, "globalAmbient");

        //---------Set the current globalAmbient settings
        gl.glProgramUniform4fv(rendering_program, globalAmbLoc, 1, globalAmbient, 0);

        //---------Get the locations of the light and material fields in the shader
        int ambLoc = gl.glGetUniformLocation(rendering_program, "light.ambient");
        int diffLoc = gl.glGetUniformLocation(rendering_program, "light.diffuse");
        int specLoc = gl.glGetUniformLocation(rendering_program, "light.specular");
        int posLoc = gl.glGetUniformLocation(rendering_program, "light.position");
        int MambLoc = gl.glGetUniformLocation(rendering_program, "material.ambient");
        int MdiffLoc = gl.glGetUniformLocation(rendering_program, "material.diffuse");
        int MspecLoc = gl.glGetUniformLocation(rendering_program, "material.specular");
        int MshiLoc = gl.glGetUniformLocation(rendering_program, "material.shininess");

        //---------Set the uniform light and material values in the shader
        gl.glProgramUniform4fv(rendering_program, ambLoc, 1, fixedLight.getAmbient(), 0);
        gl.glProgramUniform4fv(rendering_program, diffLoc, 1, fixedLight.getDiffuse(), 0);
        gl.glProgramUniform4fv(rendering_program, specLoc, 1, fixedLight.getSpecular(), 0);
        gl.glProgramUniform3fv(rendering_program, posLoc, 1, currLightPos, 0);
        gl.glProgramUniform4fv(rendering_program, MambLoc, 1, currentMaterial.getAmbient(), 0);
        gl.glProgramUniform4fv(rendering_program, MdiffLoc, 1, currentMaterial.getDiffuse(), 0);
        gl.glProgramUniform4fv(rendering_program, MspecLoc, 1, currentMaterial.getSpecular(), 0);
        gl.glProgramUniform1f(rendering_program, MshiLoc, currentMaterial.getShininess());
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String[] args) {
        new Starter();
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void dispose(GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) drawable.getGL();
        gl.glDeleteVertexArrays(1, vao, 0);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void createShaderPrograms()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];

        vBlinn1ShaderSource = util.readShaderSource("blinnVert1.shader");
        vBlinn2ShaderSource = util.readShaderSource("blinnVert2.shader");
        fBlinn2ShaderSource = util.readShaderSource("blinnFrag2.shader");

        int vertexShader1 = gl.glCreateShader(GL_VERTEX_SHADER);
        int vertexShader2 = gl.glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader2 = gl.glCreateShader(GL_FRAGMENT_SHADER);

        gl.glShaderSource(vertexShader1, vBlinn1ShaderSource.length, vBlinn1ShaderSource, null, 0);
        gl.glShaderSource(vertexShader2, vBlinn2ShaderSource.length, vBlinn2ShaderSource, null, 0);
        gl.glShaderSource(fragmentShader2, fBlinn2ShaderSource.length, fBlinn2ShaderSource, null, 0);

        gl.glCompileShader(vertexShader1);
        gl.glCompileShader(vertexShader2);
        gl.glCompileShader(fragmentShader2);

        rendering_program1 = gl.glCreateProgram();
        rendering_program2 = gl.glCreateProgram();

        gl.glAttachShader(rendering_program1, vertexShader1);
        gl.glAttachShader(rendering_program2, vertexShader2);
        gl.glAttachShader(rendering_program2, fragmentShader2);

        gl.glLinkProgram(rendering_program1);
        gl.glLinkProgram(rendering_program2);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private Matrix3D perspective(float fovy, float aspect, float n, float f)
    {
        float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);
        Matrix3D r = new Matrix3D();
        r.setElementAt(0,0,A);
        r.setElementAt(1,1,q);
        r.setElementAt(2,2,B);
        r.setElementAt(3,2,-1.0f);
        r.setElementAt(2,3,C);
        r.setElementAt(3,3,0.0f);
        return r;
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private Matrix3D lookAt(Point3D eye, Point3D target, Vector3D y)
    {
        Vector3D eyeV = new Vector3D(eye);
        Vector3D targetV = new Vector3D(target);
        Vector3D fwd = (targetV.minus(eyeV)).normalize();
        Vector3D side = (fwd.cross(y)).normalize();
        Vector3D up = (side.cross(fwd)).normalize();
        Matrix3D look = new Matrix3D();
        look.setElementAt(0,0, side.getX());
        look.setElementAt(1,0, up.getX());
        look.setElementAt(2,0, -fwd.getX());
        look.setElementAt(3,0, 0.0f);
        look.setElementAt(0,1, side.getY());
        look.setElementAt(1,1, up.getY());
        look.setElementAt(2,1, -fwd.getY());
        look.setElementAt(3,1, 0.0f);
        look.setElementAt(0,2, side.getZ());
        look.setElementAt(1,2, up.getZ());
        look.setElementAt(2,2, -fwd.getZ());
        look.setElementAt(3,2, 0.0f);
        look.setElementAt(0,3, side.dot(eyeV.mult(-1)));
        look.setElementAt(1,3, up.dot(eyeV.mult(-1)));
        look.setElementAt(2,3, (fwd.mult(-1)).dot(eyeV.mult(-1)));
        look.setElementAt(3,3, 1.0f);
        return(look);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public Texture loadTexture(String textureFileName)
    {	Texture tex = null;
        try {
            tex = TextureIO.newTexture(new File(textureFileName), false);
        } catch (Exception e) { e.printStackTrace(); }
        return tex;
    }

    public void lightUp(){
        lightLoc.setY(lightLoc.getY() + 0.25);
    }
    public void lightDown(){
        lightLoc.setY(lightLoc.getY() - 0.25);
    }

    public void lightRight(){
        lightLoc.setX(lightLoc.getX() + 0.25);
    }
    public void lightLeft(){
        lightLoc.setX(lightLoc.getX() - 0.25);
    }

    public void lightForward(){
        lightLoc.setZ(lightLoc.getZ() + 0.25);
    }
    public void lightBackward(){
        lightLoc.setZ(lightLoc.getZ() - 0.25);
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public void setLightOn() {
        if(isLightOn())
            this.lightOn = false;
        else
            this.lightOn = true;
    }

    public boolean isAxesOn() {
        return axesOn;
    }

    public void setAxesOn() {
        if(isAxesOn())
            this.axesOn = false;
        else
            this.axesOn = true;
    }
}