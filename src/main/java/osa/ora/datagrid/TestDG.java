package osa.ora.datagrid;

/**
 * Class to test local data grid server 
 * @author Osama Redhat
 *
 */
public class TestDG {

	public static void main(String[] args) {

		System.out.println("******* Remote Cache Client **********");
		CacheService cacheService=new CacheService();
		cacheService.init();
		if(cacheService.isCacheInitialized()) {
			cacheService.printCacheStatistics();
			cacheService.manipulateSomeCache();
			cacheService.printCacheStatistics();
		}
		
	}
}
