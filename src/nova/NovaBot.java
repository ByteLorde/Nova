package nova;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class NovaBot extends NovaScript {

    protected NovaScript activeScript;

    protected NovaScript[] scripts;
    protected Iterator<NovaScript> scriptIterator;

    private boolean scriptsLoaded;

    protected NovaBot(String jarName) {
        super(jarName);
        this.scriptsLoaded = false;
    }

    protected void loadScripts(String[] sortOrder) {
        try {
            NovaScript[] scripts = NovaScriptFactory.loadScriptsFromDreambot(this.scriptName);
            this.scripts = this.orderScripts(scripts, sortOrder);
            this.refreshIterator();
            this.scriptsLoaded = true;
        } catch (Exception e) {
            logError("An error has occurred loading script: " + this.scriptName + ". \n\n\t" + e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(trace -> logError(trace.toString()));
        }
    }

    private NovaScript[] orderScripts(NovaScript[] scripts, String[] sortOrder) {
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

    private void resolveActiveScript() {
        Iterator<NovaScript> listIterator = this.getScriptsAsLinkedList().descendingIterator();

        NovaScript activeScript = this.scripts[0];
        while (listIterator.hasNext()) {
            NovaScript script = listIterator.next();
            log(script.scriptName + " is complete: " + script.isComplete());
            if (script.isComplete()) {
                activeScript = script;
                break;
            }
        }
        this.activateScript(activeScript);
    }

    @Override
    public int runScript() {

        if (!this.scriptsLoaded) {
            logError("Scripts Haven't been loaded for Bot: " + this.scriptName + ". Please call nova.NovaBot.loadScript() in the code.");
            return NovaConstants.DEFAULT_DELAY;
        }

        log("Bot running: " + this.scriptName);

        if (this.scriptIterator == null) {
            this.refreshIterator();
        }

        if (this.activeScript == null) {
            this.resolveActiveScript();
        }

        if (this.activeScript.isComplete()) {
            if (!this.scriptIterator.hasNext()) {
                this.refreshIterator();
            }
            this.activateScript(this.scriptIterator.next());
        }

        log("Running Script: " + this.activeScript.scriptName);

        // Randomize delay if nothing else mutated it.
        int delay = this.activeScript.runScript();
        if (delay == NovaConstants.DEFAULT_DELAY) {
            int min =  delay - (delay / NumberUtil.getRandomInRange(2, 5));
            int max =  delay + (delay / NumberUtil.getRandomInRange(2, 5));
            delay = NumberUtil.getRandomInRange(min, max);
        }
        return delay;
    }

    protected void activateScript(NovaScript script) {
        if (this.activeScript != null) {
            this.activeScript.disable();
        }
        this.activeScript = script;

        LinkedList<NovaScript> list = this.getScriptsAsLinkedList();
        int newIteratorStartIndex = list.indexOf(this.activeScript) + 1;
        this.scriptIterator = list.listIterator(newIteratorStartIndex);
        this.activeScript.enable();
    }

    private void refreshIterator() {
        this.scriptIterator = this.getScriptsAsLinkedList().iterator();
    }
}
