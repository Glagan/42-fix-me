package org.glagan.broker;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

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

        Option tokenOption = new Option("t", "token", true, "reconnect token");
        tokenOption.setType(Number.class);
        tokenOption.setRequired(false);
        options.addOption(tokenOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        String host = "localhost";
        String token = null;
        int port = 5000;
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
            token = cmd.getOptionValue("token", null);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("broker", options);
            System.exit(1);
        }

        Socket socket;
        try {
            // * Client
            socket = new Socket(host, port);
            System.out.println("\u001B[36mConnected to \u001B[0m" + host + ":" + port);
            Client client = new Client(socket);
            // Start the Client in a thread to handle the input loop in the main thread
            Thread thread = new Thread(client);
            thread.start();
            // Send Logon
            MessageBuilder logonMessage = Message.make(MsgType.Logon).auth("fix-me");
            if (token != null) {
                logonMessage.add(Dictionary.Token, token);
            }
            client.send(logonMessage.build());

            // * Input loop
            int nextOrder = 1;
            Scanner scanner = new Scanner(System.in);
            System.out.println(
                    "---- Commands"
                            + "\nbuy {market} {instrument} {quantity} {price}: Send a Buy message"
                            + "\nsell {market} {instrument} {quantity} {price}: Send a Sell message"
                            + "\nquit,exit: Close the Broker"
                            + "\n----");
            boolean running = true;
            try {
                while (running && !socket.isClosed()) {
                    String input;
                    if (System.in.available() > 0) {
                        input = scanner.nextLine();
                    } else {
                        Thread.sleep(250);
                        continue;
                    }
                    if (input == null) {
                        running = false;
                        break;
                    }
                    String[] parts = input.split(" ");
                    if (parts.length == 0) {
                        break;
                    }
                    String command = parts[0];
                    switch (command) {
                        case "buy":
                            if (parts.length != 5) {
                                System.out.println(
                                        "\u001B[31mInvalid arguments, expected `buy {market} {instrument} {quantity} {price}`\u001B[0m");
                            } else {
                                System.out.println("\u001B[36m" + input + "\u001B[0m");
                                client.send(
                                        Message.make(MsgType.Buy)
                                                .auth(client.getId())
                                                .add(Dictionary.OrderId, "" + nextOrder++)
                                                .add(Dictionary.Market, parts[1])
                                                .add(Dictionary.Instrument, parts[2])
                                                .add(Dictionary.Quantity, parts[3])
                                                .add(Dictionary.Price, parts[4])
                                                .build());
                            }
                            break;

                        case "sell":
                            if (parts.length != 5) {
                                System.out.println(
                                        "\u001B[31mInvalid arguments, expected `sell {market} {instrument} {quantity} {price}`\u001B[0m");
                            } else {
                                System.out.println("\u001B[36m" + input + "\u001B[0m");
                                client.send(
                                        Message.make(MsgType.Sell)
                                                .auth(client.getId())
                                                .add(Dictionary.OrderId, "" + nextOrder++)
                                                .add(Dictionary.Market, parts[1])
                                                .add(Dictionary.Instrument, parts[2])
                                                .add(Dictionary.Quantity, parts[3])
                                                .add(Dictionary.Price, parts[4])
                                                .build());
                            }
                            break;

                        case "quit":
                        case "exit":
                            running = false;
                            break;

                        default:
                            System.out.println("\u001B[31mUnknown command\u001B[0m");
                            break;
                    }
                }
            } catch (IllegalStateException | NoSuchElementException | InterruptedException e) {
                // Quit on System.in close
            }
            scanner.close();

            // * Cleanup -- Should close the thread
            socket.close();
            thread.interrupt();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + host);
        } catch (ConnectException e) {
            System.err.println("Failed to connect to " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Unexpected IO error: " + e.getMessage());
        }
    }
}
