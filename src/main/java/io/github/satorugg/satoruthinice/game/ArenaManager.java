package io.github.satorugg.satoruthinice.game;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private List<Arena> arenaList = new ArrayList<>();

    public ArenaManager() {
        this.arenaList = new ArrayList<>();
    }

    public List<Arena> getArenaList() {
        return arenaList;
    }

    public Arena getArena(int ID) {
        for (Arena a : arenaList) {
            if (a.arenaID == ID) {
                return a;
            }
        }
        return null;
    }

    public void addArena(Arena a) {
        this.arenaList.add(a);
    }
}
