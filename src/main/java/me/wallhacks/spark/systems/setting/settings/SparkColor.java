package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.Spark;

import java.awt.*;

public class SparkColor {

    public SparkColor(Color color){
        this.color = color;
    }

    public Color color;
    public Rainbow rainbow = Rainbow.OFF;

    public enum Rainbow {
        OFF("Off"),
        SLOW("Slow"),
        MEDIUM("Medium"),
        FAST("Fast"),
        PSYCHO("Psycho");

        private final String name;

        Rainbow(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public Rainbow next() {
            // No bounds checking required here, because the last instance overrides
            try {
                return values()[ordinal() + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                return values()[0];
            }
        }
    }
}
