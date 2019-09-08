package net.ahven.ahvenpeli;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import net.ahven.ahvenpeli.config.Language;
import net.ahven.ahvenpeli.config.Quiz;

public class Util {
	public static String confDir = "conf";

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

	public static boolean hasFileInPath(String path) {
		return new File(confDir + File.separatorChar + path).isFile();
	}

	public static Image loadImageFromPath(String path) {
		try (FileInputStream fis = new FileInputStream(confDir + File.separatorChar + path)) {
			return new Image(fis);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to load image from path: " + path, ex);
		}
	}
	
	public static Clip loadAudioClip(String path) {
		try (FileInputStream fis = new FileInputStream(confDir + File.separatorChar + path);
				BufferedInputStream bis = new BufferedInputStream(fis)) {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(bis));
			return clip;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to load audio clip from path: " + path, ex);
		}
	}

	public static void initBackground(Quiz quiz, StackPane root) {
		if (quiz.getBackgroundImagePath() != null) {
			Image background = Util.loadImageFromPath(quiz.getBackgroundImagePath());
			ImageView backgroundView = new ImageView(background);
			backgroundView.setPreserveRatio(true);
			backgroundView.fitWidthProperty().bind(root.widthProperty());
			backgroundView.fitHeightProperty().bind(root.heightProperty());
			root.getChildren().add(backgroundView);
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
