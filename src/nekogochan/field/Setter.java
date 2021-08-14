package nekogochan.field;

import java.util.function.Consumer;

interface Setter<T> extends Consumer<T> {

    default void accept(T value) {
        this.set(value);
    }
    void set(T value);

}
