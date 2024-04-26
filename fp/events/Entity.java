package fp.events;

import java.awt.Point;

public interface Entity {
    public void draw();
    public void update();
    public void damage(int amount);
    public int getHealth();
    public void setTarget(Entity t);
    public Entity getTarget();
    public Point getPosition();
}
