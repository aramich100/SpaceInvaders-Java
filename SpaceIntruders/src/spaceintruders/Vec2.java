package spaceintruders;

public class Vec2 {

    private double x, y;

    public Vec2(double _x, double _y) {
        x = _x;
        y = _y;
    }

    public Vec2(Vec2 v) {
        x = v.getX();
        y = v.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double _x) {
        x = _x;
    }

    public void setY(double _y) {
        y = _y;
    }
}
