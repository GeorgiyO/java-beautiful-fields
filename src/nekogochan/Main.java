package nekogochan;

import nekogochan.field.Field;
import nekogochan.field.ReadField;

import java.time.LocalDate;

class Cat {

    Field<String> name;
    Field<LocalDate> birthDay;

    ReadField<LocalDate> age;

    public Cat(String _name, LocalDate _birthDay) {
        this.name = Field.of(_name);
        this.birthDay = Field.of(_birthDay);

        this.age = ReadField.dirty(() -> {
            System.out.println("Calculating age");
            var now = LocalDate.now().toEpochDay();
            return LocalDate.ofEpochDay(now - birthDay.get().toEpochDay());
        }, birthDay);
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

        for (int i = 0; i < 5; i++) {
            System.out.println(cat.age.get());
        }

        System.out.println(cat);
    }
}
