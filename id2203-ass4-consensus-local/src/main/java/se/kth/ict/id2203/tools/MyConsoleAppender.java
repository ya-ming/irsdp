package se.kth.ict.id2203.tools;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Priority;


public class MyConsoleAppender extends ConsoleAppender {
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
//        System.out.println("MyConsoleAppender");
        // 只判断是否相等，而不判断优先级
        if (priority.toInt() == 5000) {
//            System.out.println("MyConsoleAppender return false;");
            return false;
        } else {
//            System.out.println("MyConsoleAppender call super;");
            return super.isAsSevereAsThreshold(priority);
        }
    }
}
