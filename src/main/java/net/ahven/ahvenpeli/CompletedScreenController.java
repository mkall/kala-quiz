package net.ahven.ahvenpeli;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.ahven.ahvenpeli.config.Quiz;

public class CompletedScreenController {
	private final Quiz quiz;
	private final Game game;

	@FXML
	private Label congratulationsLabel;

	public CompletedScreenController(Quiz quiz, Game game) {
		this.quiz = quiz;
		this.game = game;
	}

	@FXML
	public void initialize() {
		congratulationsLabel.setText(quiz.getCongratulationsTextByLocale(game.getLocale())
				.replace("${name}", game.getPlayerName())
				.replace("${score}", Integer.toString(game.scoreProperty().get())));
	}
}
