package com.enndfp;

import com.enndfp.cli.CommandExecutor;

/**
 * @author Enndfp
 */
public class Main {
    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}