package edu.esu.kandy.slms.observer;

import java.util.ArrayList;
import java.util.List;

public class NotificationService implements NotificationSubject {

    private final List<NotificationObserver> observers = new ArrayList<>();

    @Override
    public void registerObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(NotificationEvent event) {
        for (NotificationObserver observer : observers) {
            if (event.getUser() == null || event.getUser().equals(observer)) {
                observer.update(event);
            }
        }
    }
}
