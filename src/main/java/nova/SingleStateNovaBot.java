package nova;

import java.util.Iterator;
import java.util.LinkedList;

public class SingleStateNovaBot extends NovaBot {

    protected NovaScript activeScript;
    protected Iterator<NovaScript> scriptIterator;

    protected SingleStateNovaBot(String jarName) {
        super(jarName);
    }

    @Override
    public int tick() {
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
        int delay = this.activeScript.tick();
        if (delay == NovaConstants.DEFAULT_DELAY) {
            int min =  delay - (delay / NumberUtil.getRandomInRange(2, 5));
            int max =  delay + (delay / NumberUtil.getRandomInRange(2, 5));
            delay = NumberUtil.getRandomInRange(min, max);
        }
        return delay;
    }

    @Override
    public boolean isComplete() {
        return this.isEnabled();
    }

    @Override
    public void loadScripts(String[] sortOrder) {
        super.loadScripts(sortOrder);
        this.refreshIterator();
    }

    protected void refreshIterator() {
        this.scriptIterator = this.getScriptsAsLinkedList().iterator();
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

    protected void resolveActiveScript() {
        Iterator<NovaScript> listIterator = this.getScriptsAsLinkedList().descendingIterator();

        NovaScript activeScript = this.scripts[0];
        while (listIterator.hasNext()) {
            NovaScript script = listIterator.next();
            if (script.isComplete()) {
                activeScript = script;
                break;
            }
        }
        this.activateScript(activeScript);
    }

}
