package dt.aiotpa.Model;


public class PlayerSettings {
    private boolean tpaToggle = true;
    private boolean autoAccept = false;
    private boolean autoDeny = false;

    public boolean isTpaToggle() {
        return tpaToggle;
    }

    public void setTpaToggle(boolean tpaToggle) {
        this.tpaToggle = tpaToggle;
    }

    public boolean isAutoAccept() {
        return autoAccept;
    }

    public void setAutoAccept(boolean autoAccept) {
        this.autoAccept = autoAccept;
        if (autoAccept) this.autoDeny = false;
    }

    public boolean isAutoDeny() {
        return autoDeny;
    }

    public void setAutoDeny(boolean autoDeny) {
        this.autoDeny = autoDeny;
        if(autoDeny) this.autoAccept = false;
    }
}
