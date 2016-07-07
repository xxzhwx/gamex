package com.ljzh.gamex.network;

import com.ljzh.gamex.CommonLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChannelSelector extends Thread {
    public interface ReadHandler {
        void onRead(SocketChannel channel, Object attachment, ByteBuffer buffer);
    }

    public interface WriteHandler {
        void onWritable(SocketChannel channel, Object attachment, SelectionKey selectionKey);
    }

    public interface CloseHandler {
        void onClose(SocketChannel channel, Object attachment, String reason);
    }

    public void initialize() throws IOException {
        setName("ChannelSelector");
        selector = Selector.open();
    }

    public void setReadHandler(ReadHandler readHandler) {
        this.readHandler = readHandler;
    }

    public void setWriteHandler(WriteHandler writeHandler) {
        this.writeHandler = writeHandler;
    }

    public void setCloseHandler(CloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }

    public void registerChannel(SocketChannel channel) {
        synchronized (newChannels) {
            newChannels.add(channel);
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                __registerChannels();

                int numOfKeys = selector.select(10L);
                if (numOfKeys == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isValid()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        Object attachment = key.attachment();
                        if (key.isReadable()) {
                            __handleRead(channel, attachment);
                        } else if (key.isWritable()) {
                            __handleWrite(key, channel, attachment);
                        } else {
                            CommonLogger.error("illegal key state");
                        }
                    }
                }
            } catch (IOException e) {
                CommonLogger.error(e, "run");
            }
        }
    }

    private void __registerChannels() {
        if (newChannels.isEmpty()) {
            return;
        }

        List<SocketChannel> channels = new ArrayList<>();
        synchronized (newChannels) {
            channels.addAll(newChannels);
            newChannels.clear();
        }

        for (SocketChannel channel : channels) {
            try {
                channel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                CommonLogger.error(e, "register");
            }
        }
    }

    private void __handleRead(SocketChannel channel, Object attachment) {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        try {
            int readBytes;
            while ((readBytes = channel.read(buffer)) > 0) {
                buffer.flip();
                readHandler.onRead(channel, attachment, buffer);
                buffer = ByteBuffer.allocate(256);
            }

            if (readBytes == -1) {
                __closeChannel(channel, attachment, "disconnect by peer.");
            }
        } catch (IOException e) {
            CommonLogger.error(e, "read");
            __closeChannel(channel, attachment, "read error.");
        }
    }

    private void __handleWrite(SelectionKey key, SocketChannel channel, Object attachment) {
        key.interestOps(SelectionKey.OP_READ);
        writeHandler.onWritable(channel, attachment, key);
    }

    private void __closeChannel(SocketChannel channel, Object attachment, String reason) {
        closeHandler.onClose(channel, attachment, reason);

        try {
            channel.close();
        } catch (IOException e) {
            CommonLogger.error(e, "close channel");
        }
    }

    public void shutdown() {
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
    }

    private volatile boolean running = true;
    private Selector selector;
    private ReadHandler readHandler;
    private WriteHandler writeHandler;
    private CloseHandler closeHandler;
    private final List<SocketChannel> newChannels = new ArrayList<>();
}
