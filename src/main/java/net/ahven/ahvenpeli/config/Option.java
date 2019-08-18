package net.ahven.ahvenpeli.config;

import java.util.List;

import net.ahven.ahvenpeli.Util;

public class Option {
	private List<String> text;
	private String imagePath;
	private boolean correct = false;

	public List<String> getText() {
		return text;
	}

	public String getTextByLocale(String locale) {
		return Util.getByLocale(text, locale);
	}

	public void setText(List<String> text) {
		this.text = text;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	@Override
	public String toString() {
		return "Option [text=" + text + ", imagePath=" + imagePath + ", correct=" + correct + "]";
	}
}
