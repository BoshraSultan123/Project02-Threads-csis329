public class Worker implements Runnable {

    String name;
    int start;
    int end;
    long result;

    public Worker(String name, int start, int end) {
        this.name = name;
        this.start = start;
        this.end = end;

    }

     public static boolean isPrime(int n) {
    if (n <= 1) return false;
    for (int i = 2; i <= Math.sqrt(n); i++) {
        if (n % i == 0) return false;
    }
    return true;
}

    @Override
    public void run() {
        long sum = 0;
        for (int i = start; i <= end; i++){
           if (isPrime(i)){
            sum +=i;
           }
        }
        result = sum;
         System.out.println(name + " done. Result = " + result);

    }
}