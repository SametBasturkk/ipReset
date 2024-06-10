package org.ipChecker;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        FailCounter ipChecker = new FailCounter();
        System.out.println("Is PC connected to network ? " + ipChecker.checkIP());


    }
}
