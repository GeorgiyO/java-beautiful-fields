package nekogochan.field;

import java.util.function.Supplier;

public interface ReadField<Out> extends Supplier<Out> {

    Decorator<Out> readDecorator();

    void setReadSource(Supplier<Out> getter);

    Supplier<Out> getReadSource();

    static <Out> ReadField<Out> of(Supplier<Out> getter) {
        return new ReadFieldImpl<>(getter);
    }
}

class ReadFieldImpl<Out> implements ReadField<Out> {

    public final Decorator<Out> decorator = new Decorator<>();
    private Supplier<Out> getter;

    public ReadFieldImpl(Supplier<Out> getter) {
        this.getter = getter;
    }

    @Override
    public void setReadSource(Supplier<Out> getter) {
        this.getter = getter;
    }

    @Override
    public Supplier<Out> getReadSource() {
        return getter;
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