package nekogochan.field;

import java.util.function.Supplier;

public class ComplexField<In, Out> implements IField<In, Out> {

    public WriteField<In> setter;
    public ReadField<Out> getter;

    public static <In, Out> ComplexField<In, Out> of(Setter<In> setter, Supplier<Out> getter) {
        var f = new ComplexField<In, Out>();
        f.setter = new WriteFieldImp<>(setter);
        f.getter = new ReadFieldImpl<>(getter);
        return f;
    }

    public static <In, Out> ComplexField<In, Out> of(IField<In, Out> field) {
        var f = new ComplexField<In, Out>();
        return ComplexField.of(field, field);
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
}
