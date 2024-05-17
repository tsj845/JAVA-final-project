package fp.entities;

import fp.drawing.Shape;
import fp.drawing.Transform;

public interface Entity {
    public void draw();
    public void update(double dt);
    public void damage(int amount);
    public int getHealth();
    public void destroy();
    public Shape getShape();
    public Transform transform();
    public void reload();
}
