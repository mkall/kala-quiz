package net.ahven.ahvenpeli.config;

import java.util.List;

import net.ahven.ahvenpeli.Util;

public class Question {
	private List<String> text;
	private String imagePath;
	private List<Option> options;

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

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "Question [text=" + text + ", imagePath=" + imagePath + ", options=" + options + "]";
	}
}
