package objects;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class Camera {
    private Vector3D u, v, n, view;
    private Matrix3D identity;

    public Camera() {
        init();
        view = new Vector3D(0, 0, 0);
    }

    public void init(){
        u = new Vector3D(-0.5, 0.0, 0.0, 0.0);
        v = new Vector3D(0.0, 1.0, 0.0, 0.0);
        n = new Vector3D(0.0, 0.0, 10.0, 0.0);

        identity = new Matrix3D(new double[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }

    //Move in the X direction
    public void moveX(double x){
        u.setX(u.getX() + x);
    }

    //Move in the Y direction
    public void moveY(double x){
        v.setY(v.getY() + x);
    }

    //Move in the z direction
    public void moveZ(double x){
        n.setZ(n.getZ() + x);
    }

    public double getX(){
        return u.getX();
    }

    public double getY(){
        return v.getY();
    }

    public double getZ(){
        return n.getZ();
    }

    //Pan the camera
    public void pan(double x){
        Matrix3D V_rot = new Matrix3D();
        V_rot.rotate(x, v.normalize());

        n = n.mult(V_rot);
        u = u.mult(V_rot);
    }

    //Pitch the camera
    public void pitch(double x){
        Matrix3D V_rot = new Matrix3D();
        V_rot.rotate(x, u.normalize());

        n = n.mult(V_rot);
        v = v.mult(V_rot);
    }

    //Compute the view matrix
    public Matrix3D computeView(){
        Matrix3D temp = (Matrix3D) identity.clone(), t = (Matrix3D) identity.clone();

        temp.setCol(3, new Vector3D(-view.getX(), -view.getY(), -view.getZ(), 1));

        t.setRow(0, u);
        t.setRow(1, v);
        t.setRow(2, n);

        t.concatenate(temp);

        return t;
    }
}