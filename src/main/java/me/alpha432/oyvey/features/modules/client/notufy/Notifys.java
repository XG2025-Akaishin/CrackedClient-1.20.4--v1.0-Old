package me.alpha432.oyvey.features.modules.client.notufy;

public class Notifys {//REMOVE static
    public final FadeUtils firstFade = new FadeUtils(500L);
    public final FadeUtils endFade;
    public final FadeUtils yFade = new FadeUtils(500L);
    public final String first;
    public int delayed = 55;
    public boolean end;

    public Notifys(String string) {
        this.endFade = new FadeUtils(350L);
        this.first = string;
        this.firstFade.reset();
        this.yFade.reset();
        this.endFade.reset();
        this.end = false;
    }
    public enum type {
        Notify,
        Chat,
        Both
    }
    public enum mode {
        Line,
        Fill
    }
}