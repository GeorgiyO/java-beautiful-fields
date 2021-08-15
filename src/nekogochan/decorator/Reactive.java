package nekogochan.decorator;

import nekogochan.field.Field;
import nekogochan.field.WriteField;

import java.util.function.Consumer;

public class Reactive {

    public interface Listener {
        void cancel();
    }

    public static <T> Listener onChange(WriteField<T> target, Consumer<T> listener) {
        Consumer<T> decorator = listener::accept;
        target.onValueSet(decorator);
        return () -> target.removeOnValueSetListener(decorator);
    }
}

class ReactiveTest {

    static class Cat {

        public final Field<Integer>
            age = Field.of(0),      // if you want to use Field.empty()
            strange = Field.of(0),  // then you need to add onChange listener
            agility = Field.of(0);  // after constructor params are applied

        {
            Reactive.onChange(age, (newAge) -> {
                strange.set(strange.get() + 1);
                agility.set(agility.get() + 1);
            });
        }

        public Cat(Integer age, Integer strange, Integer agility) {
            this.age.set(age);
            this.strange.set(strange);
            this.agility.set(agility);
        }

        public void incrementAge() {
            age.set(age.get() + 1);
        }
    }

    public static void main(String[] args) {
        var cat = new Cat(
            10, 14, 20
        );
        var cancelOnChange = Reactive.onChange(cat.strange, System.out::println);
        cat.incrementAge(); // prints 15
        cat.incrementAge(); // prints 16
        cancelOnChange.cancel();
        cat.incrementAge();
        cat.incrementAge();
    }
}
