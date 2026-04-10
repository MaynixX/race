package arkham.racing.ui;

import arkham.racing.model.*;
import arkham.racing.model.environment.*;
import java.util.*;

/**
 * консольный пользовательский интерфейс.
 * отвечает за весь вывод информации в консоль.
 */
public class ConsoleUI {

    public void printWelcome() {
        System.out.println("Добро пожаловать в Аркхем! Корпорация ZovAuto открывает Кубок.");
        System.out.println("По правилам лиги вы начинаете с пустым гаражом, но солидным бюджетом.");
    }

    public void printMainMenu(double playerBudget) {
        System.out.println("\n=Главное меню | Бюджет: " + playerBudget + "=");
        System.out.println("0) Быстрая инициализация для проверки лабы");
        System.out.println("1) Начать гонку");
        System.out.println("2) Купить комплектующие / нанять персонал");
        System.out.println("3) Собрать болид");
        System.out.println("4) Просмотреть болиды и гараж");
        System.out.println("5) Просмотреть пилотов и инженеров");
        System.out.println("6) Просмотреть статистику гонок");
        System.out.println("7) Просмотреть другие команды");
        System.out.println("8) Сохранить игру");
        System.out.println("9) Выход");
        System.out.print("Выберите действие: ");
    }

    public void printErrorInvalidMenuChoice() {
        System.out.println("Неверный пункт меню.");
    }

    public void printDebugInitStarted() {
        System.out.println("Инициализация тестовых данных для игрока...");
    }

    public void printDebugInitCompleted() {
        System.out.println("Команда и болид готовы");
    }

    public void printAvailableTracks(List<Track> tracks) {
        System.out.println("Доступные трассы:");
        for (int i = 0; i < tracks.size(); i++) {
            System.out.println((i + 1) + ") " + tracks.get(i).toString());
        }
        System.out.print("Выберите трассу: ");
    }

    public void printGarageMenuHeader() {
        System.out.println("\n========== СБОРКА БОЛИДА ==========");
    }

    public void printNoComponentsInGarage() {
        System.out.println("На складе нет компонентов для сборки!");
    }

    public void printNoPilotError() {
        System.out.println("У вас нет пилота! Наймите пилота перед сборкой болида.");
    }

    public void printNoEngineerError() {
        System.out.println("У вас нет инженера! Наймите инженера перед сборкой болида.");
    }

    public void printSelectComponentPrompt(String componentType) {
        System.out.println("\n--- Доступные " + componentType + " на складе ---");
        System.out.println("0) Отменить сборку");
    }

    public void printNoComponentsOfType(String componentType) {
        System.out.println("Нет доступных " + componentType + " на складе!");
    }

    public void printChooseComponentPrompt(String componentType) {
        System.out.print("Выберите " + componentType + " для установки: ");
    }

    public void printRaceStart(String trackName, String weather, int laps) {
        System.out.println("\n--- НАЧАЛО ЗАЕЗДА ---");
        System.out.println("Трасса: " + trackName + " | Погода: " + weather + " | Кругов: " + laps);
    }

    public void printRaceResults(List<Map.Entry<Team, Double>> sortedResults) {
        System.out.println("\n--- ИТОГИ ГОНКИ ---");

        for (int i = 0; i < sortedResults.size(); i++) {
            Team t = sortedResults.get(i).getKey();
            Double time = sortedResults.get(i).getValue();

            if (time == 9999.0) {
                System.out.println("Команда: " + t.getName() + " - СХОД С ДИСТАНЦИИ");
            } else {
                System.out.printf("%d место: %s (Время: %.2f сек)\n", i + 1, t.getName(), time);
            }
        }
    }

    public void printComponentsMenuHeader() {
        System.out.println("\n--- Доступные компоненты ---");
        System.out.println("0) Выход");
    }

    public void printComponentsCategory(String category) {
        System.out.println("\n--- " + category + " ---");
    }

    public void printComponentChoice() {
        System.out.print("\nВыберите компонент для покупки (0 для выхода): ");
    }

    public void printInvalidChoiceError() {
        System.out.println("Неверный выбор!");
    }

    public void printPilotsMenuHeader() {
        System.out.println("\n--- Доступные пилоты ---");
        System.out.println("0) Выход");
    }

    public void printPilotChoice() {
        System.out.print("\nВыберите пилота для найма: ");
    }

    public void printEngineersMenuHeader() {
        System.out.println("\n--- Доступные инженеры ---");
        System.out.println("0) Выход");
    }

    public void printEngineerChoice() {
        System.out.print("\nВыберите инженера для найма: ");
    }

    public void printNursesMenuHeader() {
        System.out.println("\n--- Доступные медсестры ---");
        System.out.println("0) Выход");
    }

    public void printNurseChoice() {
        System.out.print("\nВыберите медсестру для найма: ");
    }

    public void printChefsMenuHeader() {
        System.out.println("\n--- Доступные повара ---");
        System.out.println("0) Выход");
    }

    public void printChefChoice() {
        System.out.print("\nВыберите повара для найма: ");
    }

    public void printExitingGame() {
        System.out.println("\n До встречи на трассе! Спасибо за игру!");
    }

    public void printMarketInvalidChoice() {
        System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
    }
}
