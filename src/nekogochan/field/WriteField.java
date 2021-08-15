package nekogochan.field;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public interface WriteField<In> extends Setter<In> {

    Decorator<In> writeDecorator();

    void setWriteEndpoint(Consumer<In> setter);

    void onValueSet(Consumer<In> listener);

    void removeOnValueSetListener(Consumer<In> listener);

    Consumer<In> getWriteEndpoint();

    static <In> WriteField<In> of(Consumer<In> setter) {
        return new WriteFieldImpl<>(setter);
    }
}

class WriteFieldImpl<In> implements WriteField<In> {

    public final Decorator<In> decorator = new Decorator<>();
    private Consumer<In> setter;
    private final List<Consumer<In>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void setWriteEndpoint(Consumer<In> setter) {
        this.setter = setter;
    }

    @Override
    public void onValueSet(Consumer<In> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeOnValueSetListener(Consumer<In> listener) {
        listeners.remove(listener);
    }

    @Override
    public Consumer<In> getWriteEndpoint() {
        return setter;
    }

    public WriteFieldImpl(Consumer<In> setter) {
        this.setter = setter;
    }

    @Override
    public Decorator<In> writeDecorator() {
        return decorator;
    }

    @Override
    public void set(In value) {
        var result = decorator.get().apply(value);
        setter.accept(result);
        listeners.forEach((c) -> c.accept(result));
    }
}