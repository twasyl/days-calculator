package io.twasyl.days.calculator.cli;

/**
 * @author Thierry Wasylczenko
 * @version 0.1
 * @since 0.1
 */
public class Argument<VALUE> {
    protected String name;
    protected VALUE value;

    public Argument() {
        this(null, null);
    }

    public Argument(String name, VALUE value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public VALUE getValue() { return value; }
    public void setValue(VALUE value) { this.value = value; }
}
