package moe.ofs.backend.chatcmd.services;

/**
 * If message is sent to user and is waiting for user input, check consequent messages
 */
public interface ChatMessageContextListenerService {

    void startListener();

    void stopListener();
}
