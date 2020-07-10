package com.assignment.distributedfilesharingapp.common.strategy;

import com.assignment.distributedfilesharingapp.common.MessageBrokerThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageHandelingFactory {

    private final HeartBeatHandlingStrategy heartBeatHandlingStrategy;

    private final FileSearchMessageHandlingStrategy fileSearchMessageHandlingStrategy;

    private final QueryMessageHandlingStrategy queryMessageHandlingStrategy;

    public MessageHandelingFactory(
            HeartBeatHandlingStrategy heartBeatHandlingStrategy,
            FileSearchMessageHandlingStrategy fileSearchMessageHandlingStrategy,
            QueryMessageHandlingStrategy queryMessageHandlingStrategy) {
        this.heartBeatHandlingStrategy=heartBeatHandlingStrategy;
        this.fileSearchMessageHandlingStrategy = fileSearchMessageHandlingStrategy;
        this.queryMessageHandlingStrategy = queryMessageHandlingStrategy;
    }

    public MessageHandlingStrategy getMessageHandlingStrategy(MessageType messageType, MessageBrokerThread messageBroker) {
        switch (messageType) {
            case PING:
            case JOIN:
                this.heartBeatHandlingStrategy.init(messageBroker.getRoutingTable(), messageBroker.getChannelOut(), messageBroker.getTimeoutManager());
                return this.heartBeatHandlingStrategy;
            case LEAVE:
                this.heartBeatHandlingStrategy.init(messageBroker.getRoutingTable(), messageBroker.getChannelOut(), messageBroker.getTimeoutManager());
                return this.heartBeatHandlingStrategy;
            case PONG:
            case JOINOK:
                this.heartBeatHandlingStrategy.init(messageBroker.getRoutingTable(), messageBroker.getChannelOut(), messageBroker.getTimeoutManager());
                return this.heartBeatHandlingStrategy;
            case SER:
                fileSearchMessageHandlingStrategy.init(messageBroker.getRoutingTable(), messageBroker.getChannelOut(), messageBroker.getTimeoutManager());
                return fileSearchMessageHandlingStrategy;
            case SEROK:
                queryMessageHandlingStrategy.init(messageBroker.getRoutingTable(), messageBroker.getChannelOut(), messageBroker.getTimeoutManager());
                return queryMessageHandlingStrategy;
            default:
                log.info("Unknown keyword received in Response Handler : {}", messageType);
                return null;
        }
    }
}
