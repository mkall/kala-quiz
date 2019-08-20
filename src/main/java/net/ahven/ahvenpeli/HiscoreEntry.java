package net.ahven.ahvenpeli;

public class HiscoreEntry {
	private final String name;
	private final int score;

	public HiscoreEntry(String name, int score) {
		super();
		this.name = name;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

}
