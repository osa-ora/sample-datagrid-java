# Sample Data Grid - Java 

This example demonstrates how to use Data Grid to cache some data in a local development machine.  

It tests different scenarios like add/put/delete with different Java objects.   
Dynamic configurations using either code or properties file (you need to change the location of the file to point to the correct location in the class CacheService.java)

### Building

The example can be built with

    mvn clean install

### Prepare the local Environment

1. Download DataGrid server v8.0 and unzip it  
2. Go to the configuration files server/conf folder e.g. /redhat-datagrid-8.0.0-server/server/conf  
3. Add some cache containers in the configuration file: <b>infinispan.xml</b> (you can also add them   from the GUI after starting the server using: http://localhost:11222/)  
		&lt;local-cache name="accounts" start="EAGER">
		   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;encoding>&lt;value media-type="application/x-java-serialized-object"/>&lt;/encoding>
		    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;expiration lifespan="1000" max-idle="1000" />  
	   	 &lt;/local-cache>  
		 &lt;local-cache name="payments" start="EAGER">
		    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;encoding>&lt;value media-type="application/x-java-serialized-object"/>&lt;/encoding>  
		 &lt;/local-cache>  

4. Start the server e.g. /redhat-datagrid-8.0.0-server/bin/server(.bat or .sh)  
  
### Running the example

Run the TestDG class

    mvn exec:java -Dexec.mainClass="osa.ora.datagrid.TestDG"
