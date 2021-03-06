package com.HbaseIA.TwitBase;

import com.HbaseIA.TwitBase.model.Twit;
import com.HbaseIA.TwitBase.model.TwitsDAO;
import com.HbaseIA.TwitBase.model.UsersDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class TwitTools {
    private static final Logger log = Logger.getLogger(TwitTools.class.toString());
    private static final String usage =
            "twitstool action ...\n" +
                    "  help - print this message and exit.\n" +
                    "  post user text - post a new twit on user's behalf.\n" +
                    "  list user - list all twits for the specified user.\n";

    /**
     * Twit驱动
     *
     * @param args args[0]: "post" or "list"
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || "help".equals(args[0])) {
            System.out.println(usage);
            System.exit(0);
        }

        Configuration configuration = new Configuration();

        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "10.104.2.219"); // replace this zookeeper server ip address to yours
        Connection connection = ConnectionFactory.createConnection(configuration);
        TwitsDAO twitsDao = new TwitsDAO(connection);
        UsersDAO usersDao = new UsersDAO(connection);

        if ("post".equals(args[0])) {
            DateTime now = new DateTime();
            log.info(String.format("Posting twit at ...%s", now));
            twitsDao.postTwit(args[1], now, args[2]);
            Twit t = twitsDao.getTwit(args[1], now);
            usersDao.incTweetCount(args[1]);
            System.out.println("Successfully posted " + t);
        }

        if ("list".equals(args[0])) {
            List<Twit> twits = twitsDao.list(args[1]);
            log.info(String.format("Found %s twits.", twits.size()));
            for (Twit t : twits) {
                System.out.println(t);
            }
        }
        connection.close();

    }
}
