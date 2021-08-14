package nekogochan.field;

import java.util.function.Supplier;

class Lazy<T> implements Supplier<T> {

    private T value;
    private Supplier<T> getter;

    public static <T> Lazy<T> of(Supplier<T> initializer) {
        var lazy = new Lazy<T>();

        lazy.getter = () -> {
            if (lazy.value == null) {
                synchronized (lazy) {
                    if (lazy.value == null) {
                        lazy.value = initializer.get();
                    }
                }
            }
            lazy.getter = () -> lazy.value;
            return lazy.value;
        };

        return lazy;
    }

    @Override
    public T get() {
        return getter.get();
    }
}