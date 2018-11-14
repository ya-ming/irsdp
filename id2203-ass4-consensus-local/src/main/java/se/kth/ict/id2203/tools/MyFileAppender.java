package se.kth.ict.id2203.tools;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;


public class MyFileAppender extends FileAppender {
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        // 只判断是否相等，而不判断优先级
//        System.out.println("MyFileAppender");
        //            System.out.println("MyFileAppender return true;");
//            System.out.println("MyFileAppender return false;");
        return this.getThreshold().equals(priority);
    }

    @Override
    public void append(LoggingEvent event) {
        super.append(event);
    }
}
