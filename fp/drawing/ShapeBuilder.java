package fp.drawing;

import java.util.LinkedList;
import fp.Vec2;

public class ShapeBuilder {
    private LinkedList<Vec2> points = new LinkedList<>();
    public ShapeBuilder() {}
    public ShapeBuilder(Vec2[] points) {
        for (Vec2 p : points) {
            this.points.add(p);
        }
    }
    public Vec2 pop() {
        return points.removeLast();
    }
    public void push(Vec2 p) {
        points.add(p);
    }
    public Shape toShape() {
        return Shape.Poly(points.toArray(Vec2[]::new));
    }
    public Shape withTransform(Transform t) {
        return Shape.Poly(points.toArray(Vec2[]::new), t);
    }
}
