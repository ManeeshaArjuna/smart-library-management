package edu.esu.kandy.slms.command;

import java.util.Stack;

public class CommandHistory {

    private final Stack<Command> history = new Stack<>();

    public void push(Command command) {
        history.push(command);
    }

    public void undoLast() {
        if (!history.isEmpty()) {
            Command cmd = history.pop();
            cmd.undo();
            System.out.println("\u21A9\uFE0F Undone: " + cmd.getName());
        } else {
            System.out.println("\u26A0\uFE0F No commands to undo.");
        }
    }
}
