package nekogochan;

import nekogochan.decorator.Reactive;
import nekogochan.field.Field;
import nekogochan.watchable.Watchable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class Animal {
    public final Field<String>
        name = Field.empty();

    public final Field<Double>
        speed = Field.empty();

    public final Field<Double>
        position = Field.empty();

    public Animal(String name, Double speed, Double position) {
        this.name.set(name);
        this.speed.set(speed);
        this.position.set(position);
    }

    public void moveForward() {
        position.set(position.get() + speed.get());
    }
}

class Cat extends Animal {
    public Cat(String name, Double speed, Double position) {
        super(name, speed, position);
    }
}

class Dog extends Animal {
    public Dog(String name, Double speed, Double position) {
        super(name, speed, position);
    }
}

class Arena implements Watchable {

    public final Field<Cat>
        cat = Field.empty();

    public final Field<Dog>
        dog = Field.empty();

    public final Field<Integer>
        border = Field.empty(),
        delay = Field.of(100);

    public final Field<Boolean>
        running = Field.of(false);

    public final Field<String>
        winner = Field.of("none");

    public final Field<Phase>
        phase = Field.of(Phase.NONE);

    private final Thread moveThread = new Thread(() -> {
        try {
            while (running.get()) {
                cat.get().moveForward();
                dog.get().moveForward();
                Thread.sleep(delay.get());
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });

    private final List<Reactive.Listener> firstPhaseListeners = new ArrayList<>();
    private final List<Reactive.Listener> secondPhaseListeners = new ArrayList<>();

    Arena(Cat cat, Dog dog, Integer border) {
        this.cat.set(cat);
        this.dog.set(dog);
        this.border.set(border);
        init();
    }

    private void init() {
        Reactive.onChange(running, (value) -> {
            if (value) {
                moveThread.start();
            } else {
                stopAnyPhase();
            }
        });

        Reactive.onChange(phase, (value) -> {
            switch (value) {
                case NONE, END -> running.set(false);
                case FIRST -> switchToFirstPhase();
                case SECOND -> switchToSecondPhase();
            }
        });

        Reactive.onChange(border, (v) -> updateLines());
        updateLines();

        useWatch(cat.get().position);
        useWatch(dog.get().position);
        useWatch(phase);

        Reactive.onChange(phase, System.out::println);
    }

    private void switchToFirstPhase() {
        running.set(true);

        Stream.of(
            Reactive.onChange(dog.get().position, (pos) -> {
                if (pos >= cat.get().position.get()) {
                    winner.set(dog.get().name.get());
                    phase.set(Phase.END);
                }
            }),
            Reactive.onChange(cat.get().position, (pos) -> {
                if (pos >= border.get()) {
                    phase.set(Phase.SECOND);
                }
            })
        ).forEach(firstPhaseListeners::add);
    }

    private void endFirstPhase() {
        firstPhaseListeners.forEach(Reactive.Listener::cancel);
        firstPhaseListeners.clear();
    }

    private void switchToSecondPhase() {

        endFirstPhase();

        dog.get().speed.set(
            -dog.get().speed.get()
        );

        cat.get().speed.set(
            dog.get().speed.get()
            - 0.5
        );

        Stream.of(
            Reactive.onChange(dog.get().position, (pos) -> {
                if (pos <= 0) {
                    winner.set(dog.get().name.get());
                    phase.set(Phase.END);
                }
            }),
            Reactive.onChange(cat.get().position, (pos) -> {
                if (pos <= dog.get().position.get()) {
                    winner.set(cat.get().name.get());
                    phase.set(Phase.END);
                }
            })
        ).forEach(secondPhaseListeners::add);
    }

    private void endSecondPhase() {
        secondPhaseListeners.forEach(Reactive.Listener::cancel);
        secondPhaseListeners.clear();
    }

    private void stopAnyPhase() {
        endFirstPhase();
        endSecondPhase();
    }

    public void start() {
        phase.set(Phase.FIRST);
    }

    @Override
    public void update() {
        System.out.println(
            Stream.of(
                borderHorizontalLine,
                headerMiddleTemplate(
                    phase.get() == Phase.END
                    ? winner.get() + " ВЫИГРАЛ"
                    : phase.get().value),
                horizontalLine,
                emptyRow,
                raceTemplate(),
                emptyRow,
                borderHorizontalLine,
                "",
                ""
            ).reduce("", (prev, next) -> prev + "\n" + next)
        );
    }

    private void updateLines() {
        borderHorizontalLine = lineTemplate("+", "-");
        horizontalLine = lineTemplate("|", "-");
        emptyRow = lineTemplate("|", " ");
    }

    private String borderHorizontalLine;
    private String horizontalLine;
    private String emptyRow;

    private String lineTemplate(String border, String middle) {
        return border + middle.repeat(this.border.get()) + border;
    }

    private String headerMiddleTemplate(String value) {
        return String.format("| ГОНАЧКИ: %s |", value + " ".repeat(border.get() - value.length() - 11));
    }

    private String raceTemplate() {
        var catPos = (int) (double) (cat.get().position.get());
        var dogPos = (int) (double) (dog.get().position.get());

        var left = Math.min(catPos, dogPos);
        var right = Math.max(catPos, dogPos);

        return left == right
               ? "=".repeat(left) + "X" + "=".repeat(border.get() - left)
               : "=".repeat(left) + "D" + "=".repeat(right - left) + "C" + "=".repeat(border.get() - right);
    }

    private enum Phase {
        NONE("Не началась"),
        FIRST("Первая"),
        SECOND("Вторая"),
        END("Конец");

        String value;

        Phase(String value) {
            this.value = value;
        }
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var arena = new Arena(
            new Cat("Tomas", 1.0, 10.0),
            new Dog("Bobik", 1.1, 0.0),
            50
        );
        arena.start();
        Thread.sleep(1000);
    }
}
