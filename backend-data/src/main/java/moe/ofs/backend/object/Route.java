package moe.ofs.backend.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Route {
    private List<Point> points = new ArrayList<>();
    public void addPoint(Point point) {
        points.add(point);
    }
    public Point getPoint(int index) {
        return points.get(index);
    }
    public void removePoint(Point point) {
        points.remove(point);
    }
    public void removePoint(int index) {
        points.remove(index);
    }
}
