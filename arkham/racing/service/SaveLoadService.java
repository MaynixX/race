package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import java.io.*;
import java.util.*;

public class SaveLoadService {
    private static final String SAVE_DIR = "saves/";

    public SaveLoadService() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveGame(String playerName, Team playerTeam, List<Team> otherTeams, List<Track> availableTracks, String saveName) throws IOException {
        String fileName = SAVE_DIR + playerName + "_" + saveName + ".ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(playerTeam);
            oos.writeObject(otherTeams);
            oos.writeObject(availableTracks);
        }
    }

    public GameState loadGame(String playerName, String saveName) throws IOException, ClassNotFoundException {
        String fileName = SAVE_DIR + playerName + "_" + saveName + ".ser";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Team playerTeam = (Team) ois.readObject();
            List<Team> otherTeams = (List<Team>) ois.readObject();
            List<Track> availableTracks = (List<Track>) ois.readObject();
            return new GameState(playerTeam, otherTeams, availableTracks);
        }
    }

    public List<String> getSaveFiles(String playerName) {
        List<String> saves = new ArrayList<>();
        File dir = new File(SAVE_DIR);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.getName().startsWith(playerName + "_") && file.getName().endsWith(".ser")) {
                    saves.add(file.getName().substring((playerName + "_").length(), file.getName().length() - 4));
                }
            }
        }
        return saves;
    }

    public static class GameState {
        public Team playerTeam;
        public List<Team> otherTeams;
        public List<Track> availableTracks;

        public GameState(Team playerTeam, List<Team> otherTeams, List<Track> availableTracks) {
            this.playerTeam = playerTeam;
            this.otherTeams = otherTeams;
            this.availableTracks = availableTracks;
        }
    }
}