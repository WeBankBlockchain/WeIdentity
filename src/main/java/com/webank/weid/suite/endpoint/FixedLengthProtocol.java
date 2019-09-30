package com.webank.weid.suite.endpoint;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.transport.AioSession;

public class FixedLengthProtocol implements Protocol<String> {

    private static final Logger logger = LoggerFactory.getLogger(FixedLengthProtocol.class);
    private static final int INT_BYTES = 4;

    /**
     * Decode method for the fixed length protocol.
     *
     * @param readBuffer the readbuffer
     * @param session the aligned session
     * @return the decoded String
     */
    @Override
    public String decode(ByteBuffer readBuffer, AioSession<String> session) {
        if (session.getAttachment() == null
            && readBuffer.remaining() < INT_BYTES) {
            logger.error("Failed to decode: cannot fill minimum Bytes: 4");
            return null;
        }
        FixedLengthFrameDecoder fixedLengthFrameDecoder;
        if (session.getAttachment() != null) {
            fixedLengthFrameDecoder = session.getAttachment();
        } else {
            int length = readBuffer.getInt();
            fixedLengthFrameDecoder = new FixedLengthFrameDecoder(length);
            session.setAttachment(fixedLengthFrameDecoder);
        }

        if (!fixedLengthFrameDecoder.decode(readBuffer)) {
            logger.error("Unable to fetch enough Bytes");
            return null;
        }
        ByteBuffer fullBuffer = fixedLengthFrameDecoder.getBuffer();
        byte[] bytes = new byte[fullBuffer.remaining()];
        fullBuffer.get(bytes);
        session.setAttachment(null);
        return new String(bytes);
    }

    /**
     * Encode method for the fixed length protocol.
     *
     * @param msg message to be encoded.
     * @return encoded bytebuffer
     */
    public static ByteBuffer encode(String msg) {
        logger.debug("Starting to encode: ", msg);
        byte[] bytes = msg.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(INT_BYTES + bytes.length);
        buffer.putInt(bytes.length);//消息头
        buffer.put(bytes);//消息体
        buffer.flip();
        return buffer;
    }
}