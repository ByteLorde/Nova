package nova;

import java.util.Arrays;

public class MultiStateNovaBot extends NovaBot {

    protected MultiStateNovaBot(String jarName) {
        super(jarName);
    }

    public NovaScript[] getActiveScripts() {
        return Arrays.stream(this.scripts)
                .filter(NovaScript::isEnabled)
                .toArray(NovaScript[]::new);
    }

    @Override
    public int tick() {
        for (NovaScript script : this.getActiveScripts()) {
            script.tick();
        }
        return NovaConstants.DEFAULT_DELAY;
    }

    @Override
    public boolean isComplete() {
        return false;
    }


}
