package spaceintruders;

public class Alien extends Entity {

    private Vec2 anchorPos;
    private double angularFrequency;
    private double phi;
    private double stride;

    public Alien(Vec2 _anchorPos,
            double _angularFrequency,
            double _phi,
            double _stride,
            double _speed,
            double _health,
            Sprite _sprite) {
        super(new Vec2(_anchorPos), _speed, _health, 0, _sprite);
        anchorPos = _anchorPos;
        angularFrequency = _angularFrequency;
        phi = _phi;
        stride = _stride;
    }

    public void setAnchorPos(Vec2 _anchorPos) {
        anchorPos = _anchorPos;
    }

    public Vec2 getAnchorPos() {
        return anchorPos;
    }

    public void setAngularFrequency(double _angularFrequency) {
        angularFrequency = _angularFrequency;
    }

    public double getAngularFrequency() {
        return angularFrequency;
    }

    public void setPhi(double _phi) {
        phi = _phi;
    }

    public double getPhi() {
        return phi;
    }

    public void setStride(double _stride) {
        stride = _stride;
    }

    public double getStride() {
        return stride;
    }
}
