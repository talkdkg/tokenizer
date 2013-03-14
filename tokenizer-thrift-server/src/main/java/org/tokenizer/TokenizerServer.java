package org.tokenizer;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.tokenizer.thrift.ThriftTokenizerService;
import org.tokenizer.thrift.impl.TokenizerServiceImpl;

public class TokenizerServer {

    private static String host = null;
    private static int port = 32123;

    @SuppressWarnings("unchecked")
    private void start() {
        try {
            TServerSocket serverTransport = new TServerSocket(port);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(
                    serverTransport);
            args.minWorkerThreads(4);
            args.maxWorkerThreads(16);
            ThriftTokenizerService.Processor processor = new ThriftTokenizerService.Processor(
                    new TokenizerServiceImpl());
            args.processor(processor);
            TServer server = new TThreadPoolServer(args);
            System.out.println("Starting server on port: " + port);
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        TokenizerServer srv = new TokenizerServer();
        port = Integer.parseInt(args[0]);
        srv.start();
    }

}