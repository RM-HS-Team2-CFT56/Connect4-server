package srv;

/**
 * Created by mmd on 2016-11-10.
 */
public class Location {

    private int x, y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location() {

    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        if (x < 0) {
            throw new UnsupportedOperationException("value cant be less than zero");
        }
        this.x = x;
    }

    public void setY(int y) {
        if (y < 0) {
            throw new UnsupportedOperationException("value cant be less than zero");
        }
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        if (x != location.x) return false;
        return y == location.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}

