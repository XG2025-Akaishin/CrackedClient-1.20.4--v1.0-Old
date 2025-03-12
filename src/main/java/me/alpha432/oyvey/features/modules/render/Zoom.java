package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class Zoom 
    extends Module {

    public int prevFov;
    public double prevSens;
    public Setting<Integer> zoom = this.register(new Setting<>("Zoom", 1 , 10, 240));
    public Setting<Integer> fov = this.register(new Setting<>("Fov", 1 , 10, 240));

    public Zoom() {
        super("Zoom", "Zoom", Category.RENDER, true, false, true);
    }


	//@Override
	public void onEnable() {
		super.onEnable();

		prevFov = mc.options.getFov().getValue();
		prevSens = mc.options.getMouseSensitivity().getValue();

		mc.options.getFov().setValue((int) (prevFov / fov.getValue()));
		mc.options.getMouseSensitivity().setValue(prevSens / zoom.getValue());
	}

	//@Override
	public void onDisable() {
		mc.options.getFov().setValue(prevFov);
		mc.options.getMouseSensitivity().setValue(prevSens);

		super.onDisable();
	}
    
    @Override
    public void onUpdate() {//onUpdate
        //Zoom.mc.gameSettings.fovSetting = 10.0f;
        if (isOn()) {
        Zoom.mc.options.getFov().setValue((Integer)zoom.getValue());// = 10.0f;
        }
        if (isOff()){
        Zoom.mc.options.getFov().setValue((int)120.0f); // Default test
        }
    }
    
    /*@Override
    public void onDisable() {
        Zoom.mc.options.getFov().setValue((int) 120.0f);// = 120.0f;
    }*/
}
