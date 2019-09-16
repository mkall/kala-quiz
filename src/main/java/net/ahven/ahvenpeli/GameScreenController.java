package net.ahven.ahvenpeli;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.ahven.ahvenpeli.config.Option;
import net.ahven.ahvenpeli.config.Question;

public class GameScreenController {
	private static final double TIMERBAR_WIDTH = 300.0;
	private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
	
	private final Game game;

	@FXML
	private VBox gameField;
	
	@FXML
	private VBox questionField;
	
	@FXML
	private Label scoreLabel;

	@FXML
	private StackPane timerPane;

	@FXML
	private Label timeLabel;

	@FXML
	private Button stopButton;

	private Rectangle timerBar;

	public GameScreenController(Game game) {
		this.game = game;
	}

	@FXML
	public void initialize() {
		scoreLabel.textProperty().bind(game.scoreProperty().asString());
		
		Rectangle timerBorder = new Rectangle();
		timerBorder.setWidth(TIMERBAR_WIDTH);
		timerBorder.setHeight(30.0);
		timerBorder.setStroke(Color.BLACK);
		timerBorder.setFill(Color.WHITE);
		timerPane.getChildren().add(0, timerBorder);
		
		timerBar = new Rectangle();
		timerBar.setHeight(26.0);
		timerBar.setFill(Color.SLATEGRAY);
		timerPane.getChildren().add(1, timerBar);

		stopButton.setGraphic(new ImageView(Util.loadImageFromPath(game.getQuiz().getStopImagePath())));
		stopButton.setOnAction((e) -> game.abortGame());

		game.timerProperty().addListener((a, o, n) -> updateTime());
		game.currentQuestionProperty().addListener((a, o, n) -> updateQuestion(n));
		updateQuestion(game.currentQuestionProperty().get());
		
	}
	
	private void updateTime() {
		timeLabel.setText( String.format("%.1f", (double)game.timerProperty().get()/1000.0 ));
		timerBar.setWidth((TIMERBAR_WIDTH - 4.0)
				* (game.timerProperty().doubleValue() / ((double) game.getQuiz().getTimePerQuestion() * 1000.0)));
		timerBar.setTranslateX((-(TIMERBAR_WIDTH - 4) / 2.0) + (timerBar.getWidth() / 2.0));
	}

	private void updateQuestion(QuestionModel questionModel) {
		questionField.getChildren().clear();
		
		if( questionModel == null ) {
			return;
		}
		
		Question question = questionModel.getQuestion();
		questionField.getChildren().add(new Label(question.getTextByLocale(game.getLocale())));
		
		if( question.getImagePath() != null ) {
			ImageView questionImage = new ImageView(Util.loadImageFromPath(question.getImagePath()));
			questionImage.getStyleClass().add("question-image");
			questionImage.fitWidthProperty().bind(gameField.widthProperty().multiply(0.6));
			questionImage.fitHeightProperty().bind(gameField.heightProperty().multiply(0.4));
			questionImage.setPreserveRatio(true);
			questionField.getChildren().add(questionImage);
		}
		
		GridPane optionsPane = new GridPane();
		optionsPane.setVgap(20);
		optionsPane.setHgap(20);
		optionsPane.setAlignment(Pos.CENTER);
		int col = 0;
		int row = 0;
		
		for( OptionModel optionModel : questionModel.getOptions() ) {
			Option option = optionModel.getOption();
			Button button = new Button(option.getTextByLocale(game.getLocale()));
			optionModel.selectedProperty().addListener((a, o, n) -> {
				if( n.booleanValue() ) {
					if( option.isCorrect() ) {
						button.pseudoClassStateChanged(PseudoClass.getPseudoClass("correct-guess"), true);
					} else {
						button.pseudoClassStateChanged(PseudoClass.getPseudoClass("incorrect-guess"), true);
					}
				} else {
					button.pseudoClassStateChanged(PseudoClass.getPseudoClass("correct-guess"), false);
					button.pseudoClassStateChanged(PseudoClass.getPseudoClass("incorrect-guess"), false);
				}
			});
			AtomicBoolean blinkState = new AtomicBoolean();
			AtomicReference<ScheduledFuture<?>> blinkFuture = new AtomicReference<>();
			optionModel.blinkProperty().addListener((a, o, n) -> {
				if( n.booleanValue() ) {
					Runnable blinkTask = () -> Platform.runLater(() -> {
						if( optionModel.blinkProperty().get() && button.isVisible() ) {
							button.pseudoClassStateChanged(PseudoClass.getPseudoClass("blink-1"), blinkState.get());
							button.pseudoClassStateChanged(PseudoClass.getPseudoClass("blink-2"), !blinkState.get());
							blinkState.set(!blinkState.get());
						} else {
							blinkFuture.get().cancel(false);
						}
					});
					blinkFuture.set(EXECUTOR.scheduleAtFixedRate(blinkTask, 0, 500, TimeUnit.MILLISECONDS));
				} else {
					blinkFuture.get().cancel(false);
				}
			});
			
			button.getStyleClass().add("option-button");
			
			button.prefWidthProperty().bind(gameField.widthProperty().multiply(0.4));
			button.setOnAction((e) -> game.answer(option));
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
