package spaceintruders;

public class Entity {

    private Vec2 pos;
    private double speed;
    private double health;
    private double energy;
    private Sprite sprite;

    public Entity(Vec2 _pos, double _speed, double _health, double _energy, Sprite _sprite) {
        pos = _pos;
        speed = _speed;
        health = _health;
        energy = _energy;
        sprite = _sprite;
    }

    public void setPos(Vec2 _pos) {
        pos = new Vec2(_pos);
    }

    public Vec2 getPos() {
        return pos;
    }

    public void setSpeed(double _speed) {
        speed = _speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setHealth(double _health) {
        health = _health;
    }

    public double getHealth() {
        return health;
    }

    public void setEnergy(double _energy) {
        energy = _energy;
    }

    public double getEnergy() {
        return energy;
    }

    public void setSprite(Sprite _sprite) {
        sprite = _sprite;
    }
    
    public Sprite getSprite() {
        return sprite;
    }
}
