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

Then, deploy tokenizer-ui WAR file to Tomcat 7.0.32; it uses Vaadin, Atmosphere Framework, and HTML5 WebSockets.

As a minimum, you need ZooKeeper and Cassandra available at localhost. Tokenizer CA is distributed engine: you can have as many JVMs as you want; everything is managed via ZooKeeper. You can have huge cluster of executors, plus Cassandra, and single management point: Tomcat-based UI (and, in a future, command line UI). For details, see WIKI.


### XPATH ###
http://www.qutoric.com/xslt/analyser/xpathtool.html

Trip Advisor:

Splitter:
//div[starts-with(@id, 'review')]

Main Subject:
//*[@id="HEADING"]



Message Processor:

topicXPath
null

authorXPath
//div[starts-with(@class, 'username')]/span/text()

	
ageXPath
null	
	
sexXPath
null	
	
titleXPath
//div[@class='quote']/a/text()
	
contentXPath
//div[@class='entry']/p/text()[1]
	
	
dateXPath
substring-after(//span[@class='ratingDate'],'Reviewed ')

	
userRatingXPath
//img[@class='sprite-ratings']/@content

	
	
locationXPath
//div[@class='location']/text()





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


http://stackoverflow.com/questions/2615727/webpage-data-scraping-using-java
http://www.ibm.com/developerworks/xml/library/j-jtp03225/index.html
https://developers.google.com/webmasters/ajax-crawling/docs/specification

For example, if www.example.com contains <meta name="fragment" content="!"> in the head, the crawler will transform this URL into www.example.com?_escaped_fragment_= and fetch www.example.com?_escaped_fragment_= from the web server. 

test TDE-2 #close
TDE-2 #close


Vaadin, Shiro, and Juice:
http://rndjava.blogspot.co.uk/
https://github.com/davidsowerby/v7
http://www.javacodegeeks.com/2012/05/apache-shiro-part-2-realms-database-and.html



+ url_records
? url_sitemap_idx CompositeType(UTF8Type)
? timestamp_url_idx CompositeType(LongType,UTF8Type)"
+ webpage_records
+ xml_records TODO
+ message_records TODO
? weblogs_records TODO
? weblogs_records_idx0

fetched_result_records
url_head_records
host_records



#close



