package com.songoda.skyblock.challenge.player;

import com.songoda.skyblock.challenge.challenge.Challenge;

import java.util.HashMap;
import java.util.UUID;

public class PlayerChallenge {
	private UUID uuid;
	private HashMap<Challenge, Integer> challenges;

	public PlayerChallenge(UUID uuid, HashMap<Challenge, Integer> challenges) {
		this.uuid = uuid;
		this.challenges = challenges;
	}

	public UUID getUuid() {
		return uuid;
	}

	public HashMap<Challenge, Integer> getChallenges() {
		return challenges;
	}
}
