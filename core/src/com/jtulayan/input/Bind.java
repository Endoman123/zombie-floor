package com.jtulayan.input;

/**
 * Contains key values and booleans to handle two inputs that are meant to handle one type of event.
 *
 * @author Jared Tulayan
 */
public class Bind {
    private final int[] VALUES;
    private boolean isPressed, isReleased;

    /**
     * Creates an empty {@link Bind}.
     */
    public Bind() {
        VALUES = new int[2];

        isPressed = false;
        isReleased = false;
    }

    /**
     * Creates a new {@link Bind} with both values set to the specified code.
     *
     * @param v the processor code to set both values to
     */
    public Bind(int v) {
        VALUES = new int[]{
                v,
                v
        };

        isPressed = false;
        isReleased = false;
    }

    /**
     * Creates a new {@link Bind} with both values set to the specified processor codes.
     *
     * @param v1 the processor code to set the first value to
     * @param v2 the processor code to set the second value to
     */
    public Bind(int v1, int v2) {
        VALUES = new int[]{
                v1,
                v2
        };

        isPressed = false;
        isReleased = false;
    }

    // region Accessors and Modifiers

    /**
     * Gets the array of processor codes in this {@link Bind}.
     *
     * @return the array of processor codes
     */
    public int[] getValues() {
        return VALUES;
    }

    public boolean getPressed() {
        return isPressed;
    }

    public void setPressed(boolean b) {
        isPressed = b;
    }

    public boolean getReleased() {
        return isReleased;
    }

    public void setReleased(boolean b) {
        isReleased = b;
    }

    public void setValues(int a, int b) {
        VALUES[0] = a;
        VALUES[1] = b;
    }

    // endregion
}
