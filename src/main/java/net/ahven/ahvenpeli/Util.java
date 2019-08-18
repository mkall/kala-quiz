package net.ahven.ahvenpeli;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.image.Image;
import net.ahven.ahvenpeli.config.Language;

public class Util {
	public static boolean hasByLocale(List<String> text, String locale) {
		for (int i = 0; (i + 1) < text.size(); i += 2) {
			if (text.get(i).equals(locale)) {
				return true;
			}
		}
		return false;
	}

	public static String getByLocale(List<String> text, String locale) {
		for (int i = 0; (i + 1) < text.size(); i += 2) {
			if (text.get(i).equals(locale)) {
				return text.get(i + 1);
			}
		}
		return "NO_TRANSLATION";
	}

	public static void hasLocalizationFor(List<String> text, List<Language> languages) {
		for (Language lang : languages) {
			boolean found = false;
			for (int i = 0; (i + 1) < text.size(); i += 2) {
				if (text.get(i).equals(lang.getLocale())) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new RuntimeException("Missing translation for language" + lang.getLocale());
			}
		}
	}

	public static Image loadImageFromPath(String path) {
		try (FileInputStream fis = new FileInputStream(path)) {
			return new Image(fis);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to load image from path: " + path, ex);
		}
	}

	public static void runInJfxThread(Runnable r) {
		if (Platform.isFxApplicationThread()) {
			r.run();
		} else {
			Platform.runLater(r);
		}
	}
}