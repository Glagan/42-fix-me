package org.glagan.market;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {
    public static void main(String[] args) {
        Options options = new Options();

        Option hostOption = new Option("h", "host", true, "hostname of the router");
        hostOption.setRequired(false);
        options.addOption(hostOption);

        Option portOption = new Option("p", "port", true, "port of the router");
        portOption.setType(Number.class);
        portOption.setRequired(false);
        options.addOption(portOption);

        Option marketFileOption = new Option("f", "file", true, "market file");
        marketFileOption.setType(Number.class);
        marketFileOption.setRequired(false);
        options.addOption(marketFileOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        String host = "localhost";
        String marketFile = null;
        int port = 5001;
        try {
            cmd = parser.parse(options, args);
            host = cmd.getOptionValue("host", "localhost");
            if (cmd.hasOption("port")) {
                port = ((Number) cmd.getParsedOptionValue("port")).intValue();
                if (port < 0 || port > 65535) {
                    System.out.println("Invalid port " + port);
                    System.exit(1);
                }
            }
            marketFile = cmd.getOptionValue("host", null);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("broker", options);
            System.exit(1);
        }

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to " + host + ":" + port);
            MarketClient client = new MarketClient(socket);
            client.run(); // The client is a Runnable that run in the main thread
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
