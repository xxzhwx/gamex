package com.ljzh.gamex.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Serializer {
    public static final int SIZE_OF_BYTE = 1;
    public static final int SIZE_OF_SHORT = 2;
    public static final int SIZE_OF_INT = 4;
    public static final int SIZE_OF_LONG = 8;
    public static final int SIZE_OF_FLOAT = 4;
    public static final int SIZE_OF_DOUBLE = 8;

    private ByteBuffer _buffer;

    public Serializer() {
        _buffer = createNewBuffer(2);
    }

    public Serializer(ByteBuffer buffer) {
        _buffer = buffer;
    }

    public Serializer(byte[] bytes) {
        _buffer = ByteBuffer.wrap(bytes);
    }

    public void write(byte value) {
        expand(SIZE_OF_BYTE);
        _buffer.put(value);
    }

    public void write(byte[] values) {
        write(values.length);
        for (byte value : values) {
            write(value);
        }
    }

    public byte readByte() {
        return _buffer.get();
    }

    public byte[] readBytes() throws SerializeException {
        int size = readInt();
        checkSize(size);

        byte[] values = new byte[size];
        for (int i = 0; i < size; i++) {
            values[i] = readByte();
        }
        return values;
    }

    public void write(boolean value) {
        write(value ? (byte)1 : (byte)0);
    }

    public void write(boolean[] values) {
        write(values.length);
        for (boolean value : values) {
            write(value);
        }
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public boolean[] readBooleans() throws SerializeException {
        int size = readInt();
        checkSize(size);

        boolean[] values = new boolean[size];
        for (int i = 0; i < size; i++) {
            values[i] = readBoolean();
        }
        return values;
    }

    public void write(short value) {
        expand(SIZE_OF_SHORT);
        _buffer.putShort(value);
    }

    public void write(short[] values) {
        write(values.length);
        for (short value : values) {
            write(value);
        }
    }

    public short readShort() {
        return _buffer.getShort();
    }

    public short[] readShorts() throws SerializeException {
        int size = readInt();
        checkSize(size);

        short[] values = new short[size];
        for (int i = 0; i < size; i++) {
            values[i] = readShort();
        }
        return values;
    }

    public void write(int value) {
        expand(SIZE_OF_INT);
        _buffer.putInt(value);
    }

    public void write(int[] values) {
        write(values.length);
        for (int value : values) {
            write(value);
        }
    }

    public int readInt() {
        return _buffer.getInt();
    }

    public int[] readInts() throws SerializeException {
        int size = readInt();
        checkSize(size);

        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = readInt();
        }
        return values;
    }

    public void write(long value) {
        expand(SIZE_OF_LONG);
        _buffer.putLong(value);
    }

    public void write(long[] values) {
        write(values.length);
        for (long value : values) {
            write(value);
        }
    }

    public long readLong() {
        return _buffer.getLong();
    }

    public long[] readLongs() throws SerializeException {
        int size = readInt();
        checkSize(size);

        long[] values = new long[size];
        for (int i = 0; i < size; ++i) {
            values[i] = readLong();
        }
        return values;
    }

    public void write(float value) {
        expand(SIZE_OF_FLOAT);
        _buffer.putFloat(value);
    }

    public void write(float[] values) {
        write(values.length);
        for (float value : values) {
            write(value);
        }
    }

    public float readFloat() {
        return _buffer.getFloat();
    }

    public float[] readFloats() throws SerializeException {
        int size = readInt();
        checkSize(size);

        float[] values = new float[size];
        for (int i = 0; i < size; ++i) {
            values[i] = readFloat();
        }
        return values;
    }

    public void write(double value) {
        expand(SIZE_OF_DOUBLE);
        _buffer.putDouble(value);
    }

    public void write(double[] values) {
        write(values.length);
        for (double value : values) {
            write(value);
        }
    }

    public double readDouble() {
        return _buffer.getDouble();
    }

    public double[] readDoubles() throws SerializeException {
        int size = readInt();
        checkSize(size);

        double[] values = new double[size];
        for (int i = 0; i < size; ++i) {
            values[i] = readDouble();
        }
        return values;
    }

    public void write(String value) {
        write(value.getBytes(StandardCharsets.UTF_8));
    }

    public void write(String[] values) {
        write(values.length);
        for (String value : values) {
            write(value);
        }
    }

    public String readString() throws SerializeException {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    public String[] readStrings() throws SerializeException {
        int size = readInt();
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = readString();
        }
        return values;
    }

    // prepare to read from it.
    public Serializer flip() {
        _buffer.flip();
        return this;
    }

    // prepare to write to it.
    public Serializer compact() {
        _buffer.compact();
        return this;
    }

    private void expand(int size) {
        if (_buffer.position() + size > _buffer.capacity()) {
            int newCapacity = 2 * (_buffer.position() + size);
            ByteBuffer newBuffer = createNewBuffer(newCapacity);
            _buffer.flip();
            newBuffer.put(_buffer);

            _buffer = newBuffer;
        }
    }

    private void dump() {
        System.out.println("<<Serializer[" + hashCode()
                + "]capacity:" + _buffer.capacity()
                + ",position:" + _buffer.position()
                + ",limit:" + _buffer.limit()
                + ">>");
    }

    private static ByteBuffer createNewBuffer(int capacity) {
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer;
    }

    private static void checkSize(int size) throws SerializeException {
        final int MAX_SIZE = 1024 * 64;
        if (size > MAX_SIZE) {
            throw new SerializeException();
        }
    }

    public static void main(String[] args) {
        Serializer serializer = new Serializer();
        serializer.dump();
        serializer.write((byte)9);     //1
        serializer.write(true);        //1
        serializer.write((short)99);   //2
        serializer.write(999);         //4
        serializer.write(999l);        //8
        serializer.write(9999f);       //4
        serializer.write(99999d);      //8
        serializer.write("Hello,You.");//4+10
        serializer.dump();
        serializer.flip();
        serializer.dump();
        System.out.println(serializer.readByte());
        System.out.println(serializer.readBoolean());
        System.out.println(serializer.readShort());
        System.out.println(serializer.readInt());
        System.out.println(serializer.readLong());
        System.out.println(serializer.readFloat());
        System.out.println(serializer.readDouble());
        System.out.println(serializer.readString());
        serializer.dump();
        serializer.compact();
        serializer.dump();
    }
}

