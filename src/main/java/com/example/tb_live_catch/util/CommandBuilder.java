package com.example.tb_live_catch.util;

public class CommandBuilder {

    private StringBuilder builder;

    public CommandBuilder build() {
        builder = new StringBuilder();
        return this;
    }

    public CommandBuilder add(String command) {
        builder.append(command);
        builder.append(" ");
        return this;
    }
    
    public CommandBuilder add(String... commands) {
        for (int i = 0; i < commands.length; i++) {
            builder.append(commands[i]);
            builder.append(" ");
        }
        return this;
    }



}
