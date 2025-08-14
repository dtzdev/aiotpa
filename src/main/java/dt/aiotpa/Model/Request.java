package dt.aiotpa.Model;

import java.util.UUID;

public class Request {
    private final UUID from;
    private final UUID to;
    private final boolean here;

    public Request(UUID from, UUID to, boolean here) {
        this.from = from;
        this.to = to;
        this.here = here;
    }

    public UUID getFrom() { return from; }
    public UUID getTo() { return to; }
    public boolean isHere() { return here; }
}