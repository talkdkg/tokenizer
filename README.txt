
                 === About this generated project ===
<< INSERT YOUR PROJECT DESCRIPTION HERE >>

          +------------------------------------------------+
          |              Building and running              |
          +------------------------------------------------+

To build, execute:
  mvn install

To run, execute:
  PATH_TO_KAURI/bin/kauri(.sh)

and surf to
  http://localhost:8888


          +------------------------------------------------+
          |         About the prototype archetype          |
          +------------------------------------------------+

Kauri has special support for "prototyping", that is developing Web
frontends while not worrying about the backend. You can switch
between prototype and production mode at any time during the lifecycle
of a project.

In prototype mode, you work mainly with templates, while relying on
mock resource data.

More extensive information on this topic can be found in the
Kauri documentation, see http://kauriproject.org


          +------------------------------------------------+
          |              Database information              |
          +------------------------------------------------+

Depending on the runtime mode, either mocked up data resources are
used, or real database resources. The switch between both is done
by loading a different spring configuration:

  main-module/src/main/kauri/spring/[production|prototype]


For the real database, this project uses by default an in-memory
HSQL database. You can find the configuration at

  main-module/src/main/resources/META-INF/persistence.xml
