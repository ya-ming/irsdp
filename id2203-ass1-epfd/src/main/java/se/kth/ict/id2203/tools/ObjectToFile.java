package se.kth.ict.id2203.tools;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectToFile {

    private static final Logger logger = LoggerFactory.getLogger(ObjectToFile.class);

    public void writeObject(String name, Object object) {
        try {
            String path = "D:\\" + name + ".txt";
            createFile(path);

            FileOutputStream outStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

            objectOutputStream.writeObject(object);
            outStream.close();
            logger.debug("write success");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create file if not exist.
     *
     * @param path For example: "D:\foo.xml"
     */
    public static void createFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                FileOutputStream writer = new FileOutputStream(path);
                writer.write(("").getBytes());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readObject(String name) {

        Object object = null;
        String path = "D:\\" + name + ".txt";
        try {
            FileInputStream inputStream;
            inputStream = new FileInputStream(path);

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            object = objectInputStream.readObject();

            logger.debug("read success");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
            createFile(path);
            logger.debug("file not exist, creating new file");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.debug("Empty file, throw IOException");
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return object;
    }
}
