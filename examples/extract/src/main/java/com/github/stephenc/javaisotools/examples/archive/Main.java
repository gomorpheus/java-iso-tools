package com.morpheusdata.javaisotools.examples.archive;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        ReadIso tempIso = new ReadIso(new File("test.iso"), new File("test directory"));
    }
}
