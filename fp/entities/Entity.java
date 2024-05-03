package fp.entities;

import fp.drawing.Shape;
import fp.drawing.Transform;

public interface Entity {
    public void draw();
    public void update();
    public void damage(int amount);
    public int getHealth();
    public void setTarget(Entity t);
    public Entity getTarget();
    public void destroy();
    public Shape getShape();
    public Transform transform();
}
