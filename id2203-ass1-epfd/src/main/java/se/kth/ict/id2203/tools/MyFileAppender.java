package se.kth.ict.id2203.tools;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;


public class MyFileAppender extends FileAppender {
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        // 只判断是否相等，而不判断优先级
//        System.out.println("MyFileAppender");
        if (this.getThreshold().equals(priority)) {
//            System.out.println("MyFileAppender return true;");
            return true;
        }
        else {
//            System.out.println("MyFileAppender return false;");
            return false;
        }
    }

    @Override
    public void append(LoggingEvent event) {
        super.append(event);
    }
}
