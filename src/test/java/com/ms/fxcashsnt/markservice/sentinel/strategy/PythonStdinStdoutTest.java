package com.ms.fxcashsnt.markservice.sentinel.strategy;

import org.junit.Test;
import org.opensaml.xml.signature.P;

import java.io.*;
import java.util.Scanner;

/**
 * user: yandongl
 * date: 8/14/2018
 */
public class PythonStdinStdoutTest {
//    @Test
    public void pythonProcessBuilderTest() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("M:\\dist\\python\\PROJ\\core\\3.4.4\\bin\\python.exe", "elliptic_envelope.py");
        builder.directory(new File("U:\\My Documents\\sentinel\\sentinel\\trunk\\src\\src\\main\\java\\com\\ms\\fxcashsnt\\markservice\\sentinel\\strategy\\scripts"));
        Process process = builder.start();

        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
//        Thread.sleep(10*1000);
        for (int i = 0; i < 10; i++){

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            writer.write("abcde,ddds\n");
//            stdin.flush();
            writer.flush();
//            writer.close();

//            reader.readLine();
            System.out.println(reader.readLine());
//            Scanner scanner = new Scanner(stdout);

//            while (scanner.hasNextLine()) {
//                System.out.println(scanner.nextLine());
//            }

//        writer.close();
//            writer.close();
//            scanner.close();
        }

    }
}
