package org.ipChecker;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class FailCounter {

    private static final String PATH = "./failCounter.txt";
    private static final int MAX_ATTEMPTS = 3;

    public static void main(String[] args) {
        FailCounter failCounter = new FailCounter();
        try {
            if (!failCounter.checkIP()) {
                System.out.println("IP check failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFileIfNotExists() {
        File file = new File(PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
                writeCount(0); // Initialize the file with 0
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int readCount() {
        createFileIfNotExists();
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH))) {
            String line = reader.readLine();
            return (line != null) ? Integer.parseInt(line) : 0;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void writeCount(int count) {
        createFileIfNotExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH))) {
            writer.write(Integer.toString(count));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void increment() {
        int count = readCount();
        count++;
        writeCount(count);
    }

    public void reset() {
        writeCount(0);
    }

    public String getCount() {
        return Integer.toString(readCount());
    }

    public String processIP(String IP) {
        if (IP.trim().isEmpty()) {
            increment();
            if (readCount() >= MAX_ATTEMPTS) {
                return "You have reached the maximum number of attempts";
            } else {
                rebootSystem();
            }
            return "IP is empty";
        } else {
            reset();
            System.out.println(IP);
            return "IP is not empty";
        }
    }

    public boolean checkIP() throws IOException {
        String ipAddress = getIPAddress();
        boolean hasValidIP = !ipAddress.trim().isEmpty() && !ipAddress.equals("127.0.0.1");

        System.out.println("System IP Address : " + ipAddress);
        String result = processIP(ipAddress);
        System.out.println(result);

        return hasValidIP;
    }

    public String getIPAddress() throws IOException {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        String ipAddress = addr.getHostAddress();
                        return ipAddress;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private void rebootSystem() {
        // Uncomment the following lines to enable reboot on reaching max attempts
        try {
            Runtime.getRuntime().exec("shutdown -r now");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
