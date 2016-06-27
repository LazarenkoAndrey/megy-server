package ru.megy.service;


import ru.megy.repository.entity.Message;

import java.util.List;

public interface MessageService {
    List<Message> getMessageList(int top);
    Long addMessage(String text);
    void doSending();
}
