package com.company.dragons_of_mugloar.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AdTaskProbability {
    PIECE_OF_CAKE("Piece of cake", 12),
    SURE_THING("Sure thing", 11),
    WALK_IN_THE_PARK("Walk in the park", 10),
    QUITE_LIKELY("Quite likely", 9),
    HMMM("Hmmm....", 8),
    GAMBLE("Gamble", 7),
    RATHER_DETRIMENTAL("Rather detrimental", 6),
    RISKY("Risky", 5),
    PLAYING_WITH_FIRE("Playing with fire", 4),
    SUICIDE_MISSION("Suicide mission", 3),
    IMPOSSIBLE("Impossible", 2),
    UNKNOWN("Unknown", 1);

    private final String label;
    private final int weight;

    public static int getWeight(String label) {
        return Arrays.stream(values())
                .filter(p -> p.label.equalsIgnoreCase(label))
                .map(AdTaskProbability::getWeight)
                .findFirst()
                .orElse(UNKNOWN.weight);
    }
}
