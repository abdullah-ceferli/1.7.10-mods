package dev.industrialtechnology.builder;

public enum BuildMode {
    SINGLE("Single"),LINE("Line"),DIAGONAL_LINE("Diagonal line"),WALL("Wall"),FLOOR("Floor"),BOX("Box"),FRAME("Frame"),DIAGONAL_WALL("Diagonal wall"),SLOPE_FLOOR("Slope floor"),CIRCLE("Circle"),CYLINDER("Cylinder"),SPHERE("Sphere");
    public final String label;
    BuildMode(String label){this.label=label;}
    public static BuildMode at(int id){return id>=0&&id<values().length?values()[id]:LINE;}
    public boolean round(){return this==CIRCLE||this==CYLINDER||this==SPHERE;}
}
