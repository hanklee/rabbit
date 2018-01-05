/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:13
 */
package com.lixianling.rabbit;

/**
 *
 *  ID generator
 *
 * @author Xianling Li(hanklee)
 * $Id: IdGenerator.java 35 2016-01-06 15:27:41Z hank $
 */
public interface IdGenerator {

    String generateStringId();

    Long generateLongId();

    public static class DefaultIdGenerator implements IdGenerator {

        private final long datacenterIdShift = 3L;
        private final long timestampLeftShift = 10L;
        private final long sequenceMax = 8;
        private final long twepoch = 1288834974657L;
        private final long datacenterId;
        private volatile long lastTimestamp = -1L;
        private volatile long sequence = 0L;
//    private final long datacenterIdBits = 24L;

        public DefaultIdGenerator() {
            datacenterId = 1;
        }

        @Override
        public String generateStringId() {
            return generateLongId().toString();
        }

        @Override
        public synchronized Long generateLongId() {
//        System.out.println(System.currentTimeMillis());
            long timestamp = getLastTimestamp();
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) % sequenceMax;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0;
            }
            lastTimestamp = timestamp;
            return (timestamp << timestampLeftShift)
                    | (datacenterId << datacenterIdShift)
                    | sequence;
        }

        protected long tilNextMillis(long lastTimestamp) {
            long timestamp = getLastTimestamp();
            while (timestamp <= lastTimestamp) {
                timestamp = getLastTimestamp();
            }
            return timestamp;
        }

        private long getLastTimestamp(){
            return (System.currentTimeMillis() - twepoch);
        }

        public static void main(String[] args) {
            IdGenerator idGenerator =new DefaultIdGenerator();
            for (int i = 0; i < 20; i++) {
                System.out.println(idGenerator.generateLongId());
            }

            //770667812166303752
            //123456789012345678
            //146617543567369
            //192174233856507915
        }
    }
}
