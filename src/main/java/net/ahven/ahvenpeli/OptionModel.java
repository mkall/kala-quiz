package net.ahven.ahvenpeli;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import net.ahven.ahvenpeli.config.Option;

public class OptionModel {
	private final Option option;
	private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
	private final ReadOnlyBooleanWrapper blink = new ReadOnlyBooleanWrapper();
	
	public OptionModel(Option option) {
		this.option = option;
	}
	
	public Option getOption() {
		return option;
	}
	
	public ReadOnlyBooleanProperty selectedProperty() {
		return selected.getReadOnlyProperty();
	}
	
	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}
	
	public ReadOnlyBooleanProperty blinkProperty() {
		return blink.getReadOnlyProperty();
	}
	
	public void setBlink(boolean blink) {
		this.blink.set(blink);
	}
}
