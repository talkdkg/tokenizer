             Welcome to the Tokenizer Big Data Solutions
                --------------------------------------
                       http://www.tokenizer.org/

Tokenizer is a data management platform combining zettabytes
of data storage and real-time search. It's a one-stop-platform 
for any organization confronted with Big Data challenges that 
seeks rapid implementation, rock-solid performance at scale, 
and effectiveness.

Tokenizer is clusterable abstract executor, multiple nodes will try to start single clustered task instance, everything is managed via ZooKeeper. You can use CLI and GUI to manage configuration, start/stop/submit/delete tasks, and more.

For more details, check WIKI pages:
https://github.com/FuadEfendi/tokenizer/wiki/_pages

Subscribe to our mailing lists:
https://groups.google.com/forum/?hl=en&fromgroups#!forum/tokenizer-users
https://groups.google.com/forum/?hl=en&fromgroups#!forum/tokenizer-developers


Initially based upon Kauri Project:
http://www.kauriproject.org/

Inspired by Lily Project Indexer module:
http://www.lilyproject.org


To build, execute:
  mvn install

Then, deploy tokenizer-ui WAR file to Tomcat 7.0.32, it uses Vaadin, Atmosphere Framework, NIO, and HTML5 WebSockets. This WAR file includes everything what you need, except HBase instance.

You need HBase instance on localhost right now (something is still hardcoded); Tokenizer will generate schema upon startup.


You (optionally) need to download and unpack Kauri. To run as a comand line agent, execute:
  cd PATH_TO_TOKENIZER
  PATH_TO_KAURI/bin/kauri(.sh)

Use generated commands under tokenizer/tokenizer-executor-admin-cli/target 
such as add-task, update-task, list-tasks



Test: DE-2 #close
