package com.fnaf6.mod.rating;

public final class FazRating {
    public double entertainment;
    public double atmosphere;
    public double safety;
    public double revenue;

    public FazRating(double entertainment, double atmosphere, double safety, double revenue) {
        this.entertainment = clamp(entertainment);
        this.atmosphere = clamp(atmosphere);
        this.safety = clamp(safety);
        this.revenue = clamp(revenue);
    }

    public double overall() {
        return (entertainment + atmosphere + safety + revenue) / 4.0;
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(100.0, value));
    }
}
