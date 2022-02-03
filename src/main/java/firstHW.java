public class firstHW {
    private static final Object monitor = new Object();
    private static volatile char currentChar = 'A';


    public static void main(String[] args) {
        Thread threadA = new Thread(()-> {
            printA();
        });
        Thread threadB = new Thread(()-> {
            printB();
        });
        Thread threadC = new Thread(()-> {
            printC();
        });

        threadA.start();
        threadB.start();
        threadC.start();
    }


    public static void printA() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentChar != 'A') {
                        monitor.wait();
                    }
                    System.out.print("A");
                    currentChar = 'B';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printB() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentChar != 'B') {
                        monitor.wait();
                    }
                    System.out.print("B");
                    currentChar = 'C';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printC() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentChar != 'C') {
                        monitor.wait();
                    }
                    System.out.print("C");
                    currentChar = 'A';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

