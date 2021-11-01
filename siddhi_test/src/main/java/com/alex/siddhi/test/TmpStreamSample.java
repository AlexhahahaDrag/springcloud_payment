package com.alex.siddhi.test;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.core.util.EventPrinter;

public class TmpStreamSample {
    public static void main(String[] args) throws InterruptedException {

        // Create Siddhi Manager
        SiddhiManager siddhiManager = new SiddhiManager();

        String siddhiApp = " @App:name(\"temp-stream\") " +
                "  \n" +
                " @App:description(\"App_Description\") " +
                "  \n" +
//                " @source(type='kafka', " +
//                " topic.list='temp-stream-2', " +
//                " threading.option='single.thread', " +
//                " --        threading.option='topic.wise', " +
//                " group.id='siddhi-test', " +
//                " is.binary.message='true', " +
//                " bootstrap.servers='10.10.30.200:9092', " +
//                " @map(type='json',@attributes(cmCode = \"cmCode\", orgId = \"orgId\", " +
//                " timestamp = \"timestamp\",temperature=\"data.temperature\"))) " +
                " define stream DataStream(cmCode string,orgId string,timestamp long,temperature int); " +
                "  \n" +
                " @sink(type='log') " +
                " define stream LogDestDataStream(cmCode string,orgId string,timestamp long,temperature int); " +
                "  \n" +
                " from DataStream " +
                " insert into TempStream; " +
                "  \n" +
                " from TempStream " +
                " insert into LogDestDataStream; ";

        //Generate runtime
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);

        //Adding callback to retrieve output events from stream
        siddhiAppRuntime.addCallback("LogDestDataStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                //To convert and print event as a map
                //EventPrinter.print(toMap(events));
            }
        });

        //Get InputHandler to push events into Siddhi
//        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("StockStream");

        //Start processing
        siddhiAppRuntime.start();

        //Sending events to Siddhi
//        inputHandler.send(new Object[]{"IBM", 700f, 100L});
//        inputHandler.send(new Object[]{"WSO2", 60.5f, 200L});
//        inputHandler.send(new Object[]{"GOOG", 50f, 30L});
//        inputHandler.send(new Object[]{"IBM", 76.6f, 400L});
//        inputHandler.send(new Object[]{"WSO2", 45.6f, 50L});
//        Thread.sleep(500);
        siddhiAppRuntime.getSinks();
        //Shutdown runtime
//        siddhiAppRuntime.shutdown();

        //Shutdown Siddhi Manager
//        siddhiManager.shutdown();

    }
}
