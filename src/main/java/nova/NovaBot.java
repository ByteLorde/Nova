package nova;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class NovaBot extends NovaScript {

    protected NovaScript[] scripts;
    protected boolean scriptsLoaded;

    protected NovaBot(String jarName) {
        super(jarName);
        this.scriptsLoaded = false;

    }

    protected void loadScripts(String[] sortOrder) {
        try {
            NovaScript[] scripts = NovaScriptFactory.loadScriptsFromDreambot(this.scriptName);
            this.scripts = this.orderScripts(scripts, sortOrder);
            this.scriptsLoaded = true;
        } catch (Exception e) {
            logError("An error has occurred loading script: " + this.scriptName + ". \n\n\t" + e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(trace -> logError(trace.toString()));
        }
    }

    protected NovaScript[] orderScripts(NovaScript[] scripts, String[] sortOrder) {
        NovaScript[] sortedList = new NovaScript[sortOrder.length];

        if (sortOrder.length == 0) {
            logError("No Script Load Order was found for Bot: " + this.scriptName);
            return sortedList;
        }

        for (NovaScript script : scripts) {
            String nameLong = script.scriptName;
            String name = nameLong.split("\\.")[1];
            int index = new ArrayList<>(Arrays.asList(sortOrder)).indexOf(name);
            if (index == -1) {
                logError("Loaded Script: [" + name + "] but a load order for it wasn't found in " + sortOrder);
                continue;
            }
            sortedList[index] = script;
        }

        return sortedList;
    }

    public LinkedList<NovaScript> getScriptsAsLinkedList() {
        return new LinkedList<>(Arrays.asList(this.scripts));
    }


}
