import java.util.*;
import static java.util.stream.Collectors.*;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


import java.util.Iterator;
import java.util.LinkedHashSet;


public class Analysis  {

		// Types of caches supported
	enum CacheTypes {
		DirectMapped, SetAssociative, FullyAssociative,
	}
		
	// a  lru cache set simulated inherited the functionalities of 
	// Linked Hash Set
	class LruCacheLinkedHashSet<T> extends LinkedHashSet<T> {
		
		// the cache set size will be predefined and won't grow beyond that
		private long maxSize;
		public boolean isConflictMiss=false;
		public boolean isItFirstCompulsaryMiss=false;
		public boolean checkFlag =false;
		public boolean checkHitFlag=false;
		public LruCacheLinkedHashSet(long maxSize) {
			this.maxSize = maxSize;
		}
		
		// overriding this to keep the size of the set constant
		// by adding the item at the end and popping out the first item when
		@Override
		public boolean add(T item) {
			if(checkFlag == true){
				checkFlag = false;
			    return super.add(item);
			}
			if (size() == maxSize) {
				removeFirst();
			}	
			return super.add(item);
		}
		
		// kind of simple search method with lru scheme forcing
		// the most recently used item at the tail.
		// this way the least recently used will go at the head.
		public T isItHit(T item) {
			if (super.contains(item)) {
				remove(item);
				add(item);
				return item;
			}
			
			if(size()==maxSize)
				{
			     isConflictMiss=true;
				}
			if(size() == 0)
			{ 
			    isItFirstCompulsaryMiss=true;
			    this.add(item);
			    return item;
			}
				//remove(item);
				Iterator<T> iterator = iterator();
				T replace_item = iterator.next();
				this.add(item);
			    return replace_item;
		}
		public void check(T item){
			if (super.contains(item)){
				remove(item);
				checkFlag = true;
				add(item);
				checkHitFlag=true;
			}
			else{ 
				checkHitFlag= false;
			}
				
		}

		// this function will only be used inside the other class functions
		public void removeFirst() {
			if (size() > 0) {
				Iterator<T> iterator = iterator();
				T item = iterator.next();
				remove(item);
			}
		}
	}

	// its basically a collection of cache sets, with all other information required
	// to intialise the cache sets
	class GenericCache {
		
		//information regarding the cache layout
		public long blockSize = -1;
		public long cacheCapacity = -1;
		public long noOfLines = -1;
		public long noOfWordsPerLine = -1;
		public long associativity = -1;
		public long noOfSets = -1;
		
		// a list of cache sets
		public List<LruCacheLinkedHashSet<Long>> cacheHashSets;

		public GenericCache(int cachePower, int blockPower, int wordPower, long associativity, CacheTypes cacheType) {
			
			// lets start by calculating the no of lines of cache
			this.cacheCapacity = (long) Math.pow(2, cachePower);
			this.blockSize = (long) Math.pow(2, blockPower);
			long wordSize = (long) Math.pow(2, wordPower);
			this.noOfLines = this.cacheCapacity / this.blockSize;
			
			// the set size or associativity will be decided by the cache type
			if (cacheType == CacheTypes.DirectMapped)
				this.associativity = 1;

			else if (cacheType == CacheTypes.SetAssociative)
				this.associativity = associativity;

			else if (cacheType == CacheTypes.FullyAssociative)
				this.associativity = this.noOfLines;
			
			// once the no of sets decided, move forward and intialise the List of Cache sets
			this.noOfSets = this.noOfLines / this.associativity;
			this.cacheHashSets = new ArrayList<LruCacheLinkedHashSet<Long>>();

			for (int i = 0; i < this.noOfSets; i++) {
				cacheHashSets.add(new LruCacheLinkedHashSet<Long>(this.associativity));
			}
			// this will help in converting word address to block address
			this.noOfWordsPerLine = this.blockSize / wordSize;
		}
		
