package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.service.dto.ActionResult;

public class GarageService {
    /**
     * собирает машину из компонентов.
     * возвращает результат операции с сообщением об ошибке (если есть).
     * не выполняет вывод в консоль.
     */
    public ActionResult assembleCar(Team team, Engine e, Chassis c, Transmission t, Suspension s, Aerodynamics a, Tires tires) {
        if (team.getEngineers().isEmpty()) {
            return ActionResult.failure("Ошибка: нет инженеров для сборки болида! Наймите персонал");
        }

        if (e.getWeight() > c.getMaxEngineWeight()) {
            return ActionResult.failure("Ошибка: двигатель тяжелый для этого шасси");
        }

        if (!t.getSupportedEngineType().equals(e.getEngineType())) {
            return ActionResult.failure("Ошибка: трансмиссия не подходит к двигателю");
        }

        if (!s.getSupportedChassisName().equals(c.getName())) {
            return ActionResult.failure("Ошибка: подвеска не совместима с шасси");
        }

        Car car = new Car();
        car.setEngine(e);
        car.setChassis(c);
        car.setTransmission(t);
        car.setSuspension(s);
        car.setAerodynamics(a);
        car.setTires(tires);

        team.addCar(car);
        team.removeComponent(e);
        team.removeComponent(c);
        team.removeComponent(t);
        team.removeComponent(s);
        team.removeComponent(a);
        team.removeComponent(tires);

        return ActionResult.success("Болид собран и добавлен в гараж");
    }

    /**
     * собирает машину для ботов (без вывода ошибок)
     */
    public boolean assembleCarSilent(Team team, Engine e, Chassis c, Transmission t, Suspension s, Aerodynamics a, Tires tires) {
        ActionResult result = assembleCar(team, e, c, t, s, a, tires);
        return result.isSuccess();
    }
}
