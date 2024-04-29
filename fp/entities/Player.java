package fp.entities;

import java.awt.Color;

import fp.FPoint;
import fp.Observer;
import fp.drawing.DrawManager;
import fp.drawing.Shape;
import fp.drawing.ShapeBuilder;
import fp.drawing.Transform;
import fp.events.Event;
import fp.events.EventListener;
import fp.events.SEvent;

public class Player extends Kinematic implements Entity, EventListener {
    private int health;
    public Player() {
        super();
        ShapeBuilder sb = new ShapeBuilder();
        sb.push(new FPoint(0, 0));
        sb.push(new FPoint(0.1, 0));
        sb.push(new FPoint(0.1, 0.1));
        sb.push(new FPoint(0, 0.1));
        shape = sb.toShape();
        shape.fill = new Color(0, 255, 0);
        DrawManager.add(shape);
        health = 100;
        Observer.register(this);
    }
    public void trigger(Event e) {
        if (e.type.signal() && ((SEvent)e).sigcode == SEvent.SIGTICK) {
            this.update();
            this.draw();
        }
    }
    public void draw() {
        shape.draw();
    }
    public void update() {
        super.update();
    }
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {}
    }
    public int getHealth() {
        return health;
    }
    public void setTarget(Entity t) {}
    public Entity getTarget() {return null;}
    public FPoint getPosition() {
        return shape.transform.getTranslation();
    }
    public void destroy() {
        DrawManager.remove(shape);
    }
    public Shape getShape() {
        return shape;
    }
    public Transform transform() {
        return shape.transform;
    }
}
