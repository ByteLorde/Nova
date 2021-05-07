package nova;

import org.dreambot.api.script.AbstractScript;


public abstract class NovaScript extends AbstractScript {

    private boolean enabled;
    public String scriptName;

    public NovaScript(String scriptName) {
        this.scriptName = scriptName;
        this.disable();
    }


    @Override
    public int onLoop() {

        if (this.isComplete()) {
            this.disable();
        }

        if (!this.isEnabled()) {
            return NovaConstants.DEFAULT_DELAY;
        }

        return this.tick();
    }

    public void enable() {
        log("Enabling: " + this.scriptName);
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public abstract int tick();

    public abstract boolean isComplete();
}
