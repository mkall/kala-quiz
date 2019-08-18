package net.ahven.ahvenpeli;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.ahven.ahvenpeli.config.Question;

public class GameScreenController {
	private final Game game;

	@FXML
	private VBox gameField;
	
	@FXML
	private Label questionLabel;

	@FXML
	private Label scoreLabel;

	@FXML
	private Label timeLabel;

	public GameScreenController(Game game) {
		this.game = game;
	}

	@FXML
	public void initialize() {
		scoreLabel.textProperty().bind(game.scoreProperty().asString());
		
		game.timerProperty().addListener((a, o, n) -> updateTime());
		game.currentQuestionProperty().addListener((a, o, n) -> updateQuestion(n));
		updateQuestion(game.currentQuestionProperty().get());
		
	}
	
	private void updateTime() {
		timeLabel.setText( String.format("%.1f", (double)game.timerProperty().get()/1000.0 ));
	}

	private void updateQuestion(Question question) {
		questionLabel.setText(question.getTextByLocale(game.getLocale()));
	}
}
