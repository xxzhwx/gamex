package com.ljzh.gamex.network;

import com.ljzh.gamex.CommonLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChannelAcceptor extends Thread {
    public interface AcceptHandler {
        void onAccepted(SocketChannel sc);
    }

    public void initialize(String hostname, int port) throws IOException {
        setName("ChannelAcceptor");
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.bind(new InetSocketAddress(hostname, port));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void setAcceptHandler(AcceptHandler acceptHandler) {
        this.acceptHandler = acceptHandler;
    }

    @Override
    public void run() {
        while (running) {
            try {
                int numOfKeys = selector.select();
                if (numOfKeys == 0) {
                    CommonLogger.error("numOfKeys is 0, shutting down?");
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
                    sc.setOption(StandardSocketOptions.SO_SNDBUF, 65536);
                    sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                    sc.setOption(StandardSocketOptions.TCP_NODELAY, true);
                    acceptHandler.onAccepted(sc);
                }
            } catch (IOException e) {
                CommonLogger.error(e, "run");
            }
        }
    }

    public void shutdown() {
        if (!running) {
            return;
        }

        running = false;

        try {
            selector.wakeup();
            selector.close();
        } catch (IOException e) {
            CommonLogger.error(e, "close selector");
        }

        try {
            this.join();
        } catch (InterruptedException e) {
            CommonLogger.error(e, "join");
        }

        try {
            serverSocketChannel.close();
            serverSocketChannel = null;
        } catch (IOException e) {
            CommonLogger.error(e, "close serverSocketChannel");
        }
    }

    private volatile boolean running = true;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private AcceptHandler acceptHandler;
}
