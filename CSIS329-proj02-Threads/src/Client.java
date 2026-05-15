import java.net.*;

public class Client implements Runnable {

    int serverPort;
    int clientPort;
    int numWorkers;

    public Client(int serverPort, int clientPort, int numWorkers) {
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.numWorkers = numWorkers;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(clientPort);

            byte[] bytes = "register".getBytes();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            DatagramPacket registration = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
            socket.send(registration);
            System.out.println("Client " + clientPort + " registered!");

            byte[] buffer = new byte[1024];
            DatagramPacket rangePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(rangePacket);
            String range = new String(rangePacket.getData(), 0, rangePacket.getLength());
            System.out.println("Client " + clientPort + " received range: " + range);

            String[] parts = range.split(",");
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);

            int chunkSize = (end - start) / numWorkers;

            Worker[] workers = new Worker[numWorkers];
            Thread[] threads = new Thread[numWorkers];

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < numWorkers; i++) {
                int wStart = start + (i * chunkSize);
                int wEnd = start + ((i + 1) * chunkSize) - 1;
                workers[i] = new Worker("Worker" + i, wStart, wEnd);
                threads[i] = new Thread(workers[i]);
                threads[i].start();
            }

            for (int i = 0; i < numWorkers; i++) {
                threads[i].join();

            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            long totalSum = 0;
            for (int i = 0; i < numWorkers; i++) {
                totalSum += workers[i].result;
            }
            System.out.println("Client " + clientPort + " total prime sum: " + totalSum);

            System.out.println("Client " + clientPort + " execution time: " + duration + "ms");
            String resultMsg = totalSum + "," + duration;

            byte[] resultBytes = resultMsg.getBytes();
            DatagramPacket resultPacket = new DatagramPacket(
                    resultBytes, resultBytes.length,
                    serverAddress, serverPort);
            socket.send(resultPacket);
            System.out.println("Client " + clientPort + " sent result to server!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();

        }
    }

}