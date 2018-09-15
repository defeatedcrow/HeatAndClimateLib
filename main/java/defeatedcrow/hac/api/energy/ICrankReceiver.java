package defeatedcrow.hac.api.energy;

public interface ICrankReceiver {

	boolean isPressed();

	boolean isMaxPressed();

	void setPressed(boolean flag);

	void setMaxPressed(boolean flag);

}
