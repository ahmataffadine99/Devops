package worker;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.sql.*;
import org.json.JSONObject;




class Worker {
public static void main(String[] args) {
try {
 String redisHost = System.getenv("REDIS_HOST");
 Jedis redis = connectToRedis(redisHost);
 String pgHost = System.getenv("PG_HOST");
 Connection dbConn = connectToDB(pgHost);

  System.out.println("Watching vote queue : redis host : " + redisHost + " | pgHost : " + pgHost);


while (true) {
 String voteJSON = redis.blpop(0, "votes").get(1);
 JSONObject voteData = new JSONObject(voteJSON);
 String voterID = voteData.getString("voter_id");
 String vote = voteData.getString("vote");

 System.err.printf("Processing vote for '%s' by '%s'\n", vote, voterID);
 updateVote(dbConn, voterID, vote);
 }
}catch (SQLException e) {
    e.printStackTrace();
    System.exit(1);
 }
}
  static void updateVote(Connection dbConn, String voterID, String vote) throws SQLException {
    PreparedStatement insert = dbConn.prepareStatement(
       "INSERT INTO votes (id, vote) VALUES (?, ?)");
        insert.setString(1, voterID);
        insert.setString(2, vote);

        try {
           insert.executeUpdate();
        } catch (SQLException e) {
             PreparedStatement update = dbConn.prepareStatement(
               "UPDATE votes SET vote = ? WHERE id = ?");
                update.setString(1, vote);
                update.setString(2, voterID);
                update.executeUpdate();
              }
       }

        static Jedis connectToRedis(String host) {
            int redisPort = Integer.parseInt(System.getenv("REDIS_PORT"));
            Jedis conn = new Jedis(host, redisPort);


            while (true) {
             try {
                  conn.keys("*");
                  break;
            } catch (JedisConnectionException e) {
                  System.err.println("Waiting for redis");
                  e.printStackTrace();
                  sleep(1000); 
             }
          }


          System.err.println("Connected to redis");
                return conn;
    }


     static Connection connectToDB(String host) throws SQLException {
            Connection conn = null;

            try {
                  String postgresPort = System.getenv("PG_PORT");
                  Class.forName("org.postgresql.Driver");
                  String url = "jdbc:postgresql://" + host + ":" + postgresPort + "/postgres";
                  System.out.println("pg url : " + url);
                  while (conn == null) {
            try {
                  conn = DriverManager.getConnection(url, System.getenv("PG_USER"), System.getenv("PG_PASSWORD"));
                } catch (SQLException e) {
                  System.err.println("Waiting for db");
                  sleep(1000);
               }
              }


                } catch (ClassNotFoundException e) {
                   e.printStackTrace();
                   System.exit(1);
             }


           System.err.println("Connected to db");
           return conn;
        }

    static void sleep(long duration) {
         try {
             Thread.sleep(duration);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
        }






































































