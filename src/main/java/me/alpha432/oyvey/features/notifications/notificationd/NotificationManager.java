package me.alpha432.oyvey.features.notifications.notificationd;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.event.impl.Render3DEvent;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

public class NotificationManager extends Module {

    private static NotificationManager INSTANCE = new NotificationManager();

    public NotificationManager() {
    super("NotificationGui", "aga", Category.CLIENT, true, false, false);
    this.setInstance();
    }


    private Setting<modeEn> mode = this.register (register(new Setting<> ("Mode", modeEn.New)));
    public Setting <Integer> fade = this.register ( new Setting <> ( "fade", 100, 0, 2048 ) );
    public Setting <Integer> fade2 = this.register ( new Setting <> ( "fade2", 500, 0, 2048 ) );
    public Setting <Integer> gggg = this.register ( new Setting <> ( "height", 100, 0, 2048 ));
    public Setting<Float> deltt = this.register(new Setting<>("delta", 1.0f, 0f, 5.0f));
    public Setting <Integer> colorAlpha = this.register ( new Setting <> ( "Alpha", 150, 0, 255, v -> this.mode.getValue() == modeEn.New));
    public Setting <Integer> colorGreen = this.register ( new Setting <> ( "Green", 255, 0, 255, v -> this.mode.getValue() == modeEn.New));
    public Setting <Integer> colorBlue = this.register ( new Setting <> ( "Blue", 0, 0, 255, v -> this.mode.getValue() == modeEn.New));
    public Setting <Integer> colorRed = this.register ( new Setting <> ( "Red", 0, 0, 255, v -> this.mode.getValue() == modeEn.New));

    public Setting <Integer> alpha = this.register ( new Setting <> ( "Alpha", 130, 0, 255, v -> this.mode.getValue() == modeEn.Old));
    public Setting <Integer> green = this.register ( new Setting <> ( "Green", 255, 0, 255, v -> this.mode.getValue() == modeEn.Old));
    public Setting <Integer> blue = this.register ( new Setting <> ( "Blue", 0, 0, 255, v -> this.mode.getValue() == modeEn.Old));
    public Setting <Integer> red = this.register ( new Setting <> ( "Red", 0, 0, 255, v -> this.mode.getValue() == modeEn.Old));


    public enum modeEn {
        Old, New
    }


    public static NotificationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotificationManager();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
    

    private static final List<NotificationNew> notificationsnew = new ArrayList<>();

    public static void publicity(String title, String content, int second, NotificationType type) {
            notificationsnew.add(new NotificationNew(title + " " +content,typeResolver(type),second * 1000));
    }


    static NotificationNew.Type typeResolver(NotificationType type){
        switch (type){
            case INFO: return NotificationNew.Type.INFO;
            case ERROR: return NotificationNew.Type.ERROR;
            case SUCCESS: return NotificationNew.Type.SUCCESS;
            case WARNING: return NotificationNew.Type.WARNING;
        }
        return NotificationNew.Type.WARNING;
    }

    //@SubscribeEvent
        @Subscribe
    public void onRender3D(Render3DEvent event) {

            if(mode.getValue() == modeEn.New) {

                if (notificationsnew.size() > 4)
                    notificationsnew.remove(0);

                double startY = gggg.getValue() - 36;

                for (int i = 0; i < notificationsnew.size(); i++) {
                    NotificationNew notification = notificationsnew.get(i);
                    notificationsnew.removeIf(NotificationNew::shouldDelete);

                    notification.render(event.getMatrix(), startY);
                    startY -= notification.getHeight() + 3;
                }
            }

    }


    public static class TimerHelper {

        private long ms = getCurrentMS();

        private long getCurrentMS() {
            return System.currentTimeMillis();
        }

        public boolean hasReached(float milliseconds) {
            return getCurrentMS() - ms > milliseconds;
        }

        public void reset() {
            ms = getCurrentMS();
        }

        public long getTime() {
            return getCurrentMS() - ms;
        }
    }
}