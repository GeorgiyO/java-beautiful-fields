package nekogochan.field;

import java.util.function.Supplier;

public class Ref<T> implements Setter<T>, Supplier<T> {

    public T value;

    public Ref(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }
}

