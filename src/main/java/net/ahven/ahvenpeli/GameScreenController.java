package net.ahven.ahvenpeli;

import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.ahven.ahvenpeli.config.Option;
import net.ahven.ahvenpeli.config.Question;

public class GameScreenController {
	private final Game game;

	@FXML
	private VBox gameField;
	
	@FXML
	private VBox questionField;
	
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
		questionField.getChildren().clear();
		questionField.getChildren().add(new Label(question.getTextByLocale(game.getLocale())));
		
		if( question.getImagePath() != null ) {
			ImageView questionImage = new ImageView(Util.loadImageFromPath(question.getImagePath()));
			questionImage.getStyleClass().add("question-image");
			questionImage.fitWidthProperty().bind(gameField.widthProperty().multiply(0.6));
			questionImage.fitHeightProperty().bind(gameField.heightProperty().multiply(0.4));
			questionField.getChildren().add(questionImage);
		}
		
		GridPane optionsPane = new GridPane();
		optionsPane.setVgap(5);
		optionsPane.setHgap(5);
		optionsPane.setAlignment(Pos.CENTER);
		int col = 0;
		int row = 0;
		
		for( Option option : question.getOptions() ) {
			Button button = new Button(option.getTextByLocale(game.getLocale()));
			if( option.isCorrect() ) {
				ChangeListener<Boolean> revealListener = (a, o, n) -> {
					if( n.booleanValue() )  {
						button.getStyleClass().add("option-button-revealed");					
					} else {
						button.getStyleClass().remove("option-button-revealed");					
					}
				};
				game.revealAnswerProperty().addListener(revealListener);
			}
			button.getStyleClass().add("option-button");
			
			button.prefWidthProperty().bind(gameField.widthProperty().multiply(0.4));
			button.setOnAction((e) -> {
				game.answer(option);
				if( option.isCorrect() ) {
					button.pseudoClassStateChanged(PseudoClass.getPseudoClass("correct-guess"), true);
				} else {
					button.pseudoClassStateChanged(PseudoClass.getPseudoClass("incorrect-guess"), true);
				}
				
			});
			optionsPane.add(button, col, row);
			col++;
			if( col == 2 )  {
				col = 0;
				row++;
			}
		}
		questionField.getChildren().add(optionsPane);
	}
}
