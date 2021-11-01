package com.alex.siddhi.test;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.core.util.EventPrinter;

public class PostgreSample {
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
        String siddhiApp = " @App:name(\"SiddhiTestPostgre\") " +
                "  " +
                " @App:description(\"Description of the plan\") " +
                "  " +
                "@source(type = 'http', receiver.url = \"http://localhost:8006/postgreTest\", " +
                "@map(type = 'json')) " +
                " define stream TemperatureStream ( " +
                " sensor_id string, room_no string, temperature double); " +
                "  " +
                " @store(type=\"rdbms\", " +
                " jdbc.url=\"jdbc:postgresql://localhost:5432/water?currentSchema=uc\", " +
        " username=\"postgres\", password=\"123456\", " +
                " jdbc.driver.name=\"org.postgresql.Driver\") " +
                " @primaryKey('sensor_id') " +
                " define table temperature_log_table (sensor_id string, room_no string, temperature double); " +
                "  " +
                " @info(name = 'test') " +
                " from TemperatureStream " +
                " select sensor_id, room_no, temperature " +
                " insert into temperature_log_table ";

        //Generate runtime
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);

        //Adding callback to retrieve output events from stream
        siddhiAppRuntime.addCallback("TemperatureStream", new StreamCallback() {
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
