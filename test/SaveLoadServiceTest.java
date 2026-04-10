package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.SaveLoadService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class SaveLoadServiceTest {
    @Test
    public void testSaveAndLoadGame() throws Exception {
        SaveLoadService service = new SaveLoadService();
        Team player = new Team("Test Racing", 100000);
        List<Team> others = new ArrayList<>();
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1));

        service.saveGame("testuser", player, others, tracks, "testsave");

        SaveLoadService.GameState state = service.loadGame("testuser", "testsave");
        assertEquals("Test Racing", state.playerTeam.getName());
        assertEquals(1, state.availableTracks.size());
    }
}