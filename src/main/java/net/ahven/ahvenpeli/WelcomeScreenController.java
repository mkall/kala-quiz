package net.ahven.ahvenpeli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import net.ahven.ahvenpeli.config.Language;
import net.ahven.ahvenpeli.config.Quiz;

public class WelcomeScreenController {
	private final Quiz quiz;
	private final Consumer<Game> startGameOp;
	private final Consumer<Game> gameCompletedOp;

	private final List<HiscoreEntry> hiscoreEntries = new ArrayList<>();

	@FXML
	private GridPane hiscorePane;

	@FXML
	private Label welcomeLabel;

	@FXML
	private TextField nameField;

	@FXML
	private Button startButton;

	@FXML
	private FlowPane langPane;

	private String currLocale;

	public WelcomeScreenController(Quiz quiz, Consumer<Game> startGameOp, Consumer<Game> gameCompletedOp) {
		this.quiz = quiz;
		this.startGameOp = startGameOp;
		this.gameCompletedOp = gameCompletedOp;

		currLocale = quiz.getLanguages().get(0).getLocale();
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
		for (int i = 0; i < hiscoreEntries.size(); i++) {
			Label nameLabel = new Label(hiscoreEntries.get(i).getName());
			hiscorePane.add(nameLabel, 0, i);
			Label scoreLabel = new Label(Integer.toString(hiscoreEntries.get(i).getScore()));
			hiscorePane.add(scoreLabel, 1, i);
		}
	}

	public void reset() {
		nameField.setText("");
	}

	@FXML
	public void initialize() {
		assignLocale();

		for (Language lang : quiz.getLanguages()) {
			Button langButton = new Button();
			Image langImg = Util.loadImageFromPath(lang.getImagePath());
			langButton.setGraphic(new ImageView(langImg));
			langButton.setOnAction((e) -> {
				currLocale = lang.getLocale();
				assignLocale();
			});
			langPane.getChildren().add(langButton);
		}

		startButton.addEventHandler(ActionEvent.ACTION, (e) -> startGame());
	}

	private void startGame() {
		startGameOp.accept(new Game(quiz, currLocale, nameField.getText(), gameCompletedOp));
	}

	private void assignLocale() {
		welcomeLabel.setText(quiz.getWelcomeTextByLocale(currLocale));
		startButton.setText(quiz.getStartGameByLocale(currLocale));
	}
}
