package org.glagan.market;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glagan.core.Dictionary;
import org.glagan.core.Message;
import org.glagan.core.MessageBuilder;
import org.glagan.core.MsgType;

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

        Option marketFileOption = new Option("f", "file", true, "market file path");
        marketFileOption.setRequired(false);
        options.addOption(marketFileOption);

        Option databasePortOption = new Option("d", "db-port", true, "port of the database");
        databasePortOption.setType(Number.class);
        databasePortOption.setRequired(false);
        options.addOption(databasePortOption);

        Option databaseHostOption = new Option("b", "db-host", true, "host of the database");
        databaseHostOption.setRequired(false);
        options.addOption(databaseHostOption);

        Option databaseUserOption = new Option("u", "db-user", true, "user of the database");
        databaseUserOption.setRequired(false);
        options.addOption(databaseUserOption);

        Option databaseNameOption = new Option("n", "db-name", true, "name of the database");
        databaseNameOption.setRequired(false);
        options.addOption(databaseNameOption);

        Option databasePasswordOption = new Option("p", "db-password", true, "password of the database");
        databasePasswordOption.setRequired(false);
        options.addOption(databasePasswordOption);

        Option tokenOption = new Option("t", "token", true, "reconnect token");
        tokenOption.setType(Number.class);
        tokenOption.setRequired(false);
        options.addOption(tokenOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        String token = null;
        String host = "localhost";
        String marketFilePath = null;
        String databaseHost = "localhost";
        int databasePort = 5442;
        String databaseName = "fix-me";
        String databaseUser = "admin";
        String databasePassword = "admin";
        int port = 5001;
        try {
            cmd = parser.parse(options, args);
            host = cmd.getOptionValue("host", "localhost");
            if (cmd.hasOption("port")) {
                port = ((Number) cmd.getParsedOptionValue("port")).intValue();
                if (port < 0 || port > 65535) {
                    System.out.println("\u001B[31mInvalid port \u001B[0m" + port);
                    System.exit(1);
                }
            }
            marketFilePath = cmd.getOptionValue("file", null);
            databaseHost = cmd.getOptionValue("db-host", "localhost");
            if (cmd.hasOption("db-port")) {
                databasePort = ((Number) cmd.getParsedOptionValue("db-port")).intValue();
                if (databasePort < 0 || databasePort > 65535) {
                    System.out.println("\u001B[31mInvalid database port \u001B[0m" + databasePort);
                    System.exit(1);
                }
            }
            databaseName = cmd.getOptionValue("db-name", "fix-me");
            databaseUser = cmd.getOptionValue("db-user", "admin");
            databasePassword = cmd.getOptionValue("db-password", "admin");
            token = cmd.getOptionValue("token", null);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("broker", options);
            System.exit(1);
        }

        // Create the Database and run the blocking queue in a thread
        Database database = new Database(databaseHost, databasePort, databaseUser, databasePassword, databaseName);
        Thread databaseThread = new Thread(database);
        databaseThread.start();

        // Parse and handle market file
        if (marketFilePath != null) {
            Market.loadFromFilePath(marketFilePath);
        }
        if (!Market.hasInstruments()) {
            Market.generateRandom(5);
        }

        // Show Market instruments to be able to use them in Brokers
        Market.printInstruments();

        try (Socket socket = new Socket(host, port)) {
            System.out.println("\u001B[36mConnected to \u001B[0m" + host + ":" + port);
            Client client = new Client(socket, database);
            // Start the client in a thread to be able to send the Logon message
            Thread thread = new Thread(client);
            thread.start();
            // Send Logon
            MessageBuilder logonMessage = Message.make(MsgType.Logon).auth("fix-me");
            if (token != null) {
                logonMessage.add(Dictionary.Token, token);
            }
            client.send(logonMessage.build());
            thread.join();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + host);
        } catch (ConnectException e) {
            System.err.println("Failed to connect to " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Unexpected IO error: " + e.getMessage());
        } catch (InterruptedException e) {
            // Ignore expected error
        }

        // Cleanup
        database.close();
        databaseThread.interrupt();
    }
}
