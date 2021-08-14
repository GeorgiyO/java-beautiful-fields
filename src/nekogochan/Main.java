package nekogochan;

import nekogochan.field.Field;
import nekogochan.field.ReadField;

import java.time.LocalDate;

class Cat {

    final Field<String> name = Field.empty();
    final Field<LocalDate> birthDay = Field.empty();

    final ReadField<LocalDate> age = ReadField.dirty(() -> {
        System.out.println("Calculating age");
        var now = LocalDate.now().toEpochDay();
        return LocalDate.ofEpochDay(now - birthDay.get().toEpochDay());
    }, birthDay);

    public Cat(String name, LocalDate birthDay) {
        this.name.set(name);
        this.birthDay.set(birthDay);
    }

    @Override
    public String toString() {
        return String.format(
                """
                Beautiful cat,
                Name: %s,
                Birth Time: %s,
                Age: %s
                """,
                name.get(), birthDay.get(), age.get()
        );
    }
}

public class Main {
    public static void main(String[] args) {

        var cat = new Cat("Tomas", LocalDate.of(2004, 11, 13));

        System.out.println(cat.age.get());  // prints 'Calculating age'
        System.out.println(cat.age.get());
        cat.birthDay.set(LocalDate.of(2008, 11, 13));
        System.out.println(cat.age.get());  // prints 'Calculating age'
    }
}
