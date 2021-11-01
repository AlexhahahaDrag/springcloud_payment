package com.alex.siddhi.test;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.core.stream.output.sink.Sink;
import io.siddhi.core.util.EventPrinter;

import java.util.Collection;
import java.util.List;

public class SimpleFilterSample {
    public static void main(String[] args) throws InterruptedException {

        // Create Siddhi Manager
        SiddhiManager siddhiManager = new SiddhiManager();

        //Siddhi Application
//        String siddhiApp = "" +
//                "define stream StockStream (symbol string, price float, volume long); " +
//                "" +
//                "@info(name = 'query1') " +
//                "from StockStream[volume < 150] " +
//                "select symbol, price " +
//                "insert into OutputStream;";
        String siddhiApp = "@App:name('CountOverTime') " +
        "@App:description('Receive events via HTTP, and logs the number of events received during last 15 seconds') " +
        " \n" +
                "@source(type = 'http', receiver.url = \"http://localhost:8006/production\", " +
        "@map(type = 'json')) " +
                "define stream ProductionStream (name string, amount double); " +
                " \n" +
                "@sink(type = 'log') " +
                "define stream TotalCountStream (totalCount long); " +
                " \n" +
                "-- Count the incoming events " +
                "@info(name = 'query1') " +
                "from ProductionStream#window.time(15 sec) " +
                "select count() as totalCount  " +
                "insert into TotalCountStream; ";

        //Generate runtime
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);

        //Adding callback to retrieve output events from stream
        siddhiAppRuntime.addCallback("TotalCountStream", new StreamCallback() {
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
