/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:14
 */
package com.lixianling.rabbit.util;

import com.lixianling.rabbit.IdGenerator;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: LongIdGenerator.java 36 2016-01-06 17:24:04Z hank $
 */
public class LongIdGenerator implements IdGenerator {

    private final long datacenterIdBits = 24L;
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long timestampBits = 37L;
    private final long datacenterIdShift = 2L;
    private final long timestampLeftShift = 26L;
    private final long sequenceMax = 4;
    private final long twepoch = 1288834974657L;
    private final long datacenterId;
    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;


    public LongIdGenerator() {
        datacenterId = getDatacenterId();
//        System.out.println(maxDatacenterId); // MAX 16,777,215
    }

    protected long getDatacenterId() {
        try {
//            InetAddress ip = InetAddress.getLocalHost();
//            System.out.println(ip);
//            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            byte[] mac = null;
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                mac = network.getHardwareAddress();

                if (mac != null) {
//                    System.out.print("Current MAC address : ");
//
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < mac.length; i++) {
//                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//                    }
//                    System.out.println(sb.toString());
                    break;
                }

            }
//            byte[] mac = network.getHardwareAddress();
//System.out.println(DatatypeConverter.printHexBinary(mac));
            long id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
//System.out.println(id);
            return id;
        } catch (SocketException e) {
            // nothing
        }
        return 0;
    }

    // 10,000,000 under 24 bit + 2bit (3)

    public String generateStringId() {
        return generateLongId().toString();
    }

    public synchronized Long generateLongId() {
        long timestamp = System.currentTimeMillis();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
//        System.out.println((timestamp - twepoch));
//        System.out.println(datacenterId);
        Long id = ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | sequence;
        return id;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        LongIdGenerator idGenerator = new LongIdGenerator();
        System.out.println(idGenerator.generateLongId());

        Long tt = 1228032236284911616L;

        tt = tt >> 13L;
        tt = (tt & 0xFFFFFFF);

        System.out.println(tt);
        System.out.println(1005<<2 | 3);
        System.out.println(4023 >> 2);
        System.out.println(128599354991L << 26 | 4023);
        System.out.println(128599354991L << 26);

        tt = 8630156624578744247L;
        tt = tt >> 2;
        tt = (tt & 0xFFFFFF);
        System.out.println(tt);
//        System.out.println(8630156624578744247L - 8630156624578740224L);
    }
}
