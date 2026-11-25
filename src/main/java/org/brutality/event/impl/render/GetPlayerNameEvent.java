package org.brutality.event.impl.render;

import net.minecraft.scoreboard.Score;
import org.brutality.event.Event;

public class GetPlayerNameEvent extends Event {
    private Score score;
    private String playerName;
    private int scorePoints2;
    private boolean flag = true;

    public boolean isFlag() {
        return this.flag;
    }

    public GetPlayerNameEvent() {
        this.score = null;
        this.playerName = null;
        this.scorePoints2 = 0;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void handleScore(Score score) {
        this.score = score;
        if (score != null) {
            this.playerName = score.getPlayerName();
            this.scorePoints2 = score.getScorePoints();
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public GetPlayerNameEvent(Score score) {
        this.score = score;
        if (score != null) {
            this.playerName = score.getPlayerName();
            this.scorePoints2 = score.getScorePoints();
        }
    }

    public Score getScore() {
        return this.score;
    }

    public int getScorePoints() {
        return this.scorePoints2;
    }
}
