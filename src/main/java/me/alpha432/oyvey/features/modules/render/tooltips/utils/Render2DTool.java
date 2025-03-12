package me.alpha432.oyvey.features.modules.render.tooltips.utils;

public class Render2DTool {
    public record Rectangle(float x, float y, float x1, float y1) {
        public boolean contains(double x, double y) {
            return x >= this.x && x <= x1 && y >= this.y && y <= y1;
        }
    }
}
