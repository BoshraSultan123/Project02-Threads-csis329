public class Main {
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server(9500);
        new Thread(s).start();

        Thread.sleep(500);

        for (int exp = 0; exp < 20; exp++) {
            new Thread(new Client(9500, 9501, 4)).start(); //number 4 indicating 4 workers for this client (you can change to 1 for singlethreading)
            new Thread(new Client(9500, 9502, 4)).start();
            new Thread(new Client(9500, 9503, 4)).start();
            new Thread(new Client(9500, 9504, 4)).start();
            new Thread(new Client(9500, 9505, 4)).start();
            Thread.sleep(15000);
        }
    }
}