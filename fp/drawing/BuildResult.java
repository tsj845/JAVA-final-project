package fp.drawing;

import fp.entities.Collider;

public class BuildResult {
    public final Shape shape;
    public final Collider collider;
    public BuildResult(Shape s, Collider c) {
        shape = s;
        collider = c;
    }
}
