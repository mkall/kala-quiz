package net.ahven.ahvenpeli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import net.ahven.ahvenpeli.config.Option;
import net.ahven.ahvenpeli.config.Question;
import net.ahven.ahvenpeli.config.Quiz;

public class Game {
	private static final ScheduledExecutorService EXECUTOR = Executors
			.newSingleThreadScheduledExecutor((r) -> new Thread(r, "GameExecutor"));
	private final Quiz quiz;
	private final String locale;
	private final String playerName;
	private final List<Question> questions;

	private final ReadOnlyLongWrapper timer = new ReadOnlyLongWrapper();
	private final ReadOnlyIntegerWrapper score = new ReadOnlyIntegerWrapper();
	private final ReadOnlyBooleanWrapper revealAnswer = new ReadOnlyBooleanWrapper();
	private final ReadOnlyObjectWrapper<Question> currQuestion = new ReadOnlyObjectWrapper<>();

	private BiConsumer<String, Integer> gameFinishedCallback = null;

	private int currQuestionIndex = 0;
	private ScheduledFuture<?> timerFuture;

	public Game(Quiz quiz, String locale, String playerName) {
		this.quiz = quiz;
		this.locale = locale;
		this.playerName = playerName;

		questions = initRandomQuestions();
		currQuestion.set(questions.get(0));
		score.set(0);
		resetAndStartTimer();
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public BiConsumer<String, Integer> getGameFinishedCallback() {
		return gameFinishedCallback;
	}

	/**
	 * @param gameFinishedCallback First argument is the player name, the second is
	 *                             the score.
	 */
	public void setGameFinishedCallback(BiConsumer<String, Integer> gameFinishedCallback) {
		this.gameFinishedCallback = gameFinishedCallback;
	}

	public ReadOnlyObjectProperty<Question> currentQuestionProperty() {
		return currQuestion.getReadOnlyProperty();
	}

	public ReadOnlyIntegerProperty scoreProperty() {
		return score.getReadOnlyProperty();
	}

	public ReadOnlyLongProperty timerProperty() {
		return timer.getReadOnlyProperty();
	}

	public ReadOnlyBooleanProperty revealAnswerProperty() {
		return revealAnswer.getReadOnlyProperty();
	}

	public String getLocale() {
		return locale;
	}

	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Call from JFX thread.
	 */
	public void answer(Option option) {
		if( option.isCorrect() ) {
			int newScore = score.get() + quiz.getScorePerCorrectAnswer();
			if( timer.get() > 0 ) {
				// Small bonus for being fast
				newScore += (int) Math.round((double) (timer.get() / (quiz.getTimePerQuestion() * 1000))
						* (double) quiz.getScoreMaximumTimeBonus());
			}
			score.set(newScore);
		}
		revealAnswerAndJumpToNextQuestion();
	}

	private void resetAndStartTimer() {
		Util.runInJfxThread(() -> timer.set(quiz.getTimePerQuestion() <= 0 ? -1 : (quiz.getTimePerQuestion() * 1000)));
		startTimer();
	}

	private void revealAnswerAndJumpToNextQuestion() {
		if( timerFuture != null ) {
			timerFuture.cancel(false);
			timerFuture = null;
		}
		revealAnswer.set(true);
		EXECUTOR.schedule(() -> {
			Util.runInJfxThread(() -> {
				revealAnswer.set(false);
				nextQuestion();
			});
		}, quiz.getRevealAnswerTime(), TimeUnit.SECONDS);
	}

	private void nextQuestion() {
		currQuestionIndex++;
		currQuestion.set(questions.size() > currQuestionIndex ? questions.get(currQuestionIndex) : null);
		if (currQuestion.get() != null) {
			resetAndStartTimer();
		}
	}

	public void startTimer() {
		AtomicLong refTime = new AtomicLong(System.nanoTime());
		Runnable timerTask = () -> {
			Util.runInJfxThread(() -> {
				long now = System.nanoTime();
				if (timer.get() > 0) {
					timer.set(Math.max(0, timer.get() - ((now - refTime.get()) / 1000000L)));
					if (timer.get() == 0) {
						revealAnswerAndJumpToNextQuestion();
					}
				}
				refTime.set(now);
			});
		};
		timerFuture = EXECUTOR.scheduleAtFixedRate(timerTask, 100, 100, TimeUnit.MILLISECONDS);
	}

	private List<Question> initRandomQuestions() {
		Random random = new Random(System.currentTimeMillis());
		
		List<IndexWeight> indices = new ArrayList<>(quiz.getQuestions().size());
		for (int i = 0; i < quiz.getQuestions().size(); i++) {
			indices.add(new IndexWeight(i, random.nextInt()));
		}
		Collections.sort(indices, (a, b) -> a.weight - b.weight);

		int count = Math.min(quiz.getQuestionsPerGame(), quiz.getQuestions().size());
		List<Question> retVal = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			retVal.add(quiz.getQuestions().get(indices.get(i).index));
		}
		return retVal;
	}

	private class IndexWeight {
		private int index;
		private int weight;

		public IndexWeight(int index, int weight) {
			super();
			this.index = index;
			this.weight = weight;
		}
	}

}
