package ru.megy.main;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by andrey.lazarenko on 20.05.2016.
 */
public class TestMain {
    public static void main(String[] args) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                for(int i=0;i<10;i++) {
                    System.out.println(i + " - " + this.isInterrupted());
                    System.out.println(i + " - " + this.isInterrupted());
                    System.out.println(i + " - " + this.isInterrupted());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(i + " - InterruptedException");
                        this.interrupt();
                    }
                }
            }
        };
        thread.start();

        try {
            Thread.sleep(5200);
        } catch (InterruptedException e) {
            System.out.println("main - InterruptedException");
        }
        thread.interrupt();
    }
}
