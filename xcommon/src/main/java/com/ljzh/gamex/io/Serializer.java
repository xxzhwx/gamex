package com.ljzh.gamex.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Serializer {
    public static final int BYTES_OF_BYTE = Byte.BYTES;
    public static final int BYTES_OF_SHORT = Short.BYTES;
    public static final int BYTES_OF_INT = Integer.BYTES;
    public static final int BYTES_OF_LONG = Long.BYTES;
    public static final int BYTES_OF_FLOAT = Float.BYTES;
    public static final int BYTES_OF_DOUBLE = Double.BYTES;

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
        expand(BYTES_OF_BYTE);
        _buffer.put(value);
    }

    public void write(int index, byte value) {
        expand(index, BYTES_OF_BYTE);
        _buffer.put(index, value);
    }

    public void write(byte[] values) {
        write(values.length);
        for (byte value : values) {
            write(value);
        }
    }

    public void write(int index, byte[] values) {
        write(index, values.length);
        index += BYTES_OF_INT;

        for (byte value : values) {
            write(index, value);
            index += BYTES_OF_BYTE;
        }
    }

    public byte readByte() {
        return _buffer.get();
    }

    public byte readByte(int index) {
        return _buffer.get(index);
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

    public byte[] readBytes(int index) throws SerializeException {
        int size = readInt(index);
        index += BYTES_OF_INT;
        checkSize(size);

        byte[] values = new byte[size];
        for (int i = 0; i < size; i++) {
            values[i] = readByte(index);
            index += BYTES_OF_BYTE;
        }
        return values;
    }

    public void write(boolean value) {
        write(value ? (byte)1 : (byte)0);
    }

    public void write(int index, boolean value) {
        write(index, value ? (byte)1 : (byte)0);
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

    public boolean readBoolean(int index) {
        return readByte(index) != 0;
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
        expand(BYTES_OF_SHORT);
        _buffer.putShort(value);
    }

    public void write(int index, short value) {
        expand(index, BYTES_OF_SHORT);
        _buffer.putShort(index, value);
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

    public short readShort(int index) {
        return _buffer.getShort(index);
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
        expand(BYTES_OF_INT);
        _buffer.putInt(value);
    }

    public void write(int index, int value) {
        expand(index, BYTES_OF_INT);
        _buffer.putInt(index, value);
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

    public int readInt(int index) {
        return _buffer.getInt(index);
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
        expand(BYTES_OF_LONG);
        _buffer.putLong(value);
    }

    public void write(int index, long value) {
        expand(index, BYTES_OF_LONG);
        _buffer.putLong(index, value);
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

    public long readLong(int index) {
        return _buffer.getLong(index);
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
        expand(BYTES_OF_FLOAT);
        _buffer.putFloat(value);
    }

    public void write(int index, float value) {
        expand(index, BYTES_OF_FLOAT);
        _buffer.putFloat(index, value);
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

    public float readFloat(int index) {
        return _buffer.getFloat(index);
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
        expand(BYTES_OF_DOUBLE);
        _buffer.putDouble(value);
    }

    public void write(int index, double value) {
        expand(index, BYTES_OF_DOUBLE);
        _buffer.putDouble(index, value);
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

    public double readDouble(int index) {
        return _buffer.getDouble(index);
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

    public Serializer skip(int numOfBytes) {
        expand(numOfBytes);
        int pos = _buffer.position();
        _buffer.position(pos + numOfBytes);
        return this;
    }

    private void expand(int size) {
        expand(_buffer.position(), size);
    }

    private void expand(int index, int size) {
        if (index + size > _buffer.capacity()) {
            int newCapacity = 2 * (index + size);
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
//        test0();
        testSkip();
    }

    private static void test0() {
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

    private static void testSkip() {
        Serializer serializer = new Serializer();
        serializer.skip(15);
        serializer.write(2, new byte[]{1,2,3,4,5,6,7,8,9});
        serializer.dump();
        serializer.flip();
        byte[] bytes = serializer.readBytes(2);
        for (byte b : bytes) {
            System.out.print("" + b + "\t");
        }
    }
}

