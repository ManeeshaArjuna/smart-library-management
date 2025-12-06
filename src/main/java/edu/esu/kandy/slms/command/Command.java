package edu.esu.kandy.slms.command;

public interface Command {
    void execute();
    void undo();
    String getName();
}
