package com.old.silence.core.context.distributed;

/**
 * @author moryzang
 */
public class SampleEvent implements DistributedEvent {
    private static final long serialVersionUID = -3771199387539770550L;
    private String eventCode;

    public SampleEvent(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public boolean validate() {
        return false;
    }
}
