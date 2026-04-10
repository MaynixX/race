package arkham.racing;

import arkham.racing.model.Team;
import arkham.racing.service.GameService;
import arkham.racing.service.SaveLoadService;
import arkham.racing.ui.ConsoleUI;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        Scanner scanner = new Scanner(System.in);
        SaveLoadService saveLoadService = new SaveLoadService();

        ui.printWelcome();

        System.out.print("Введите ваше имя: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }

        // предложить загрузку сохранения
        System.out.println("Доступные сохранения:");
        var saves = saveLoadService.getSaveFiles(playerName);
        if (saves.isEmpty()) {
            System.out.println("Нет сохранений.");
        } else {
            for (int i = 0; i < saves.size(); i++) {
                System.out.println((i + 1) + ") " + saves.get(i));
            }
            System.out.print("Выберите сохранение для загрузки (0 - начать новую игру): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            if (choice > 0 && choice <= saves.size()) {
                try {
                    var state = saveLoadService.loadGame(playerName, saves.get(choice - 1));
                    GameService game = new GameService(state.playerTeam, state.otherTeams, state.availableTracks, ui);
                    game.start();
                    return;
                } catch (Exception e) {
                    System.out.println("Ошибка загрузки: " + e.getMessage());
                }
            }
        }

        Team player = new Team(playerName + " Racing", 10000000.0);

        GameService game = new GameService(player, ui);
        game.start();
    }
}
