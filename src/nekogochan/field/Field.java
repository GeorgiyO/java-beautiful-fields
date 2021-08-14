package nekogochan.field;

import java.util.function.Supplier;

public class Field<T> implements IField<T, T> {

    private final ComplexField<T, T> complexField;

    private Field(ComplexField<T, T> complexField) {
        this.complexField = complexField;
    }

    public static <T> Field<T> of(Setter<T> setter, Supplier<T> getter) {
        return new Field<>(ComplexField.of(setter, getter));
    }

    public static <T> Field<T> of(IField<T, T> field) {
        return new Field<>(ComplexField.of(field));
    }

    public static <T> Field<T> of(Ref<T> ref) {
        return new Field<>(ComplexField.of(ref, ref));
    }

    public static <T> Field<T> of(T value) {
        var ref = new Ref<>(value);
        return Field.of(ref);
    }

    public static <T> Field<T> empty() {
        var ref = new Ref<T>(null);
        return Field.of(ref);
    }

    @Override
    public T get() {
        return complexField.get();
    }

    @Override
    public void set(T value) {
        complexField.set(value);
    }

    @Override
    public Decorator<T> readDecorator() {
        return complexField.readDecorator();
    }

    @Override
    public Decorator<T> writeDecorator() {
        return complexField.writeDecorator();
    }
}