		// again a typical search function with lru functionality in built
		public long isItHit(long wordAddress) {
			long blockAddress = wordAddress / this.noOfWordsPerLine;

			long index = blockAddress % this.noOfSets;

			long returnBlockAddress = cacheHashSets.get((int) index).isItHit(blockAddress);
			
            if(cacheHashSets.get((int) index).isItFirstCompulsaryMiss == true )//first compulsary miss and this block is executed only once for each set in its lifetime
			{
			cacheHashSets.get((int) index).isItFirstCompulsaryMiss = false; 
			return -1;
			}
			if(blockAddress == returnBlockAddress)//it means a hit
			return wordAddress;
			else if(cacheHashSets.get((int) index).isConflictMiss==true)//conflict miss so returning the kicked out block's first word address
			{
			    cacheHashSets.get((int) index).isConflictMiss=false;
			    return returnBlockAddress*this.noOfWordsPerLine;
			}
			else //non-first compulsary misses 
			return -1;
		
		}
				public long check(long wordAddress) {
			long blockAddress = wordAddress / this.noOfWordsPerLine;

			long index = blockAddress % this.noOfSets;

			cacheHashSets.get((int) index).check(blockAddress);//lrulinkedcache check not returning
			
            
			if(cacheHashSets.get((int) index).checkHitFlag == true)//it means a hit
			return wordAddress;
			
			else //any miss 
			return -1;
		
		}
		public void ValidateCache(long wordAddress)
		{
		    long blockAddress = wordAddress / this.noOfWordsPerLine;

			long index = blockAddress % this.noOfSets;
			
		    if (cacheHashSets.get((int) index).contains(blockAddress))
				cacheHashSets.get((int) index).remove(blockAddress);
				
		}
	}

	

	
	

	
	public static void main(String[] args)
	{
	    System.out.println("hey");
	    Analysis obj = new Analysis();
	    obj.Main();
  // do stuff
  }
  public void Main()
  { 
	
     GenericCache l2 = new GenericCache(19,6,0,8,CacheTypes.SetAssociative); 
     GenericCache l3 = new GenericCache(21,6,0,16,CacheTypes.SetAssociative);
	 //GenericCache l = new GenericCache(21,6,0,0,CacheTypes.FullyAssociative); use this to calculate capacity misses
	 long l2miss=0,l3miss=0,l3coldmiss=0,l3conflictMiss=0,capacityMiss=0;
	 Hashtable<Long,Long > addressTable = new Hashtable<Long,Long>();
	 
	 Scanner in = new Scanner(System.in);
	 System.out.print("Enter the cache type A or B or C : ");
	 String ch;
	 ch=in.nextLine();
	 try {
      File myObj = new File("bzip.txt");
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        String hexAddress = myReader.nextLine();
		long wordAddress = Long.parseLong(hexAddress,16);
		long blockAddress = wordAddress/64;
		
		//for cold misses run the below snippet
		//if(!addressTable.containsValue(blockAddress))
		//{addressTable.put(l3coldmiss,blockAddress);l3coldmiss++;}
		
		
		//for capacity miss use the below code don't forget to subtract l3conflictMiss from this number to get the capacity misses of our l3 cache
		//long temp = l.isItHit(wordAddress);  
		//if(temp == wordAddress)
		//{capacityMiss++;continue;}
		//else
		//	continue;
		
		switch(ch){
		case "A" : {
			long returnWordAddressL2,returnBlockAddressL2,returnWordAddressL3,returnBlockAddressL3;
			
			returnWordAddressL2 = l2.isItHit(wordAddress);
			returnBlockAddressL2 = returnWordAddressL2/64;
			
			if(returnBlockAddressL2 == blockAddress)
					continue;
				
			if(returnWordAddressL2 == -1)
			{
				l2miss++;
				returnWordAddressL3 = l3.isItHit(wordAddress);
				returnBlockAddressL3 = returnWordAddressL3/64;
				
				if(returnBlockAddressL3 == blockAddress)
					continue;
							
				if(returnWordAddressL3 == -1)
				{
					l3miss++;
					continue;
				}
				
				else //conflict miss in L3cache
				{
					l3conflictMiss++;
					l3miss++;
					l2.ValidateCache(returnWordAddressL3);
					continue;
				}
			}
			
			else //conflict miss in L2cache
			{
				l2miss++;
				returnWordAddressL3 = l3.isItHit(wordAddress);
				returnBlockAddressL3 = returnWordAddressL3/64;
				
				if(returnBlockAddressL3 == blockAddress)
					continue;
							
				if(returnWordAddressL3 == -1)
				{
					l3miss++;
					continue;
				}
				
				else //conflict miss in L3cache
				{
					l3conflictMiss++;
					l3miss++;
					l2.ValidateCache(returnWordAddressL3);
					continue;
				}
			}
			
		  }
        case "B" :{
			long returnWordAddressL2,returnBlockAddressL2,returnWordAddressL3,returnBlockAddressL3;
			
			returnWordAddressL2 = l2.isItHit(wordAddress);
			returnBlockAddressL2 = returnWordAddressL2/64;
			
			if(returnBlockAddressL2 == blockAddress)
					continue;
				
			
			else //conflict or compulsary miss in L2cache
			{
				l2miss++;
				returnWordAddressL3 = l3.isItHit(wordAddress);
				returnBlockAddressL3 = returnWordAddressL3/64;
				
				if(returnBlockAddressL3 == blockAddress)
					continue;
							

				else //conflict or compulsary miss in L3cache
				{
					l3miss++;
					//l2.ValidateCache(returnWordAddressL3); no need to validate as it is NINE
					continue;
				}
			}
			
		}
		case "C" : {
			long returnWordAddressL2,returnBlockAddressL2,returnWordAddressL3,returnBlockAddressL3;
			
			returnWordAddressL2 = l2.isItHit(wordAddress);
			returnBlockAddressL2 = returnWordAddressL2/64;
			
			if(returnBlockAddressL2 == blockAddress)
					continue;
				

			if(returnWordAddressL2 == -1) //compulsary miss in L2cache so no adding in l3
			{
				l2miss++;
				returnWordAddressL3 = l3.check(wordAddress);//checking for hit or miss in l3
				returnBlockAddressL3 = returnWordAddressL3/64;
				
				if(returnBlockAddressL3 == blockAddress)
				{
				l3.ValidateCache(wordAddress);
				continue;
				}
							

				else //any miss in L3cache
				{
					l3miss++;
					continue;
				}
			}
			
			else // conflict miss only now push into L3 cache
			{
				l2miss++;
				returnWordAddressL3 = l3.check(wordAddress);//checking for hit or miss in l3
				returnBlockAddressL3 = returnWordAddressL3/64;
				
				if(returnBlockAddressL3 == blockAddress){
					l3.ValidateCache(wordAddress);
					long temp = l3.isItHit(returnWordAddressL2);// pushing the evicted into l3
					continue;
				}
							

				else //conflict miss in L3cache
				{
					l3miss++;
					long temp = l3.isItHit(returnWordAddressL2);// pushing the evicted into l3
					continue;
				}				
				}
			
		}
			      
		}
	  }
      myReader.close();
    } 
  
	catch (FileNotFoundException e) 
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}	
	 System.out.println("L2 cache misses are : "+l2miss+" L3 cache misses are : "+l3miss+" "+l3conflictMiss);
	 //System.out.println("the number of cold misses are : "+addressTable.size());
	 //System.out.println("the number of capacity misses are "+capacityMiss);
	  
     
  }

}