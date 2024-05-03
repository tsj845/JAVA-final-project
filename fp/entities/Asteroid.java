package fp.entities;

import fp.drawing.Drawable;
import fp.drawing.Shape;
import fp.drawing.Transform;
import fp.events.Event;
import fp.events.EventListener;

public class Asteroid extends Kinematic implements Entity, EventListener, Drawable {
    private int health;
    private Entity targetEnt;
    public Asteroid() {}
    public void trigger(Event e) {}
    public void draw() {
        shape.draw();
    }
    public void update() {
        super.update();
    }
    public int getHealth() {return health;}
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            destroy();
        }
    }
    public void setTarget(Entity t) {targetEnt = t;}
    public Entity getTarget() {return targetEnt;}
    public void destroy() {}
    public Shape getShape() {return shape;}
    public Transform transform() {return shape.transform;}
}
