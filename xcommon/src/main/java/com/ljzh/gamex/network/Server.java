package com.ljzh.gamex.network;

import java.io.IOException;

public abstract class Server implements
        ChannelAcceptor.AcceptHandler,
        ChannelSelector.ReadHandler,
        ChannelSelector.WriteHandler,
        ChannelSelector.CloseHandler {
    private ChannelAcceptor channelAcceptor;
    private ChannelSelector channelSelector;

    public Server() {
        channelAcceptor = new ChannelAcceptor();
        channelSelector = new ChannelSelector();
    }

    public void start(String hostname, int port) throws IOException {
        channelAcceptor.initialize(hostname, port);
        channelAcceptor.setAcceptHandler(this);
        channelAcceptor.start();
        channelSelector.initialize();
        channelSelector.setReadHandler(this);
        channelSelector.setWriteHandler(this);
        channelSelector.setCloseHandler(this);
        channelSelector.start();
    }

    public void shutdown() {
        channelAcceptor.shutdown();
        channelSelector.shutdown();
    }
}
