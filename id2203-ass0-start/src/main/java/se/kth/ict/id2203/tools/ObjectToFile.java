package se.kth.ict.id2203.tools;

import java.io.*;
import java.util.ArrayList;

public class ObjectToFile {
    public void writeObject(String name, Object object) {
        try {
            FileOutputStream outStream = new FileOutputStream("D:\\" + name + ".txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

            objectOutputStream.writeObject(object);
            outStream.close();
            System.out.println("successful");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readObject(String name) {

        ArrayList<String> list = null;
        try {
            FileInputStream inputStream;
            inputStream = new FileInputStream("D:\\" + name + ".txt");

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            list = new ArrayList<String>();
            list = (ArrayList<String>) objectInputStream.readObject();

            System.out.println("List: " + list.toString());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }
}