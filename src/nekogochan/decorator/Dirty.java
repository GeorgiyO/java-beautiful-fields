package nekogochan.decorator;

import nekogochan.field.Field;
import nekogochan.field.ReadField;
import nekogochan.field.Ref;
import nekogochan.field.WriteField;

import java.util.function.Supplier;

public class Dirty {

    static <T> void makeDirty(ReadField<T> readField, WriteField<?>... dependencies) {
        var dirty = new Ref<>(true);
        var cache = new Ref<T>(null);

        var readSource = readField.getReadSource();

        Supplier<T> result = () -> {
            if (dirty.get()) {
                cache.set(readSource.get());
                dirty.set(false);
            }
            return cache.get();
        };

        readField.setReadSource(result);

        for (var dependency : dependencies) {
            dependency.writeDecorator().add((value) -> {
                dirty.set(true);
                return value;
            });
        }
    }
}

class DirtyTest {

    static class Cat {

        public final Field<Integer>
            age = Field.empty(),
            strange = Field.empty(),
            agility = Field.empty();

        public final ReadField<Double> speed =
            ReadField.of(() -> {
                System.out.println("calculating speed");
                return (strange.get() + agility.get() * 1.5)
                       * (1.00 - age.get() / 100.0);
            });

        {
            Dirty.makeDirty(speed, age, strange, agility);
        }

        public Cat(Integer age, Integer strange, Integer agility) {
            this.age.set(age);
            this.strange.set(strange);
            this.agility.set(agility);
        }
    }

    public static void main(String[] args) {
        var cat = new Cat(
            10, 14, 20
        );
        System.out.println(cat.speed.get());    // prints 'calculating speed'
        System.out.println(cat.speed.get());
        cat.age.set(cat.age.get() + 1);
        System.out.println(cat.speed.get());    // prints 'calculating speed
        System.out.println(cat.speed.get());
    }
}
