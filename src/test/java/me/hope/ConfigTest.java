package me.hope;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigTest {
    public static void main(String[] args) {
        File importFile = new File("D:\\repsect\\Minecraft\\HopeGift\\src\\test\\resources\\cdk.txt");
        List<String> cdks = Collections.synchronizedList(new ArrayList<>());
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(importFile), Charset.defaultCharset()));
            String lineText;
            while((lineText = br.readLine()) != null){
                System.out.println(lineText);
                //cdks.addAll(Arrays.asList(lineText.split(",")));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
