package osa.ora.datagrid;

import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.ServerStatistics;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryExpired;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryRemoved;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.NearCacheMode;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryExpiredEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryRemovedEvent;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;

import osa.ora.datagrid.beans.Account;
import osa.ora.datagrid.beans.Payment;
/**
 * Class to test local data grid server 
 * @author Osama Redhat
 *
 */
public class CacheService {

	private RemoteCache<String, Account> accountsCache;
	private RemoteCache<String, Payment> paymentCache;
	private boolean cacheInitialized=false;
	public CacheService() {
		
	}
	public void init() {
		System.out.println("Initializing Remote Cache");
		try {
			ConfigurationBuilder builder = getConfigurationBuilder("d:/workspace/datagrid/src/main/java/hotrod-client.properties");
			//builder = getConfigurationBuilderByCode("127.0.0.1", 11222, "admin", "password");
			//set marshaling using Java Serialization
			builder.marshaller(new JavaSerializationMarshaller()).addJavaSerialWhiteList("osa.ora.datagrid.beans.Account","osa.ora.datagrid.beans.Payment");
			//set near cache (local in the JVM)
			//Near cache cannot be used with maxIdle for expiration
			builder.nearCache()
			  .mode(NearCacheMode.INVALIDATED)
			  .maxEntries(15);
			RemoteCacheManager remoteManager = new RemoteCacheManager(builder.build());
			System.out.println("Current Server address:" + remoteManager.getServers()[0].toString());
			accountsCache = remoteManager.getCache("accounts");
			paymentCache = remoteManager.getCache("payments");
			accountsCache.addClientListener(new CacheService().new SampleCacheListenerClass());
			paymentCache.addClientListener(new CacheService().new SampleCacheListenerClass());
			cacheInitialized=true;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public void printCacheStatistics() {
		System.out.println("Account Cache Statistics:");
		printStatistics(accountsCache.serverStatistics());
		System.out.println("Payment Cache Statistics:");
		printStatistics(paymentCache.serverStatistics());
		cacheInitialized=true;
	}
	/**
	 * reset caches
	 * @param cache
	 */
	public void resetCache() {
		//clear cache entries
		accountsCache.clear();
		paymentCache.clear();
	}
	/**
	 * Add/remove some objects from the cache
	 */
	public void manipulateSomeCache() {
		//add and remove some cache elements
		for(int i=0;i<50;i++) {
			String key="key"+i;
			Account account=new Account(i,"Osama Oransa","223234",1);
			Payment payment=new Payment(i,"1300 USD","Cairo, Egypt");
			accountsCache.put(key, account);
			accountsCache.replace(key, account);
			//paymentCache.putIfAbsent(key, payment);
			CompletableFuture<?> future=paymentCache.putAsync(key, payment);
			String value = (String) accountsCache.get(key).getName();
			System.out.println("value:" + value);
			value = (String) accountsCache.get(key).getName();
			System.out.println("value:" + value);
			//System.out.println("size:" + accountsCache.size());
			//System.out.println("size:" + accountsCache.keySet().size());
			
			MetadataValue<Account> metadata = accountsCache.getWithMetadata(key);
			System.out.println("Check for key:" + accountsCache.containsKey(key));
			if(metadata!=null) System.out.println("Metadata key version:" + metadata.getVersion());
			else System.out.println("Metadata is null");
			//System.out.println("**version:"+accountsCache.getVersioned(key).getVersion());
			try {
				if(future.isDone()) {
					System.out.println("==== Added");
				}else {
					System.out.println("==== Not Added");
				}
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			try {
				//if sleep more than the expired configuration
				//object will be removed from the cache as expired
				Thread.sleep(1100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//check if cache item didn't expire yet
			if(accountsCache.containsKey(key)) {
	    		System.out.println("*** Will delete non-expired account with key=" + key);
				accountsCache.remove(key);
		    	if(paymentCache.containsKey(key)) {
		    		System.out.println("*** Will remove the related payment " + key);
		    		paymentCache.remove(key);
		    	}
				System.out.println("Check for key:" + accountsCache.containsKey(key));
			}else {
				System.out.println("*** Expired item with key=" + key);
			}				
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Get Configuration Builder by properties file
	 * @param configFile
	 * @return
	 */
	private static ConfigurationBuilder getConfigurationBuilder(String configFile) {
		ConfigurationBuilder builder=new ConfigurationBuilder();
		Properties p = new Properties();
		try (Reader r = new FileReader(configFile)) {
			p.load(r);
			builder.withProperties(p);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return builder;
	}
	/**
	 * Get Configuration builder by code
	 * @return
	 */
	private static ConfigurationBuilder getConfigurationBuilderByCode(String host, int port, String user, String password) {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder
		   .addServer()
		      .host(host)
		      .port(port)
		   .security()
		      .authentication()
		         .username(user)
		         .password(password);
		/*builder.security()
	      .authentication().saslMechanism("PLAIN")
	         .username(user)
	         .password(password);*/
		return builder;
	}
	/**
	 * Print server statistics
	 * @param ss
	 */
	public void printStatistics(ServerStatistics ss) {
		System.out.println("No of enteries:"+ss.getStatistic(ServerStatistics.CURRENT_NR_OF_ENTRIES));
		System.out.println("No of hits:"+ss.getStatistic(ServerStatistics.HITS));
		System.out.println("No of misses:"+ss.getStatistic(ServerStatistics.MISSES));
		System.out.println("No of remove hits:"+ss.getStatistic(ServerStatistics.REMOVE_HITS));
		System.out.println("No of retrivals:"+ss.getStatistic(ServerStatistics.RETRIEVALS));
		System.out.println("No of remove misses:"+ss.getStatistic(ServerStatistics.REMOVE_MISSES));
		System.out.println("Time since start:"+ss.getStatistic(ServerStatistics.TIME_SINCE_START));
	}
	public boolean isCacheInitialized() {
		return cacheInitialized;
	}
	/**
	 * Client cache listener
	 * @author Osama Redhat
	 *
	 */
	@ClientListener
	class SampleCacheListenerClass {
	  @ClientCacheEntryCreated
	  public void print(ClientCacheEntryCreatedEvent<Object> event) {
	    System.out.println("New entry " + event + " created in the cache");
	  }
	  @ClientCacheEntryRemoved
	  public void print(ClientCacheEntryRemovedEvent<Object> event) {
	    System.out.println("Entry " + event + " removed from the cache");
	  }
	  @ClientCacheEntryExpired
	  public void print(ClientCacheEntryExpiredEvent<Object> event) {
	    System.out.println("Entry " + event  + " expired in the cache");
	  }
	  @ClientCacheEntryModified
	  public void print(ClientCacheEntryModifiedEvent<Object> event) {
	    System.out.println("Entry " + event  + " modified in the cache");
	  }
	}
}
