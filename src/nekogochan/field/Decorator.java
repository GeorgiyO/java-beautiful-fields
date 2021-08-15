package nekogochan.field;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Decorator<T> {

    private List<UnaryOperator<T>> decorators = new ArrayList<>();
    private UnaryOperator<T> current = UnaryOperator.identity();

    public Decorator<T> set(UnaryOperator<T> decorator) {
        this.decorators = new ArrayList<>(decorators);
        return this;
    }

    public Decorator<T> add(UnaryOperator<T> decorator) {
        decorators.add(decorator);
        rebuildResultDecorator();
        return this;
    }

    public Decorator<T> remove(UnaryOperator<T> decorator) {
        decorators.remove(decorator);
        rebuildResultDecorator();
        return this;
    }

    public UnaryOperator<T> get() {
        return current;
    }

    private void rebuildResultDecorator() {
        current = decorators.stream()
                            .reduce(UnaryOperator.identity(),
                                    (prev, next) -> (value) -> next.apply(prev.apply(value))
                            );
    }
}
