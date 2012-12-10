             Welcome to the Tokenizer Big Data Solutions
                --------------------------------------
                       http://www.tokenizer.org/

Tokenizer is a data management platform combining zettabytes of data storage and real-time search. It's a one-stop-platform 
for any organization confronted with Big Data challenges that seeks rapid implementation, rock-solid performance at scale, 
and effectiveness.

Tokenizer is clusterable abstract executor, multiple nodes will try to start single clustered task instance, everything is managed via ZooKeeper. 
You can use CLI and GUI to manage configuration, start/stop/submit/delete tasks, and more.

For more details, check WIKI pages:
https://github.com/FuadEfendi/tokenizer/wiki/_pages

Subscribe to our mailing lists:
https://groups.google.com/forum/?hl=en&fromgroups#!forum/tokenizer-users
https://groups.google.com/forum/?hl=en&fromgroups#!forum/tokenizer-developers

To build, execute:
  mvn install

Then, deploy tokenizer-ui WAR file to Tomcat 7.0.32; it uses Vaadin, Atmosphere Framework, NIO, and HTML5 WebSockets.

As a minimum, you need ZooKeeper and Cassandra vailable at localhost. Tokenizer CA is distributed engine: yopu can have as many JVMs as you want; everything is managed via ZooKeeper. For details, see WIKI.


XPath Samples:
www.amazon.com splitter
//table[@id='productReviews']//td/div

userRating
/div/div/span

title
/div/div/span[2]/b

date
/div/div/span[2]/nobr

author
/div/div[2]/div/div[2]/a/span

topic
/div/div[3]/b

content
/div

