package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.module.modules.world.NoteBot;
import me.wallhacks.spark.util.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class NoteBotCommand extends Command {
    public static NoteBotCommand INSTANCE;
    ArrayList<String> stupid = new ArrayList();
    ConcurrentHashMap<String, String> smart = new ConcurrentHashMap();
    public NoteBotCommand() {
        super();
        INSTANCE = this;
        refresh();
        addOption("download", arg -> {
            NoteBot.INSTANCE.downloadSongs();
        });
        addOption("select", arg -> {
            try {
                NoteBot.INSTANCE.setSong(smart.get(arg));
            } catch (NullPointerException e) {
                Spark.sendInfo("Could not find song");
            }
        }, stupid);
        addOption("refresh", arg -> {
            refresh();
        });
    }

    public void refresh() {
        stupid.clear();
        smart.clear();
        for (String s : FileUtil.listFilesForFolder(NoteBot.INSTANCE.getNoteBotDir().getAbsolutePath(), ".notebot")) {
            String render = s.replaceAll(" ", "_").substring(0, s.length() - 8);
            stupid.add(render);
            smart.put(render, s);
        }
    }

    @Override
    public String getName() {
        return "song";
    }
}
