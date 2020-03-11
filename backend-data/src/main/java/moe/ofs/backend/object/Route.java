package moe.ofs.backend.object;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<Point> points = new ArrayList<>();
    public void addPoint(Point point) {
        points.add(point);
    }
    public void removePoint(Point point) {
        points.remove(point);
    }
    public void removePoint(int index) {
        points.remove(index);
    }
}
