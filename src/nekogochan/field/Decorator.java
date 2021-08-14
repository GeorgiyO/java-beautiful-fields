package nekogochan.field;

import java.util.function.UnaryOperator;

public class Decorator<T> {

    private UnaryOperator<T> decorator = UnaryOperator.identity();

    public Decorator<T> set(UnaryOperator<T> decorator) {
        this.decorator = decorator;
        return this;
    }

    public Decorator<T> add(UnaryOperator<T> decorator) {
        var prev = this.decorator;
        this.decorator = (v) -> {
            v = prev.apply(v);
            return decorator.apply(v);
        };
        return this;
    }

    public UnaryOperator<T> get() {
        return this.decorator;
    }
}
