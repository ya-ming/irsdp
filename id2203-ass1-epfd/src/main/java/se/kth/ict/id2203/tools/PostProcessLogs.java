package se.kth.ict.id2203.tools;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class PostProcessLogs {

    static LinkedList<File> fileList;
    static Multimap<Long, String> multimap;

    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                fileList.add(fileEntry);
//                System.out.println(fileEntry.getName());
            }
        }
    }

    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
//                System.out.println("line " + line + ": " + tempString);
                line++;

                String idString = tempString.substring(0, 15);
                Long id = Long.parseLong(idString.trim());
                String message = tempString.substring(16, tempString.length());

//                System.out.println("id " + id + ": " + message);

                multimap.put(id, message);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        String instanceA = "Client";
        String instanceB = "Paxos";
        int pairs = 3;
        if (args.length == 3) {
            instanceA = args[0];
            instanceB = args[1];
            pairs = Integer.parseInt(args[2]);
        }


        final File folder = new File("logs/");

        fileList = new LinkedList<>();
        multimap = TreeMultimap.create();

        listFilesForFolder(folder);

        System.out.println(fileList);

        for (File file:fileList
             ) {
            readFileByLines(file.getAbsolutePath());
        }

        BufferedWriter out=new BufferedWriter(new FileWriter("log.puml"));

//        out.write("@startuml");
//        out.newLine();
//        out.write("Client1<-->Paxos1: Start");
//        out.newLine();
//        out.write("Client2<-->Paxos2: Start");
//        out.newLine();
//        out.write("Client3<-->Paxos3: Start");
//        out.newLine();

        out.write("@startuml");
        out.newLine();
        for (int i = 1; i <= pairs; ++i) {
            out.write(instanceA + i + "<-->" + instanceB + i);
            out.newLine();
        }

        Iterator iter = multimap.entries().iterator();
        while(iter.hasNext())
        {
            Map.Entry<Long, String> entry = (Map.Entry<Long, String>)iter.next();
            System.out.println(String.format("%d:%s", entry.getKey(),entry.getValue()));
            String msg = entry.getValue();
            if (msg.contains("PrepareMessage") ||
                    msg.contains("HeartbeatRequestBleMessage")) {
                msg = msg.replace("->", "-[#blue]>");
            }
            else if(msg.contains("PrepareAckMessage") ||
                    msg.contains("HeartbeatResponseBleMessage")) {
                msg = msg.replace("->", "-[#green]>");
            }
            else if(msg.contains("NackMessage")) {
                msg = msg.replace("->", "-[#red]>");
            }
            else if(msg.contains("AcceptMessage")) {
                msg = msg.replace("->", "-[#purple]>");
            }
            else if(msg.contains("AcceptAckMessage")) {
                msg = msg.replace("->", "-[#orange]>");
            }
            else if(msg.contains("@@")) {
                msg = msg.replace("@@", "\n");
            }

            out.write(msg);
            out.newLine();
        }

        out.write("@enduml");
        out.flush();
        out.close();
    }
}
