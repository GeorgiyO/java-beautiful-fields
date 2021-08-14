package nekogochan.field;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ReadField<Out> extends Supplier<Out> {

    Decorator<Out> readDecorator();

    static <Out> ReadField<Out> of(Supplier<Out> getter) {
        return new ReadFieldImpl<>(getter);
    }

    static <Out> ReadField<Out> dirty(Supplier<Out> getter, WriteField<?>... dependencies) {
        var dirty = new Ref<>(true);
        var cache = new Ref<Out>(null);

        var result = ReadField.of(() -> {
            if (dirty.get()) {
                cache.set(getter.get());
                dirty.set(false);
            }
            return cache.get();
        });

        for (var dependency : dependencies) {
            dependency.writeDecorator().add((value) -> {
                dirty.set(true);
                return value;
            });
        }

        return result;
    }
}

class ReadFieldImpl<Out> implements ReadField<Out> {

    public final Decorator<Out> decorator = new Decorator<>();
    private final Supplier<Out> getter;

    public ReadFieldImpl(Supplier<Out> getter) {
        this.getter = getter;
    }

    @Override
    public Out get() {
        return decorator.get().apply(getter.get());
    }

    @Override
    public Decorator<Out> readDecorator() {
        return decorator;
    }
}