package neilyich.lr3;

public record H(double a, double b, double c, double d, double e) {
    public double at(double x, double y) {
        return a*x*x + b*y*y + c*x*y + d*x + e*y;
    }

    public double xx() {
        return 2 * a;
    }

    public double yy() {
        return 2 * b;
    }

    public double xAt(double x, double y) {
        return 2*a*x + c*y + d;
    }

    public double yAt(double x, double y) {
        return 2*b*y + c*x + e;
    }
}
