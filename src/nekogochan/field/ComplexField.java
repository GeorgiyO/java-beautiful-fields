package nekogochan.field;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ComplexField<In, Out> implements IField<In, Out> {

    public final WriteField<In> setter;
    public final ReadField<Out> getter;

    public ComplexField(WriteField<In> setter, ReadField<Out> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    public static <In, Out> ComplexField<In, Out> of(Setter<In> setter, Supplier<Out> getter) {
        return new ComplexField<>(
            WriteField.of(setter),
            ReadField.of(getter)
        );
    }

    public static <In, Out> ComplexField<In, Out> of(IField<In, Out> field) {
        return ComplexField.of(field, field);
    }

    @Override
    public void setReadSource(Supplier<Out> getter) {
        this.getter.setReadSource(getter);
    }

    @Override
    public Supplier<Out> getReadSource() {
        return getter.getReadSource();
    }

    @Override
    public void setWriteEndpoint(Consumer<In> setter) {
        this.setter.setWriteEndpoint(setter);
    }

    @Override
    public void onValueSet(Consumer<In> listener) {
        setter.onValueSet(listener);
    }

    @Override
    public void removeOnValueSetListener(Consumer<In> listener) {
        setter.removeOnValueSetListener(listener);
    }

    @Override
    public Consumer<In> getWriteEndpoint() {
        return setter.getWriteEndpoint();
    }

    @Override
    public Out get() {
        return getter.get();
    }

    @Override
    public void set(In value) {
        setter.set(value);
    }

    @Override
    public Decorator<Out> readDecorator() {
        return getter.readDecorator();
    }

    @Override
    public Decorator<In> writeDecorator() {
        return setter.writeDecorator();
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }
}
