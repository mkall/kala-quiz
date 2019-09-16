package net.ahven.ahvenpeli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import net.ahven.ahvenpeli.config.Language;
import net.ahven.ahvenpeli.config.Quiz;

public class WelcomeScreenController {
	private final Quiz quiz;
	private final Consumer<String> startGameOp;

	private final List<HiscoreEntry> hiscoreEntries = new ArrayList<>();

	@FXML
	private GridPane hiscorePane;

	@FXML
	private VBox welcomeLabelsBox;

	@FXML
	private FlowPane langPane;

	public WelcomeScreenController(Quiz quiz, Consumer<String> startGameOp) {
		this.quiz = quiz;
		this.startGameOp = startGameOp;
	}

	public void addHiscoreEntry(HiscoreEntry hiscoreEntry) {
		int position = 0;
		for (int i = 0; i < hiscoreEntries.size(); i++) {
			if (hiscoreEntries.get(i).getScore() < hiscoreEntry.getScore()) {
				break;
			}
			position = i + 1;
		}
		hiscoreEntries.add(position, hiscoreEntry);
		if (hiscoreEntries.size() > 5) {
			hiscoreEntries.remove(5);
		}
		hiscorePane.getChildren().clear();
		int row = 0;
		for (int i = 0; i < hiscoreEntries.size(); i++) {
			Label positionLabel = new Label(Integer.toString((i + 1)) + ".");
			positionLabel.setPrefWidth(20);
			hiscorePane.add(positionLabel, 0, row);
			Label nameLabel = new Label(hiscoreEntries.get(i).getName());
			nameLabel.setAlignment(Pos.CENTER_LEFT);
			nameLabel.setPrefWidth(260.0);
			hiscorePane.add(nameLabel, 1, row);
			Label scoreLabel = new Label(Integer.toString(hiscoreEntries.get(i).getScore()));
			scoreLabel.setPrefWidth(80);
			scoreLabel.setAlignment(Pos.CENTER_RIGHT);

			hiscorePane.add(scoreLabel, 2, row++);
			hiscorePane.add(new Separator(Orientation.HORIZONTAL), 0, row++, 3, 1);
		}
	}

	public void reset() {

	}

	@FXML
	public void initialize() {
		for (Language lang : quiz.getLanguages()) {
			Label langLabel = new Label(quiz.getWelcomeTextByLocale(lang.getLocale()));
			welcomeLabelsBox.getChildren().add(langLabel);

			Button langButton = new Button();
			Image langImg = Util.loadImageFromPath(lang.getImagePath());
			langButton.setGraphic(new ImageView(langImg));
			langButton.setOnAction((e) -> {
				startGameOp.accept(lang.getLocale());
			});
			langPane.getChildren().add(langButton);
		}
	}
}
