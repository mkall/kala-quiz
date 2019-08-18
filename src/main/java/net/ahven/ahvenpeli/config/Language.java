package net.ahven.ahvenpeli.config;

public class Language {
	private String locale;
	private String imagePath;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public String toString() {
		return "Language [locale=" + locale + ", imagePath=" + imagePath + "]";
	}

}
