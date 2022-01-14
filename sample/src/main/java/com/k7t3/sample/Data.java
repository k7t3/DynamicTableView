package com.k7t3.sample;

import javafx.scene.paint.Color;

public class Data {

    private final String name;

    private final Color color;

    public Data(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
