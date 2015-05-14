package com.mathgames.sciencegadgets.client;

import com.mathgames.sciencegadgets.client.ui.SymbolicEquation.Operation;

public enum Game {
	Addition_1(GameType.CLICK, Operation.PLUS),
	Addition_2(GameType.MULTIPLE_CHOICE, Operation.PLUS),
	Addition_3(GameType.INPUT, Operation.PLUS),;

	GameType gameType;
	Operation operation;

	Game(GameType gameType, Operation operation) {
		this.gameType = gameType;
		this.operation = operation;
	}

	public enum GameType {
		CLICK, MULTIPLE_CHOICE, INPUT;
	}

	public Operation getOperation() {
		return operation;
	}
}