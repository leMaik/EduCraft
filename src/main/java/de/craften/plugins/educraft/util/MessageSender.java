package de.craften.plugins.educraft.util;

/**
 * A sender for messages.
 */
public interface MessageSender {
    /**
     * Sends the given message.
     *
     * @param message message to send
     */
    void sendMessage(String message);
}
