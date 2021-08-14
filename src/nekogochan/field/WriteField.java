package nekogochan.field;

import java.util.function.Consumer;

public interface WriteField<In> extends Setter<In> {

    Decorator<In> writeDecorator();

    static <In> WriteField<In> of(Consumer<In> setter) {
        return new WriteFieldImp<>(setter);
    }
}

class WriteFieldImp<In> implements WriteField<In> {

    public final Decorator<In> decorator = new Decorator<>();
    private final Consumer<In> setter;

    public WriteFieldImp(Consumer<In> setter) {
        this.setter = setter;
    }

    @Override
    public Decorator<In> writeDecorator() {
        return decorator;
    }

    @Override
    public void set(In value) {
        setter.accept(decorator.get().apply(value));
    }
}