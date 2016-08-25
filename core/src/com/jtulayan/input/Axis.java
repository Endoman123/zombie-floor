package com.jtulayan.input;

/**
 * Contains logic to handle two {@link Bind}s that, when used together, handle a specific value.
 * <p>
 * There are a few values here to keep in mind that go along with an {@code Axis}:
 * <ul>
 * <li>Sensitivity: The translationSpeed at which an {@code Axis} will travel from its current value to any polar</li>
 * <li>Gravity: The translationSpeed at which an {@code Axis} will travel from its current value to 0</li>
 * <li>Deadzone: The range at which any value within the range is considered 0, ranging from [-deadzone, deadzone]</li>
 * </ul>
 *
 * @author Jared Tulayan
 */
public class Axis {
    private Bind positive, negative;
    private float delta, sensitivity, gravity, deadzone;

    /**
     * Creates an empty {@link Axis} with the default {@link Bind}s and default
     * sensitivity, gravity, and deadzone values.
     */
    public Axis() {
        positive = new Bind();
        negative = new Bind();

        delta = 0;
        sensitivity = 1;
        gravity = 1;
        deadzone = 0;
    }

    /**
     * Creates an empty {@link Axis} with the specified {@link Bind}s and default
     * sensitivity, gravity, and deadzone values.
     *
     * @param p the {@link Bind} to act as the positive button
     * @param n the {@link Bind} to act as the negative button
     */
    public Axis(Bind p, Bind n) {
        positive = p;
        negative = n;

        delta = 0;
        sensitivity = 1;
        gravity = 1;
        deadzone = 0;
    }

    /**
     * Creates an empty {@link Axis} with the specified {@link Bind}s and specified
     * sensitivity, gravity, and deadzone values.
     *
     * @param p  the {@link Bind} to act as the positive button
     * @param n  the {@link Bind} to act as the negative button
     * @param s  the value of the sensitivity
     * @param g  the value of the gravity
     * @param dz the value of the deadzone
     */
    public Axis(Bind p, Bind n, float s, float g, float dz) {
        positive = p;
        negative = n;

        delta = 0;
        sensitivity = s;
        gravity = g;
        deadzone = dz;
    }
}
