https://dev.twitter.com/docs/streaming-apis/connecting

1. mvn achetype:generate -Dfilter=wicket
648: remote -> xaloon-archetype-wicket-jpa-spring (-)

2.
 1 down vote accepted
	

Wicket + Spring + JPA archetype with sample application (uses Wicket 1.4.9)

To adjust your version of Wicket, just edit the POM file's Wicket depending version.

To use this archetype in Eclipse, I did the following: ran maven at the command line to generate a project from the archetype:

    mvn archetype:generate -B  \ 
   -DarchetypeCatalog=http://legup.googlecode.com/svn/repo/archetype-catalog.xml \
   -DarchetypeArtifactId=wicket-spring-jpa-archetype \
   -DarchetypeGroupId=com.jweekend -DarchetypeVersion=0.8.3 \
   -DgroupId=com.mycompany -DartifactId=mycompany \
   -Dversion=1.0-SNAPSHOT -Dpackage=com.mycompany

Cd'd into the project directory, ran mvn jetty_run to download dependencies, build, and run the app.

Ran mvn eclipse:eclipse to generate eclipse metadata.

In Eclipse, File| New project | "from existing source" to import it.
 


3.

After googling for solution, i found this post: http://markmail.org/message/4uu6q3fxnlqk2zgo in that post you must run your web-apps using this command :

    mvn org.mortbay.jetty:maven-jetty-plugin:run

After execute. The first time, there’s a dizzying number of downloads, after the first time you run your web-apps, you could run your web-apps using usual command like :

    mvn jetty:run
    
 
 
JPA: 
18 - standalone eclipselink
74 - VAADIN + JPA
     