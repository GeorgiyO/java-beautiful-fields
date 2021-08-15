package nekogochan.watchable;

import nekogochan.decorator.Reactive;
import nekogochan.field.Field;
import nekogochan.field.WriteField;

public interface Watchable {

    default <T> void useWatch(WriteField<T> target) {
        Reactive.onChange(target, (value) -> update());
    }

    void update();
}

class Console implements Watchable {

    Field<String>
        header = Field.of(""),
        user = Field.of("");

    Field<Integer>
        time = Field.of(0);

    Thread timeUpdater;

    public void init() {
        useWatch(time);

        Reactive.onChange(time, System.out::println);

        timeUpdater = new Thread(() -> {
            while (true) {
                time.set(time.get() + 1);
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timeUpdater.setDaemon(true);
    }

    public Console(String header, String user) {
        this.header.set(header);
        this.user.set(user);
        init();
    }

    @Override
    public void update() {
        System.out.printf(
            """
                ------------------------------------------------
                %s
                ------------------------------------------------
                Hello, %s
                Current time is: %s
                                
                """,
            header, user, time
        );
    }
}

class WatchableTest {
    public static void main(String[] args) throws InterruptedException {
        var console = new Console("N E K O G O C H A N ' S  L A I R", "Nekogochan");
        console.timeUpdater.start();
        Thread.sleep(10000);
    }
}
