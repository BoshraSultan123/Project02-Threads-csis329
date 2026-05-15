import java.net.*;

public class Server implements Runnable {

    int port;
    int totalRange = 10_000_000;
    int numClients = 5;
    int numExperiments = 20;
    long[][] times = new long[numExperiments][numClients];

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            System.out.println("Server ready on port " + port);

            int chunkSize = totalRange / numClients;

            for (int exp = 0; exp < numExperiments; exp++) {
                System.out.println("\n--- Experiment " + (exp + 1) + " ---");

                int[] clientPorts = new int[numClients];
                for (int i = 0; i < numClients; i++) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    clientPorts[i] = request.getPort();
                    System.out.println("Client registered from port: " + clientPorts[i]);
                }

                for (int i = 0; i < numClients; i++) {
                    int start = i * chunkSize + 1;
                    int end = (i + 1) * chunkSize;
                    String range = start + "," + end;
                    byte[] rangeBytes = range.getBytes();
                    InetAddress clientAddress = InetAddress.getByName("localhost");
                    DatagramPacket assignment = new DatagramPacket(rangeBytes, rangeBytes.length, clientAddress, clientPorts[i]);
                    socket.send(assignment);
                    System.out.println("Sent range " + range + " to port " + clientPorts[i]);
                }

                long grandTotal = 0;
                for (int i = 0; i < numClients; i++) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket resultPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(resultPacket);
                    String resultStr = new String(resultPacket.getData(), 0, resultPacket.getLength());
                    String[] resultParts = resultStr.split(",");
                    long partialSum = Long.parseLong(resultParts[0]);
                    long clientTime = Long.parseLong(resultParts[1]);
                    times[exp][i] = clientTime;
                    System.out.println("Server received from port " + resultPacket.getPort() +
                            " | sum: " + partialSum + " | time: " + clientTime + "ms");
                    grandTotal += partialSum;
                }

                System.out.println("Final Prime Sum: " + grandTotal);

            } 

            printTable(); 

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }

    void printTable() {
        System.out.println("\n========== RESULTS TABLE ==========");
        System.out.printf("%-12s", "Experiment");
        for (int i = 0; i < numClients; i++) {
            System.out.printf("| Client%-5d", i + 1);
        }
        System.out.println();
        System.out.println("---------------------------------------------------");

        for (int exp = 0; exp < numExperiments; exp++) {
            System.out.printf("%-12d", exp + 1);
            for (int i = 0; i < numClients; i++) {
                System.out.printf("| %-11d", times[exp][i]);
            }
            System.out.println();
        }

        System.out.println("---------------------------------------------------");
        System.out.printf("%-12s", "Average");
        for (int i = 0; i < numClients; i++) {
            long sum = 0;
            for (int exp = 0; exp < numExperiments; exp++) {
                sum += times[exp][i];
            }
            System.out.printf("| %-11d", sum / numExperiments);
        }
        System.out.println();
        System.out.println("===================================");
    }
}